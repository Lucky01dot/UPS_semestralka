package Pieces;

import utils.Chessboard;
import utils.Piece;

/**
 * Třída reprezentující figurku střelce (Bishop).
 */
public class Bishop extends Piece {

    private final Chessboard chessboard;

    /**
     * Konstruktor pro vytvoření nové figurky střelce.
     * @param col Sloupec, kde je střelec umístěn.
     * @param row Řádek, kde je střelec umístěn.
     * @param isWhite Určuje, zda je střelec bílý (true) nebo černý (false).
     * @param chessboard Deska, na které je střelec umístěn.
     */
    public Bishop(int col, int row, boolean isWhite, Chessboard chessboard) {
        // Předání cesty k obrázku pro bílého nebo černého střelce
        super(col, row, isWhite, 
          isWhite ? System.getProperty("user.dir") + "/Pieces/white-bishop.png" : 
          System.getProperty("user.dir") + "/Pieces/black-bishop.png", 
          chessboard);
        this.chessboard = chessboard; // Uložení odkazu na šachovnici
    }

    /**
     * Kontrola, zda je pohyb střelce platný.
     * Střelec se pohybuje diagonálně, takže rozdíl mezi sloupcem a řádkem musí být stejný.
     * @param col Cíl sloupce, na který chce střelec jít.
     * @param row Cíl řádku, na který chce střelec jít.
     * @return true, pokud je pohyb platný, jinak false.
     */
    @Override
    public boolean isValidMovement(int col, int row) {
        // Střelec se pohybuje diagonálně, takže rozdíl v řádcích a sloupcích musí být stejný
        return Math.abs(this.col - col) == Math.abs(this.row - row);
    }

    /**
     * Zkontroluje, zda pohyb střelce nevede do kolize s jinou figurkou.
     * @param col Cíl sloupce pro pohyb.
     * @param row Cíl řádku pro pohyb.
     * @return true, pokud pohyb koliduje s nějakou figurkou, jinak false.
     */
    @Override
    public boolean moveCollidesWithPiece(int col, int row) {
        // Určení směru pohybu na sloupci a řádku
        int colDirection = (col > this.col) ? 1 : -1; // Pohyb vlevo nebo vpravo
        int rowDirection = (row > this.row) ? 1 : -1; // Pohyb nahoru nebo dolů

        int distance = Math.abs(this.col - col); // Vzdálenost (pohyb je diagonální, takže vzdálenost mezi sloupci je stejná i pro řádky)

        // Kontrola kolize na všech políčkách mezi výchozím a cílovým políčkem
        for (int i = 1; i < distance; i++) {
            int currentCol = this.col + i * colDirection;
            int currentRow = this.row + i * rowDirection;

            // Pokud je na políčku jiná figurka, pohyb je blokován
            if (chessboard.getPiece(currentCol, currentRow) != null) {
                return true; // Kolize detekována
            }
        }

        return false; // Žádná kolize, pohyb je možný
    }
}
