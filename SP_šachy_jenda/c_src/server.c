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
// Global variables
pthread_mutex_t client_mutex = PTHREAD_MUTEX_INITIALIZER;
int server_socket;
lobby_manager *manager;
game_manager *g_manager;
int game_id_counter = 1; 

/*
void print_active_games(game_manager *g_manager) {
    if (!g_manager) {
        printf("Error: g_manager is NULL.\n");
        return;
    }

    printf("Current active games (%d):\n", g_manager->active_games);

    for (int i = 0; i < g_manager->active_games; i++) {
        game *current_game = &g_manager->games[i];
        if (!current_game) {
            printf("Error: Game at index %d is NULL.\n", i);
            continue;
        }

        printf("Game ID: %d\n", current_game->id);

        if (current_game->white_player) {
            printf("White Player: %s (Socket: %d)\n", 
                   current_game->white_player->name, 
                   current_game->white_player->socket_ID);
        } else {
            printf("White Player: None\n");
        }

        if (current_game->black_player) {
            printf("Black Player: %s (Socket: %d)\n", 
                   current_game->black_player->name, 
                   current_game->black_player->socket_ID);
        } else {
            printf("Black Player: None\n");
        }

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

void print_lobbies(const lobby_manager *lm) {
    printf("Lobbies (%d):\n", lm->lobby_count);
    for (int i = 0; i < lm->lobby_count; i++) {
        const lobby *l = &lm->lobbies[i];
        printf("Lobby %d:\n", i + 1);

        // Výpis hráčů v lobby
        printf("  Players:\n");
        if (l->whiteplayer) {
            printf("    Name: %s\n", l->whiteplayer->name);
            printf("    Role: White\n");
            printf("    Ready: %s\n", l->whiteplayer->is_ready ? "Yes" : "No");
        } else {
            printf("    White player: None\n");
        }

        if (l->blackplayer) {
            printf("    Name: %s\n", l->blackplayer->name);
            printf("    Role: Black\n");
            printf("    Ready: %s\n", l->blackplayer->is_ready ? "Yes" : "No");
        } else {
            printf("    Black player: None\n");
        }

        // Výpis stavu hry
        printf("  Game Started: %s\n", l->game_started ? "Yes" : "No");
    }
}

*/

/*

*****************Utility functions****************

*/
void trim_newline(char *str) {
    size_t len = strlen(str);
    while (len > 0 && isspace((unsigned char)str[len - 1])) {
        str[len - 1] = '\0';
        len--;
    }
}
bool add_player_to_lobby(lobby_manager *manager, client *new_client) {
    if (!new_client) {
        printf("Error: Invalid client data provided to add_player_to_lobby.\n");
        return false;
    }

    printf("Debug: Attempting to add player %s to a lobby...\n", new_client->name);

    for (int i = 0; i < MAX_LOBBIES; i++) {
        lobby *l = &manager->lobbies[i];

        // Kontrola dostupného místa v lobby
        if (!l->whiteplayer || !l->blackplayer) {
            printf("Debug: Lobby %d has space. Adding player...\n", i + 1);

            // Určení role pro nového hráče
            if (!l->whiteplayer) {
                l->whiteplayer = new_client; // Přidání jako "White"
                new_client->is_white = true;
                printf("Debug: Player %s added to lobby %d as White.\n", new_client->name, i + 1);
            } else if (!l->blackplayer) {
                l->blackplayer = new_client; // Přidání jako "Black"
                new_client->is_white = false;
                printf("Debug: Player %s added to lobby %d as Black.\n", new_client->name, i + 1);
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

            // Nastavení stavu hráče a lobby
            new_client->is_ready = false;
            l->game_started = false;

            return true;
        }
    }

    printf("Debug: All lobbies are full. Player %s could not be added.\n", new_client->name);
    return false;
}
void create_lobby_manager(lobby_manager **manager) {
    *manager = malloc(sizeof(lobby_manager));
    if (!*manager) {
        perror("Failed to allocate memory for lobby manager");
        exit(EXIT_FAILURE);
    }

    // Inicializace počtu lobby
    (*manager)->lobby_count = MAX_LOBBIES;

    // Inicializace každého lobby
    for (int i = 0; i < MAX_LOBBIES; i++) {
        (*manager)->lobbies[i].whiteplayer = NULL;
        (*manager)->lobbies[i].blackplayer = NULL;
        (*manager)->lobbies[i].game_started = false;
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

bool all_ready(const lobby *l) {
    // Zkontrolujeme, zda jsou oba hráči přítomní a připraveni
    return l->whiteplayer && l->blackplayer &&
           l->whiteplayer->is_ready && l->blackplayer->is_ready;
}


void send_message(int client_socket, const char *message) {
    printf("Sending message to socket %d: %s\n", client_socket, message);
;
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

        // Zavření socketů a uvolnění paměti pro hráče
        if (l->whiteplayer) {
            close(l->whiteplayer->socket_ID);
            free(l->whiteplayer);
            l->whiteplayer = NULL;
        }
        if (l->blackplayer) {
            close(l->blackplayer->socket_ID);
            free(l->blackplayer);
            l->blackplayer = NULL;
        }
    }
    pthread_mutex_unlock(&client_mutex);

    // Uvolnění paměti pro manager a zavření serverového socketu
    free(manager);
    close(server_socket);
    exit(0);
}


//////////////////////////////////////////////////////////////////////////////////////////////////////////////


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
                pthread_mutex_lock(&client_mutex);
                handle_login(buffer,client_socket);
                pthread_mutex_unlock(&client_mutex);
            }else if (strcmp(buffer, "client_ready;") == 0) {
                pthread_mutex_lock(&client_mutex);
                handle_ready(client_socket);
                pthread_mutex_unlock(&client_mutex);
            } else if (strcmp(buffer, "client_unready;") == 0) {
                printf("Processing client_unready command...\n");

                handle_unready(client_socket);
                pthread_mutex_unlock(&client_mutex);
            } else if (strcmp(buffer, "logout;") == 0) {
            pthread_mutex_lock(&client_mutex);
            process_logout(client_socket);
            pthread_mutex_unlock(&client_mutex);
            send_message(client_socket, "LOGOUT_SUCCESS\n");
            
        } else if(strncmp(buffer, "leave_game;", 11) == 0) {
            process_leave_game(g_manager,buffer); // Předání ukazatele na g_manager a zprávy
            
        }  else if (strncmp(buffer, "make_move;", 10) == 0) {
            handle_move(buffer,client_socket);
        } else if (strncmp(buffer, "game_end;", 9) == 0) {
            pthread_mutex_lock(&client_mutex);
            handle_game_end(manager,g_manager,buffer);
            pthread_mutex_unlock(&client_mutex);
        } else if (strncmp(buffer, "stop_game;", 10) == 0) {
            handle_stop_game(buffer,client_socket);
        } else if(strncmp(buffer, "reconnect_game;",15) == 0){
            handle_reconnect_game(buffer,client_socket);
        } else {
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

    pthread_mutex_lock(&client_mutex);
    cleanup_disconnected_client(client_socket);
    pthread_mutex_unlock(&client_mutex);

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
    server_addr.sin_addr.s_addr = INADDR_ANY; // Naslouchání na všech adresách
    if (inet_pton(AF_INET, server_ip, &server_addr.sin_addr) <= 0) {
        perror("Invalid IP address");
        close(server_socket);
        exit(EXIT_FAILURE);
    }
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
