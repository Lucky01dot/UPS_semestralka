package Windows;

import Message.Client_Make_Move;
import utils.Chessboard;
import utils.Move;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.ArrayList;

public class Multiplayer {

    String ServerAdress;
    int ServerPort;


    private Connection connection;

    private Client client;
    private Timer connectionChecker;
    Client WhitePlayer;
    Client BlackPlayer;

    public JFrame mainWindow;
    public JFrame loginFrame;
    public JFrame LobbyFrame;
    public JFrame GameFrame;
    public Chessboard chessboard;


    public Multiplayer(JFrame mainWindow,String ServerAdress,int ServerPort) {
        this.mainWindow = mainWindow;
        this.ServerAdress = ServerAdress;
        this.ServerPort = ServerPort;
        WhitePlayer = new Client("",true,true,null);
        BlackPlayer = new Client("",false,true,null);
        chessboard = new Chessboard(null,this);

        if(!ServerAdress.isEmpty() && ServerPort != 0){
            initializeConnection();
            startConnectionChecker();
            System.out.println("Connection checker started.");
        }



    }

    // Initialize server connection
    private void initializeConnection() {
        connection = new Connection(this, ServerAdress, ServerPort);
        connection.connect();
        this.client = new Client("",false,false,null);
        // Additional setup if required for connection or client.
    }

    // Handle disconnection
    public void disconnect() {
        // Zavření připojení, pokud existuje a je aktivní
        if (connection != null && connection.isConnected()) {
            connection.closeConnection();
        }
        stopConnectionChecker();
        connectionChecker = null;


        client = null;
        WhitePlayer = null;
        BlackPlayer = null;
        chessboard = null;


        // Zavření všech oken kromě hlavního (přihlašovacího)
        if (GameFrame != null) {
            GameFrame.setVisible(false);
            GameFrame.dispose();
        }
        if (LobbyFrame != null) {
            LobbyFrame.setVisible(false);
            LobbyFrame.dispose();
        }
        if (loginFrame != null) {
            loginFrame.setVisible(false);
            loginFrame.dispose();
        }
        mainWindow.setVisible(true);
    }

    public void startConnectionChecker() {
        connectionChecker = new Timer(5000, e -> {
            try {
                if (!getConnection().isConnected()) {
                    SwingUtilities.invokeLater(this::handleServerDisconnection);
                }
            } catch (Exception ex) {
                // Ošetření chyb při pokusu o kontrolu připojení
                SwingUtilities.invokeLater(this::handleServerDisconnection);
            }
        });
        connectionChecker.start();
    }




    public void stopConnectionChecker() {
        if (connectionChecker != null) {
            connectionChecker.stop();
        }
    }

    public Connection getConnection() {
        return connection;
    }

    public Client getClient() {
        return client;
    }

    public Client getBlackPlayer() {
        return BlackPlayer;
    }


    public Client getWhitePlayer() {
        return WhitePlayer;
    }




    public void handleServerDisconnection() {
        stopConnectionChecker();
        System.out.println("Server disconnected. Returning to main window.");
        JOptionPane.showMessageDialog(mainWindow,
                "Server is not available. Returning to the main window.",
                "Connection Error",
                JOptionPane.ERROR_MESSAGE);
        disconnect();
    }


    public void startGame(int gameID) {
        if (WhitePlayer == null || BlackPlayer == null) {
            System.err.println("Both White and Black players must be assigned before starting the game.");
            return;
        }


        // Zobrazení okna s odpočtem
        showCountdownWindow(gameID, WhitePlayer.getName(), BlackPlayer.getName());
    }

    private void showCountdownWindow(int gameID, String whitePlayerName, String blackPlayerName) {
        JFrame countdownFrame = new JFrame("Game Starting Soon");
        countdownFrame.setSize(400, 250);
        countdownFrame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        countdownFrame.setLocationRelativeTo(null);

        if (whitePlayerName.equals(getClient().getName())) {
            whitePlayerName = whitePlayerName + " (YOU)";
        }
        if (blackPlayerName.equals(getClient().getName())) {
            blackPlayerName = blackPlayerName + " (YOU)";
        }

        // Hlavní panel
        JPanel panel = new JPanel(new BorderLayout());
        countdownFrame.add(panel);

        // Jména hráčů
        JLabel playersLabel = new JLabel(
                "<html><div style='text-align: center;'>" +
                        "White Player: <b>" + whitePlayerName + "</b><br>" +
                        "Black Player: <b>" + blackPlayerName + "</b>" +
                        "</div></html>",
                SwingConstants.CENTER
        );
        playersLabel.setFont(new Font("Arial", Font.PLAIN, 16));
        panel.add(playersLabel, BorderLayout.NORTH);

        // Odpočet
        JLabel countdownLabel = new JLabel("Game starts in 10 seconds!", SwingConstants.CENTER);
        countdownLabel.setFont(new Font("Arial", Font.BOLD, 20));
        panel.add(countdownLabel, BorderLayout.CENTER);

        countdownFrame.setVisible(true);
        MultiplayerWindow multiplayerWindow = new MultiplayerWindow(this, gameID);

        // Spuštění odpočtu
        Timer timer = new Timer(1000, new ActionListener() {
            int countdown = 10;

            @Override
            public void actionPerformed(ActionEvent e) {
                if (countdown > 0) {
                    countdownLabel.setText("Game starts in " + countdown + " seconds!");
                    countdown--;
                } else {
                    ((Timer) e.getSource()).stop();
                    countdownFrame.dispose(); // Zavření okna s odpočtem

                    // Spuštění hry po dokončení odpočtu
                    try {

                        GameFrame = multiplayerWindow.createGameWindow();
                        LobbyFrame.setVisible(false);

                        System.out.println("Game started with GameID: " + gameID);
                    } catch (Exception ex) {
                        ex.printStackTrace();
                        JOptionPane.showMessageDialog(null,
                                "Error while starting the game: " + ex.getMessage(),
                                "Error", JOptionPane.ERROR_MESSAGE);
                    }
                }
            }
        });

        timer.start();
    }









}
