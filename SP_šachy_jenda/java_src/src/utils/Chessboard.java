package utils;

import Message.Client_End_Game;
import Pieces.*;
import Connection.Game;
import Connection.Multiplayer;
import enums.GameState;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

public class Chessboard extends JPanel {
    public int BOXSIZE = 85;  // Velikost jednoho pole šachovnice (šířka a výška)
    public int COL = 8;  // Počet sloupců šachovnice
    public int ROW = 8;  // Počet řádků šachovnice
    ArrayList<Piece> pieces = new ArrayList<>();  // Seznam všech figur na šachovnici
    public Piece selectedPiece;  // Aktuálně vybraná figura
    private boolean whiteToMove = true;  // Výchozí stav: Bílý hráč začíná
    private boolean isGameOver = false;  // Stav hry, zda už skončila
    private GameState gameState = GameState.IN_PROGRESS;  // Stav hry (probíhá, vítěz, remíza)
    private Game game;  // Objekt obsahující informace o hráčích a hře
    Multiplayer multiplayer;  // Objekt pro multiplayerovou hru
    Input input;  // Objekt pro zpracování uživatelského vstupu (kliknutí myší)
    public CheckScanner checkScanner = new CheckScanner(this);  // Objekt pro detekci šachu


    public Chessboard(Game game, Multiplayer multiplayer) {
        this.game = game;
        this.multiplayer = multiplayer;
        this.setPreferredSize(new Dimension(COL * BOXSIZE, ROW * BOXSIZE));  // Nastavení velikosti šachovnice
        input = new Input(this, multiplayer);  // Vytvoření objektu pro zpracování vstupu
        this.addMouseListener(input);  // Přidání posluchače pro kliknutí myší
        this.addMouseMotionListener(input);  // Přidání posluchače pro pohyb myši
        addPieces();  // Přidání figur na šachovnici
    }

    public boolean isWhiteToMove() {
        return whiteToMove;
    }

    public void setWhiteToMove(boolean whiteToMove) {
        this.whiteToMove = whiteToMove;

    }

    public Game getGame() {
        return game;
    }
    public void setGame(Game game) {
        this.game = game;

    }


    public void addPieces() {
        // Černé figurky (nahoře)
        pieces.add(new Rook(0, 7, true, this));
        pieces.add(new Knight(1, 7, true, this));
        pieces.add(new Bishop(2, 7, true, this));
        pieces.add(new Queen(3, 7, true, this));  // Queen na D
        pieces.add(new King(4, 7, true, this));   // King na E
        pieces.add(new Bishop(5, 7, true, this));
        pieces.add(new Knight(6, 7, true, this));
        pieces.add(new Rook(7, 7, true, this));
        for (int i = 0; i < COL; i++) {
            pieces.add(new Pawn(i, 6, true, this));  // Černí pěšci
        }

        // Bílé figurky (dole)
        pieces.add(new Rook(0, 0, false, this));
        pieces.add(new Knight(1, 0, false, this));
        pieces.add(new Bishop(2, 0, false, this));
        pieces.add(new Queen(3, 0, false, this));  // Queen na D
        pieces.add(new King(4, 0, false, this));   // King na E
        pieces.add(new Bishop(5, 0, false, this));
        pieces.add(new Knight(6, 0, false, this));
        pieces.add(new Rook(7, 0, false, this));
        for (int i = 0; i < COL; i++) {
            pieces.add(new Pawn(i, 1, false, this));  // Bílí pěšci
        }
    }

    public void makeMove(Move move) {
        if (isGameOver) {
            System.out.println("Game over! No moves allowed.");
            return;
        }


        if (move.piece instanceof Pawn) {
            movePawn(move);
        } else if (move.piece instanceof King) {
            moveKing(move);
        }

        // Aktualizace pozice figurky
        move.piece.col = move.newCol;
        move.piece.row = move.newRow;
        move.piece.setXPos(move.newCol * BOXSIZE);
        move.piece.setYPos(move.newRow * BOXSIZE);
        move.piece.isfirstmoved = false;

        // Odstranění zachycené figurky
        if (move.capture != null) {
            capture(move.capture);
        }

        // Změna hráče na tahu
        setWhiteToMove(!isWhiteToMove());

        repaint(); // Překreslení šachovnice


        updateGameState();
    }



    private void moveKing(Move move) {
        if (Math.abs(move.piece.col - move.newCol) == 2) {
            Piece rook;
            if (move.piece.col < move.newCol) {
                rook = getPiece(7, move.piece.row);
                rook.col = 5;
            } else {
                rook = getPiece(0, move.piece.row);
                rook.col = 3;
            }
            rook.xPos = rook.col * BOXSIZE;
        }
    }

    private void movePawn(Move move) {
        // Promotion
        int colorIndex = move.piece.isWhite ? 0 : 7; // Řada pro povýšení
        if (move.newRow == colorIndex) {
            promotePawn(move);
        }
    }

    private void promotePawn(Move move) {
        pieces.add(new Queen(move.newCol, move.newRow, move.piece.isWhite, this));
        capture(move.piece);
    }

    public void capture(Piece piece) {
        pieces.remove(piece);
    }

    public boolean isValidMove(Move move) {
        if (isGameOver) {
            return false;
        }

        if (sameTeam(move.piece, move.capture)) {
            return false;
        }
        if (!move.piece.isValidMovement(move.newCol, move.newRow)) {
            return false;
        }
        if (move.piece.moveCollidesWithPiece(move.newCol, move.newRow)) {
            return false;
        }
        if (checkScanner.isKingChecked(move)) {
            return false;
        }
        return true;
    }

    public boolean sameTeam(Piece p1, Piece p2) {
        if (p1 == null || p2 == null) {
            return false;
        }
        return p1.isWhite == p2.isWhite;
    }

    public Piece getPiece(int col, int row) {
        for (Piece piece : pieces) {
            if (piece.col == col && piece.row == row) {
                return piece;
            }
        }
        return null;
    }

    private void updateGameState() {
        Piece king = findKind(isWhiteToMove());
        if (checkScanner.isGameOver(king)) {
            if (checkScanner.isKingChecked(new Move(this, king, king.col, king.row))) {
                gameState = isWhiteToMove() ? GameState.BLACK_WINS : GameState.WHITE_WINS;
                isGameOver = true;
                if(multiplayer != null) {
                    multiplayer.inactivityTimer.stop();
                    String winner = isWhiteToMove() ? game.getBlack().getName() : game.getWhite().getName();
                    // Zobrazení dialogového okna
                    JOptionPane.showMessageDialog(this, "Hráč " + winner + " vyhrál!", "Konec hry", JOptionPane.INFORMATION_MESSAGE);
                    // Odeslání zprávy pouze od vítěze
                    if ((isWhiteToMove() && multiplayer.getClient().getName().equals(game.getBlack().getName())) ||
                            (!isWhiteToMove() && multiplayer.getClient().getName().equals(game.getWhite().getName()))) {
                        multiplayer.getConnection().sendMessage(new Client_End_Game(game.getGameID()));
                    }
                    multiplayer.GameFrame.setVisible(false);
                    multiplayer.GameFrame.dispose();
                    multiplayer.LobbyFrame.setVisible(true);
                    multiplayer.GameFrame.setVisible(false);
                    multiplayer.GameFrame.dispose();
                    multiplayer.LobbyFrame.setVisible(true);
                    multiplayer.getWhitePlayer().setName("");
                    multiplayer.getBlackPlayer().setName("");
                    multiplayer.getWhitePlayer().setGame(null);
                    multiplayer.getBlackPlayer().setGame(null);
                    game = null;
                    multiplayer.getClient().setReady(false);

                }
                else {
                    String winner = isWhiteToMove() ? "Black" : "White";
                    JOptionPane.showMessageDialog(this, "Hráč " + winner + " vyhrál!", "Konec hry", JOptionPane.INFORMATION_MESSAGE);
                }


            } else {
                gameState = GameState.STALEMATE;
                JOptionPane.showMessageDialog(this, "Hra skončila patem.", "Konec hry", JOptionPane.INFORMATION_MESSAGE);
            }
            isGameOver = true;
        } else {
            gameState = GameState.IN_PROGRESS;
        }
    }


    Piece findKind(boolean isWhite) {
        for (Piece piece : pieces) {
            if (piece.isWhite == isWhite && piece instanceof King) {
                return piece;
            }
        }
        return null;
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;


        // Získání aktuální šířky a výšky panelu
        int panelWidth = getWidth();
        int panelHeight = getHeight();

        // Dynamicky nastavíme BOXSIZE podle velikosti okna (vezmeme nejmenší možnou hodnotu)
        BOXSIZE = Math.min(panelWidth / COL, panelHeight / ROW); // Škálování podle velikosti okna

        // Vypočítáme odsazení (margin) pro středování šachovnice
        int xOffset = (panelWidth - (COL * BOXSIZE)) / 2;
        int yOffset = (panelHeight - (ROW * BOXSIZE)) / 2;

        Piece king = findKind(isWhiteToMove());
        boolean isKingInCheck = king != null && checkScanner.isKingChecked(new Move(this, king, king.col, king.row));
        boolean isKingInCheckmate = isGameOver && isKingInCheck;
        // Kreslíme šachovnici s odsazením
        for (int x = 0; x < ROW; x++) {
            for (int y = 0; y < COL; y++) {
                // Nastavení barvy pole
                if (isKingInCheckmate && king.col == y && king.row == x) {
                    g2d.setColor(Color.RED); // Zvýraznění krále při matu červeně
                } else if (isKingInCheck && king.col == y && king.row == x) {
                    g2d.setColor(Color.YELLOW); // Zvýraznění krále při šachu žlutě
                } else {
                    g2d.setColor((y + x) % 2 == 0 ? Color.WHITE : Color.GRAY); // Standardní barvy šachovnice
                }
                g2d.fillRect(xOffset + y * BOXSIZE, yOffset + x * BOXSIZE, BOXSIZE, BOXSIZE);
            }
        }


        String currentPlayer = isWhiteToMove() ? "White's Turn" : "Black's Turn";
        g2d.setColor(Color.BLACK);
        g2d.drawString(currentPlayer, 10, 20);

        if(selectedPiece != null) {
            for(int r = 0; r < ROW; r++) {
                for(int c = 0; c < COL; c++) {
                    if(isValidMove(new Move(this,selectedPiece,c,r))){
                        g2d.setColor(new Color(70, 180, 60, 190));
                        g2d.fillRect(xOffset+ c*BOXSIZE,yOffset+r*BOXSIZE,BOXSIZE,BOXSIZE);
                    }

                }
            }
        }

        // Kreslíme všechny figurky
        for (Piece piece : pieces) {
            if (piece != selectedPiece) {
                // Dynamické kreslení podle aktualizovaného BOXSIZE
                int pieceCol = piece.getCol();
                int pieceRow = piece.getRow();
                Image pieceImage = piece.getImage();
                if (pieceImage != null) {
                    g2d.drawImage(pieceImage, xOffset + pieceCol * BOXSIZE, yOffset + pieceRow * BOXSIZE, BOXSIZE, BOXSIZE, this);
                }
            }
        }

        // Kreslíme přetahovanou figurku
        if (selectedPiece != null) {
            Image selectedPieceImage = selectedPiece.getImage();
            if (selectedPieceImage != null) {
                // Aktualizace pozice podle BOXSIZE
                g2d.drawImage(selectedPieceImage, xOffset + selectedPiece.getXPos(), yOffset + selectedPiece.getYPos(), BOXSIZE, BOXSIZE, this);
            }
        }
    }
}
