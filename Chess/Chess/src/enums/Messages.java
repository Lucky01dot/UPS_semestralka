package enums;

public enum Messages {

    CLIENT_LOGIN("login"),

    CLIENT_IS_READY("client_ready"),
    CLIENT_IS_UNREADY("client_unready"),
    CLIENT_LOGOUT("logout"),
    CLIENT_LEAVE_GAME("leave_game"),
    CLIENT_RECONNECT_GAME("reconnect_game"),
    CLIENT_MAKE_MOVE("make_move"),


    SERVER_IS_CONNECTED("is_connected"),
    SERVER_LOGIN_OK("login_ok"),
    SERVER_LOGIN_FALSE("login_false"),
    SERVER_START_GAME("start_game"),
    SERVER_KEEP_ALIVE("keep_alive");


    private final String name;

    // Constructor to set the message name
    Messages(String name) {
        this.name = name;
    }

    public String getMessageName() {
        return this.name;
    }

    @Override
    public String toString() {
        return this.name;
    }

    // Static method to get the enum by name
    public static Messages getMessageByName(String name) {
        try {
            return Messages.valueOf(name.toUpperCase()); // Using valueOf for better performance
        } catch (IllegalArgumentException e) {
            return null; // Return null if no match is found
        }
    }
}
