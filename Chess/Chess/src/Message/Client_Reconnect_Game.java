package Message;

import Constants.Constants;
import Windows.Client;
import enums.Messages;

public class Client_Reconnect_Game extends Message {
    int GameID;
    Client client;
    public Client_Reconnect_Game(int GameID, Client client) {
        this.name = Messages.CLIENT_RECONNECT_GAME;
        this.GameID = GameID;
        this.client = client;
    }

    @Override
    public String toString() {
        return this.name.toString() + Constants.valueSeparator + this.client.getName() + Constants.valueSeparator + "GAME_ID: " + this.GameID + Constants.valueSeparator +"\n";
    }
}
