package utils;

import Message.Client_Make_Move;
import Windows.Game;
import Windows.Multiplayer;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class Input extends MouseAdapter {

    private final Chessboard chessboard;
    Multiplayer multiplayer;

    public Input(Chessboard chessboard,Multiplayer multiplayer) {
        this.chessboard = chessboard;
        this.multiplayer = multiplayer;

    }

    @Override
    public void mousePressed(MouseEvent e) {
        // Kontrola, zda tah provádí správný hráč
        if (chessboard.isWhiteToMove() && !chessboard.getGame().getWhite().getName().equals(multiplayer.getClient().getName())) {
            System.out.println("It's White's turn! Waiting for White to play.");
            return;
        }

        if (!chessboard.isWhiteToMove() && !chessboard.getGame().getBlack().getName().equals(multiplayer.getClient().getName())) {
            System.out.println("It's Black's turn! Waiting for Black to play.");
            return;
        }

        // Získání odsazení (xOffset, yOffset) z metody Chessboard
        int xOffset = (chessboard.getWidth() - (chessboard.COL * chessboard.BOXSIZE)) / 2;
        int yOffset = (chessboard.getHeight() - (chessboard.ROW * chessboard.BOXSIZE)) / 2;

        // Výpočet logických pozic (sloupec a řádek) z pozice kliknutí myší s přihlédnutím k odsazení
        int col = (e.getX() - xOffset) / chessboard.BOXSIZE;
        int row = (e.getY() - yOffset) / chessboard.BOXSIZE;

        // Získání figurky na kliknuté pozici
        Piece piece = chessboard.getPiece(col, row);
        if (piece != null && piece.isWhite == chessboard.isWhiteToMove()) {
            chessboard.selectedPiece = piece;
        } else {
            chessboard.selectedPiece = null; // Zrušení výběru
        }
    }

    @Override
    public void mouseDragged(MouseEvent e) {
        if (chessboard.selectedPiece != null) {
            // Získání odsazení (xOffset, yOffset) z metody Chessboard
            int xOffset = (chessboard.getWidth() - (chessboard.COL * chessboard.BOXSIZE)) / 2;
            int yOffset = (chessboard.getHeight() - (chessboard.ROW * chessboard.BOXSIZE)) / 2;

            // Aktualizace pozice figurky při přetahování tak, aby střed figurky byl pod kurzorem
            int pieceSize = chessboard.BOXSIZE; // Předpokládáme, že figurka je stejně velká jako políčko
            chessboard.selectedPiece.setXPos(e.getX() - xOffset - pieceSize / 2);
            chessboard.selectedPiece.setYPos(e.getY() - yOffset - pieceSize / 2);

            // Překreslení šachovnice
            chessboard.repaint();
        }
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        if (chessboard.selectedPiece != null) {
            // Získání odsazení (xOffset, yOffset) z metody Chessboard
            int xOffset = (chessboard.getWidth() - (chessboard.COL * chessboard.BOXSIZE)) / 2;
            int yOffset = (chessboard.getHeight() - (chessboard.ROW * chessboard.BOXSIZE)) / 2;

            // Výpočet sloupce a řádku, kam se figurka pustila
            int col = (e.getX() - xOffset) / chessboard.BOXSIZE;
            int row = (e.getY() - yOffset) / chessboard.BOXSIZE;

            // Vytvoření tahu
            Move move = new Move(chessboard, chessboard.selectedPiece, col, row);

            // Zkontrolování platnosti tahu a aktualizace pozice figurky
            if (chessboard.isValidMove(move)) {
                chessboard.makeMove(move);
                // Odeslání tahu druhému klientovi
                if (chessboard.multiplayer.getConnection() != null) {
                    chessboard.multiplayer.getConnection().sendMessage(new Client_Make_Move(chessboard.getGame().getGameID(), move));
                }
            } else {
                // Vrať figurku zpět na původní místo, pokud je tah neplatný
                chessboard.selectedPiece.setXPos(chessboard.selectedPiece.getCol() * chessboard.BOXSIZE);
                chessboard.selectedPiece.setYPos(chessboard.selectedPiece.getRow() * chessboard.BOXSIZE);
            }

            // Uvolnění vybrané figurky
            chessboard.selectedPiece = null;
            chessboard.repaint(); // Překreslení po uvolnění
        }
    }

}
