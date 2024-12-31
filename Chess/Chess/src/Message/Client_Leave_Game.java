package Message;

import Constants.Constants;
import Windows.Client;
import enums.Messages;

public class Client_Leave_Game extends Message {
    Client client;
    public Client_Leave_Game(Client client){
        name = Messages.CLIENT_LEAVE_GAME;
        this.client = client;
    }
    @Override
    public String toString() {
        return this.name.toString() + Constants.valueSeparator + this.client.getName() + Constants.valueSeparator + "\n";
    }
}
