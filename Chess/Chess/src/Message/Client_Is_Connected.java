package Message;

import enums.*;

public class Client_Is_Connected extends Message{

    public Client_Is_Connected() {
        this.name = Messages.SERVER_IS_CONNECTED;
    }
}
