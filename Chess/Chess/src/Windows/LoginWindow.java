package Windows;

import Constants.Constants;
import Message.Client_Login;
import Message.Message;

import javax.swing.*;
import java.awt.*;

public class LoginWindow {
    // --- UI Components ---
    private Label nameOfGame;
    private Label loginLabel;
    private Button loginButton;
    private TextField enterName;
    private String playerName = "";
    private Multiplayer multiplayer;


    public LoginWindow(Multiplayer multiplayer) {
        this.multiplayer = multiplayer;

    }

    private void initializeUIComponents() {
        nameOfGame = new Label(Constants.MultG_title);
        loginLabel = new Label(Constants.loginName);
        loginButton = new Button(Constants.buttonLogin);
        enterName = new TextField();

        // Add a listener to the TextField to capture input
        enterName.addTextListener(e -> playerName = enterName.getText());

        // Optional: Clear any leading/trailing spaces
        enterName.addActionListener(e -> playerName = enterName.getText().trim());
    }

    // --- Create Login Window ---
    public JFrame createLoginWindow() {
        // Inicializace hlavního okna pro přihlášení
        multiplayer.loginFrame = new JFrame(Constants.Login_title);
        multiplayer.loginFrame.setMinimumSize(new Dimension(Constants.MainW_WIDTH, Constants.MainW_HEIGHT));
        multiplayer.loginFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        multiplayer.loginFrame.setLocationRelativeTo(null);
        multiplayer.loginFrame.setLayout(new BorderLayout());
        initializeUIComponents();

        // Vytvoření panelu pro login
        JPanel loginPanel = new JPanel();
        loginPanel.setLayout(new GridLayout(4, 1, 10, 10)); // 4 řádky pro komponenty

        loginLabel.setAlignment(Label.LEFT);
        loginPanel.add(loginLabel);
        loginPanel.add(enterName);
        loginPanel.add(loginButton);

        multiplayer.loginFrame.add(loginPanel, BorderLayout.CENTER);
        JButton backButton = new JButton(Constants.BackButton_title);
        multiplayer.loginFrame.add(backButton, BorderLayout.SOUTH);

        // Akce tlačítka zpět
        backButton.addActionListener(e -> {
            multiplayer.loginFrame.setVisible(false);
            multiplayer.mainWindow.setVisible(true);
            multiplayer.getConnection().closeConnection();
        });

        // Akce tlačítka login
        loginButton.addActionListener(e -> {
            if (!playerName.isEmpty()) {

                    try {
                        multiplayer.getConnection().sendMessage(new Client_Login(playerName));
                        multiplayer.getClient().setName(playerName);
                        multiplayer.loginFrame.setVisible(false);
                        LobbyWindow lobbyWindow = new LobbyWindow(multiplayer);
                        multiplayer.LobbyFrame = lobbyWindow.createLobbyWindow();
                        multiplayer.LobbyFrame.setVisible(true);
                    } catch (Exception ex) {
                        JOptionPane.showMessageDialog(multiplayer.loginFrame,
                                "An error occurred while trying to login. Please try again later.",
                                "Login Error", JOptionPane.ERROR_MESSAGE);
                        multiplayer.disconnect(); // Vrácení do hlavního okna při chybě
                    }

            } else {
                JOptionPane.showMessageDialog(multiplayer.loginFrame, "Please enter your name first.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        // Kontrola připojení při načítání okna
        if (!multiplayer.getConnection().isConnected()) {
            JOptionPane.showMessageDialog(null,
                    "Cannot connect to the server. Returning to the main window.",
                    "Connection Error", JOptionPane.ERROR_MESSAGE);
            multiplayer.disconnect();
            return null; // Pokud není připojení, okno se neotevře
        }

        multiplayer.loginFrame.setVisible(true);
        return multiplayer.loginFrame;
    }

}
