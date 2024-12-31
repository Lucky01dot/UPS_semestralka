package Message;

import enums.Messages;

public class Client_Is_Unready extends Message {
    boolean is_ready;
    public Client_Is_Unready(boolean is_ready){
        this.is_ready = is_ready;
        this.name = Messages.CLIENT_IS_UNREADY;

    }
}
