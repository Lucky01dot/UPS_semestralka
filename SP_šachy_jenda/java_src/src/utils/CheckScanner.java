package utils;

import Pieces.*;

public class CheckScanner {

    Chessboard chessboard;

    // Konstruktor třídy CheckScanner, který přijímá šachovnici jako argument
    public CheckScanner(Chessboard chessboard) {
        this.chessboard = chessboard;
    }

    // Hlavní metoda pro kontrolu, zda je král v šachu po provedení tahu
    public boolean isKingChecked(Move move) {

        // Hledání krále (na základě barvy)
        Piece king = chessboard.findKind(move.piece.isWhite);
        assert king != null;

        // Souřadnice krále
        int kingCol = king.getCol();
        int kingRow = king.getRow();

        // Pokud je vybraný figurka král a pohybuje se, aktualizujeme pozici krále
        if(chessboard.selectedPiece != null && chessboard.selectedPiece instanceof King && chessboard.selectedPiece == move.piece) {
            kingCol = move.newCol;
            kingRow = move.newRow;
        }

        // Kontrola, zda je král ohrožen jakoukoliv figurou
        return hitbyRook(move.newCol, move.newRow,king,kingCol,kingRow, 0,1) || // up
                hitbyRook(move.newCol, move.newRow,king,kingCol,kingRow, 1,0) || // right
                hitbyRook(move.newCol, move.newRow,king,kingCol,kingRow, 0,-1) || // down
                hitbyRook(move.newCol, move.newRow,king,kingCol,kingRow, -1,0) || // left

                hitbyBishop(move.newCol, move.newRow,king,kingCol,kingRow, -1,-1) || // up left
                hitbyBishop(move.newCol, move.newRow,king,kingCol,kingRow, 1,-1) || // up right
                hitbyBishop(move.newCol, move.newRow,king,kingCol,kingRow, 1,1) ||  // down right
                hitbyBishop(move.newCol, move.newRow,king,kingCol,kingRow, -1,1) || // down left

                hitByKnight(move.newCol, move.newRow, king,kingCol,kingRow) ||
                hitByPawn(move.newCol, move.newRow, king,kingCol,kingRow) ||
                hitByKing(king,kingCol,kingRow);
    }

    // Metoda pro detekci šachu věží nebo královnou
    private boolean hitbyRook(int col, int row, Piece king, int kingCol, int kingRow, int colVal, int rowVal) {
        for (int i = 1; i < 8; i++) {
            // Kontrola, zda je na cestě mezi králem a cílovou pozicí nějaká figura
            if (kingCol + (i * colVal) == col && kingRow + (i * rowVal) == row) {
                break;
            }
            // Získání figury na pozici (královské souřadnice + krok)
            Piece piece = chessboard.getPiece(kingCol + (i * colVal), kingRow + (i * rowVal));
            if (piece != null && piece != chessboard.selectedPiece) {
                // Pokud je to protivníkova věž nebo královna, král je ohrožen
                if (!chessboard.sameTeam(piece, king) && (piece instanceof Rook || piece instanceof Queen)) {
                    return true;
                }
                break;
            }
        }
        return false;
    }

    // Metoda pro detekci šachu střelcem nebo královnou
    private boolean hitbyBishop(int col, int row, Piece king, int kingCol, int kingRow, int colVal, int rowVal) {
        for (int i = 1; i < 8; i++) {
            // Kontrola, zda je na cestě mezi králem a cílovou pozicí nějaká figura
            if (kingCol - (i * colVal) == col && kingRow - (i * rowVal) == row) {
                break;
            }
            Piece piece = chessboard.getPiece(kingCol - (i * colVal), kingRow - (i * rowVal));
            if (piece != null && piece != chessboard.selectedPiece) {
                // Pokud je to protivníkův střelec nebo královna, král je ohrožen
                if (!chessboard.sameTeam(piece, king) && (piece instanceof Bishop || piece instanceof Queen)) {
                    return true;
                }
                break;
            }
        }
        return false;
    }

    // Metoda pro detekci šachu jezdcem
    private boolean hitByKnight(int col, int row, Piece king, int kingCol, int kingRow) {
        // Kontrola všech 8 možných pozic, kde může jezdec ohrozit krále
        return checkKnight(chessboard.getPiece(kingCol - 1, kingRow - 2), king, col, row) ||
                checkKnight(chessboard.getPiece(kingCol + 1, kingRow - 2), king, col, row) ||
                checkKnight(chessboard.getPiece(kingCol + 2, kingRow - 1), king, col, row) ||
                checkKnight(chessboard.getPiece(kingCol + 2, kingRow + 1), king, col, row) ||
                checkKnight(chessboard.getPiece(kingCol + 1, kingRow + 2), king, col, row) ||
                checkKnight(chessboard.getPiece(kingCol - 1, kingRow + 2), king, col, row) ||
                checkKnight(chessboard.getPiece(kingCol - 2, kingRow + 1), king, col, row) ||
                checkKnight(chessboard.getPiece(kingCol - 2, kingRow - 1), king, col, row);
    }

    // Pomocná metoda pro kontrolu, zda je daná figura jezdcem, který může ohrozit krále
    private boolean checkKnight(Piece p, Piece k, int col, int row) {
        return p != null && !chessboard.sameTeam(p, k) && p instanceof Knight && !(p.col == col && p.row == row);
    }

    // Metoda pro detekci šachu králem
    private boolean hitByKing(Piece king, int kingCol, int kingRow) {
        // Kontrola všech 8 možných pozic, kde může král ohrozit jiného krále
        return checkKing(chessboard.getPiece(kingCol - 1, kingRow - 1), king) ||
                checkKing(chessboard.getPiece(kingCol + 1, kingRow - 1), king) ||
                checkKing(chessboard.getPiece(kingCol, kingRow - 1), king) ||
                checkKing(chessboard.getPiece(kingCol - 1, kingRow), king) ||
                checkKing(chessboard.getPiece(kingCol + 1, kingRow), king) ||
                checkKing(chessboard.getPiece(kingCol - 1, kingRow + 1), king) ||
                checkKing(chessboard.getPiece(kingCol + 1, kingRow + 1), king) ||
                checkKing(chessboard.getPiece(kingCol, kingRow + 1), king);
    }

    // Pomocná metoda pro kontrolu, zda je daná figura králem, který může ohrozit jiného krále
    private boolean checkKing(Piece p, Piece k) {
        return p != null && !chessboard.sameTeam(p, k) && p instanceof King;
    }

    // Metoda pro detekci šachu pěšcem
    private boolean hitByPawn(int col, int row, Piece king, int kingCol, int kingRow) {
        int colorVal = king.isWhite ? -1 : 1;
        return checkPawn(chessboard.getPiece(kingCol + 1, kingRow + colorVal), king, col, row) ||
                checkPawn(chessboard.getPiece(kingCol - 1, kingRow + colorVal), king, col, row);
    }

    // Pomocná metoda pro kontrolu, zda je daná figura pěšcem, který může ohrozit krále
    private boolean checkPawn(Piece p, Piece k, int col, int row) {
        return p != null && !chessboard.sameTeam(p, k) && p instanceof Pawn && !(p.col == col && p.row == row);
    }

    // Metoda pro kontrolu, zda je hra u konce (mat nebo pat)
    public boolean isGameOver(Piece king) {
        // Pro každou figurku na straně hráče, který ovládá daného krále, provádí kontrolu všech možných tahů
        for (Piece piece : chessboard.pieces) {
            if (chessboard.sameTeam(piece, king)) {
                chessboard.selectedPiece = piece == king ? king : null;
                // Pokud některý tah vede k platnému pohybu, hra nekončí
                for (int row = 0; row < chessboard.ROW; row++) {
                    for (int col = 0; col < chessboard.COL; col++) {
                        Move move = new Move(chessboard, piece, col, row);
                        if (chessboard.isValidMove(move)) {
                            return false;  // Existuje platný tah, hra pokračuje
                        }
                    }
                }
            }
        }
        return true;  // Neexistuje žádný platný tah, hra končí (může to být mat nebo pat)
    }
}
