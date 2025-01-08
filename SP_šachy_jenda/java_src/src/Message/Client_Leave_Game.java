package Message;

import Constants.Constants;
import Connection.Client;
import enums.Messages;

/**
 * Třída pro zprávu, která informuje o odchodu klienta ze hry.
 */
public class Client_Leave_Game extends Message {

    // Objekt klienta, který opustil hru
    Client client;

    /**
     * Konstruktor třídy Client_Leave_Game.
     *
     * @param client Klient, který opustil hru.
     */
    public Client_Leave_Game(Client client) {
        name = Messages.CLIENT_LEAVE_GAME; // Nastavení názvu zprávy na CLIENT_LEAVE_GAME
        this.client = client; // Uložení objektu klienta
    }

    /**
     * Vrací zprávu jako řetězec, který bude odeslán na server.
     *
     * @return Formátovaný řetězec zprávy.
     */
    @Override
    public String toString() {
        // Vytvoří zprávu ve formátu: "CLIENT_LEAVE_GAME;clientName;\n"
        return this.name.toString() + Constants.valueSeparator + this.client.getName() + Constants.valueSeparator + "\n";
    }
}
