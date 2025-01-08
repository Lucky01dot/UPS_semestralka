package Message;

import Constants.Constants;
import enums.Messages;
import utils.Move;

/**
 * Třída pro zprávu, která informuje o tahu, který hráč provedl.
 */
public class Client_Make_Move extends Message {

    private int GameID;              // ID hry, ke které tah patří
    private String pieceType;        // Typ figurky, která byla pohnuta
    private int oldCol;              // Původní sloupec figurky
    private int oldRow;              // Původní řádek figurky
    private int newCol;              // Nový sloupec figurky
    private int newRow;              // Nový řádek figurky
    private String capturedPieceType; // Typ zajaté figurky, pokud existuje

    /**
     * Konstruktor pro vytvoření zprávy o tahu.
     * @param gameID ID hry, ke které tah patří
     * @param move Objekt reprezentující tah
     */
    public Client_Make_Move(int gameID, Move move) {
        this.name = Messages.CLIENT_MAKE_MOVE; // Nastavení názvu zprávy na CLIENT_MAKE_MOVE

        this.GameID = gameID; // Nastavení ID hry

        // Nastavení typu figurky (první písmeno, velké pro bílé, malé pro černé)
        char pieceChar = move.piece.getClass().getSimpleName().charAt(0);
        this.pieceType = move.piece.isWhite() ? Character.toUpperCase(pieceChar) + "" : Character.toLowerCase(pieceChar) + "";

        this.oldCol = move.oldCol; // Nastavení původního sloupce
        this.oldRow = move.oldRow; // Nastavení původního řádku
        this.newCol = move.newCol; // Nastavení nového sloupce
        this.newRow = move.newRow; // Nastavení nového řádku

        // Nastavení typu zajaté figurky (pokud existuje)
        if (move.capture != null) {
            char captureChar = move.capture.getClass().getSimpleName().charAt(0);
            this.capturedPieceType = move.capture.isWhite() ? Character.toUpperCase(captureChar) + "" : Character.toLowerCase(captureChar) + "";
        } else {
            this.capturedPieceType = "none"; // Pokud žádná figurka není zajata, nastaví se "none"
        }
    }

    /**
     * Převod objektu na formát zprávy pro server.
     * @return Formátovaná zpráva pro server
     */
    @Override
    public String toString() {
        return String.format("%s;%d;%s;%d;%d;%d;%d;%s",
                this.name.toString(), GameID,
                pieceType, oldCol, oldRow, newCol, newRow,
                capturedPieceType);
    }
}
