package Connection;

import utils.Chessboard;

/**
 * Reprezentuje šachovou hru, obsahuje informace o hráčích a stavu hry.
 */
public class Game {
    // Unikátní identifikátor hry
    int gameID;

    // Hráč hrající za bílé figurky
    Client white;

    // Hráč hrající za černé figurky
    Client black;

    // Šachovnice reprezentující stav hry
    Chessboard chessboard;

    /**
     * Konstruktor pro inicializaci hry.
     *
     * @param gameID     Unikátní identifikátor hry.
     * @param white      Hráč hrající za bílé figurky.
     * @param black      Hráč hrající za černé figurky.
     * @param chessboard Šachovnice reprezentující stav hry.
     */
    public Game(int gameID, Client white, Client black, Chessboard chessboard) {
        this.gameID = gameID;
        this.white = white;
        this.black = black;
        this.chessboard = chessboard;
    }

    /**
     * Vrací ID hry.
     *
     * @return Unikátní identifikátor hry.
     */
    public int getGameID() {
        return gameID;
    }

    /**
     * Vrací šachovnici hry.
     *
     * @return Šachovnice reprezentující stav hry.
     */
    public Chessboard getChessboard() {
        return chessboard;
    }

    /**
     * Vrací klienta hrajícího za černé figurky.
     *
     * @return Hráč černých figurek.
     */
    public Client getBlack() {
        return black;
    }

    /**
     * Vrací klienta hrajícího za bílé figurky.
     *
     * @return Hráč bílých figurek.
     */
    public Client getWhite() {
        return white;
    }

    /**
     * Nastavuje hráče hrajícího za bílé figurky.
     *
     * @param white Hráč bílých figurek.
     */
    public void setWhite(Client white) {
        this.white = white;
    }

    /**
     * Nastavuje hráče hrajícího za černé figurky.
     *
     * @param black Hráč černých figurek.
     */
    public void setBlack(Client black) {
        this.black = black;
    }
}
