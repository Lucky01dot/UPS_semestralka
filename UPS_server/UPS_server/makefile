# Compiler and flags
CC = gcc
CFLAGS = -Wall -Wextra -g

# Executable name
EXEC = chess_server

# Source files and object files
SRC = server.c
OBJ = $(SRC:.c=.o)

# Default target to build the executable
all: $(EXEC)

# Link object files to create the executable
$(EXEC): $(OBJ)
	$(CC) $(CFLAGS) -o $(EXEC) $(OBJ) -lpthread

# Compile each source file into an object file
%.o: %.c header.h
	$(CC) $(CFLAGS) -c $< -o $@

# Clean up object files and the executable
clean:
	rm -f $(OBJ) $(EXEC)

# Run the server (example usage)
run: $(EXEC)
	./$(EXEC)
