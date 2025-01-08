# Directories
C_SRC_DIR = c_src
C_BIN_DIR = c_bin
JAVA_SRC_DIR = java_src/src
JAVA_BIN_DIR = java_bin
JAR_FILE = chess_app.jar

# Compiler and flags
CC = gcc
CFLAGS = -Wall -Wextra -O2
JAVAC = javac
JAR = jar

# C source files and object files
C_SRCS = $(wildcard $(C_SRC_DIR)/*.c)
C_OBJS = $(patsubst $(C_SRC_DIR)/%.c,$(C_BIN_DIR)/%.o,$(C_SRCS))

# Java source files and corresponding class files
JAVA_SRCS = $(shell find $(JAVA_SRC_DIR) -name "*.java")
JAVA_CLASSES = $(patsubst $(JAVA_SRC_DIR)/%.java,$(JAVA_BIN_DIR)/%.class,$(JAVA_SRCS))

# Targets
all: server.exe $(JAR_FILE)

server.exe: $(C_OBJS)
	$(CC) $(CFLAGS) $(C_OBJS) -o server.exe

# Create JAR file
$(JAR_FILE): java_bin
	@echo "Creating JAR file $(JAR_FILE)..."
	echo "Main-Class: Chess" > Manifest.txt
	$(JAR) cfm $(JAR_FILE) Manifest.txt -C $(JAVA_BIN_DIR) .
	rm Manifest.txt

# Compiling Java files
java_bin: $(JAVA_CLASSES)

$(JAVA_BIN_DIR)/%.class: $(JAVA_SRC_DIR)/%.java
	mkdir -p $(JAVA_BIN_DIR)
	@echo "Compiling $<..."
	$(JAVAC) -d $(JAVA_BIN_DIR) -sourcepath $(JAVA_SRC_DIR) $<

# C object files
$(C_BIN_DIR)/%.o: $(C_SRC_DIR)/%.c
	mkdir -p $(C_BIN_DIR)
	$(CC) $(CFLAGS) -c $< -o $@

# Clean target
clean:
	rm -rf $(C_BIN_DIR)/* $(JAVA_BIN_DIR)/* server.exe $(JAR_FILE)
