package Message;

import Constants.Constants;
import enums.Messages;

/**
 * Třída reprezentující zprávu "Keep Alive", která je odesílána serverem,
 * aby udržela připojení aktivní mezi klientem a serverem.
 */
public class Server_Keep_Alive extends Message {

    // Konstruktor, který nastaví typ zprávy na SERVER_KEEP_ALIVE
    public Server_Keep_Alive(){
        this.name = Messages.SERVER_KEEP_ALIVE; // Nastavení názvu zprávy na SERVER_KEEP_ALIVE
    }

    /**
     * Převod zprávy na řetězec pro odeslání.
     * @return Formátovaná zpráva jako řetězec
     */
    @Override
    public String toString() {
        return this.name.toString() + Constants.valueSeparator; // Vytvoření řetězce se jménem zprávy a oddělovačem
    }
}
