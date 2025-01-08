package utils;

import Message.Client_Make_Move;
import Connection.Multiplayer;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class Input extends MouseAdapter {

    private final Chessboard chessboard;  // Odkaz na šachovnici
    Multiplayer multiplayer;  // Odkaz na multiplayer, pokud je zapnutý

    // Konstruktor, který přijímá šachovnici a multiplayer
    public Input(Chessboard chessboard, Multiplayer multiplayer) {
        this.chessboard = chessboard;
        this.multiplayer = multiplayer;
    }

    // Metoda volaná při stisknutí tlačítka myši (výběr figury)
    @Override
    public void mousePressed(MouseEvent e) {
        if (multiplayer != null) {
            // Kontrola, zda je hra pozastavena (např. čekání na tah)
            if (multiplayer.DONT_MOVE) {
                System.out.println("You cant play the game now.");
                return;  // Zastaví provádění, pokud hráč nemůže táhnout
            }

            // Kontrola, zda je na tahu správný hráč
            if (chessboard.isWhiteToMove() && !chessboard.getGame().getWhite().getName().equals(multiplayer.getClient().getName())) {
                System.out.println("It's White's turn! Waiting for White to play.");
                return;  // Pokud není na tahu bílý hráč, zamezí výběru
            }

            if (!chessboard.isWhiteToMove() && !chessboard.getGame().getBlack().getName().equals(multiplayer.getClient().getName())) {
                System.out.println("It's Black's turn! Waiting for Black to play.");
                return;  // Pokud není na tahu černý hráč, zamezí výběru
            }
        }

        // Získání odsazení pro správnou pozici kliknutí na šachovnici
        int xOffset = (chessboard.getWidth() - (chessboard.COL * chessboard.BOXSIZE)) / 2;
        int yOffset = (chessboard.getHeight() - (chessboard.ROW * chessboard.BOXSIZE)) / 2;

        // Výpočet logických pozic sloupce a řádku na základě pozice kliknutí
        int col = (e.getX() - xOffset) / chessboard.BOXSIZE;
        int row = (e.getY() - yOffset) / chessboard.BOXSIZE;

        // Získání figury na pozici, kam hráč kliknul
        Piece piece = chessboard.getPiece(col, row);
        if (piece != null && piece.isWhite == chessboard.isWhiteToMove()) {
            // Pokud je figura na správné barvě, vybere ji
            chessboard.selectedPiece = piece;
        } else {
            // Pokud není na správné pozici nebo je to prázdné pole, zruší výběr
            chessboard.selectedPiece = null;
        }
    }

    // Metoda volaná při pohybu myši (přetahování figury)
    @Override
    public void mouseDragged(MouseEvent e) {
        if (chessboard.selectedPiece != null) {
            // Získání odsazení pro správné zobrazení přetahování
            int xOffset = (chessboard.getWidth() - (chessboard.COL * chessboard.BOXSIZE)) / 2;
            int yOffset = (chessboard.getHeight() - (chessboard.ROW * chessboard.BOXSIZE)) / 2;

            // Aktualizace pozice figury tak, aby střed figury byl pod kurzorem
            int pieceSize = chessboard.BOXSIZE; // Velikost políčka je stejná jako velikost figury
            chessboard.selectedPiece.setXPos(e.getX() - xOffset - pieceSize / 2);
            chessboard.selectedPiece.setYPos(e.getY() - yOffset - pieceSize / 2);

            // Překreslení šachovnice, aby se změna pozice figury projevila
            chessboard.repaint();
        }
    }

    // Metoda volaná při uvolnění tlačítka myši (potvrzení tahu)
    @Override
    public void mouseReleased(MouseEvent e) {
        if (chessboard.selectedPiece != null) {
            // Získání odsazení pro správné zobrazení umístění po uvolnění
            int xOffset = (chessboard.getWidth() - (chessboard.COL * chessboard.BOXSIZE)) / 2;
            int yOffset = (chessboard.getHeight() - (chessboard.ROW * chessboard.BOXSIZE)) / 2;

            // Výpočet logických pozic sloupce a řádku, kam byla figura uvolněna
            int col = (e.getX() - xOffset) / chessboard.BOXSIZE;
            int row = (e.getY() - yOffset) / chessboard.BOXSIZE;

            // Vytvoření nového tahu na základě aktuální pozice figury
            Move move = new Move(chessboard, chessboard.selectedPiece, col, row);

            // Kontrola platnosti tahu
            if (chessboard.isValidMove(move)) {
                if (multiplayer != null) {
                    // Pokud je hra v multiplayeru, pošle tah ostatním hráčům
                    chessboard.multiplayer.getConnection().sendMessage(new Client_Make_Move(chessboard.getGame().getGameID(), move));
                }
                // Pokud je tah platný, provede ho
                chessboard.makeMove(move);
            } else {
                // Pokud tah není platný, vrátí figurku zpět na původní pozici
                chessboard.selectedPiece.setXPos(chessboard.selectedPiece.getCol() * chessboard.BOXSIZE);
                chessboard.selectedPiece.setYPos(chessboard.selectedPiece.getRow() * chessboard.BOXSIZE);
            }

            // Uvolnění vybrané figury a překreslení šachovnice
            chessboard.selectedPiece = null;
            chessboard.repaint();
        }
    }
}
