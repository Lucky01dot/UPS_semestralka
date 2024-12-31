package Pieces;

import utils.Chessboard;
import utils.Piece;

public class Bishop extends Piece {

    private final Chessboard chessboard;

    public Bishop(int col, int row, boolean isWhite, Chessboard chessboard) {
        // Pass the image path for the white or black bishop
        super(col, row, isWhite, isWhite ? "Pieces/white-bishop.png" : "Pieces/black-bishop.png", chessboard);
        this.chessboard = chessboard;
    }

    @Override
    public boolean isValidMovement(int col, int row) {
        // Bishop moves diagonally, so the difference in rows and columns must be equal
        return Math.abs(this.col - col) == Math.abs(this.row - row);
    }

    @Override
    public boolean moveCollidesWithPiece(int col, int row) {
        // up left
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

        return false; // Žádná kolize, pohyb je možný
    }
}
