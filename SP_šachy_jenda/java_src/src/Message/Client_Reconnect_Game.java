package Message;

import Constants.Constants;
import enums.Messages;

/**
 * Třída pro zprávu, která informuje server o pokusu klienta připojit se zpět do hry.
 */
public class Client_Reconnect_Game extends Message {

    private int GameID; // ID hry, ke které se klient pokouší připojit

    /**
     * Konstruktor pro vytvoření zprávy o pokusu o opětovné připojení do hry.
     * @param GameID ID hry, ke které se klient připojuje
     */
    public Client_Reconnect_Game(int GameID) {
        this.name = Messages.CLIENT_RECONNECT_GAME; // Nastavení názvu zprávy na CLIENT_RECONNECT_GAME
        this.GameID = GameID; // Nastavení ID hry
    }

    /**
     * Převod objektu na formát zprávy pro server.
     * @return Formátovaná zpráva pro server
     */
    @Override
    public String toString() {
        // Vytvoření zprávy ve formátu: název zprávy;ID hry;
        return this.name.toString() + Constants.valueSeparator + this.GameID + Constants.valueSeparator;
    }
}
