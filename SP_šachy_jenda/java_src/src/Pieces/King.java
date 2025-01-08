package Pieces;

import utils.Chessboard;
import utils.Move;
import utils.Piece;

/**
 * Třída reprezentující figurku krále (King).
 */
public class King extends Piece {
    private Chessboard chessboard;

    /**
     * Konstruktor pro vytvoření nové figurky krále.
     * @param col Sloupec, kde je král umístěn.
     * @param row Řádek, kde je král umístěn.
     * @param isWhite Určuje, zda je král bílý (true) nebo černý (false).
     * @param chessboard Deska, na které je král umístěn.
     */
    public King(int col, int row, boolean isWhite, Chessboard chessboard) {
        super(col, row, isWhite, 
          isWhite ? System.getProperty("user.dir") + "/Pieces/white-king.png" : 
          System.getProperty("user.dir") + "/Pieces/black-king.png", 
          chessboard);
        this.chessboard = chessboard; // Uložení odkazu na šachovnici
    }

    /**
     * Kontrola, zda je pohyb krále platný.
     * Král se pohybuje o jedno políčko v jakémkoli směru (vodorovně, svisle nebo diagonálně),
     * nebo provádí rošádu.
     * @param col Cíl sloupce pro pohyb.
     * @param row Cíl řádku pro pohyb.
     * @return true, pokud je pohyb platný, jinak false.
     */
    public boolean isValidMovement(int col, int row) {
        // Král se může pohybovat o jedno políčko (vodorovně, svisle nebo diagonálně),
        // nebo provádí rošádu (specialní pohyb)
        return Math.abs((col - this.col) * (row - this.row)) == 1 || Math.abs(col - this.col) + Math.abs(row - this.row) == 1 || canCastle(col, row);
    }

    /**
     * Zkontroluje, zda pohyb krále nevede do kolize s jinou figurkou.
     * @param col Cíl sloupce pro pohyb.
     * @param row Cíl řádku pro pohyb.
     * @return false, protože král nemá žádné speciální kolize, pohyb je vždy platný.
     */
    public boolean moveCollidesWithPiece(int col, int row) {
        return false; // Pohyb krále není blokován kolizí s jinými figurkami.
    }

    /**
     * Zkontroluje, zda je možné provést rošádu.
     * Rošáda je speciální pohyb, kde král a věž vymění své pozice, pokud nejsou mezi nimi žádné jiné figurky.
     * @param col Cíl sloupce pro rošádu.
     * @param row Cíl řádku pro rošádu.
     * @return true, pokud je možné provést rošádu, jinak false.
     */
    private boolean canCastle(int col, int row) {
        // Kontrola, zda se král pohybuje na správnou pozici pro rošádu
        if (this.row == row) {
            // Rošáda na pravou stranu (sloupec 6)
            if (col == 6) {
                Piece rook = chessboard.getPiece(7, row); // Věž na pozici (7, řádek)
                // Kontrola, zda byla věž a král už v minulosti pohnutí
                if (rook != null && rook.isfirstmoved && isfirstmoved) {
                    // Zkontroluj, zda jsou mezi králem a věží volná políčka a zda se král nedostane do šachu
                    return chessboard.getPiece(5, row) == null &&
                            chessboard.getPiece(6, row) == null &&
                            !chessboard.checkScanner.isKingChecked(new Move(chessboard, this, 5, row)) &&
                            !chessboard.checkScanner.isKingChecked(new Move(chessboard, this, 6, row));
                }
            }
            // Rošáda na levou stranu (sloupec 2)
            else if (col == 2) {
                Piece rook = chessboard.getPiece(0, row); // Věž na pozici (0, řádek)
                if (rook != null && rook.isfirstmoved && isfirstmoved) {
                    // Zkontroluj, zda jsou mezi králem a věží volná políčka a zda se král nedostane do šachu
                    return chessboard.getPiece(3, row) == null &&
                            chessboard.getPiece(2, row) == null &&
                            chessboard.getPiece(1, row) == null &&
                            !chessboard.checkScanner.isKingChecked(new Move(chessboard, this, 3, row)) &&
                            !chessboard.checkScanner.isKingChecked(new Move(chessboard, this, 2, row));
                }
            }
        }

        return false; // Rošáda není možná, pokud nejsou splněny podmínky
    }
}
