package utils;

import Pieces.*;

public class CheckScanner {

    Chessboard chessboard;

    public CheckScanner(Chessboard chessboard) {
        this.chessboard = chessboard;
    }

    public boolean isKingChecked(Move move) {

        Piece king = chessboard.findKind(move.piece.isWhite);
        assert king != null;

        int kingCol = king.getCol();
        int kingRow = king.getRow();

        if(chessboard.selectedPiece != null && chessboard.selectedPiece instanceof King && chessboard.selectedPiece == move.piece) {
            kingCol = move.newCol;
            kingRow = move.newRow;
        }




        return hitbyRook(move.newCol, move.newRow,king,kingCol,kingRow, 0,1) || //up
                hitbyRook(move.newCol, move.newRow,king,kingCol,kingRow, 1,0) || //right
                hitbyRook(move.newCol, move.newRow,king,kingCol,kingRow, 0,-1) || //down
                hitbyRook(move.newCol, move.newRow,king,kingCol,kingRow, -1,0) || //left

                hitbyBishop(move.newCol, move.newRow,king,kingCol,kingRow, -1,-1) || //up left
                hitbyBishop(move.newCol, move.newRow,king,kingCol,kingRow, 1,-1) || //up right
                hitbyBishop(move.newCol, move.newRow,king,kingCol,kingRow, 1,1) ||  //down right
                hitbyBishop(move.newCol, move.newRow,king,kingCol,kingRow, -1,1) || //down left

                hitByKnight(move.newCol, move.newRow, king,kingCol,kingRow) ||
                hitByPawn(move.newCol, move.newRow, king,kingCol,kingRow) ||
                hitByKing(king,kingCol,kingRow);
    }

    private boolean hitbyRook(int col, int row, Piece king, int kingCol, int kingRow, int colVal, int rowVal) {
        for (int i = 1; i < 8; i++) {
            if (kingCol + (i * colVal) == col && kingRow + (i * rowVal) == row) {
                break;
            }
            Piece piece = chessboard.getPiece(kingCol + (i * colVal), kingRow + (i * rowVal));
            if (piece != null && piece != chessboard.selectedPiece) {
                if (!chessboard.sameTeam(piece, king) && (piece instanceof Rook || piece instanceof Queen)) {
                    return true;
                }
                break;
            }
        }
        return false;
    }
    private boolean hitbyBishop(int col, int row, Piece king,int kingCol, int kingRow, int colVal, int rowVal) {

        for(int i = 1; i< 8 ;i++){
            if(kingCol - (i * colVal)== col && kingRow - (i * rowVal)== row){
                break;
            }
            Piece piece = chessboard.getPiece(kingCol - (i * colVal), kingRow - (i * rowVal));
            if(piece != null && piece != chessboard.selectedPiece){
                if(!chessboard.sameTeam(piece,king) && (piece instanceof Bishop || piece instanceof Queen)){
                    return true;
                }
                break;

            }

        }
        return false;



    }
    private boolean hitByKnight(int col, int row, Piece king, int kingCol, int kingRow){
        return checkKnight(chessboard.getPiece(kingCol-1,kingRow-2),king,col,row) ||
            checkKnight(chessboard.getPiece(kingCol+1,kingRow-2),king,col,row) ||
            checkKnight(chessboard.getPiece(kingCol+2,kingRow-1),king,col,row) ||
            checkKnight(chessboard.getPiece(kingCol+2,kingRow+1),king,col,row) ||
            checkKnight(chessboard.getPiece(kingCol+1,kingRow+2),king,col,row) ||
            checkKnight(chessboard.getPiece(kingCol-1,kingRow+2),king,col,row) ||
            checkKnight(chessboard.getPiece(kingCol-2,kingRow+1),king,col,row) ||
            checkKnight(chessboard.getPiece(kingCol-2,kingRow-1),king,col,row);



    }
    private boolean  checkKnight(Piece p, Piece k, int col, int row){
        return p!= null && !chessboard.sameTeam(p,k) && p instanceof Knight && !(p.col == col && p.row == row);
    }

    private boolean hitByKing(Piece king,int kingCol,int kingRow){
        return checkKing(chessboard.getPiece(kingCol -1,kingRow-1),king) ||
                checkKing(chessboard.getPiece(kingCol +1,kingRow-1),king) ||
                checkKing(chessboard.getPiece(kingCol,kingRow-1),king) ||
                checkKing(chessboard.getPiece(kingCol -1,kingRow),king) ||
                checkKing(chessboard.getPiece(kingCol +1,kingRow),king) ||
                checkKing(chessboard.getPiece(kingCol -1,kingRow+1),king) ||
                checkKing(chessboard.getPiece(kingCol +1,kingRow+1),king) ||
                checkKing(chessboard.getPiece(kingCol,kingRow+1),king);
    }
    private boolean checkKing(Piece p,Piece k){
        return p != null && !chessboard.sameTeam(p,k) && p instanceof King;
    }
    private boolean hitByPawn(int col,int row,Piece king,int kingCol, int kingRow){
        int colorVal = king.isWhite ? -1 : 1;
        return checkPawn(chessboard.getPiece(kingCol+1,kingRow + colorVal),king,col,row) ||
                checkPawn(chessboard.getPiece(kingCol-1,kingRow + colorVal),king,col,row);
    }
    private boolean checkPawn(Piece p,Piece k,int col,int row){
        return p != null && !chessboard.sameTeam(p,k) && p instanceof Pawn && !(p.col == col && p.row == row);
    }

    public boolean isGameOver(Piece king) {
        for (Piece piece : chessboard.pieces) {
            if (chessboard.sameTeam(piece, king)) {
                chessboard.selectedPiece = piece == king ? king : null;
                for (int row = 0; row < chessboard.ROW; row++) {
                    for (int col = 0; col < chessboard.COL; col++) {
                        Move move = new Move(chessboard, piece, col, row);
                        // Add debugging to check if moves are being evaluated correctly

                        if (chessboard.isValidMove(move)) {

                            return false;  // There is a valid move, so the game is not over
                        }
                    }
                }
            }
        }

        return true;  // No valid moves left, game over (could be checkmate or stalemate)
    }



}
