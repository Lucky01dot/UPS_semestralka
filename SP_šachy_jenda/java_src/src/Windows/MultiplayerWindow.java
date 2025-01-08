package Windows;

import Connection.Game;
import Connection.Multiplayer;
import Constants.Constants;
import Message.Client_Leave_Game;
import Message.Client_Reconnect_Game;
import Message.Sever_Stop_Game;

import javax.swing.*;
import java.awt.*;

public class MultiplayerWindow {

    Multiplayer multiplayer;
    int gameID;

    Game game;


    private static final int INACTIVITY_LIMIT = 5 * 60 * 1000;

    public MultiplayerWindow(Multiplayer multiplayer, int gameID) {
        this.multiplayer = multiplayer;
        this.gameID = gameID;
    }

    private void startInactivityTimer() {
        multiplayer.inactivityTimer = new Timer(INACTIVITY_LIMIT, e -> {
            JOptionPane.showMessageDialog(multiplayer.GameFrame,
                    "You have been inactive for too long. Reconnecting...",
                    "Inactivity Warning",
                    JOptionPane.WARNING_MESSAGE);
            createReconnectWindow();
        });

        multiplayer.inactivityTimer.setRepeats(false); // Jednorázové spuštění
        multiplayer.inactivityTimer.start();

        // Přidání posluchače pro resetování časovače při uživatelské akci
        multiplayer.GameFrame.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent e) {
                resetInactivityTimer();
            }
        });
        multiplayer.GameFrame.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent e) {
                resetInactivityTimer();
            }
        });
    }

    private void resetInactivityTimer() {
        if (multiplayer.inactivityTimer != null) {
            multiplayer.inactivityTimer.restart();
        }
    }

    // --- Create Game Window ---
    public JFrame createGameWindow() {
        // Kontrola připojení při otevření okna
        if (!multiplayer.getConnection().isConnected()) {
            JOptionPane.showMessageDialog(null,
                    "Lost connection to server. Returning to the main window.",
                    "Connection Lost", JOptionPane.WARNING_MESSAGE);
            multiplayer.disconnect();
            return null;
        }

        // Inicializace hlavního okna hry
        multiplayer.GameFrame = new JFrame(Constants.MultG_title + " " + multiplayer.getClient().getName());
        multiplayer.GameFrame.setMinimumSize(new Dimension(Constants.MultW_WIDTH, Constants.MultW_HEIGHT));
        multiplayer.GameFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        multiplayer.GameFrame.setLocationRelativeTo(null);
        multiplayer.GameFrame.setLayout(new BorderLayout());

        // Nastavení okna na celou obrazovku
        multiplayer.GameFrame.setExtendedState(JFrame.MAXIMIZED_BOTH);

        // Panel pro zobrazení jmen hráčů a ID hry
        JPanel infoPanel = new JPanel(new BorderLayout());
        infoPanel.setBackground(Color.LIGHT_GRAY);
        infoPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JLabel whitePlayerLabel = new JLabel("White: " + multiplayer.getWhitePlayer().getName(), SwingConstants.LEFT);
        JLabel blackPlayerLabel = new JLabel("Black: " + multiplayer.getBlackPlayer().getName(), SwingConstants.LEFT);
        JLabel gameIDLabel = new JLabel("Game ID: " + gameID, SwingConstants.CENTER);

        whitePlayerLabel.setOpaque(true);
        blackPlayerLabel.setOpaque(true);

        whitePlayerLabel.setBackground(Color.WHITE);
        blackPlayerLabel.setBackground(Color.GRAY);

        infoPanel.add(whitePlayerLabel, BorderLayout.WEST);
        infoPanel.add(blackPlayerLabel, BorderLayout.EAST);
        infoPanel.add(gameIDLabel, BorderLayout.SOUTH);

        multiplayer.GameFrame.add(infoPanel, BorderLayout.NORTH);

        game = new Game(gameID, multiplayer.getWhitePlayer(), multiplayer.getBlackPlayer(), multiplayer.chessboard);
        multiplayer.chessboard.setGame(game);
        multiplayer.getClient().setGame(game);
        multiplayer.getWhitePlayer().setGame(game);
        multiplayer.getBlackPlayer().setGame(game);
        multiplayer.GameFrame.add(multiplayer.chessboard, BorderLayout.CENTER);

        // Tlačítko Zpět
        JButton LeaveButton = new JButton(Constants.Leave_title);
        multiplayer.GameFrame.add(LeaveButton, BorderLayout.SOUTH);

        LeaveButton.addActionListener(e -> {
            int confirm = JOptionPane.showConfirmDialog(
                    multiplayer.GameFrame,
                    "Are you sure you want to leave the game?",
                    "Confirm Leave",
                    JOptionPane.YES_NO_OPTION
            );

            if (confirm == JOptionPane.YES_OPTION) {
                createReconnectWindow();
            }
        });

        // Nastavení viditelnosti
        multiplayer.GameFrame.setVisible(true);

        startInactivityTimer();

        return multiplayer.GameFrame;
    }

    // --- Reconnect Window ---
    private void createReconnectWindow() {
        if (multiplayer.inactivityTimer != null) {
            multiplayer.inactivityTimer.stop(); // Zastavení časovače při otevření okna
        }
        JFrame reconnectFrame = new JFrame("Reconnect");
        reconnectFrame.setMinimumSize(new Dimension(500, 400));
        reconnectFrame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        reconnectFrame.setLocationRelativeTo(null);
        multiplayer.getConnection().sendMessage(new Sever_Stop_Game(gameID));
        multiplayer.DONT_MOVE = true;

        JPanel panel = new JPanel(new BorderLayout());
        JLabel label = new JLabel("Do you want to reconnect to the game or leave completely?", SwingConstants.CENTER);
        panel.add(label, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new FlowLayout());

        // Tlačítko pro znovupřipojení do hry
        JButton reconnectButton = new JButton("Reconnect to Game");
        buttonPanel.add(reconnectButton);

        // Tlačítko pro odpojení a vytvoření nového lobby
        JButton leaveButton = new JButton("Leave and Create New Lobby");
        buttonPanel.add(leaveButton);

        panel.add(buttonPanel, BorderLayout.SOUTH);
        reconnectFrame.add(panel);

        // Akce při kliknutí na tlačítko Reconnect
        reconnectButton.addActionListener(e -> {
            if (multiplayer.getConnection().isConnected()) {
                reconnectFrame.setVisible(false);
                reconnectFrame.dispose();
                multiplayer.getConnection().sendMessage(new Client_Reconnect_Game(gameID));
                multiplayer.DONT_MOVE = false;
            } else {
                JOptionPane.showMessageDialog(reconnectFrame, "Reconnect failed. Returning to main menu.");
                multiplayer.disconnect();
            }
        });

        // Akce při kliknutí na tlačítko Leave
        leaveButton.addActionListener(e -> {
            multiplayer.DONT_MOVE = false;
            multiplayer.getConnection().sendMessage(new Client_Leave_Game(multiplayer.getClient()));
            multiplayer.getClient().setReady(false);
            multiplayer.GameFrame.setVisible(false);
            multiplayer.GameFrame.dispose();

            reconnectFrame.dispose();
            multiplayer.LobbyFrame.setVisible(true);
            game = null;
            multiplayer.chessboard = null;
        });

        reconnectFrame.setVisible(true);
    }
}
