package Pieces;

import utils.Chessboard;
import utils.Piece;

/**
 * Třída reprezentující královnu (Queen) v šachu.
 */
public class Queen extends Piece {

    private final Chessboard chessboard;

    /**
     * Konstruktor pro vytvoření nové královny.
     * @param col Sloupec, kde je královna umístěna.
     * @param row Řádek, kde je královna umístěna.
     * @param isWhite Určuje, zda je královna bílá (true) nebo černá (false).
     * @param chessboard Deska, na které je královna umístěna.
     */
    public Queen(int col, int row, boolean isWhite, Chessboard chessboard) {
        super(col, row, isWhite, 
          isWhite ? System.getProperty("user.dir") + "/Pieces/white-queen.png" : 
          System.getProperty("user.dir") + "/Pieces/black-queen.png", 
          chessboard);
        this.chessboard = chessboard;
    }

    /**
     * Kontrola, zda je pohyb královny platný.
     * Královna se může pohybovat horizontálně, vertikálně nebo diagonálně.
     * @param col Cíl sloupce pro pohyb.
     * @param row Cíl řádku pro pohyb.
     * @return true, pokud je pohyb platný, jinak false.
     */
    @Override
    public boolean isValidMovement(int col, int row) {
        // Královna se může pohybovat vertikálně, horizontálně nebo diagonálně
        return this.col == col || this.row == row || Math.abs(this.col - col) == Math.abs(this.row - row);
    }

    /**
     * Kontrola, zda pohyb královny nekoliduje s jinou figurou.
     * Královna se může pohybovat po řádcích, sloupcích nebo diagonálách.
     * @param col Cíl sloupce pro pohyb.
     * @param row Cíl řádku pro pohyb.
     * @return true, pokud pohyb koliduje s nějakou figurou, jinak false.
     */
    @Override
    public boolean moveCollidesWithPiece(int col, int row) {
        if(this.col == col || this.row == row) {
            // Kontrola pohybu doprava (zleva doprava)
            if (this.col < col) {
                for (int c = this.col + 1; c < col; c++) {
                    if (chessboard.getPiece(c, this.row) != null) {
                        return true; // Pokud existuje překážka mezi aktuální a cílovou pozicí
                    }
                }
            }
            // Kontrola pohybu doleva (zprava doleva)
            if (this.col > col) {
                for (int c = this.col - 1; c > col; c--) {
                    if (chessboard.getPiece(c, this.row) != null) {
                        return true;
                    }
                }
            }
            // Kontrola pohybu nahoru (zespoda nahoru)
            if (this.row > row) {
                for (int r = this.row - 1; r > row; r--) {
                    if (chessboard.getPiece(this.col, r) != null) {
                        return true;
                    }
                }
            }
            // Kontrola pohybu dolů (zhora dolů)
            if (this.row < row) {
                for (int r = this.row + 1; r < row; r++) {
                    if (chessboard.getPiece(this.col, r) != null) {
                        return true;
                    }
                }
            }
        }
        else {
            // Pokud se pohybuje diagonálně
            int colDirection = (col > this.col) ? 1 : -1; // Směr pohybu ve sloupci
            int rowDirection = (row > this.row) ? 1 : -1; // Směr pohybu v řádku

            int distance = Math.abs(this.col - col); // U vzdálenosti mezi sloupci (diagonální pohyb je stejný pro sloupce i řádky)

            // Kontrola kolize při pohybu diagonálně
            for (int i = 1; i < distance; i++) {
                int currentCol = this.col + i * colDirection;
                int currentRow = this.row + i * rowDirection;

                if (chessboard.getPiece(currentCol, currentRow) != null) {
                    return true; // Pokud je mezi aktuální a cílovou pozicí překážka
                }
            }
        }
        return false; // Žádná kolize, pohyb je možný
    }
}
