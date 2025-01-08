package Connection;

import Message.Message;
import Windows.LobbyWindow;
import utils.Move;
import utils.Piece;
import Message.Server_Keep_Alive;

import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Objects;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

// Třída `Connection` zajišťuje připojení klienta k serveru, posílání a přijímání zpráv a další související logiku.
public class Connection {

    private Socket socket;                       // Socket pro komunikaci se serverem
    private BufferedWriter out;                 // Výstupní proud pro odesílání zpráv
    private BufferedReader in;                  // Vstupní proud pro příjem zpráv
    private Multiplayer multiplayer;            // Reference na logiku multiplayeru

    private static String SERVER_HOST = "";     // IP adresa serveru
    private static int SERVER_PORT = 0;         // Port serveru
    private final Queue<String> messageQueue = new ConcurrentLinkedQueue<>(); // Fronta pro zpracování zpráv
    private volatile boolean running = true;    // Indikátor, zda je připojení aktivní
    private JFrame reconnectionFrame;           // Okno pro zobrazení stavu opětovného připojení

    // Konstruktor, který nastaví základní parametry připojení
    public Connection(Multiplayer multiplayer, String SERVER_HOST, int SERVER_PORT) {
        this.multiplayer = multiplayer;
        Connection.SERVER_HOST = SERVER_HOST;
        Connection.SERVER_PORT = SERVER_PORT;
    }

    // --- Připojení k serveru ---
    public void connect() {
        try {
            System.out.println("Connecting to server...");
            socket = new Socket(SERVER_HOST, SERVER_PORT);
            out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())); // Inicializace výstupu
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));   // Inicializace vstupu
            System.out.println("Connected to server.");
            startKeepAlive(); // Spuštění keep-alive mechanismu

            // Spuštění vlákna pro naslouchání serveru
            new Thread(this::listenToServer).start();
        } catch (UnknownHostException e) {
            System.err.println("Unknown host: " + SERVER_HOST);
            e.printStackTrace();
        } catch (IOException e) {
            System.err.println("Error connecting to server: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // --- Keep-Alive Mechanismus ---
    // Posílá pravidelné zprávy serveru, aby spojení zůstalo aktivní
    private void startKeepAlive() {
        new Thread(() -> {
            while (running) {
                try {
                    Thread.sleep(30000); // Keep-alive interval (30 sekund)
                    if (isConnected()) {
                        sendMessage(new Server_Keep_Alive()); // Odeslání keep-alive zprávy
                    } else {
                        System.err.println("Keep-alive failed. Connection lost.");
                        multiplayer.handleServerDisconnection();
                    }
                } catch (InterruptedException e) {
                    System.err.println("Keep-alive thread interrupted: " + e.getMessage());
                }
            }
        }).start();
    }

    // --- Odpojení od serveru ---
    public void closeConnection() {
        running = false; // Zastavení vlákna
        try {
            if (socket != null && !socket.isClosed()) {
                out.close();
                in.close();
                socket.close();
                System.out.println("Disconnected from server.");
            }
        } catch (IOException e) {
            System.err.println("Error while closing connection: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // --- Odesílání zpráv ---
    public void sendMessage(Message message) {
        if (isConnected()) {
            try {
                String serializedMessage = message.toString(); // Serializace zprávy
                out.write(serializedMessage);
                out.newLine(); // Přidání konce řádku
                out.flush();   // Vyprázdnění bufferu
                System.out.println("Sent to server: " + serializedMessage);
            } catch (IOException e) {
                multiplayer.handleServerDisconnection();
                System.err.println("Error sending message to server: " + e.getMessage());
                e.printStackTrace();
            }
        } else {
            System.err.println("Cannot send message. Not connected to server.");
        }
    }

    // --- Opětovné připojení ---
    public void reconnect() {
        try {
            System.out.println("Attempting to reconnect to server...");
            closeConnection(); // Uzavření starého spojení
            connect();         // Znovupřipojení k serveru
        } catch (Exception e) {
            System.err.println("Error reconnecting to server: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // --- Naslouchání serveru ---
    private void listenToServer() {
        try {
            String message;
            while (running && (message = in.readLine()) != null) {
                messageQueue.add(message); // Přidání zprávy do fronty
                processNextMessage();      // Zpracování zprávy
            }
        } catch (IOException e) {
            System.err.println("Connection to server lost: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // --- Zpracování zpráv ---
    private void processNextMessage() {
        while (!messageQueue.isEmpty()) {
            String message = messageQueue.poll(); // Vyjmutí zprávy z fronty
            processServerMessage(message);        // Zpracování konkrétní zprávy
        }
    }


    // Zpracování zprávy od serveru
    private void processServerMessage(String message) {
        try {

            if (message.startsWith("NAME:")) {
                String[] parts = message.split(";");
                String name = null;
                String role = null;

                for (String part : parts) {
                    if (part.startsWith("NAME:")) {
                        name = part.substring(5); // Odstraňuje "NAME:"
                    } else if (part.startsWith("ROLE:")) {
                        role = part.substring(5); // Odstraňuje "ROLE:"
                    }
                }


                if (name != null && role != null) {
                    multiplayer.getClient().setName(name);
                    multiplayer.getClient().setWhite(role.equals("WHITE"));
                    multiplayer.loginFrame.setVisible(false);
                    LobbyWindow lobbyWindow = new LobbyWindow(multiplayer);
                    multiplayer.LobbyFrame = lobbyWindow.createLobbyWindow();
                    multiplayer.LobbyFrame.setVisible(true);
                } else {
                    System.err.println("Invalid message format for client setup: " + message);
                }

            } else if (message.startsWith("START_GAME;")) {
                String[] parts = message.split(";");
                if (parts.length == 5) {
                    int gameID = Integer.parseInt(parts[1]);
                    String whitePlayerName = parts[2];
                    String blackPlayerName = parts[3];
                    String role = parts[4];

                    System.out.println("Game started! White: " + whitePlayerName + ", Black: " + blackPlayerName + ", You are: " + role);

                    multiplayer.getWhitePlayer().setName(whitePlayerName);
                    multiplayer.getBlackPlayer().setName(blackPlayerName);

                    multiplayer.startGame(gameID);
                } else {
                    System.err.println("Invalid START_GAME message format: " + message);
                }

            } else if (message.equals("UNREADY")) {
                System.out.println("You are marked as unready.");
                multiplayer.getClient().setReady(false);

            } else if (message.equals("READY")) {
                System.out.println("You are marked as ready.");
                multiplayer.getClient().setReady(true);

            } else if (message.startsWith("UPDATE")) {
                processUpdateMessage(message);
            } else if(message.equals("KEEP_ALIVE_OK")){
                System.out.println("You are marked as keep alive.");
            } else if(message.equals("LOGOUT_SUCCESS")){
                System.out.println("You are marked as logout.");
            } else if(message.equals("SERVER_OPPONENT_LEFT_GAME")){
                JOptionPane.showMessageDialog(null, "Opponent left game. Back to the Lobby.");
                multiplayer.inactivityTimer = null;
                multiplayer.DONT_MOVE = false;
                reconnectionFrame.setVisible(false);
                reconnectionFrame.dispose();
                multiplayer.getClient().setReady(false);
                multiplayer.GameFrame.setVisible(false);
                multiplayer.GameFrame.dispose();
                multiplayer.LobbyFrame.setVisible(true);



            } else if(message.equals("OPPONENT_DISCONNECT_GAME")){
                JOptionPane.showMessageDialog(null, "Opponent left App. Back to the main menu.");
                multiplayer.DONT_MOVE = false;
                multiplayer.disconnect();
            } else if(message.equals("USER_EXIST")){
                JOptionPane.showMessageDialog(null, "User already exists. Choose different username.");

            } else if (message.equals("GAME_STOPPED")) {
                multiplayer.DONT_MOVE = true;
                String opponentName;
                if(Objects.equals(multiplayer.getClient().getName(), multiplayer.getWhitePlayer().getName())){
                    opponentName = multiplayer.getBlackPlayer().getName();
                } else{
                    opponentName = multiplayer.getWhitePlayer().getName();
                }

                reconnectionFrame = new JFrame("Game Stopped");
                JLabel label = new JLabel("The game has been stopped. Waiting for a opponent " + opponentName + " .", SwingConstants.CENTER);
                label.setFont(new Font("Arial", Font.PLAIN, 16));

                reconnectionFrame.add(label);
                reconnectionFrame.setSize(600, 200);
                reconnectionFrame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
                reconnectionFrame.setLocationRelativeTo(null);
                reconnectionFrame.setVisible(true);


            } else if(message.equals("GAME_RECONNECTED")){
                multiplayer.DONT_MOVE = false;
                reconnectionFrame.setVisible(false);
                reconnectionFrame.dispose();
            }else {
                System.err.println("Unrecognized server message: " + message);
            }
        } catch (Exception e) {
            System.err.println("Error processing server message: " + message);
            e.printStackTrace();
        }
    }

    // Zpracování UPDATE zprávy
    private void processUpdateMessage(String message) {
        try {
            String[] parts = message.split(";");

            // Získání dat z příchozí zprávy
            int gameID = Integer.parseInt(parts[1]); // ID hry
            String pieceType = parts[2]; // Typ figurky (jedno písmeno)
            int oldCol = Integer.parseInt(parts[3]);
            int oldRow = Integer.parseInt(parts[4]);
            int newCol = Integer.parseInt(parts[5]);
            int newRow = Integer.parseInt(parts[6]);
            String capturedPieceType = parts[7].equals("none") ? null : parts[7]; // Typ zajaté figurky (jedno písmeno)

            SwingUtilities.invokeLater(() -> {
                if (multiplayer.chessboard != null && multiplayer.chessboard.getGame().getGameID() == gameID) {
                    Piece movingPiece = multiplayer.chessboard.getPiece(oldCol, oldRow);

                    // Kontrola, zda se typ figurky shoduje
                    if (movingPiece != null && getPieceTypeLetter(movingPiece).equals(pieceType)) {
                        System.out.println(getPieceTypeLetter(movingPiece));
                        System.out.println(pieceType);
                        Move move = new Move(multiplayer.chessboard, movingPiece, newCol, newRow);


                        multiplayer.chessboard.makeMove(move);
                        multiplayer.chessboard.repaint();
                    } else {
                        System.err.println("Invalid UPDATE: Piece mismatch or not found at position (" + oldCol + "," + oldRow + ")");
                    }
                } else {
                    System.err.println("Game with ID " + gameID + " not found.");
                }
            });
        } catch (Exception e) {
            System.err.println("Error processing UPDATE message: " + message);
            e.printStackTrace();
        }
    }

    /**
     * Vrátí typ figurky jako jedno písmeno (velké pro bílé, malé pro černé).
     *
     * @param piece Figurky, která se kontroluje.
     * @return Jednopísmenný typ figurky.
     */
    private String getPieceTypeLetter(Piece piece) {
        char firstLetter = piece.getClass().getSimpleName().charAt(0);
        return piece.isWhite() ? Character.toUpperCase(firstLetter) + "" : Character.toLowerCase(firstLetter) + "";
    }


    // Kontrola, zda je klient připojen k serveru
    public boolean isConnected() {
        return socket != null && socket.isConnected() && !socket.isClosed();
    }

}
