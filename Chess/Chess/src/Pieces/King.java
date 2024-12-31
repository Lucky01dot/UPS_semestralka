package Pieces;

import utils.Chessboard;
import utils.Move;
import utils.Piece;

public class King extends Piece {
    private Chessboard chessboard;
    public King(int col, int row, boolean isWhite,Chessboard chessboard) {
        super(col, row, isWhite,  isWhite ? "Pieces/white-king.png" : "Pieces/black-king.png",chessboard);
        this.chessboard = chessboard;

    }

    public boolean isValidMovement(int col, int row) {
        return Math.abs((col - this.col) * (row - this.row)) == 1 || Math.abs(col - this.col) + Math.abs(row - this.row) == 1 || canCastle(col,row);
    }
    public boolean moveCollidesWithPiece(int col, int row) {return false;}

    private boolean canCastle(int col, int row) {

        if(this.row == row){

            if(col == 6){
                Piece rook = chessboard.getPiece(7,row);
                if(rook != null && rook.isfirstmoved && isfirstmoved){
                    return chessboard.getPiece(5,row) == null &&
                            chessboard.getPiece(6,row) == null &&
                            !chessboard.checkScanner.isKingChecked(new Move(chessboard,this,5,row)) &&
                            !chessboard.checkScanner.isKingChecked(new Move(chessboard, this, 6, row));
                }
            }
            else if(col == 2){
                Piece rook = chessboard.getPiece(0,row);
                if(rook != null && rook.isfirstmoved && isfirstmoved){
                    return chessboard.getPiece(3,row) == null &&
                            chessboard.getPiece(2,row) == null &&
                            chessboard.getPiece(1,row) == null &&
                            !chessboard.checkScanner.isKingChecked(new Move(chessboard,this,3,row)) &&
                            !chessboard.checkScanner.isKingChecked(new Move(chessboard, this, 2, row));
                }
            }
        }


        return false;
    }
}
