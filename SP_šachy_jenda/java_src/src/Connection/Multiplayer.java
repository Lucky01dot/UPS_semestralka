package Connection;

import Windows.MultiplayerWindow;
import utils.Chessboard;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Třída pro správu multiplayerové hry, včetně připojení, odpojení a spuštění hry.
 */
public class Multiplayer {

    // Adresa serveru
    String ServerAdress;

    // Port serveru
    int ServerPort;

    // Připojení k serveru
    private Connection connection;

    // Klient aktuálního hráče
    private Client client;

    // Hráči: bílý a černý
    Client WhitePlayer;
    Client BlackPlayer;

    public Timer inactivityTimer;
    public JLabel lobbyplayerLabel;

    // Indikátor, zda je zakázáno tahat
    public boolean DONT_MOVE = false;
    public int playerCount = 0;


    // Různá okna aplikace
    public JFrame mainWindow;
    public JFrame loginFrame;
    public JFrame LobbyFrame;
    public JFrame GameFrame;

    // Šachovnice hry
    public Chessboard chessboard;

    /**
     * Konstruktor třídy Multiplayer.
     *
     * @param mainWindow   Hlavní okno aplikace.
     * @param ServerAdress Adresa serveru.
     * @param ServerPort   Port serveru.
     */
    public Multiplayer(JFrame mainWindow, String ServerAdress, int ServerPort) {
        this.mainWindow = mainWindow;
        this.ServerAdress = ServerAdress;
        this.ServerPort = ServerPort;
        lobbyplayerLabel = new JLabel("Players: "+playerCount+"/2", SwingConstants.RIGHT);
        // Inicializace hráčů
        WhitePlayer = new Client("", true, true,0, null);
        BlackPlayer = new Client("", false, true,0, null);

        // Inicializace připojení, pokud jsou k dispozici údaje
        if (!ServerAdress.isEmpty() && ServerPort != 0) {
            initializeConnection();
        }
    }

    /**
     * Inicializace připojení k serveru.
     */
    private void initializeConnection() {
        connection = new Connection(this, ServerAdress, ServerPort);
        connection.connect();
        this.client = new Client("", false, false,0, null);
    }

    /**
     * Odpojení od serveru a ukončení všech oken.
     */
    public void disconnect() {
        // Zavření připojení, pokud existuje
        if (connection != null && connection.isConnected()) {
            connection.closeConnection();
        }

        // Vyčištění všech proměnných
        client = null;
        WhitePlayer = null;
        BlackPlayer = null;
        chessboard = null;

        // Zavření všech oken kromě hlavního
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

    // Getter pro připojení
    public Connection getConnection() {
        return connection;
    }

    // Getter pro aktuálního klienta
    public Client getClient() {
        return client;
    }

    // Getter pro černého hráče
    public Client getBlackPlayer() {
        return BlackPlayer;
    }

    // Getter pro bílého hráče
    public Client getWhitePlayer() {
        return WhitePlayer;
    }

    /**
     * Řešení situace, kdy server odpojil spojení.
     */
    public void handleServerDisconnection() {
        System.out.println("Server disconnected. Returning to main window.");
        JOptionPane.showMessageDialog(mainWindow,
                "Server is not available. Returning to the main window.",
                "Connection Error",
                JOptionPane.ERROR_MESSAGE);
        disconnect();
    }

    /**
     * Spuštění nové hry.
     *
     * @param gameID Identifikátor hry.
     */
    public void startGame(int gameID) {
        if (WhitePlayer == null || BlackPlayer == null) {
            System.err.println("Both White and Black players must be assigned before starting the game.");
            return;
        }

        // Zobrazení odpočítávacího okna
        showCountdownWindow(gameID, WhitePlayer.getName(), BlackPlayer.getName());
    }

    /**
     * Zobrazení odpočítávacího okna před zahájením hry.
     *
     * @param gameID          Identifikátor hry.
     * @param whitePlayerName Jméno bílého hráče.
     * @param blackPlayerName Jméno černého hráče.
     */
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

        // Panel s odpočtem
        JPanel panel = new JPanel(new BorderLayout());
        countdownFrame.add(panel);

        JLabel playersLabel = new JLabel(
                "<html><div style='text-align: center;'>" +
                        "White Player: <b>" + whitePlayerName + "</b><br>" +
                        "Black Player: <b>" + blackPlayerName + "</b>" +
                        "</div></html>",
                SwingConstants.CENTER
        );
        playersLabel.setFont(new Font("Arial", Font.PLAIN, 16));
        panel.add(playersLabel, BorderLayout.NORTH);

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
                    countdownFrame.dispose(); // Zavření okna

                    // Spuštění hry po dokončení odpočtu
                    try {
                        chessboard = new Chessboard(null, Multiplayer.this);
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
    public void updatePlayerCount(int newPlayerCount) {
        playerCount = newPlayerCount; // Aktualizace počtu hráčů
        lobbyplayerLabel.setText("Players: "+newPlayerCount + "/2"); // Nastavení nového textu
        lobbyplayerLabel.revalidate(); // Přepočítání layoutu
        lobbyplayerLabel.repaint(); // Překreslení labelu
    }


}
