#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <pthread.h>
#include <sys/socket.h>
#include <netinet/in.h>
#include <arpa/inet.h>
#include <signal.h>
#include <unistd.h>
#include <stdbool.h>
#include <ctype.h>
#include "header.h"
#include <errno.h>


void cleanup_disconnected_client(int client_socket) {
    if (!manager || !g_manager) {
        printf("Error: manager or g_manager is NULL.\n");
        return;
    }

    // Kontrola lobby
    for (int i = 0; i < manager->lobby_count; i++) {
        printf("Checking lobby %d\n", i + 1);
        lobby *l = &manager->lobbies[i];

        if (l->whiteplayer && l->whiteplayer->socket_ID == client_socket) {
            printf("Removing white player %s from lobby %d.\n", l->whiteplayer->name, i + 1);
            free(l->whiteplayer);
            l->whiteplayer = NULL;
        } else if (l->blackplayer && l->blackplayer->socket_ID == client_socket) {
            printf("Removing black player %s from lobby %d.\n", l->blackplayer->name, i + 1);
            free(l->blackplayer);
            l->blackplayer = NULL;
        }

        if (l->whiteplayer == NULL && l->blackplayer == NULL) {
            printf("Lobby %d is now empty. Removing it.\n", i + 1);
            memset(l, 0, sizeof(lobby)); // Možné místo chyby
            manager->lobby_count--;
        }
    }

    // Kontrola her
    for (int i = 0; i < g_manager->active_games; i++) {
        printf("Checking game %d\n", i + 1);
        game *g = &g_manager->games[i];

        if (!g) {
            printf("Game pointer is NULL at index %d.\n", i);
            continue;
        }

        if (g->white_player && g->white_player->socket_ID == client_socket) {
            printf("White player disconnected from game %d.\n", g->id);
            free(g->white_player);
            g->white_player = NULL;
        } else if (g->black_player && g->black_player->socket_ID == client_socket) {
            printf("Black player disconnected from game %d.\n", g->id);
            free(g->black_player);
            g->black_player = NULL;
        }

        if (g->white_player == NULL && g->black_player == NULL) {
            printf("Game %d is now empty. Removing it.\n", g->id);
            memset(g, 0, sizeof(game)); // Možné místo chyby
            g_manager->active_games--;
        } else {
            client *remaining_player = g->white_player ? g->white_player : g->black_player;
            if (remaining_player && remaining_player->socket_ID > 0) {
                send_message(remaining_player->socket_ID, "OPPONENT_DISCONNECT_GAME\n");
                printf("Game %d: Informed remaining player (socket ID: %d) about opponent disconnection.\n",
                       g->id, remaining_player->socket_ID);
            }
        }
    }
}


void process_logout(int client_socket) {
    // Procházíme všechny lobby
    for (int i = 0; i < manager->lobby_count; i++) {
        lobby *l = &manager->lobbies[i];
        
        bool player_removed = false;

        // Kontrola hráče jako whiteplayer
        if (l->whiteplayer && l->whiteplayer->socket_ID == client_socket) {
            printf("Removing White player %s from lobby %d.\n", l->whiteplayer->name, i + 1);

            // Uvolnění paměti a nastavení na NULL
            free(l->whiteplayer);
            l->whiteplayer = NULL;
            player_removed = true;
        }

        // Kontrola hráče jako blackplayer
        if (l->blackplayer && l->blackplayer->socket_ID == client_socket) {
            printf("Removing Black player %s from lobby %d.\n", l->blackplayer->name, i + 1);

            // Uvolnění paměti a nastavení na NULL
            free(l->blackplayer);
            l->blackplayer = NULL;
            player_removed = true;
        }

        // Pokud byl hráč odstraněn, upravte stav lobby
        if (player_removed) {
            printf("Player successfully removed from lobby %d.\n", i + 1);

            // Snížení počtu hráčů
            if (l->whiteplayer == NULL && l->blackplayer == NULL) {
                l->game_started = false; // Pokud je lobby prázdné, hra nemůže pokračovat
            }
             int player_count = (l->whiteplayer != NULL) +
                                    (l->blackplayer != NULL);
            char update_message[128];
            snprintf(update_message, sizeof(update_message),
                        "PLAYER_COUNT;%d\n", player_count);
            // Odeslání zprávy pouze existujícím hráčům
            if (l->whiteplayer) {
                send_message(l->whiteplayer->socket_ID, update_message);
            }
            if (l->blackplayer) {
                send_message(l->blackplayer->socket_ID, update_message);
            }

            break; // Hráč nalezen a odstraněn, není třeba pokračovat
        }
    }
}


void process_leave_game(game_manager *g_manager, const char *message) {
    char command[20];
    char player_name[50];

    // Parse the message
    sscanf(message, "%[^;];%[^;];", command, player_name);

    if (strcmp(command, "leave_game") != 0) {
        printf("Invalid command in process_leave_game: %s\n", command);
        return;
    }

    pthread_mutex_lock(&client_mutex);
    printf("Debug: Mutex locked.\n");

    for (int i = 0; i < g_manager->active_games; i++) {
        game *current_game = &g_manager->games[i];
        printf("Debug: Checking game %d...\n", current_game->id);

        if ((current_game->white_player && strcmp(current_game->white_player->name, player_name) == 0) || 
            (current_game->black_player && strcmp(current_game->black_player->name, player_name) == 0)) {

            printf("Player %s leaving game %d\n", player_name, current_game->id);

            client *white_player = current_game->white_player;
            client *black_player = current_game->black_player;

            // Log player info
            if (white_player) {
                printf("White player: %s\n", white_player->name);
            }
            if (black_player) {
                printf("Black player: %s\n", black_player->name);
            }

            // Notify the remaining player
            if (white_player && strcmp(white_player->name, player_name) != 0) {
                send_message(white_player->socket_ID, "SERVER_OPPONENT_LEFT_GAME\n");
            }
            if (black_player && strcmp(black_player->name, player_name) != 0) {
                send_message(black_player->socket_ID, "SERVER_OPPONENT_LEFT_GAME\n");
            }

            if (white_player) {
                // Reset role před přidáním do lobby
                white_player->is_white = false;
                add_player_to_lobby(manager, white_player);
            }
            if (black_player) {
                // Reset role před přidáním do lobby
                black_player->is_white = false;
                add_player_to_lobby(manager, black_player);
            }

            // Remove game
            g_manager->games[i] = g_manager->games[g_manager->active_games - 1];
            g_manager->active_games--;
            printf("Game %d ended due to player leaving.\n", current_game->id);

            

            pthread_mutex_unlock(&client_mutex);
            printf("Debug: Mutex unlocked.\n");
            return;
        }
    }

    printf("Player %s not found in any active game\n", player_name);
    pthread_mutex_unlock(&client_mutex);
    printf("Debug: Mutex unlocked.\n");
}







void handle_game_end(lobby_manager *manager, game_manager *g_manager, char *buffer) {
    if (strncmp(buffer, "game_end;", 9) == 0) {
        char *game_id_str = buffer + 9; // Extract the portion after "game_end;"
        int game_id = atoi(game_id_str); // Convert the game ID to an integer
        
        

        // Find the game in the game_manager
        game *current_game = NULL;
        for (int i = 0; i < g_manager->active_games; i++) {
            if (g_manager->games[i].id == game_id) {
                current_game = &g_manager->games[i];
                break;
            }
        }

        if (current_game != NULL) {
            if (current_game->white_player != NULL) {
               add_player_to_lobby(manager, current_game->white_player);
            }
            if (current_game->black_player != NULL) {
               add_player_to_lobby(manager, current_game->black_player);
            }

            // Remove the game from the game_manager
            int game_index = -1;
            for (int i = 0; i < g_manager->active_games; i++) {
                if (g_manager->games[i].id == game_id) {
                    game_index = i;
                    break;
                }
            }
            if (game_index != -1) {
                // Shift remaining games to fill the gap
                for (int i = game_index; i < g_manager->active_games - 1; i++) {
                    g_manager->games[i] = g_manager->games[i + 1];
                }
                g_manager->active_games--;
            }
        } else {
            fprintf(stderr, "Game with ID %d not found.\n", game_id);
        }

        
    }
}

void handle_login(char *buffer, int client_socket) {
    // Extrakce jména ze zprávy
    char *name_start = buffer + 6; // Přeskočíme "login;"
    char *name_end = strchr(name_start, ';'); // Najdeme konec jména (první výskyt ';')
    if (!name_end) {
        printf("Invalid login format. Expected format: login;<name>;\n");
        send_message(client_socket, "ERROR: Invalid login format.\n");
        close(client_socket);
        pthread_mutex_unlock(&client_mutex);
        return;
    }
    *name_end = '\0'; // Nahradíme ';' nulovým znakem pro ukončení řetězce

    // Kontrola, zda už existuje uživatel se stejným jménem
    bool name_exists = false;
    for (int i = 0; i < MAX_LOBBIES; i++) {
        lobby *l = &manager->lobbies[i];
        if ((l->whiteplayer && strcmp(l->whiteplayer->name, name_start) == 0) ||
            (l->blackplayer && strcmp(l->blackplayer->name, name_start) == 0)) {
            name_exists = true;
            break;
        }
    }
    for (int i = 0; i < g_manager->active_games; i++) {
        game *g = &g_manager->games[i];
        if ((g->white_player && strcmp(g->white_player->name, name_start) == 0) ||
            (g->black_player && strcmp(g->black_player->name, name_start) == 0)) {
            name_exists = true;
            break;
        }
    }

    if (name_exists) {
        printf("User with name '%s' already exists.\n", name_start);
        send_message(client_socket, "USER_EXIST\n");
    } else {
        // Vytvoření klienta s extrahovaným jménem
        client *new_client = create_client(client_socket, name_start);
        lobby *assigned_lobby = NULL;
        if (new_client) {
            // Přidání hráče do lobby
            bool added_to_lobby = false;
            int lobby_id = -1;
            for (int i = 0; i < MAX_LOBBIES; i++) {
                lobby *l = &manager->lobbies[i];
                if (!l->game_started) { // Kontrolujeme pouze volné lobby
                    if (!l->whiteplayer) {
                        l->whiteplayer = new_client;
                        new_client->is_white = true;
                        added_to_lobby = true;
                        assigned_lobby = l;
                        lobby_id = i + 1;
                        break;
                    } else if (!l->blackplayer) {
                        l->blackplayer = new_client;
                        new_client->is_white = false;
                        added_to_lobby = true;
                        assigned_lobby = l;
                        lobby_id = i + 1;
                        break;
                    }
                }
            }
            if (assigned_lobby) {
                int player_count = (assigned_lobby->whiteplayer != NULL) +
                                    (assigned_lobby->blackplayer != NULL);
                char update_message[128];
                snprintf(update_message, sizeof(update_message),
                            "PLAYER_COUNT;%d\n", player_count);
                if(assigned_lobby->whiteplayer){
                    send_message(assigned_lobby->whiteplayer->socket_ID,update_message);
                }
                if(assigned_lobby->blackplayer){
                    send_message(assigned_lobby->blackplayer->socket_ID,update_message);
                }
            }


            if (added_to_lobby) {
                // Sestavení a odeslání zprávy s informacemi o jménu, roli a ID lobby
                char message[256];
                snprintf(message, sizeof(message),
                         "NAME:%s;ROLE:%s;READY:NO;LOBBY_ID:%d\n",
                         new_client->name,
                         new_client->is_white ? "WHITE" : "BLACK",
                         lobby_id);
                send_message(client_socket, message);


            } else {
                printf("No available lobbies for the user '%s'.\n", name_start);
                send_message(client_socket, "NO_AVAILABLE_LOBBIES\n");
                free(new_client);
            }
        } else {
            send_message(client_socket, "ERROR: Failed to create client.\n");
        }
    }
}


void handle_ready(int client_socket){
    bool found_client = false;

    for (int i = 0; i < manager->lobby_count; i++) {
        lobby *l = &manager->lobbies[i];

        if ((l->whiteplayer && l->whiteplayer->socket_ID == client_socket) ||
            (l->blackplayer && l->blackplayer->socket_ID == client_socket)) {
            
            client *current_client = (l->whiteplayer && l->whiteplayer->socket_ID == client_socket)
                                    ? l->whiteplayer
                                    : l->blackplayer;

            current_client->is_ready = true;
            found_client = true;
            send_message(client_socket, "READY\n");
            printf("Client %s in lobby %d is ready.\n", current_client->name, i + 1);

            if (l->whiteplayer && l->blackplayer &&
                l->whiteplayer->is_ready && l->blackplayer->is_ready) {
                // Inicializace nové hry
                game *new_game = initialize_game(l->whiteplayer, l->blackplayer);
                if (!new_game) {
                    perror("Failed to initialize new game");
                    send_message(client_socket, "ERROR: Failed to initialize game.\n");
                    pthread_mutex_unlock(&client_mutex);
                    
                }

                // Přidání hry do game manageru
                if (g_manager->active_games < MAX_GAMES) {
                    g_manager->games[g_manager->active_games++] = *new_game; // Kopie hry
                
                } else {
                    perror("Game manager is full");
                    send_message(client_socket, "ERROR: Game manager full.\n");
                    free(new_game); // Uvolnění alokované paměti
                    pthread_mutex_unlock(&client_mutex);
                    
                }

                // Poslání detailů o hráčích
                for (int k = 0; k < 2; k++) {
                    client *player = (k == 0) ? l->whiteplayer : l->blackplayer;
                    char player_message[256];
                    snprintf(
                        player_message, sizeof(player_message),
                        "START_GAME;%d;%s;%s;%s\n",
                        new_game->id,                      // ID hry
                        l->whiteplayer->name,              // Jméno hráče WHITE
                        l->blackplayer->name,              // Jméno hráče BLACK
                        player->is_white ? "WHITE" : "BLACK" // Role aktuálního hráče
                    );

                    // Poslat každému hráči
                    send_message(player->socket_ID, player_message);
                }

                l->game_started = true;
                printf("Game started in lobby %d with ID %d.\n", i + 1, new_game->id);

                // Odstranění hráčů z lobby
                l->whiteplayer = NULL;
                l->blackplayer = NULL;
            }

            break;
        }
    }

    if (!found_client) {
        printf("Error: Client not found for client_ready command.\n");
        send_message(client_socket, "ERROR: Client not found.\n");
    }
}
void handle_unready(int client_socket){
    pthread_mutex_lock(&client_mutex);
    bool found_client = false;

    for (int i = 0; i < manager->lobby_count; i++) {
    lobby *l = &manager->lobbies[i];

    if ((l->whiteplayer && l->whiteplayer->socket_ID == client_socket) ||
    (l->blackplayer && l->blackplayer->socket_ID == client_socket)) {

    client *current_client = (l->whiteplayer && l->whiteplayer->socket_ID == client_socket)
                            ? l->whiteplayer
                            : l->blackplayer;

    current_client->is_ready = false;
    found_client = true;
    send_message(client_socket, "UNREADY\n");
    printf("Client %s in lobby %d is unready.\n", current_client->name, i + 1);
    break;
    }
    }

    if (!found_client) {
    printf("Error: Client not found for client_unready command.\n");
    send_message(client_socket, "ERROR: Client not found.\n");
    }
}
void handle_move(char *buffer, int client_socket) {
    // Rozdeleni zpravy podle stredniku
    char temp[256];
    strcpy(temp, buffer); // Zkopirujeme zpravu, aby nebyla modifikovana
    char *token = strtok(temp, ";");

    if (strcmp(token, "make_move") != 0) {
        send_message(client_socket, "error;Invalid message format\n");
        return;
    }

    // Ziskani parametru tahu
    char *gameID_str = strtok(NULL, ";");
    char *pieceType = strtok(NULL, ";");
    char *oldCol_str = strtok(NULL, ";");
    char *oldRow_str = strtok(NULL, ";");
    char *newCol_str = strtok(NULL, ";");
    char *newRow_str = strtok(NULL, ";");
    char *capturedPieceType = strtok(NULL, ";");

    if (!gameID_str || !pieceType || !oldCol_str || !oldRow_str || 
        !newCol_str || !newRow_str) {
        send_message(client_socket, "error;Incomplete message\n");
        return;
    }

    int gameID = atoi(gameID_str);
    int oldCol = atoi(oldCol_str);
    int oldRow = atoi(oldRow_str);
    int newCol = atoi(newCol_str);
    int newRow = atoi(newRow_str);

    pthread_mutex_lock(&client_mutex);

    // Vyhledani hry podle GameID
    game *current_game = NULL;
    for (int i = 0; i < g_manager->active_games; i++) {
        if (g_manager->games[i].id == gameID) {
            current_game = &g_manager->games[i];
            break;
        }
    }

    if (current_game == NULL) {
        send_message(client_socket, "error;Game not found\n");
        pthread_mutex_unlock(&client_mutex);
        return;
    }

    // Validace tahu
    if (oldCol < 0 || oldCol >= 8 || oldRow < 0 || oldRow >= 8 ||
        newCol < 0 || newCol >= 8 || newRow < 0 || newRow >= 8) {
        send_message(client_socket, "error;Invalid move\n");
        pthread_mutex_unlock(&client_mutex);
        return;
    }

    // Kontrola, zda na dane pozici je spravna figurka
    char piece = current_game->board[oldRow][oldCol];
    if (piece == ' ') {
        send_message(client_socket, "error;No piece at the source position\n");
        pthread_mutex_unlock(&client_mutex);
        return;
    }

    // Rozpoznani typu figurky
    bool is_white_piece = isupper(piece); // Bile figury jsou velka pismena
    bool is_black_piece = islower(piece); // Cerne figury jsou mala pismena

    if ((current_game->is_white_turn && !is_white_piece) ||
        (!current_game->is_white_turn && !is_black_piece)) {
        send_message(client_socket, "error;Invalid piece for the current player\n");
        pthread_mutex_unlock(&client_mutex);
        return;
    }

    

    // Aktualizace herniho stavu (presun figurky na sachovnici)
    current_game->board[oldRow][oldCol] = ' ';
    current_game->board[newRow][newCol] = piece;

    // Poslat aktualizaci vsem hracum ve hre
    char update[256];
    snprintf(update, sizeof(update), "UPDATE;%d;%s;%d;%d;%d;%d;%s\n",
             gameID, pieceType, oldCol, oldRow, newCol, newRow,
             capturedPieceType != NULL ? capturedPieceType : "none");

    if (current_game->black_player && current_game->white_player) {
        send_message(current_game->black_player->socket_ID, update);
        send_message(current_game->white_player->socket_ID, update);
    }

    // Prepnuti tahu
    current_game->is_white_turn = !current_game->is_white_turn;
    pthread_mutex_unlock(&client_mutex);
}
void handle_invalid_move(char *buffer, int client_socket) {
    // Rozdělení zprávy podle separátoru (např. ";")
    char temp[256];
    strcpy(temp, buffer);
    char *token = strtok(temp, ";");

    // Validace typu zprávy
    if (strcmp(token, "SERVER_INVALID_MOVE") != 0) {
        send_message(client_socket, "error;Invalid message type\n");
        return;
    }

    // Získání parametrů ze zprávy
    int gameID = atoi(strtok(NULL, ";"));
    int newCol = atoi(strtok(NULL, ";"));
    int newRow = atoi(strtok(NULL, ";"));
    int oldCol = atoi(strtok(NULL, ";"));
    int oldRow = atoi(strtok(NULL, ";"));
    char *piece = strtok(NULL, ";");
    char *capture = strtok(NULL, ";");

    // Synchronizace pomocí mutexu
    pthread_mutex_lock(&client_mutex);

    // Vyhledání hry podle gameID
    game *current_game = NULL;
    for (int i = 0; i < g_manager->active_games; i++) {
        if (g_manager->games[i].id == gameID) {
            current_game = &g_manager->games[i];
            break;
        }
    }

    if (current_game == NULL) {
        pthread_mutex_unlock(&client_mutex);
        send_message(client_socket, "error;Game not found\n");
        return;
    }


    // Vrácení tahu na šachovnici
    current_game->board[oldRow][oldCol] = (piece && strcmp(piece, "none") != 0) ? piece[0] : ' ';
    current_game->board[newRow][newCol] = (capture && strcmp(capture, "none") != 0) ? capture[0] : ' ';

    

    // Uvolnění mutexu
    pthread_mutex_unlock(&client_mutex);
}


void handle_stop_game(char *buffer,int client_socket){
    pthread_mutex_lock(&client_mutex);

    // Extrakce ID hry ze zprávy
    char *id_start = buffer + 10; // Přeskočíme "stop_game;"
    int game_id = atoi(id_start); // Převod ID hry na celé číslo

    printf("Processing stop_game for game ID: %d\n", game_id);

    // Hledání hry podle ID
    game *current_game = NULL;
    for (int i = 0; i < g_manager->active_games; i++) {
        if (g_manager->games[i].id == game_id) {
            current_game = &g_manager->games[i];
            break;
        }
    }

    if (current_game == NULL) {
        printf("Game with ID %d not found.\n", game_id);
        send_message(client_socket, "ERROR: Game not found.\n");
        pthread_mutex_unlock(&client_mutex);
        
    }

    // Najít druhého hráče a poslat zprávu o ukončení hry
    client *opponent = NULL;
    if (current_game->white_player && current_game->white_player->socket_ID != client_socket) {
        opponent = current_game->white_player;
    } else if (current_game->black_player && current_game->black_player->socket_ID != client_socket) {
        opponent = current_game->black_player;
    }

    if (opponent) {
        send_message(opponent->socket_ID, "GAME_STOPPED\n");
        printf("Game ID %d stopped. Notified player %s (Socket: %d).\n", 
            game_id, opponent->name, opponent->socket_ID);
    } else {
        printf("No opponent found to notify for game ID %d.\n", game_id);
    }

    

    pthread_mutex_unlock(&client_mutex);
}
void handle_reconnect_game(char *buffer,int client_socket){
    pthread_mutex_lock(&client_mutex);

    // Extrakce ID hry ze zprávy
    char *id_start = buffer + 15; // Přeskočíme "stop_game;"
    int game_id = atoi(id_start); // Převod ID hry na celé číslo

    printf("Processing stop_game for game ID: %d\n", game_id);

    // Hledání hry podle ID
    game *current_game = NULL;
    for (int i = 0; i < g_manager->active_games; i++) {
        if (g_manager->games[i].id == game_id) {
            current_game = &g_manager->games[i];
            break;
        }
    }

    if (current_game == NULL) {
        printf("Game with ID %d not found.\n", game_id);
        send_message(client_socket, "ERROR: Game not found.\n");
        pthread_mutex_unlock(&client_mutex);
        
    }

    // Najít druhého hráče a poslat zprávu o ukončení hry
    client *opponent = NULL;
    if (current_game->white_player && current_game->white_player->socket_ID != client_socket) {
        opponent = current_game->white_player;
    } else if (current_game->black_player && current_game->black_player->socket_ID != client_socket) {
        opponent = current_game->black_player;
    }

    if (opponent) {
        send_message(opponent->socket_ID, "GAME_RECONNECTED\n");
        printf("Game ID %d stopped. Notified player %s (Socket: %d).\n", 
            game_id, opponent->name, opponent->socket_ID);
    } else {
        printf("No opponent found to notify for game ID %d.\n", game_id);
    }

    
    

    pthread_mutex_unlock(&client_mutex);
}
    
