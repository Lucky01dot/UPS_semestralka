package Message;

import enums.Messages;

/**
 * Třída pro zprávu, která označuje, zda je klient připraven.
 */
public class Client_Is_Ready extends Message {

    // Indikátor, zda je klient připraven (true/false)
    boolean is_ready;

    /**
     * Konstruktor třídy Client_Is_Ready.
     *
     * @param is_ready Hodnota, která určuje, zda je klient připraven.
     */
    public Client_Is_Ready(boolean is_ready) {
        this.is_ready = is_ready; // Nastavení hodnoty připravenosti klienta
        this.name = Messages.CLIENT_IS_READY; // Nastavení názvu zprávy na CLIENT_IS_READY
    }
}
