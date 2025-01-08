package Windows;

import Connection.Multiplayer;
import Constants.Constants;

import javax.swing.*;
import java.awt.*;

public class MainWindow {

    public static JFrame mainWindow;
    public static JFrame connectionWindow;


    // Konstruktor pro inicializaci adresy serveru a portu
    public MainWindow() {

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
            // Vytvoření dialogového okna
            JFrame connectionWindow = new JFrame("Connect to Server");
            connectionWindow.setSize(400, 200);
            connectionWindow.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
            connectionWindow.setLocationRelativeTo(null); // Centrování na obrazovce

            // Panel s layoutem pro lepší zarovnání
            JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
            mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10)); // Padding

            // Grid panel pro vstupy
            JPanel inputPanel = new JPanel(new GridLayout(2, 2, 10, 10));

            // Labely a textová pole
            JLabel serverAddressLabel = new JLabel("Server Address:");
            JTextField serverAddressField = new JTextField("127.0.0.1");
            JLabel portLabel = new JLabel("Port:");
            JTextField portField = new JTextField("8080");

            inputPanel.add(serverAddressLabel);
            inputPanel.add(serverAddressField);
            inputPanel.add(portLabel);
            inputPanel.add(portField);

            // Tlačítko panel
            JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
            JButton connectButton = new JButton("Connect");
            connectButton.setPreferredSize(new Dimension(100, 30));
            buttonPanel.add(connectButton);

            // Přidání panelů do hlavního panelu
            mainPanel.add(inputPanel, BorderLayout.CENTER);
            mainPanel.add(buttonPanel, BorderLayout.SOUTH);

            connectionWindow.add(mainPanel);
            connectionWindow.setVisible(true);

            // Akce po stisknutí tlačítka "Connect"
            connectButton.addActionListener(ev -> {
                String serverAddress = serverAddressField.getText();
                int port;

                try {
                    port = Integer.parseInt(portField.getText());
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(connectionWindow, "Invalid port number.", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                // Vytvoření objektu Multiplayer s adresou a portem
                Multiplayer multiplayer = new Multiplayer(mainWindow, serverAddress, port);
                if (multiplayer.getConnection().isConnected()) {  // Pokud je připojeno k serveru
                    connectionWindow.dispose();  // Zavření okna pro zadání připojení
                    mainWindow.setVisible(false);  // Skrytí hlavního okna
                    LoginWindow loginWindow = new LoginWindow(multiplayer);  // Vytvoření okna pro přihlášení
                    multiplayer.loginFrame = loginWindow.createLoginWindow();  // Vytvoření přihlašovacího okna
                    multiplayer.loginFrame.setVisible(true);  // Zobrazení přihlašovacího okna
                } else {
                    JOptionPane.showMessageDialog(connectionWindow, "Failed to connect to server.", "Connection Error", JOptionPane.ERROR_MESSAGE);
                }
            });
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
