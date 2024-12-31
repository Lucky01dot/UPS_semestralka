#include "header.h"
#include <stdlib.h>  // For malloc, realloc, exit, EXIT_FAILURE
#include <string.h>  // For strdup, strcpy, strncpy, strcmp
#include <stdio.h>   // For perror, fprintf, stderr
#include <stdbool.h>

void create_clients(clients **array_clients) {
    *array_clients = malloc(sizeof(clients));
    if (!*array_clients) {
        perror("Failed to allocate memory for clients");
        exit(1);
    }
    (*array_clients)->clients = malloc(MAX_CLIENTS * sizeof(client *));
if ((*array_clients)->clients == NULL) {
    perror("Failed to allocate memory for clients array");
    exit(1);
}
}


client *add_client(clients **array_clients, char *name, int socket_ID, char *role) {
    if ((*array_clients)->clients_count >= MAX_CLIENTS) {
        printf("Maximum number of clients reached. Cannot add client %s.\n", name);
        return NULL;
    }

    client *new_client = malloc(sizeof(client));
    if (!new_client) {
        perror("Failed to allocate memory for new client");
        return NULL;
    }

    new_client->name = strdup(name);
    new_client->socket_ID = socket_ID;
    new_client->is_ready = false;
    strcpy(new_client->role, role);

    (*array_clients)->clients[(*array_clients)->clients_count++] = new_client;

    return new_client;
}


void set_ready_status(client *cl, int status) {
    if (status == 0 || status == 1) {
        cl->is_ready = status;
    } else {
        fprintf(stderr, "Invalid ready status for client: %d\n", status);
    }
}

client* get_client_by_socket_ID(clients *array_clients, int socket_ID) {
    for (int i = 0; i < array_clients->clients_count; i++) {
        if (array_clients->clients[i]->socket_ID == socket_ID) {
            return array_clients->clients[i];
        }
    }
    return NULL;
}

void set_role(client **cl, const char *role) {
    if (cl && *cl && role) {
        strncpy((*cl)->role, role, sizeof((*cl)->role) - 1);
        (*cl)->role[sizeof((*cl)->role) - 1] = '\0'; // Null-terminate
    }
}

void set_ready(client **cl, int ready) {
    if (cl && *cl) {
        (*cl)->is_ready = ready;
    }
}



void client_remove(clients **array_clients, int socket_ID) {
    if (!array_clients || !(*array_clients) || (*array_clients)->clients_count == 0) {
        fprintf(stderr, "No clients to remove.\n");
        return;
    }

    clients *arr = *array_clients;
    int found = -1;

    // Najdeme klienta podle socket_ID
    for (int i = 0; i < arr->clients_count; i++) {
        if (arr->clients[i]->socket_ID == socket_ID) {
            found = i;
            break;
        }
    }

    if (found == -1) {
        fprintf(stderr, "Client with Socket ID %d not found.\n", socket_ID);
        return;
    }

    // Uvolnění paměti odstraněného klienta
    free(arr->clients[found]->name);  // Uvolníme řetězec `name`
    free(arr->clients[found]);       // Uvolníme klienta

    // Přesuneme zbývající klienty, aby nebyly díry v poli
    for (int i = found; i < arr->clients_count - 1; i++) {
        arr->clients[i] = arr->clients[i + 1];
    }

    // Zmenšíme velikost pole
    arr->clients_count--;
    arr->clients = realloc(arr->clients, sizeof(client *) * arr->clients_count);

    if (arr->clients_count > 0 && !arr->clients) {
        perror("Failed to reallocate clients array after removal");
        exit(EXIT_FAILURE);
    }

    

    printf("Client with Socket ID %d removed successfully.\n", socket_ID);
}


