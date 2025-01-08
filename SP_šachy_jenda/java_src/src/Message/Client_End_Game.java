package Message;

import Constants.Constants;
import enums.Messages;

/**
 * Třída pro zprávu o ukončení hry od klienta.
 */
public class Client_End_Game extends Message {

    // Identifikátor hry
    int GameID;

    /**
     * Konstruktor třídy Client_End_Game.
     *
     * @param GameID Identifikátor hry, kterou chce klient ukončit.
     */
    public Client_End_Game(int GameID) {
        this.GameID = GameID;
        this.name = Messages.CLIENT_GAME_END; // Nastavení názvu zprávy na CLIENT_GAME_END
    }

    /**
     * Metoda pro převod objektu na textový řetězec.
     *
     * @return Textová reprezentace zprávy ve formátu:
     *         "CLIENT_GAME_END[hodnota GameID]"
     */
    @Override
    public String toString() {
        // Vytvoření řetězce s názvem zprávy a identifikátorem hry odděleným konstantou valueSeparator
        return name.toString() + Constants.valueSeparator + GameID + Constants.valueSeparator;
    }
}
