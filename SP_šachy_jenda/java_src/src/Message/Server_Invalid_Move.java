package Message;

import Connection.Game;
import Constants.Constants;
import enums.Messages;
import utils.Move;

public class Server_Invalid_Move extends Message {
    private final Move move;
    private final Game game;

    public Server_Invalid_Move(Move move, Game game) {
        if (move == null) {
            throw new IllegalArgumentException("Move cannot be null");
        }

        this.move = move;
        this.game = game;
        this.name = Messages.SERVER_INVALID_MOVE;
    }

    @Override
    public String toString() {
        return this.name.toString() + Constants.valueSeparator + this.game.getGameID() +
                Constants.valueSeparator + this.move.newCol +
                Constants.valueSeparator + this.move.newRow +
                Constants.valueSeparator + this.move.oldCol +
                Constants.valueSeparator + this.move.oldRow +
                Constants.valueSeparator + (this.move.piece != null ? this.move.piece : "none") +
                Constants.valueSeparator + (this.move.capture != null ? this.move.capture : "none") + Constants.valueSeparator;
    }
}
