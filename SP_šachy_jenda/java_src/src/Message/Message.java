package Message;

import Constants.Constants;
import enums.*;

/**
 * Abstraktní třída pro zprávy, které mohou být odesílány mezi klientem a serverem.
 * Každá konkrétní zpráva bude dědit tuto třídu a přizpůsobí si její chování.
 */
public abstract class Message {

    public Messages name = null; // Název zprávy (typ zprávy, např. CLIENT_LOGIN)
    private String message = ""; // Obsah zprávy, který bude odeslán

    /**
     * Převod zprávy na řetězec pro její odeslání.
     * @return Formátovaná zpráva jako řetězec
     */
    @Override
    public String toString() {
        String separator = Constants.valueSeparator; // Oddělovač hodnot, definovaný ve Constants
        String nameString = (name != null) ? name.toString() : "UNDEFINED"; // Pokud je název zprávy definován, použije ho, jinak "UNDEFINED"
        return nameString + separator + message; // Formátovaná zpráva
    }

    // Gettery a settery pro název zprávy a její obsah

    public Messages getName() {
        return name; // Vrátí název zprávy
    }

    public void setName(Messages name) {
        this.name = name; // Nastaví název zprávy
    }

    public String getMessage() {
        return message; // Vrátí obsah zprávy
    }

    public void setMessage(String message) {
        this.message = message; // Nastaví obsah zprávy
    }
}
