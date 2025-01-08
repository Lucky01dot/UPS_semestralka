package Pieces;

import utils.Chessboard;
import utils.Piece;

/**
 * Třída reprezentující pěšce (Pawn) v šachu.
 */
public class Pawn extends Piece {

    private final Chessboard chessboard;

    /**
     * Konstruktor pro vytvoření nového pěšce.
     * @param col Sloupec, kde je pěšec umístěn.
     * @param row Řádek, kde je pěšec umístěn.
     * @param isWhite Určuje, zda je pěšec bílý (true) nebo černý (false).
     * @param chessboard Deska, na které je pěšec umístěn.
     */
    public Pawn(int col, int row, boolean isWhite, Chessboard chessboard) {
        super(col, row, isWhite, 
          isWhite ? System.getProperty("user.dir") + "/Pieces/white-pawn.png" : 
          System.getProperty("user.dir") + "/Pieces/black-pawn.png", 
          chessboard);
        this.chessboard = chessboard;
    }

    /**
     * Kontrola, zda je pohyb pěšce platný.
     * Pěšec se pohybuje o jedno políčko dopředu, ale může se pohnout o dvě políčka při prvním pohybu.
     * Dále může sebere soupeřovu figurku na sousedních políčkách v přední řadě.
     * @param col Cíl sloupce pro pohyb.
     * @param row Cíl řádku pro pohyb.
     * @return true, pokud je pohyb platný, jinak false.
     */
    public boolean isValidMovement(int col, int row) {
        int colorIndex = isWhite ? 1 : -1; // Určení směru pohybu podle barvy pěšce

        // Push pawn 1: Pěšec se může pohnout o jedno políčko dopředu
        if(this.col == col && row == this.row - colorIndex && chessboard.getPiece(col,row) == null)
            return true;

        // Push pawn 2: Pěšec se může pohnout o dvě políčka, pokud se jedná o jeho první tah
        if(isfirstmoved && this.col == col && row == this.row - colorIndex * 2 && chessboard.getPiece(col,row) == null && chessboard.getPiece(col,row + colorIndex) == null)
            return true;

        // Capture left: Pěšec může sebrat figuru vlevo
        if(col == this.col - 1 && row == this.row - colorIndex && chessboard.getPiece(col,row ) != null)
            return true;

        // Capture right: Pěšec může sebrat figuru vpravo
        if(col == this.col + 1 && row == this.row - colorIndex && chessboard.getPiece(col,row ) != null)
            return true;

        return false;
    }
}
