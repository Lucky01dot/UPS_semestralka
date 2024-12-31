package Message;

import Constants.Constants;
import enums.Messages;

public class Server_Keep_Alive extends Message{

    public Server_Keep_Alive(){
        this.name = Messages.SERVER_KEEP_ALIVE;
    }

    @Override
    public String toString() {
        return this.name.toString() + Constants.valueSeparator;
    }
}
