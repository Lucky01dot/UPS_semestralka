package Message;

import Connection.Game;
import Constants.Constants;
import enums.Messages;
import utils.Move;

public class Server_Invalid_Move extends Message {
    private final Move move;
    private final Game game;


    private String pieceType;        // Typ figurky, která byla pohnuta
    private int oldCol;              // Původní sloupec figurky
    private int oldRow;              // Původní řádek figurky
    private int newCol;              // Nový sloupec figurky
    private int newRow;              // Nový řádek figurky
    private String capturedPieceType; // Typ zajaté figurky, pokud existuje

    public Server_Invalid_Move(Move move, Game game) {
        if (move == null) {
            throw new IllegalArgumentException("Move cannot be null");
        }

        char pieceChar = move.piece.getClass().getSimpleName().charAt(0);
        this.pieceType = move.piece.isWhite() ? Character.toUpperCase(pieceChar) + "" : Character.toLowerCase(pieceChar) + "";

        this.oldCol = move.oldCol; // Nastavení původního sloupce
        this.oldRow = move.oldRow; // Nastavení původního řádku
        this.newCol = move.newCol; // Nastavení nového sloupce
        this.newRow = move.newRow; // Nastavení nového řádku

        this.move = move;
        this.game = game;
        this.name = Messages.SERVER_INVALID_MOVE;

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
        return String.format("%s;%d;%d;%d;%d;%d;%s;%s",
                this.name.toString(), this.game.getGameID(),
                 newCol, newRow, oldCol, oldRow, pieceType,
                capturedPieceType);
    }
}
