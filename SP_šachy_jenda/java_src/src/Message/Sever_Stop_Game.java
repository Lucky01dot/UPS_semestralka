package Message;

import Constants.Constants;
import enums.Messages;

/**
 * Třída reprezentující zprávu o zastavení hry ze strany serveru.
 */
public class Sever_Stop_Game extends Message {

    // ID hry, která má být zastavena
    int GameID;

    /**
     * Konstruktor třídy, který inicializuje ID hry a nastaví název zprávy.
     * @param GameID ID hry, která má být zastavena
     */
    public Sever_Stop_Game(int GameID) {
        this.GameID = GameID; // Nastavení ID hry
        this.name = Messages.SERVER_STOP_GAME; // Nastavení názvu zprávy na SERVER_STOP_GAME
    }

    /**
     * Převod zprávy na řetězec pro odeslání.
     * @return Formátovaná zpráva jako řetězec
     */
    @Override
    public String toString() {
        // Vytvoření řetězce obsahujícího název zprávy a ID hry, oddělené hodnotovým oddělovačem
        return this.name.toString() + Constants.valueSeparator + this.GameID + Constants.valueSeparator;
    }
}
