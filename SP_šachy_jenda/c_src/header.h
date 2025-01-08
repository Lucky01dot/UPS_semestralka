#ifndef HEADER_H
#define HEADER_H

#include <stdbool.h>
#include <pthread.h>

// Konstanty definující omezení serveru
#define MAX_LOBBIES 5         // Maximální počet lobby
#define LOBBY_CAPACITY 2      // Maximální počet hráčů v jedné lobby
#define MAX_CLIENTS (MAX_LOBBIES * LOBBY_CAPACITY) // Maximální počet klientů na serveru
#define MAX_GAMES 5           // Maximální počet aktivních her
#define KEEP_ALIVE_INTERVAL 30 // Interval v sekundách pro odesílání keep-alive zpráv

// Struktura reprezentující klienta připojeného k serveru
typedef struct client {
    int socket_ID;         // Identifikátor socketu klienta
    char name[50];         // Jméno klienta
    bool is_ready;         // Indikátor, zda je hráč připraven
    bool is_white;         // Určuje, zda je hráč "bílý" v šachové hře
} client;

// Struktura reprezentující seznam všech klientů
typedef struct {
    client *clients[MAX_CLIENTS]; // Pole ukazatelů na klienty
    int clients_count;           // Aktuální počet klientů
} clients;

// Struktura reprezentující lobby (místnost pro hráče)
typedef struct {
    client *whiteplayer;  // Hráč hrající za bílé
    client *blackplayer;  // Hráč hrající za černé
    bool game_started;    // Indikátor, zda byla hra zahájena
} lobby;

// Struktura pro správu lobby
typedef struct {
    lobby lobbies[MAX_LOBBIES]; // Seznam lobby
    int lobby_count;            // Aktuální počet lobby
} lobby_manager;

// Struktura reprezentující jednu hru
typedef struct {
    int id;                     // Unikátní ID hry
    client *white_player;       // Hráč hrající za bílé
    client *black_player;       // Hráč hrající za černé
    char board[8][8];           // Herní šachovnice
    bool is_white_turn;         // Indikátor, zda je tah bílého hráče
} game;

// Struktura pro správu her
typedef struct {
    game games[MAX_GAMES];      // Pole her
    int active_games;           // Počet aktuálně aktivních her
} game_manager;

// Struktura pro logování statistik serveru
typedef struct {
    int count_messages;         // Počet přijatých zpráv
    int count_bytes;            // Počet přenesených bajtů
    int count_bad_transmissions;// Počet chybných přenosů
    int count_connections;      // Počet navázaných spojení
} log_info;

// Globální proměnné
extern pthread_mutex_t client_mutex;    // Mutex pro synchronizaci přístupu ke klientům
extern int server_socket;               // Socket serveru
extern lobby_manager *manager;          // Ukazatel na správce lobby
extern game_manager *g_manager;         // Ukazatel na správce her
extern int game_id_counter;             // Počítadlo ID her

// Deklarace funkcí
void sigint_handler(); // Obsluha signálu SIGINT pro ukončení serveru
void *client_handler(void *arg); // Funkce pro zpracování komunikace s klientem
void send_message(int client_socket, const char *message); // Odeslání zprávy klientovi
bool add_player_to_lobby(lobby_manager *manager, client *new_client); // Přidání hráče do lobby
client *create_client(int socket_ID, const char *name); // Vytvoření nového klienta
game *initialize_game(client *white, client *black);   // Inicializace nové hry

// Funkce pro správu klientů
void cleanup_disconnected_client(int client_socket); // Vyčištění odpojeného klienta
void process_logout(int client_socket);              // Zpracování odhlášení klienta
void process_leave_game(game_manager *g_manager, const char *message); // Odchod hráče ze hry

// Funkce pro zpracování různých příkazů od klientů
void handle_game_end(lobby_manager *manager, game_manager *g_manager, char *buffer); // Konec hry
void handle_login(char *message, int client_socket);  // Zpracování přihlášení
void handle_ready(int client_socket);                // Označení hráče jako připraveného
void handle_unready(int client_socket);              // Zrušení označení hráče jako připraveného
void handle_move(char *buffer, int client_socket);   // Zpracování tahu hráče
void handle_stop_game(char *buffer, int client_socket); // Ukončení hry
void handle_reconnect_game(char *buffer, int client_socket); // Opětovné připojení hráče ke hře

#endif // SERVER_H
