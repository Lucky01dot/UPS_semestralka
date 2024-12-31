#define MAX_LOBBIES 5
#define LOBBY_CAPACITY 2
#define MAX_CLIENTS MAX_LOBBIES*LOBBY_CAPACITY
#define MAX_GAMES 5
#define KEEP_ALIVE_INTERVAL 30 // Čas v sekundách pro odesílání keep-alive zpráv




// Structures for client and logging info
typedef struct client {
    int socket_ID;
    char name[50];
    bool is_ready;
    bool is_white; // Nový atribut pro uložení role
} client;


typedef struct {
    client *clients[MAX_CLIENTS];
    int clients_count;
} clients;

typedef struct {
    client *players[LOBBY_CAPACITY]; // Pole hráčů v lobby
    int player_count;                // Počet aktuálních hráčů v lobby
    bool game_started;               // Indikátor, zda hra byla zahájena
} lobby;

typedef struct {
    lobby lobbies[MAX_LOBBIES]; // Seznam lobby
    int lobby_count;            // Aktuální počet lobby
} lobby_manager;

typedef struct {
    int count_messages;
    int count_bytes;
    int count_bad_transmissions;
    int count_connections;
} log_info;

typedef struct {
    int id; // Unikátní ID hry
    client *white_player;
    client *black_player;
    char board[8][8]; // nebo jiný formát dle herního pole
    bool is_white_turn;
} game;



typedef struct {
    game games[MAX_GAMES];
    int active_games;
} game_manager;





// Global variables
pthread_mutex_t client_mutex = PTHREAD_MUTEX_INITIALIZER;
int server_socket;
lobby_manager *manager;
game_manager *g_manager;
int game_id_counter = 1; // Globální proměnná


// Function prototypes
void sigint_handler();
void *client_handler(void *arg);
client *add_client(clients **array_clients, int socket_ID, const char *name);
void remove_client(clients **array_clients, int socket_ID);
void send_message(int client_socket, const char *message);
bool add_player_to_lobby(lobby_manager *manager, client *new_client);