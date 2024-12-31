package Windows;

import Message.Message;
import utils.Chessboard;
import utils.Move;
import utils.Piece;
import Message.Server_Keep_Alive;

import javax.swing.*;
import java.io.*;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

public class Connection {

    private Socket socket;
    private BufferedWriter out; // Nahrazeno za BufferedWriter
    private BufferedReader in;
    private Multiplayer multiplayer;

    private static String SERVER_HOST = ""; // IP adresa serveru
    private static int SERVER_PORT = 0; // Port serveru
    private final Queue<String> messageQueue = new ConcurrentLinkedQueue<>();
    private volatile boolean running = true; // Pro zastavení vlákna při ukončení spojení

    public Connection(Multiplayer multiplayer, String SERVER_HOST, int SERVER_PORT) {
        this.multiplayer = multiplayer;
        Connection.SERVER_HOST = SERVER_HOST;
        Connection.SERVER_PORT = SERVER_PORT;
    }

    // Připojení ke serveru
    public void connect() {
        try {
            System.out.println("Connecting to server...");
            socket = new Socket(SERVER_HOST, SERVER_PORT);
            out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())); // BufferedWriter pro výstup
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));   // BufferedReader pro vstup
            System.out.println("Connected to server.");
            startKeepAlive();



            // Spuštění vlákna pro příjem zpráv od serveru
            new Thread(this::listenToServer).start();
        } catch (UnknownHostException e) {
            System.err.println("Unknown host: " + SERVER_HOST);
            e.printStackTrace();
        } catch (IOException e) {
            System.err.println("Error connecting to server: " + e.getMessage());
            e.printStackTrace();
        }
    }
    private void startKeepAlive() {
        new Thread(() -> {
            while (running) {
                try {
                    Thread.sleep(30000); // Posílání každých 10 sekund
                    if (isConnected()) {
                        sendMessage(new Server_Keep_Alive()); // Posílá zprávu PING
                    }
                } catch (InterruptedException e) {
                    System.err.println("Keep-alive thread interrupted: " + e.getMessage());
                }
            }
        }).start();
    }


    // Odpojení od serveru
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

    // Odeslání zprávy na server
    public void sendMessage(Message message) {
        if (isConnected()) {
            try {
                String serializedMessage = message.toString();
                out.write(serializedMessage);
                out.newLine(); // Přidání konce řádku
                out.flush();   // Vyprázdnění bufferu
                System.out.println("Sent to server: " + serializedMessage);
            } catch (IOException e) {
                System.err.println("Error sending message to server: " + e.getMessage());
                e.printStackTrace();
            }
        } else {
            System.err.println("Cannot send message. Not connected to server.");
        }
    }

    // Metoda pro opětovné připojení k serveru
    public void reconnect() {
        try {
            System.out.println("Attempting to reconnect to server...");

            // Uzavření starého připojení, pokud existuje
            closeConnection();

            // Znovu vytvoření socketu a komunikačních proudů
            socket = new Socket(SERVER_HOST, SERVER_PORT);
            out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())); // BufferedWriter
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));   // BufferedReader

            System.out.println("Reconnected to server.");

            // Spuštění vlákna pro příjem zpráv od serveru
            new Thread(this::listenToServer).start();


        } catch (UnknownHostException e) {
            System.err.println("Unknown host during reconnect: " + SERVER_HOST);
            e.printStackTrace();
        } catch (IOException e) {
            System.err.println("Error reconnecting to server: " + e.getMessage());
            e.printStackTrace();
        }


    }

    // Poslouchání zpráv od serveru
    private void listenToServer() {
        try {
            String message;
            while (running && (message = in.readLine()) != null) {
                messageQueue.add(message);
                processNextMessage();
            }
        } catch (IOException e) {
            System.err.println("Connection to server lost: " + e.getMessage());
            e.printStackTrace();
            multiplayer.handleServerDisconnection();
            reconnect();
        }
    }

    // Zpracování další zprávy ve frontě
    private void processNextMessage() {
        while (!messageQueue.isEmpty()) {
            String message = messageQueue.poll();
            processServerMessage(message);
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
                //System.out.println("konečně");
            } else if(message.equals("KEEP_ALIVE_OK")){
                System.out.println("You are marked as keep alive.");
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
