package Windows;

import utils.Chessboard;

public class Game {
    int gameID;
    Client white;
    Client black;
    Chessboard chessboard;
    public Game(int gameID, Client white, Client black, Chessboard chessboard){
        this.gameID = gameID;
        this.white = white;
        this.black = black;
        this.chessboard = chessboard;

    }




    public int getGameID() {
        return gameID;
    }

    public Chessboard getChessboard() {
        return chessboard;
    }

    public Client getBlack() {
        return black;
    }
    public Client getWhite() {
        return white;
    }

    public void setWhite(Client white) {
        this.white = white;
    }
    public void setBlack(Client black) {
        this.black = black;
    }
}
