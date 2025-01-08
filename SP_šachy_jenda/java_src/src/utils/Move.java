package utils;

public class Move {
    // Sloupce a řádky pro starou a novou pozici figury
    public int oldCol;
    public int oldRow;
    public int newCol;
    public int newRow;

    // Odkaz na figuru, která provádí tah
    public Piece piece;

    // Odkaz na figuru, která byla zachycena (pokud je to platné)
    public Piece capture;

    // Konstruktor pro vytvoření tahu
    public Move(Chessboard chessboard, Piece piece, int newCol, int newRow) {
        // Uložení původní pozice figury (před provedením tahu)
        this.oldCol = piece.col;
        this.oldRow = piece.row;

        // Uložení nové pozice, kam se figura přesunula
        this.newCol = newCol;
        this.newRow = newRow;

        // Uložení informace o figurce, která provádí tah
        this.piece = piece;

        // Zjištění, zda byla na nové pozici zachycena nějaká figura
        this.capture = chessboard.getPiece(newCol, newRow); // Získání figury na nové pozici
    }
}
