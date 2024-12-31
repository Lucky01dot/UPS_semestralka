package Pieces;

import utils.Chessboard;
import utils.Piece;

public class Rook extends Piece {
    Chessboard chessboard;

    public Rook(int col, int row, boolean isWhite, Chessboard chessboard) {
        super(col, row, isWhite, isWhite ? "Pieces/white-rook.png" : "Pieces/black-rook.png", chessboard);
        this.chessboard = chessboard;
    }

    @Override
    public boolean isValidMovement(int col, int row) {
        // Věž se pohybuje pouze po sloupcích (vertikálně) nebo po řádcích (horizontálně)
        return this.col == col || this.row == row;
    }

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
        return false; // Žádná kolize, pohyb je možný
    }
}
