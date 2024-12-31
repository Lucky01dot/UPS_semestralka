package utils;

import Message.Client_Make_Move;
import Pieces.*;
import Windows.Game;
import Windows.Multiplayer;
import enums.GameState;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

public class Chessboard extends JPanel {
    public int BOXSIZE = 85;
    public int COL = 8;
    public int ROW = 8;
    ArrayList<Piece> pieces = new ArrayList<>();
    public Piece selectedPiece;
    private boolean whiteToMove = true; // Výchozí stav, bílý hráč začíná

    private boolean isGameOver = false;
    private GameState gameState = GameState.IN_PROGRESS;

    private Game game; // Obsahuje informace o hráčích
    Multiplayer multiplayer;

    Input input;
    public CheckScanner checkScanner = new CheckScanner(this);

    public Chessboard(Game game, Multiplayer multiplayer) {
        this.game = game;
        this.multiplayer = multiplayer;
        this.setPreferredSize(new Dimension(COL * BOXSIZE, ROW * BOXSIZE));
        input = new Input(this,multiplayer);
        this.addMouseListener(input);
        this.addMouseMotionListener(input);
        addPieces();
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




        // Validace tahu
        if (!isValidMove(move)) {
            System.out.println("Invalid move.");
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

        // Aktualizace herního stavu
        updateGameState();

        repaint(); // Překreslení šachovnice
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
            } else {
                gameState = GameState.STALEMATE;
            }
            isGameOver = true; // Signalizace konce hry
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

        // Kreslíme šachovnici s odsazením
        for (int x = 0; x < ROW; x++) {
            for (int y = 0; y < COL; y++) {
                g2d.setColor((y + x) % 2 == 0 ? Color.WHITE : Color.GRAY);
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
