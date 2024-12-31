package Pieces;

import utils.Chessboard;
import utils.Piece;

public class Queen extends Piece {

    private final Chessboard chessboard;

    public Queen(int col, int row, boolean isWhite,Chessboard chessboard) {
        super(col, row, isWhite,  isWhite ? "Pieces/white-queen.png" : "Pieces/black-queen.png",chessboard);
        this.chessboard = chessboard;


    }
    @Override
    public boolean isValidMovement(int col, int row) {
        // Věž se pohybuje pouze po sloupcích (vertikálně) nebo po řádcích (horizontálně)
        return this.col == col || this.row == row || Math.abs(this.col - col) == Math.abs(this.row - row);
    }

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
            int colDirection = (col > this.col) ? 1 : -1; // Moving left or right
            int rowDirection = (row > this.row) ? 1 : -1; // Moving up or down

            int distance = Math.abs(this.col - col); // Since movement is diagonal, row distance is the same

            for (int i = 1; i < distance; i++) {
                int currentCol = this.col + i * colDirection;
                int currentRow = this.row + i * rowDirection;

                if (chessboard.getPiece(currentCol, currentRow) != null) {
                    return true; // Collision detected
                }
            }

        }
        return false; // Žádná kolize, pohyb je možný
    }
}
