package Message;

import enums.Messages;

/**
 * Třída pro zprávu, která informuje o odhlášení klienta.
 */
public class Client_Logout extends Message {

    /**
     * Konstruktor třídy Client_Logout.
     * Nastavuje název zprávy na CLIENT_LOGOUT.
     */
    public Client_Logout() {
        this.name = Messages.CLIENT_LOGOUT; // Nastavení názvu zprávy na CLIENT_LOGOUT
    }
}
