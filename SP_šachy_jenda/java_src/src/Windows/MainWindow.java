package Windows;

import Connection.Multiplayer;
import Constants.Constants;

import javax.swing.*;
import java.awt.*;

public class MainWindow {

    static JFrame mainWindow;
    static String serverAdress;
    static int serverPort;

    // Konstruktor pro inicializaci adresy serveru a portu
    public MainWindow(String serverAdress, int serverPort) {
        MainWindow.serverAdress = serverAdress;
        MainWindow.serverPort = serverPort;
    }

    // Vytvoření hlavního okna
    public JFrame createMainWindow() {
        mainWindow = new JFrame("Chess Game");  // Vytvoření okna pro šachy
        mainWindow.setSize(new Dimension(Constants.MainW_WIDTH, Constants.MainW_HEIGHT));  // Nastavení velikosti okna
        mainWindow.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);  // Ukončení aplikace při zavření okna
        mainWindow.setLocationRelativeTo(null);  // Umístění okna na střed obrazovky

        JPanel mainPanel = new JPanel(new BorderLayout());  // Panel pro hlavní obsah okna
        mainWindow.setContentPane(mainPanel);  // Nastavení hlavního panelu

        JLabel backgroundLabel = createBackgroundLabel();  // Vytvoření pozadí s obrázkem
        backgroundLabel.setLayout(new BorderLayout());  // Nastavení layoutu pro pozadí
        mainPanel.add(backgroundLabel, BorderLayout.CENTER);  // Přidání pozadí do hlavního panelu

        // Nastavení titulku pro hru
        JLabel titleLabel = new JLabel("Chess Game");
        titleLabel.setFont(new Font("Serif", Font.BOLD, 48));  // Nastavení fontu
        titleLabel.setForeground(Color.WHITE);  // Bílé písmo
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);  // Horizontální zarovnání
        backgroundLabel.add(titleLabel, BorderLayout.NORTH);  // Přidání titulku na pozadí

        JPanel buttonPanel = new JPanel(new GridBagLayout());  // Panel pro tlačítka
        buttonPanel.setOpaque(false);  // Nastavení transparentnosti panelu
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 0, 10, 0);  // Nastavení mezer mezi tlačítky

        // Vytvoření tlačítek
        JButton singleplayerButton = createButton(Constants.Singleplayer_button_name);  // Tlačítko pro singleplayer
        JButton multiplayerButton = createButton(Constants.Multiplayer_button_name);  // Tlačítko pro multiplayer
        JButton exitButton = createButton(Constants.Exit_button_name);  // Tlačítko pro exit

        // Umístění tlačítek do panelu
        gbc.gridx = 0;
        gbc.gridy = 0;
        buttonPanel.add(singleplayerButton, gbc);

        gbc.gridy = 1;
        buttonPanel.add(multiplayerButton, gbc);

        gbc.gridy = 2;
        buttonPanel.add(exitButton, gbc);

        backgroundLabel.add(buttonPanel, BorderLayout.CENTER);  // Přidání tlačítek na pozadí

        // Definování akcí pro jednotlivá tlačítka
        handleSingleplayerAction(singleplayerButton);
        handleMultiplayerAction(multiplayerButton);
        handleExitAction(exitButton);

        mainWindow.setVisible(true);  // Zobrazení hlavního okna
        return mainWindow;  // Vrácení hlavního okna
    }

    // Akce pro multiplayer tlačítko
    private static void handleMultiplayerAction(JButton multiplayerButton) {
        multiplayerButton.addActionListener(e -> {
            // Pokud je tlačítko stisknuto, vytvoří se objekt Multiplayer a připojí se k serveru
            Multiplayer multiplayer = new Multiplayer(mainWindow, serverAdress, serverPort);

            if (multiplayer.getConnection().isConnected()) {  // Pokud je připojeno k serveru
                mainWindow.setVisible(false);  // Skrytí hlavního okna
                LoginWindow loginWindow = new LoginWindow(multiplayer);  // Vytvoření okna pro přihlášení
                multiplayer.loginFrame = loginWindow.createLoginWindow();  // Vytvoření přihlašovacího okna
                multiplayer.loginFrame.setVisible(true);  // Zobrazení přihlašovacího okna
            }
        });
    }

    // Vytvoření pozadí pro okno
    private static JLabel createBackgroundLabel() {
        JLabel backgroundLabel = new JLabel();
        backgroundLabel.setBackground(Color.ORANGE);  // Nastavení pozadí na oranžovou
        ImageIcon gifIcon = new ImageIcon(System.getProperty("user.dir") + "/img/chess.gif");
        backgroundLabel.setIcon(gifIcon);  // Nastavení GIF jako pozadí
        backgroundLabel.setBounds(0, 0, Constants.MainW_WIDTH, Constants.MainW_HEIGHT);  // Nastavení velikosti pozadí
        backgroundLabel.setHorizontalAlignment(SwingConstants.CENTER);  // Zarovnání na střed
        backgroundLabel.setVerticalAlignment(SwingConstants.CENTER);  // Vertikální zarovnání na střed
        return backgroundLabel;  // Vrácení labelu s pozadím
    }

    // Vytvoření tlačítka s daným textem
    private static JButton createButton(String text) {
        JButton button = new JButton(text);  // Vytvoření tlačítka s textem
        button.setPreferredSize(new Dimension(Constants.ReadyB_WIDTH, Constants.ReadyB_HEIGHT));  // Nastavení velikosti tlačítka
        return button;  // Vrácení tlačítka
    }

    // Akce pro tlačítko Exit
    private static void handleExitAction(JButton exitButton) {
        exitButton.addActionListener(e -> System.exit(0));  // Ukončení aplikace
    }

    // Akce pro tlačítko Singleplayer
    private static void handleSingleplayerAction(JButton singleplayerButton) {
        singleplayerButton.addActionListener(e -> {
            mainWindow.setVisible(false);  // Skrytí hlavního okna
            SingleplayerWindow sWindow = new SingleplayerWindow();  // Vytvoření okna pro singleplayer
            sWindow.createSingleplayerWindow(mainWindow);
        });
    }
}
