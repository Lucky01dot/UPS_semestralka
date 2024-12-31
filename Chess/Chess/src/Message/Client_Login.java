package Message;

import Constants.Constants;
import enums.Messages;

public class Client_Login extends Message {
    private String userName;

    public Client_Login(String userName) {
        this.name = Messages.CLIENT_LOGIN;
        this.userName = userName;
    }

    @Override
    public String toString() {
        return this.name.toString() + Constants.valueSeparator + this.userName + Constants.valueSeparator + "\n";
    }


}
