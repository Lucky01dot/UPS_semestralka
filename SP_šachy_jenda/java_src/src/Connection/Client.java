package Connection;

// Třída `Client` reprezentuje klienta ve hře. Obsahuje informace o jménu, barvě (bílý/černý),
// připravenosti hráče a aktuální hře, do které je klient zapojen.
public class Client {

    private String name;           // Jméno klienta
    private boolean isWhite;       // Určuje, zda je klient přiřazen jako bílý hráč
    private boolean ready;         // Stav připravenosti klienta (true = připraven, false = nepřipraven)
    Game game;                     // Instance aktuální hry, do které je klient zapojen

    // --- Konstruktor ---
    // Inicializuje klienta se zadaným jménem, barvou, stavem připravenosti a hrou.
    public Client(String name, boolean isWhite, boolean ready, Game game) {
        this.name = name;
        this.isWhite = isWhite;
        this.ready = ready;
        this.game = game;
    }

    // --- Gettery a Settery ---
    // Získává jméno klienta
    public String getName() {
        return name;
    }

    // Nastavuje jméno klienta
    public void setName(String name) {
        this.name = name;
    }

    // Získává aktuální hru, do které je klient zapojen
    public Game getGame() {
        return game;
    }

    // Nastavuje aktuální hru, do které je klient zapojen
    public void setGame(Game game) {
        this.game = game;
    }

    // Získává stav připravenosti klienta
    public boolean isReady() {
        return ready;
    }

    // Nastavuje stav připravenosti klienta
    public void setReady(boolean ready) {
        this.ready = ready;
    }

    // Přepíná stav připravenosti klienta (true <-> false)
    // a vrací nový stav.
    public boolean toggleReadyStatus() {
        this.ready = !this.ready;
        return this.ready;
    }

    // Nastavuje barvu klienta (bílý/černý)
    public void setWhite(boolean white) {
        isWhite = white;
    }

    // Získává, zda je klient přiřazen jako bílý hráč
    public boolean isWhite() {
        return isWhite;
    }
}
