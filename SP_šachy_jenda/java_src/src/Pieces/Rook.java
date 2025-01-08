package Pieces;

import utils.Chessboard;
import utils.Piece;

/**
 * Třída reprezentující věž (Rook) v šachu.
 */
public class Rook extends Piece {
    private final Chessboard chessboard;

    /**
     * Konstruktor pro vytvoření nové věže.
     * @param col Sloupec, kde je věž umístěna.
     * @param row Řádek, kde je věž umístěna.
     * @param isWhite Určuje, zda je věž bílá (true) nebo černá (false).
     * @param chessboard Deska, na které je věž umístěna.
     */
    public Rook(int col, int row, boolean isWhite, Chessboard chessboard) {
        super(col, row, isWhite, 
          isWhite ? System.getProperty("user.dir") + "/Pieces/white-rook.png" : 
          System.getProperty("user.dir") + "/Pieces/black-rook.png", 
          chessboard);
        this.chessboard = chessboard;
    }

    /**
     * Kontrola, zda je pohyb věže platný.
     * Věž se může pohybovat pouze vertikálně nebo horizontálně (po řádcích nebo sloupcích).
     * @param col Cíl sloupce pro pohyb.
     * @param row Cíl řádku pro pohyb.
     * @return true, pokud je pohyb platný, jinak false.
     */
    @Override
    public boolean isValidMovement(int col, int row) {
        // Věž se pohybuje pouze po sloupcích nebo po řádcích
        return this.col == col || this.row == row;
    }

    /**
     * Kontrola, zda pohyb věže nekoliduje s jinou figurou.
     * Pokud věž pohybuje po řádku nebo sloupci, je potřeba zkontrolovat, zda mezi původní a cílovou pozicí není nějaká figura.
     * @param col Cíl sloupce pro pohyb.
     * @param row Cíl řádku pro pohyb.
     * @return true, pokud pohyb koliduje s nějakou figurou, jinak false.
     */
    @Override
    public boolean moveCollidesWithPiece(int col, int row) {
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
                    return true; // Pokud existuje překážka mezi aktuální a cílovou pozicí
                }
            }
        }
        // Kontrola pohybu nahoru (zespoda nahoru)
        if (this.row > row) {
            for (int r = this.row - 1; r > row; r--) {
                if (chessboard.getPiece(this.col, r) != null) {
                    return true; // Pokud existuje překážka mezi aktuální a cílovou pozicí
                }
            }
        }
        // Kontrola pohybu dolů (zhora dolů)
        if (this.row < row) {
            for (int r = this.row + 1; r < row; r++) {
                if (chessboard.getPiece(this.col, r) != null) {
                    return true; // Pokud existuje překážka mezi aktuální a cílovou pozicí
                }
            }
        }
        return false; // Žádná kolize, pohyb je možný
    }
}
