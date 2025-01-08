package enums;

// Výčtová třída `Messages` definuje různé zprávy používané při komunikaci mezi klientem a serverem.
// Každá hodnota výčtu odpovídá určitému typu zprávy a je reprezentována řetězcem.
public enum Messages {

    // --- Zprávy od klienta ---
    CLIENT_LOGIN("login"),                // Zpráva pro přihlášení klienta
    CLIENT_IS_READY("client_ready"),      // Zpráva, že klient je připraven ke hře
    CLIENT_IS_UNREADY("client_unready"),  // Zpráva, že klient již není připraven ke hře
    CLIENT_LOGOUT("logout"),              // Zpráva pro odhlášení klienta
    CLIENT_LEAVE_GAME("leave_game"),      // Zpráva pro opuštění hry klientem
    CLIENT_RECONNECT_GAME("reconnect_game"), // Zpráva pro znovupřipojení klienta do hry
    CLIENT_MAKE_MOVE("make_move"),        // Zpráva informující o tahu klienta
    CLIENT_GAME_END("game_end"),          // Zpráva oznamující konec hry z pohledu klienta

    // --- Zprávy od serveru ---
    SERVER_IS_CONNECTED("is_connected"), // Zpráva ověřující, zda je klient připojen k serveru
    SERVER_STOP_GAME("stop_game"),       // Zpráva oznamující ukončení hry serverem
    SERVER_KEEP_ALIVE("keep_alive");     // Zpráva pro udržení aktivního spojení mezi klientem a serverem

    // Privátní proměnná pro uchování textové reprezentace zprávy
    private final String name;

    // Konstruktor nastavující hodnotu proměnné `name` pro každou zprávu
    Messages(String name) {
        this.name = name;
    }

    // Metoda pro získání názvu zprávy
    public String getMessageName() {
        return this.name;
    }

    // Přepis metody `toString` pro vrácení názvu zprávy jako řetězce
    @Override
    public String toString() {
        return this.name;
    }
}
