package Pieces;

import utils.Chessboard;
import utils.Piece;

public class Pawn extends Piece {
    private final Chessboard chessboard;

    public Pawn(int col, int row, boolean isWhite,Chessboard chessboard) {
        super(col, row, isWhite,  isWhite ? "Pieces/white-pawn.png" : "Pieces/black-pawn.png",chessboard);
        this.chessboard = chessboard;

    }
    public boolean isValidMovement(int col, int row) {
        int colorIndex = isWhite ? 1 : -1;

        //push pawn 1
        if(this.col == col && row == this.row - colorIndex && chessboard.getPiece(col,row) == null)
            return true;

        // push pawn 2
        if(isfirstmoved && this.col == col && row == this.row - colorIndex * 2 && chessboard.getPiece(col,row) == null && chessboard.getPiece(col,row + colorIndex) == null)
            return true;


        //capture left
        if(col == this.col - 1 && row == this.row - colorIndex && chessboard.getPiece(col,row ) != null)
            return true;

        //capture right
        if(col == this.col + 1 && row == this.row - colorIndex && chessboard.getPiece(col,row ) != null)
            return true;




        return false;
    }
}
