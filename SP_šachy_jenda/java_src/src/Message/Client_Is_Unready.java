package Message;

import enums.Messages;

/**
 * Třída pro zprávu, která označuje, že klient není připraven.
 */
public class Client_Is_Unready extends Message {

    // Indikátor, zda je klient připraven (true/false)
    boolean is_ready;

    /**
     * Konstruktor třídy Client_Is_Unready.
     *
     * @param is_ready Hodnota, která určuje, zda klient není připraven.
     */
    public Client_Is_Unready(boolean is_ready) {
        this.is_ready = is_ready; // Nastavení hodnoty připravenosti klienta
        this.name = Messages.CLIENT_IS_UNREADY; // Nastavení názvu zprávy na CLIENT_IS_UNREADY
    }
}
