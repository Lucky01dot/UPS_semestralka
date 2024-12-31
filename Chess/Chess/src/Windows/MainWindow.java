package Windows;

import Constants.Constants;

import javax.swing.*;
import java.awt.*;

public class MainWindow {

    static JFrame mainWindow;
    static String serverAdress;
    static int serverPort;

    public MainWindow(String serverAdress, int serverPort) {
        MainWindow.serverAdress = serverAdress;
        MainWindow.serverPort = serverPort;
    }



    public JFrame createMainWindow() {
        mainWindow = new JFrame("Chess Game");
        mainWindow.setSize(new Dimension(Constants.MainW_WIDTH, Constants.MainW_HEIGHT));
        mainWindow.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        mainWindow.setLocationRelativeTo(null);

        JPanel mainPanel = new JPanel(new BorderLayout());
        mainWindow.setContentPane(mainPanel);

        JLabel backgroundLabel = createBackgroundLabel();
        backgroundLabel.setLayout(new BorderLayout());
        mainPanel.add(backgroundLabel, BorderLayout.CENTER);

        JLabel titleLabel = new JLabel("Chess Game");
        titleLabel.setFont(new Font("Serif", Font.BOLD, 48));
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        backgroundLabel.add(titleLabel, BorderLayout.NORTH);

        JPanel buttonPanel = new JPanel(new GridBagLayout());
        buttonPanel.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 0, 10, 0);

        JButton singleplayerButton = createButton(Constants.Singleplayer_button_name);
        JButton multiplayerButton = createButton(Constants.Multiplayer_button_name);
        JButton exitButton = createButton(Constants.Exit_button_name);

        gbc.gridx = 0;
        gbc.gridy = 0;
        buttonPanel.add(singleplayerButton, gbc);

        gbc.gridy = 1;
        buttonPanel.add(multiplayerButton, gbc);

        gbc.gridy = 2;
        buttonPanel.add(exitButton, gbc);

        backgroundLabel.add(buttonPanel, BorderLayout.CENTER);

        handleSingleplayerAction(singleplayerButton);
        handleMultiplayerAction(multiplayerButton);
        handleExitAction(exitButton);

        mainWindow.setVisible(true);
        return mainWindow;
    }
    private static void handleMultiplayerAction(JButton multiplayerButton) {
        multiplayerButton.addActionListener(e -> {


                Multiplayer multiplayer = new Multiplayer(mainWindow,serverAdress,serverPort);

                if(multiplayer.getConnection().isConnected()){
                    mainWindow.setVisible(false);
                    LoginWindow loginWindow = new LoginWindow(multiplayer);
                    multiplayer.loginFrame = loginWindow.createLoginWindow();
                    multiplayer.loginFrame.setVisible(true);
                }





        });
    }

    private static JLabel createBackgroundLabel() {
        JLabel backgroundLabel = new JLabel();
        backgroundLabel.setBackground(Color.ORANGE);
        ImageIcon gifIcon = new ImageIcon("img/chess.gif");
        backgroundLabel.setIcon(gifIcon);
        backgroundLabel.setBounds(0, 0, Constants.MainW_WIDTH, Constants.MainW_HEIGHT);
        backgroundLabel.setHorizontalAlignment(SwingConstants.CENTER);
        backgroundLabel.setVerticalAlignment(SwingConstants.CENTER);
        return backgroundLabel;
    }

    private static JButton createButton(String text) {
        JButton button = new JButton(text);
        button.setPreferredSize(new Dimension(Constants.ReadyB_WIDTH, Constants.ReadyB_HEIGHT));
        return button;
    }

    private static void handleExitAction(JButton exitButton) {
        exitButton.addActionListener(e -> System.exit(0));
    }

    private static void handleSingleplayerAction(JButton singleplayerButton) {
        singleplayerButton.addActionListener(e -> {
            mainWindow.setVisible(false);
            SingleplayerWindow sWindow = new SingleplayerWindow(mainWindow);
        });
    }




}
