package Windows;

import Connection.Multiplayer;
import Constants.Constants;
import Message.Client_Is_Ready;
import Message.Client_Is_Unready;
import Message.Client_Logout;

import javax.swing.*;
import java.awt.*;

public class LobbyWindow {
    private final Multiplayer multiplayer;

    // Konstruktor pro inicializaci lobby okna s odkazem na multiplayer objekt
    public LobbyWindow(Multiplayer multiplayer) {
        this.multiplayer = multiplayer;
    }

    // --- Vytvoření lobby okna ---
    public JFrame createLobbyWindow() {
        // Vytvoření hlavního okna lobby
        multiplayer.LobbyFrame = new JFrame(Constants.Lobby_title);  // Nastavení názvu okna
        multiplayer.LobbyFrame.setMinimumSize(new Dimension(Constants.MainW_WIDTH, Constants.MainW_HEIGHT));  // Nastavení minimální velikosti
        multiplayer.LobbyFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);  // Uzavření aplikace při zavření okna
        multiplayer.LobbyFrame.setLocationRelativeTo(null);  // Okno se otevře ve středu obrazovky
        multiplayer.LobbyFrame.setLayout(new BorderLayout());  // Nastavení layoutu pro BorderLayout

        // Kontrola připojení při otevření lobby okna
        if (!multiplayer.getConnection().isConnected()) {
            // Zobrazení varování, pokud není připojeno k serveru
            JOptionPane.showMessageDialog(multiplayer.LobbyFrame,
                    "Lost connection to server. Returning to the main window.",
                    "Connection Lost", JOptionPane.WARNING_MESSAGE);
            multiplayer.disconnect();  // Odpojení od serveru
            return null;  // Přerušení načítání lobby okna
        }

        // Vytvoření panelu pro hráče (info o hráči, stav atd.)
        JPanel playerPanel = createPlayerPanel();
        multiplayer.LobbyFrame.add(playerPanel, BorderLayout.CENTER);  // Přidání panelu do středu okna

        // Tlačítko Zpět
        JButton backButton = new JButton(Constants.BackButton_title);  // Tlačítko pro návrat na přihlašovací obrazovku
        backButton.setFont(new Font("Arial", Font.BOLD, 14));
        backButton.setBackground(new Color(220, 53, 69));  // Červené tlačítko
        backButton.setForeground(Color.WHITE);
        backButton.setFocusPainted(false);
        backButton.setBorder(BorderFactory.createEmptyBorder(5, 15, 5, 15));
        multiplayer.LobbyFrame.add(backButton, BorderLayout.SOUTH);  // Přidání tlačítka do dolní části okna

        // Akce při kliknutí na tlačítko Zpět
        backButton.addActionListener(e -> {
            // Odeslání zprávy pro odhlášení
            multiplayer.getConnection().sendMessage(new Client_Logout());
            multiplayer.LobbyFrame.setVisible(false);  // Skrytí lobby okna
            multiplayer.LobbyFrame.dispose();  // Uvolnění prostředků spojených s oknem
            multiplayer.loginFrame.setVisible(true);  // Zobrazení přihlašovací obrazovky
        });

        multiplayer.LobbyFrame.setVisible(true);  // Zobrazení okna
        return multiplayer.LobbyFrame;  // Vrácení vytvořeného okna
    }

    // Metoda pro vytvoření panelu pro hráče (zobrazení informace o hráči)
    private JPanel createPlayerPanel() {
        JPanel playerPanel = new JPanel();
        playerPanel.setLayout(new GridBagLayout());  // Použití GridBagLayout pro flexibilní umístění komponent
        playerPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));  // Nastavení okrajů panelu

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;  // Vyplnění celé šířky
        gbc.insets = new Insets(10, 10, 10, 10);  // Nastavení mezery mezi komponentami

        // Role hráče (Bílý nebo Černý)
        JLabel roleLabel = new JLabel("Role: " + (multiplayer.getClient().isWhite() ? "White" : "Black"));
        roleLabel.setFont(new Font("Serif", Font.BOLD, 18));  // Nastavení fontu
        roleLabel.setHorizontalAlignment(SwingConstants.CENTER);  // Zarovnání na střed
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;  // Šířka 2 sloupce
        playerPanel.add(roleLabel, gbc);  // Přidání komponenty do panelu

        // Jméno hráče
        JLabel playerLabel = new JLabel("Player: " + multiplayer.getClient().getName());
        playerLabel.setFont(new Font("Serif", Font.BOLD, 18));
        playerLabel.setHorizontalAlignment(SwingConstants.CENTER);
        gbc.gridy = 1;
        playerPanel.add(playerLabel, gbc);

        // Tlačítko pro změnu stavu Ready/Not Ready
        JButton readyButton = new JButton("Ready");  // Tlačítko pro přepnutí stavu "Připraven/Ne připraven"
        readyButton.setFont(new Font("Arial", Font.BOLD, 14));
        readyButton.setBackground(new Color(0, 123, 255));  // Modré tlačítko
        readyButton.setForeground(Color.WHITE);
        readyButton.setFocusPainted(false);
        readyButton.setBorder(BorderFactory.createEmptyBorder(5, 15, 5, 15));

        // Akce při kliknutí na tlačítko Ready/Not Ready
        readyButton.addActionListener(e -> {
            // Přepínání stavu Ready/Not Ready
            boolean isReady = multiplayer.getClient().toggleReadyStatus();
            readyButton.setText(isReady ? "Not Ready" : "Ready");  // Změna textu tlačítka

            // Odesílání zprávy na server o aktuálním stavu
            try {
                if (isReady) {
                    multiplayer.getConnection().sendMessage(new Client_Is_Ready(isReady));
                } else {
                    multiplayer.getConnection().sendMessage(new Client_Is_Unready(isReady));
                }
            } catch (Exception ex) {
                // Ošetření chyby při odesílání zprávy
                JOptionPane.showMessageDialog(multiplayer.LobbyFrame,
                        "Failed to update status. Please try again.",
                        "Error", JOptionPane.ERROR_MESSAGE);
                multiplayer.disconnect();  // Odpojení od serveru
            }
        });

        gbc.gridy = 2;
        playerPanel.add(readyButton, gbc);

        return playerPanel;  // Vrácení panelu hráče
    }

}
