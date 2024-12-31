package Windows;

import Constants.Constants;
import Message.Client_Is_Ready;
import Message.Client_Is_Unready;
import Message.Client_Logout;

import javax.swing.*;
import java.awt.*;

public class LobbyWindow {
    private final Multiplayer multiplayer;

    LobbyWindow(Multiplayer multiplayer) {
        this.multiplayer = multiplayer;
    }

    // --- Create Lobby Window ---
    public JFrame createLobbyWindow() {
        multiplayer.LobbyFrame = new JFrame(Constants.Lobby_title);
        multiplayer.LobbyFrame.setMinimumSize(new Dimension(Constants.MainW_WIDTH, Constants.MainW_HEIGHT));
        multiplayer.LobbyFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        multiplayer.LobbyFrame.setLocationRelativeTo(null);
        multiplayer.LobbyFrame.setLayout(new BorderLayout());

        // Kontrola připojení při otevření lobby okna
        if (!multiplayer.getConnection().isConnected()) {
            JOptionPane.showMessageDialog(multiplayer.LobbyFrame,
                    "Lost connection to server. Returning to the main window.",
                    "Connection Lost", JOptionPane.WARNING_MESSAGE);
            multiplayer.disconnect();
            return null; // Přerušení načítání lobby okna
        }

        // Vytvoření panelu hráče
        JPanel playerPanel = createPlayerPanel();
        multiplayer.LobbyFrame.add(playerPanel, BorderLayout.CENTER);

        // Tlačítko Zpět
        JButton backButton = new JButton(Constants.BackButton_title);
        multiplayer.LobbyFrame.add(backButton, BorderLayout.SOUTH);

        backButton.addActionListener(e -> {

            multiplayer.getConnection().sendMessage(new Client_Logout());

            multiplayer.LobbyFrame.setVisible(false);
            multiplayer.loginFrame.setVisible(true);
        });

        multiplayer.LobbyFrame.setVisible(true);
        return multiplayer.LobbyFrame;
    }

    private JPanel createPlayerPanel() {
        JPanel playerPanel = new JPanel();
        playerPanel.setLayout(new GridLayout(3, 1, 10, 10)); // Layout pro role info

        // Role hráče
        JLabel roleLabel = new JLabel("Role: " + (multiplayer.getClient().isWhite() ? "White" : "Black"));
        roleLabel.setFont(new Font("Serif", Font.BOLD, 18));
        roleLabel.setHorizontalAlignment(SwingConstants.CENTER);

        // Jméno hráče
        JLabel playerLabel = new JLabel("Player: " + multiplayer.getClient().getName());
        playerLabel.setFont(new Font("Serif", Font.BOLD, 18));
        playerLabel.setHorizontalAlignment(SwingConstants.CENTER);

        // Tlačítko pro změnu stavu Ready/Not Ready
        JButton readyButton = new JButton("Ready");
        readyButton.addActionListener(e -> {

            // Přepínání stavu Ready/Not Ready
            boolean isReady = multiplayer.getClient().toggleReadyStatus();
            readyButton.setText(isReady ? "Not Ready" : "Ready");

            // Odesílání zprávy na server
            try {
                if (isReady) {
                    multiplayer.getConnection().sendMessage(new Client_Is_Ready(isReady));
                } else {
                    multiplayer.getConnection().sendMessage(new Client_Is_Unready(isReady));
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(multiplayer.LobbyFrame,
                        "Failed to update status. Please try again.",
                        "Error", JOptionPane.ERROR_MESSAGE);
                multiplayer.disconnect();
            }
        });

        playerPanel.add(roleLabel);
        playerPanel.add(playerLabel);
        playerPanel.add(readyButton);

        return playerPanel;
    }
}
