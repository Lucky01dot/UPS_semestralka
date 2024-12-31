package Message;

import Constants.Constants;
import enums.*;

public abstract class Message {
    public Messages name = null;
    private String message = "";

    @Override
    public String toString() {
        String separator = Constants.valueSeparator;
        String nameString = (name != null) ? name.toString() : "UNDEFINED";
        return nameString + separator + message;
    }


    public Messages getName() {
        return name;
    }

    public void setName(Messages name) {
        this.name = name;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }


}
