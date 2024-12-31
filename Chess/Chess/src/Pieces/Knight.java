package Pieces;

import utils.Chessboard;
import utils.Piece;

public class Knight extends Piece {
    public Knight(int col, int row, boolean isWhite,Chessboard chessboard) {
        super(col, row, isWhite,  isWhite ? "Pieces/white-knight.png" : "Pieces/black-knight.png",chessboard);


    }

    public boolean isValidMovement(int col, int row) {
        return Math.abs(col - this.col) * Math.abs(row - this.row) == 2;
    }
}
