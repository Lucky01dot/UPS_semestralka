package Message;

import Constants.Constants;
import enums.Messages;
import utils.Move;

public class Client_Make_Move extends Message {
    private int GameID;
    private String pieceType;
    private int oldCol;
    private int oldRow;
    private int newCol;
    private int newRow;
    private String capturedPieceType;

    public Client_Make_Move(int gameID, Move move) {
        this.name = Messages.CLIENT_MAKE_MOVE;

        this.GameID = gameID;

        // Nastavení typu figurky (první písmeno, velké pro bílé, malé pro černé)
        char pieceChar = move.piece.getClass().getSimpleName().charAt(0);
        this.pieceType = move.piece.isWhite() ? Character.toUpperCase(pieceChar) + "" : Character.toLowerCase(pieceChar) + "";

        this.oldCol = move.oldCol;
        this.oldRow = move.oldRow;
        this.newCol = move.newCol;
        this.newRow = move.newRow;

        // Nastavení typu zajaté figurky (pokud existuje)
        if (move.capture != null) {
            char captureChar = move.capture.getClass().getSimpleName().charAt(0);
            this.capturedPieceType = move.capture.isWhite() ? Character.toUpperCase(captureChar) + "" : Character.toLowerCase(captureChar) + "";
        } else {
            this.capturedPieceType = "none";
        }
    }

    // Převod na zprávu pro server
    @Override
    public String toString() {
        return String.format("%s;%d;%s;%d;%d;%d;%d;%s",
                this.name.toString(), GameID,
                pieceType, oldCol, oldRow, newCol, newRow,
                capturedPieceType);
    }
}
