# Directories
C_SRC_DIR = c_src
C_BIN_DIR = c_bin
JAVA_SRC_DIR = java_src/src
JAVA_BIN_DIR = java_bin

# Compiler and flags
CC = gcc
CFLAGS = -Wall -Wextra -O2
JAVAC = javac

# C source files and object files
C_SRCS = $(wildcard $(C_SRC_DIR)/*.c)
C_OBJS = $(patsubst $(C_SRC_DIR)/%.c,$(C_BIN_DIR)/%.o,$(C_SRCS))

# Java source files
JAVA_SRCS = $(shell find $(JAVA_SRC_DIR) -name "*.java")

# Targets
all: server.exe java_bin

server.exe: $(C_OBJS)
	$(CC) $(CFLAGS) $(C_OBJS) -o server.exe

# Compiling Java files
java_bin: $(JAVA_SRCS)
	mkdir -p $(JAVA_BIN_DIR)
	@echo "Compiling Java files..."
	$(JAVAC) -d $(JAVA_BIN_DIR) $(JAVA_SRCS)

# C object files
c_bin: $(C_OBJS)

$(C_BIN_DIR)/%.o: $(C_SRC_DIR)/%.c
	mkdir -p $(C_BIN_DIR)
	$(CC) $(CFLAGS) -c $< -o $@

# Clean target
clean:
	rm -rf $(C_BIN_DIR)/* $(JAVA_BIN_DIR)/* server.exe
