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

#define DEFAULT_SERVER_ADDRESS "127.0.0.1"
#define DEFAULT_PORT 12000

/*



vyřešit situaci s reconnectem, ale to budu řešit až budu mít implementovaný tahy
Otestovat pořádně!!! a ende


*/

// Utility functions
void trim_newline(char *str) {
    size_t len = strlen(str);
    while (len > 0 && isspace((unsigned char)str[len - 1])) {
        str[len - 1] = '\0';
        len--;
    }
}
void remove_player_from_games(game_manager *g_manager, int socket_ID) {
    for (int i = 0; i < g_manager->active_games; i++) {
        game *g = &g_manager->games[i];

        if ((g->white_player && g->white_player->socket_ID == socket_ID) ||
            (g->black_player && g->black_player->socket_ID == socket_ID)) {
            
            printf("Removing player from game ID %d.\n", g->id);

            // Uvolni paměť hráčů, pokud existují
            if (g->white_player && g->white_player->socket_ID == socket_ID) {
                free(g->white_player);
                g->white_player = NULL;
            }
            if (g->black_player && g->black_player->socket_ID == socket_ID) {
                free(g->black_player);
                g->black_player = NULL;
            }

            // Posuň hry v poli `games` tak, aby odstraněná hra byla nahrazena
            for (int j = i; j < g_manager->active_games - 1; j++) {
                g_manager->games[j] = g_manager->games[j + 1];
            }

            g_manager->active_games--; // Sniž počet aktivních her
            i--; // Restartuj index, protože jsme hry posunuli
        }
    }
}

void process_logout(int client_socket) {
    pthread_mutex_lock(&client_mutex);

    // Odstraň hráče z lobby
    for (int i = 0; i < manager->lobby_count; i++) {
        lobby *l = &manager->lobbies[i];
        for (int j = 0; j < l->player_count; j++) {
            if (l->players[j] && l->players[j]->socket_ID == client_socket) {
                printf("Removing client %s from lobby %d.\n", l->players[j]->name, i + 1);
                free(l->players[j]);
                l->players[j] = NULL;

                // Posuň ostatní hráče v lobby
                for (int k = j; k < l->player_count - 1; k++) {
                    l->players[k] = l->players[k + 1];
                }
                l->players[l->player_count - 1] = NULL;
                l->player_count--;
                break;
            }
        }
    }

    // Odstraň hráče z her
    remove_player_from_games(g_manager, client_socket);

    pthread_mutex_unlock(&client_mutex);
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

    pthread_mutex_lock(&client_mutex); // Lock for thread safety
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

            // Reset players
            if (white_player) {
                white_player->is_ready = false;
                
            }
            if (black_player) {
                black_player->is_ready = false;
                
            }

            // Remove game
            g_manager->games[i] = g_manager->games[g_manager->active_games - 1];
            g_manager->active_games--;
            printf("Game %d ended due to player leaving.\n", current_game->id);

            // Notify clients
            if (white_player) {
                send_message(white_player->socket_ID, "SERVER_LEAVE_GAME\n");
            }
            if (black_player) {
                send_message(black_player->socket_ID, "SERVER_LEAVE_GAME\n");
            }

            pthread_mutex_unlock(&client_mutex);
            printf("Debug: Mutex unlocked.\n");
            return;
        }
    }

    printf("Player %s not found in any active game\n", player_name);
    pthread_mutex_unlock(&client_mutex);
    printf("Debug: Mutex unlocked.\n");
}
void print_active_games(game_manager *g_manager) {
    printf("Current active games (%d):\n", g_manager->active_games);
    for (int i = 0; i < g_manager->active_games; i++) {
        game *current_game = &g_manager->games[i];
        printf("Game ID: %d\n", current_game->id);
        printf("White Player: %s (Socket: %d)\n", 
               current_game->white_player->name, 
               current_game->white_player->socket_ID);
        printf("Black Player: %s (Socket: %d)\n", 
               current_game->black_player->name, 
               current_game->black_player->socket_ID);
        printf("Is White Turn: %s\n", current_game->is_white_turn ? "Yes" : "No");
        printf("Board State:\n");
        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                printf("%c ", current_game->board[row][col]);
            }
            printf("\n");
        }
        printf("---------------\n");
    }
}









void create_lobby_manager(lobby_manager **manager) {
    *manager = malloc(sizeof(lobby_manager));
    if (!*manager) {
        perror("Failed to allocate memory for lobby manager");
        exit(EXIT_FAILURE);
    }
    (*manager)->lobby_count = MAX_LOBBIES;
    for (int i = 0; i < MAX_LOBBIES; i++) {
        (*manager)->lobbies[i].player_count = 0;
        (*manager)->lobbies[i].game_started = false;
        for (int j = 0; j < LOBBY_CAPACITY; j++) {
            (*manager)->lobbies[i].players[j] = NULL;
        }
    }
}

void create_game_manager(game_manager **manager) {
    *manager = malloc(sizeof(game_manager));
    if (!*manager) {
        perror("Failed to allocate memory for game manager");
        exit(EXIT_FAILURE);
    }
    (*manager)->active_games = 0;
    for (int i = 0; i < MAX_GAMES; i++) {
        (*manager)->games[i].white_player = NULL;
        (*manager)->games[i].black_player = NULL;
        (*manager)->games[i].is_white_turn = true;
    }
}
game *initialize_game(client *white, client *black) {
    game *new_game = malloc(sizeof(game));
    if (!new_game) {
        perror("Failed to allocate memory for new game");
        return NULL;
    }

    new_game->id = game_id_counter++;
    new_game->white_player = white;
    new_game->black_player = black;
    new_game->is_white_turn = true;

    // Inicializace výchozího rozložení šachovnice
    char initial_board[8][8] = {
        {'r', 'n', 'b', 'q', 'k', 'b', 'n', 'r'}, // Černé figury
        {'p', 'p', 'p', 'p', 'p', 'p', 'p', 'p'}, // Černí pěšci
        {' ', ' ', ' ', ' ', ' ', ' ', ' ', ' '}, // Prázdné řádky
        {' ', ' ', ' ', ' ', ' ', ' ', ' ', ' '},
        {' ', ' ', ' ', ' ', ' ', ' ', ' ', ' '},
        {' ', ' ', ' ', ' ', ' ', ' ', ' ', ' '},
        {'P', 'P', 'P', 'P', 'P', 'P', 'P', 'P'}, // Bílí pěšci
        {'R', 'N', 'B', 'Q', 'K', 'B', 'N', 'R'}  // Bílé figury
    };

    memcpy(new_game->board, initial_board, sizeof(initial_board));

    return new_game;
}



client *create_client(int socket_ID, const char *name) {
    client *new_client = malloc(sizeof(client));
    if (!new_client) {
        perror("Failed to allocate memory for new client");
        return NULL;
    }
    new_client->socket_ID = socket_ID;
    strncpy(new_client->name, name, sizeof(new_client->name) - 1);
    new_client->name[sizeof(new_client->name) - 1] = '\0';
    new_client->is_white = false;
    new_client->is_ready = false;
    return new_client;
}

bool add_player_to_lobby(lobby_manager *manager, client *new_client) {
    if (!new_client ) {
        printf("Error: Invalid client data provided to add_player_to_lobby.\n");
        return false;
    }

    printf("Debug: Attempting to add player %s to a lobby...\n", new_client->name);
    pthread_mutex_lock(&client_mutex);

    for (int i = 0; i < MAX_LOBBIES; i++) {
        lobby *l = &manager->lobbies[i];

        if (l->player_count < LOBBY_CAPACITY) { // Kontrola dostupného místa
            l->players[l->player_count] = new_client;

            if (l->player_count == 0) {
                new_client->is_white = true; // První hráč je White
            } else if (l->player_count == 1) {
                new_client->is_white = false; // Druhý hráč je Black
            }

            l->player_count++;
            printf("Debug: Player %s successfully added to lobby %d as %s\n", 
                   new_client->name, i + 1, new_client->is_white ? "White" : "Black");
            printf("Lobby %d: Players = %d\n", i + 1, l->player_count);
            pthread_mutex_unlock(&client_mutex);
            return true;
        }
    }

    printf("Debug: All lobbies are full. Player %s could not be added.\n", new_client->name);
    pthread_mutex_unlock(&client_mutex);
    return false;
}



bool all_ready(lobby *l) {
    return l->player_count == LOBBY_CAPACITY &&
           l->players[0]->is_ready && l->players[1]->is_ready;
}

void send_message(int client_socket, const char *message) {
    printf("Sending message to socket %d: %s\n", client_socket, message);

    ssize_t result = send(client_socket, message, strlen(message), 0);
    if (result == -1) {
        perror("Error sending message");
    } else {
        printf("Message sent successfully (%zd bytes)\n", result);
    }
}


void sigint_handler() {
    printf("\nShutting down server...\n");

    pthread_mutex_lock(&client_mutex);
    for (int i = 0; i < manager->lobby_count; i++) {
        lobby *l = &manager->lobbies[i];
        for (int j = 0; j < l->player_count; j++) {
            if (l->players[j]) {
                close(l->players[j]->socket_ID);
                free(l->players[j]);
            }
        }
    }
    pthread_mutex_unlock(&client_mutex);

    free(manager);
    close(server_socket);
    exit(0);
}

// Funkce pro výpis šachovnice
void print_board(char board[8][8]) {
    printf("  a b c d e f g h\n"); // Sloupce
    printf("  ----------------\n");
    for (int i = 0; i < 8; i++) {
        printf("%d|", 8 - i); // Řádky (čísla šachovnice)
        for (int j = 0; j < 8; j++) {
            printf("%c ", board[i][j] == ' ' ? '.' : board[i][j]); // Prázdná pole zobrazit jako '.'
        }
        printf("|%d\n", 8 - i); // Řádky (čísla šachovnice)
    }
    printf("  ----------------\n");
    printf("  a b c d e f g h\n\n");
}
void cleanup_disconnected_client(int client_socket) {
    pthread_mutex_lock(&client_mutex);

    // Odstranění z lobby
    for (int i = 0; i < manager->lobby_count; i++) {
        lobby *l = &manager->lobbies[i];
        for (int j = 0; j < LOBBY_CAPACITY; j++) {
            if (l->players[j] && l->players[j]->socket_ID == client_socket) {
                printf("Removing client %s from lobby %d.\n", l->players[j]->name, i + 1);
                free(l->players[j]);
                l->players[j] = NULL;
                l->player_count--;

                // Pokud je lobby prázdná, odstraníme ji
                if (l->player_count == 0) {
                    printf("Lobby %d is now empty. Removing it.\n", i + 1);
                    memset(l, 0, sizeof(lobby));
                    manager->lobby_count--;
                }
                break;
            }
        }
    }

    // Odstranění z her
    for (int i = 0; i < g_manager->active_games; i++) {
        game *g = &g_manager->games[i];
        bool game_removed = false;

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
            memset(g, 0, sizeof(game));
            g_manager->active_games--;
            game_removed = true;
        } else {
            // Oznámení zbývajícímu hráči
            client *remaining_player = g->white_player ? g->white_player : g->black_player;

            if (remaining_player && remaining_player->socket_ID > 0) {
                send_message(remaining_player->socket_ID, "error;Opponent disconnected. Game over.\n");
                printf("Game %d: Informed remaining player (socket ID: %d) about opponent disconnection.\n", 
                       g->id, remaining_player->socket_ID);
            } else {
                printf("Game %d: Error: Remaining player not found or invalid socket ID.\n", g->id);
            }
        }

        // Pokud hra byla ukončena, přerušit smyčku, protože se ukazatele staly neplatnými
        if (game_removed) {
            break;
        }
    }

    pthread_mutex_unlock(&client_mutex);
}






void *client_handler(void *arg) {
    int client_socket = *(int *)arg;
    free(arg);
    char buffer[2048];
    int bytes_received;

    printf("Client handler started for socket %d\n", client_socket);

    time_t last_activity = time(NULL); // Poslední aktivita klienta

    while (1) {
        struct timeval timeout = {KEEP_ALIVE_INTERVAL, 0}; // Nastavte časový limit pro recv
        if (setsockopt(client_socket, SOL_SOCKET, SO_RCVTIMEO, &timeout, sizeof(timeout)) < 0) {
            perror("setsockopt failed");
        }
        bytes_received = recv(client_socket, buffer, sizeof(buffer) - 1, 0);

        if(bytes_received > 0){
            buffer[bytes_received] = '\0';
            trim_newline(buffer);
            printf("Received: '%s'\n", buffer);

            last_activity = time(NULL); // Aktualizace poslední aktivity
            if (strcmp(buffer, "keep_alive;") == 0) {
                send_message(client_socket, "KEEP_ALIVE_OK\n");
                continue;
            } else if (strncmp(buffer, "login;", 6) == 0) {
            printf("Processing login command...\n");

            // Extrakce jména ze zprávy
            char *name_start = buffer + 6; // Přeskočíme "login;"
            char *name_end = strchr(name_start, ';'); // Najdeme konec jména (první výskyt ';')
            if (!name_end) {
                printf("Invalid login format. Expected format: login;<name>;\n");
                send_message(client_socket, "ERROR: Invalid login format.\n");
                close(client_socket);
                return NULL;
            }
            *name_end = '\0'; // Nahradíme ';' nulovým znakem pro ukončení řetězce

            // Vytvoření klienta s extrahovaným jménem
            client *new_client = create_client(client_socket, name_start);
            if (new_client) {
                if (add_player_to_lobby(manager, new_client)) {
                    // Sestavení a odeslání zprávy s informacemi o roli, připravenosti a jménu
                    char message[256];
                    snprintf(message, sizeof(message),
                            "NAME:%s;ROLE:%s;READY:%s\n",
                            new_client->name,
                            new_client->is_white ? "WHITE" : "BLACK",
                            new_client->is_ready ? "YES" : "NO");
                    send_message(client_socket, message);
                } else {
                    send_message(client_socket, "No available lobbies.\n");
                    free(new_client);
                    close(client_socket);
                    return NULL;
                }
            }
        } else if (strcmp(buffer, "client_ready;") == 0) {
            printf("Processing client_ready command...\n");

            pthread_mutex_lock(&client_mutex);
            bool found_client = false;

            for (int i = 0; i < manager->lobby_count; i++) {
                lobby *l = &manager->lobbies[i];

                for (int j = 0; j < l->player_count; j++) {
                    if (l->players[j] && l->players[j]->socket_ID == client_socket) {
                        l->players[j]->is_ready = true;
                        found_client = true;
                        send_message(client_socket, "READY\n");
                        printf("Client %s in lobby %d is ready.\n", l->players[j]->name, i + 1);

                        if (all_ready(l)) {
                            // Inicializace nové hry
                            game *new_game = initialize_game(l->players[0], l->players[1]);
                            if (!new_game) {
                                perror("Failed to initialize new game");
                                send_message(client_socket, "ERROR: Failed to initialize game.\n");
                                pthread_mutex_unlock(&client_mutex);
                                
                            }

                            // Přidání hry do game manageru
                            if (g_manager->active_games < MAX_GAMES) {
                                g_manager->games[g_manager->active_games++] = *new_game; // Kopie hry
                                print_active_games(g_manager);
                            } else {
                                perror("Game manager is full");
                                send_message(client_socket, "ERROR: Game manager full.\n");
                                free(new_game); // Uvolnění alokované paměti
                                pthread_mutex_unlock(&client_mutex);
                                
                            }

                            // Poslání detailů o hráčích
                            for (int k = 0; k < l->player_count; k++) {
                                char player_message[256];
                                snprintf(
                                    player_message, sizeof(player_message),
                                    "START_GAME;%d;%s;%s;%s\n",
                                    new_game->id,                      // ID hry
                                    l->players[0]->name,               // Jméno hráče WHITE
                                    l->players[1]->name,               // Jméno hráče BLACK
                                    l->players[k]->is_white ? "WHITE" : "BLACK" // Role aktuálního hráče
                                );

                                //Poslat každému hráči
                                send_message(l->players[k]->socket_ID, player_message);
                                    
                                
                            }

                            l->game_started = true;
                            printf("Game started in lobby %d with ID %d.\n", i + 1, new_game->id);
                        }


                        break;
                    }
                }

                if (found_client) break;
            }

            if (!found_client) {
                printf("Error: Client not found for client_ready command.\n");
                send_message(client_socket, "ERROR: Client not found.\n");
            }

            pthread_mutex_unlock(&client_mutex);
        } else if (strcmp(buffer, "client_unready;") == 0) {
            printf("Processing client_unready command...\n");
            pthread_mutex_lock(&client_mutex);
            bool found_client = false;

            for (int i = 0; i < manager->lobby_count; i++) {
                lobby *l = &manager->lobbies[i];

                for (int j = 0; j < l->player_count; j++) {
                    if (l->players[j] && l->players[j]->socket_ID == client_socket) {
                        l->players[j]->is_ready = false;
                        found_client = true;
                        send_message(client_socket, "UNREADY\n");
                        printf("Client %s in lobby %d is unready.\n", l->players[j]->name, i + 1);
                        break;
                    }
                }

                if (found_client) break;
            }

            if (!found_client) {
                printf("Error: Client not found for client_unready command.\n");
                send_message(client_socket, "ERROR: Client not found.\n");
            }

            pthread_mutex_unlock(&client_mutex);
        } else if (strcmp(buffer, "logout;") == 0) {
            printf("Processing logout command...\n");
            process_logout(client_socket);
            send_message(client_socket, "LOGOUT_SUCCESS\n");
            close(client_socket);
            return NULL;
        } else if(strncmp(buffer, "leave_game;", 11) == 0) {
            process_leave_game(g_manager,buffer); // Předání ukazatele na g_manager a zprávy
        }  else if (strncmp(buffer, "make_move;", 10) == 0) {
            // Rozdělení zprávy podle středníků
            char temp[256];
            strcpy(temp, buffer); // Zkopírujeme zprávu, aby nebyla modifikována
            char *token = strtok(temp, ";");

            if (strcmp(token, "make_move") != 0) {
                send_message(client_socket, "error;Invalid message format\n");
                return;
            }

            // Získání parametrů tahu
            int gameID = atoi(strtok(NULL, ";"));
            char *pieceType = strtok(NULL, ";");
            int oldCol = atoi(strtok(NULL, ";"));
            int oldRow = atoi(strtok(NULL, ";"));
            int newCol = atoi(strtok(NULL, ";"));
            int newRow = atoi(strtok(NULL, ";"));
            char *capturedPieceType = strtok(NULL, ";");

            pthread_mutex_lock(&client_mutex);

            // Vyhledání hry podle GameID
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

            // Kontrola, zda na dané pozici je správná figurka
            char piece = current_game->board[oldRow][oldCol];
           

            // Rozpoznání typu figurky
            bool is_white_piece = isupper(piece); // Bílé figury jsou velká písmena
            bool is_black_piece = islower(piece); // Černé figury jsou malá písmena

            if ((current_game->is_white_turn && !is_white_piece) ||
                (!current_game->is_white_turn && !is_black_piece)) {
                send_message(client_socket, "error;Invalid piece for the current player\n");
                pthread_mutex_unlock(&client_mutex);
                return;
            }

            

            // Aktualizace herního stavu (přesun figurky na šachovnici)
            current_game->board[oldRow][oldCol] = ' ';
            current_game->board[newRow][newCol] = piece;

            // Výpis aktuální šachovnice po tahu
            printf("Game ID: %d - Board after move:\n", gameID);
            print_board(current_game->board);


                        // Poslat aktualizaci všem hráčům ve hře
            char update[256];
            snprintf(update, sizeof(update), "UPDATE;%d;%s;%d;%d;%d;%d;%s\n",
                    gameID, pieceType, oldCol, oldRow, newCol, newRow,
                    capturedPieceType != NULL ? capturedPieceType : "none");

            if (current_game->is_white_turn && current_game->black_player) {
                // Pokud je nyní na tahu bílý, pošleme aktualizaci černému hráči
                send_message(current_game->black_player->socket_ID, update);
            } else if (!current_game->is_white_turn && current_game->white_player) {
                // Pokud je nyní na tahu černý, pošleme aktualizaci bílému hráči
                send_message(current_game->white_player->socket_ID, update);
            }


            // Přepnutí tahu
            current_game->is_white_turn = !current_game->is_white_turn;



            pthread_mutex_unlock(&client_mutex);
        }else {
            printf("Unknown command received: '%s'\n", buffer);
            send_message(client_socket, "Unknown command.\n");
        }
    }else if (bytes_received == 0) {
        printf("Client disconnected on socket %d.\n", client_socket);
        break;
    } else {
        if (errno == EWOULDBLOCK || errno == EAGAIN) {
            time_t now = time(NULL);
            if (now - last_activity > KEEP_ALIVE_INTERVAL * 2) {
                printf("Client timeout on socket %d.\n", client_socket);
                break; // Odpojení kvůli nečinnosti
            }
        } else {
            perror("recv");
            break;
        }
    }
        
    }

    cleanup_disconnected_client(client_socket);

    close(client_socket);
    printf("Client handler exiting for socket %d.\n", client_socket);
    return NULL;
}

int main(int argc, char *argv[]) {
// Výchozí hodnoty
    char server_ip[INET_ADDRSTRLEN] = DEFAULT_SERVER_ADDRESS;
    int port = DEFAULT_PORT;

    // Zpracování argumentů příkazového řádku
    if (argc >= 2) {
        char *input = argv[1];
        char *ip = strtok(input, "/");
        char *port_str = strtok(NULL, "/");

        if (ip) {
            strncpy(server_ip, ip, INET_ADDRSTRLEN - 1);
            server_ip[INET_ADDRSTRLEN - 1] = '\0';
        }
        if (port_str) {
            port = atoi(port_str);
        }

        // Kontrola, zda byly obě části správně načteny
        if (!ip || !port_str) {
            fprintf(stderr, "Invalid address format. Use <IP>/<PORT> (e.g., 127.0.0.1/10000)\n");
            exit(EXIT_FAILURE);
        }
    }

    // Výpis nastavení serveru
    printf("Server starting on %s:%d\n", server_ip, port);

    // Nastavení SIGINT handleru pro bezpečné ukončení
    signal(SIGINT, sigint_handler);

    // Vytvoření socketu
    int server_socket = socket(AF_INET, SOCK_STREAM, 0);
    if (server_socket == -1) {
        perror("Socket creation failed");
        exit(EXIT_FAILURE);
    }

    // Nastavení adresy serveru
    struct sockaddr_in server_addr = {0};
    server_addr.sin_family = AF_INET;
    server_addr.sin_addr.s_addr = inet_addr(server_ip);
    server_addr.sin_port = htons(port);

    // Bindování serveru na zadanou IP adresu a port
    if (bind(server_socket, (struct sockaddr *)&server_addr, sizeof(server_addr)) == -1) {
        perror("Bind failed");
        close(server_socket);
        exit(EXIT_FAILURE);
    }

    if (listen(server_socket, MAX_CLIENTS) == -1) {
        perror("Listen failed");
        close(server_socket);
        exit(EXIT_FAILURE);
    }

    printf("Server listening on %s:%d\n", server_ip, port);

    create_lobby_manager(&manager);
    create_game_manager(&g_manager);

    while (1) {
        struct sockaddr_in client_addr;
        socklen_t addr_len = sizeof(client_addr);
        int *client_socket = malloc(sizeof(int));
        if (!client_socket) {
            perror("Failed to allocate memory for client socket");
            continue;
        }

        *client_socket = accept(server_socket, (struct sockaddr *)&client_addr, &addr_len);
        if (*client_socket == -1) {
            perror("Accept failed");
            free(client_socket);
            continue;
        }

        printf("Client connected: %s:%d\n", inet_ntoa(client_addr.sin_addr), ntohs(client_addr.sin_port));

        pthread_t thread_id;
        if (pthread_create(&thread_id, NULL, client_handler, client_socket) != 0) {
            perror("Failed to create thread");
            free(client_socket);
            continue;
        }
        pthread_detach(thread_id);
    }

    close(server_socket);
    return 0;
}
