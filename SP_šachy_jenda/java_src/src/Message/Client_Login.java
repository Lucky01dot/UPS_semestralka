package Message;

import Constants.Constants;
import enums.Messages;

/**
 * Třída pro zprávu, která informuje o přihlášení klienta do systému.
 */
public class Client_Login extends Message {

    // Uživatelovo jméno pro přihlášení
    private String userName;

    /**
     * Konstruktor třídy Client_Login.
     *
     * @param userName Jméno uživatele, který se přihlašuje.
     */
    public Client_Login(String userName) {
        this.name = Messages.CLIENT_LOGIN; // Nastavení názvu zprávy na CLIENT_LOGIN
        this.userName = userName; // Uložení jména uživatele
    }

    /**
     * Vrací zprávu jako řetězec, který bude odeslán na server.
     *
     * @return Formátovaný řetězec zprávy.
     */
    @Override
    public String toString() {
        // Vytvoří zprávu ve formátu: "CLIENT_LOGIN;userName;\n"
        return this.name.toString() + Constants.valueSeparator + this.userName + Constants.valueSeparator + "\n";
    }
}
