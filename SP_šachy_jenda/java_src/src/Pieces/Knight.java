package Pieces;

import utils.Chessboard;
import utils.Piece;

/**
 * Třída reprezentující figurku jezdce (Knight).
 */
public class Knight extends Piece {

    /**
     * Konstruktor pro vytvoření nové figurky jezdce.
     * @param col Sloupec, kde je jezdec umístěn.
     * @param row Řádek, kde je jezdec umístěn.
     * @param isWhite Určuje, zda je jezdec bílý (true) nebo černý (false).
     * @param chessboard Deska, na které je jezdec umístěn.
     */
    public Knight(int col, int row, boolean isWhite, Chessboard chessboard) {
        super(col, row, isWhite, 
          isWhite ? System.getProperty("user.dir") + "/Pieces/white-knight.png" : 
          System.getProperty("user.dir") + "/Pieces/black-knight.png", 
          chessboard);
    }

    /**
     * Kontrola, zda je pohyb jezdce platný.
     * Jezdec se pohybuje ve tvaru "L", tj. o dvě políčka v jednom směru (vodorovně nebo svisle) a o jedno políčko v druhém směru.
     * @param col Cíl sloupce pro pohyb.
     * @param row Cíl řádku pro pohyb.
     * @return true, pokud je pohyb platný, jinak false.
     */
    public boolean isValidMovement(int col, int row) {
        // Jezdec se pohybuje o dvě políčka v jednom směru a o jedno políčko v druhém směru
        return Math.abs(col - this.col) * Math.abs(row - this.row) == 2;
    }
}
