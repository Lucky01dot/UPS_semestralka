package Message;

import enums.Messages;

public class Client_Is_Ready extends Message {
    boolean is_ready;
    public Client_Is_Ready(boolean is_ready) {
        this.is_ready = is_ready;
        this.name = Messages.CLIENT_IS_READY;
    }

}
