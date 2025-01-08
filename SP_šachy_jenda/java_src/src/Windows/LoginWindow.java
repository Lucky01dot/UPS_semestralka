package Windows;

import Connection.Multiplayer;
import Constants.Constants;
import Message.Client_Login;

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

    // Konstruktor pro inicializaci objektu LoginWindow s odkazem na multiplayer
    public LoginWindow(Multiplayer multiplayer) {
        this.multiplayer = multiplayer;
    }

    // Metoda pro inicializaci UI komponent pro přihlášení
    private void initializeUIComponents() {
        nameOfGame = new Label(Constants.MultG_title);  // Název hry
        loginLabel = new Label(Constants.loginName);    // Popis pro zadání jména
        loginButton = new Button(Constants.buttonLogin);  // Tlačítko pro přihlášení
        enterName = new TextField();  // Textové pole pro zadání jména hráče

        // Přidání posluchače pro TextField, aby bylo možné zachytit text
        enterName.addTextListener(e -> playerName = enterName.getText());

        // Volitelné: Odstranění mezer na začátku a konci jména
        enterName.addActionListener(e -> playerName = enterName.getText().trim());
    }

    // Vytvoření okna pro přihlášení
    public JFrame createLoginWindow() {
        // Inicializace hlavního okna pro přihlášení
        multiplayer.loginFrame = new JFrame(Constants.Login_title);
        multiplayer.loginFrame.setMinimumSize(new Dimension(Constants.MainW_WIDTH, Constants.MainW_HEIGHT));  // Nastavení minimální velikosti okna
        multiplayer.loginFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);  // Ukončení aplikace při zavření okna
        multiplayer.loginFrame.setLocationRelativeTo(null);  // Umístění okna na střed obrazovky
        multiplayer.loginFrame.setLayout(new BorderLayout());  // Použití BorderLayoutu pro rozložení komponent
        initializeUIComponents();  // Inicializace komponent

        // Vytvoření panelu pro přihlášení
        JPanel loginPanel = new JPanel();
        loginPanel.setLayout(new GridBagLayout());  // Použití GridBagLayout pro flexibilní umístění komponent
        loginPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));  // Nastavení okrajů panelu

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;  // Vyplnění šířky komponenty
        gbc.insets = new Insets(10, 10, 10, 10);  // Nastavení mezer mezi komponentami

        // Label pro zadání jména
        JLabel loginLabel = new JLabel("Enter your name:");  // Popis pro jméno
        loginLabel.setFont(new Font("Arial", Font.BOLD, 16));  // Nastavení fontu
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;  // Šířka komponenty na 2 sloupce
        loginPanel.add(loginLabel, gbc);  // Přidání do panelu

        // Textové pole pro zadání jména
        JTextField enterName = new JTextField();
        enterName.setFont(new Font("Arial", Font.PLAIN, 14));  // Nastavení fontu
        enterName.setPreferredSize(new Dimension(200, 30));  // Nastavení preferované velikosti
        gbc.gridy = 1;
        gbc.gridwidth = 2;
        loginPanel.add(enterName, gbc);  // Přidání textového pole

        // Tlačítko pro přihlášení
        JButton loginButton = new JButton("Login");  // Tlačítko pro přihlášení
        loginButton.setFont(new Font("Arial", Font.BOLD, 14));  // Nastavení fontu
        loginButton.setBackground(new Color(0, 123, 255));  // Modrá barva pozadí
        loginButton.setForeground(Color.WHITE);  // Bílé písmo
        loginButton.setFocusPainted(false);  // Zamezení změně vzhledu při zaměření
        loginButton.setBorder(BorderFactory.createEmptyBorder(5, 15, 5, 15));  // Nastavení okrajů
        gbc.gridy = 2;
        gbc.gridwidth = 1;  // Šířka 1 sloupec
        gbc.gridx = 0;
        loginPanel.add(loginButton, gbc);  // Přidání tlačítka

        // Tlačítko pro návrat zpět
        JButton backButton = new JButton("Back");
        backButton.setFont(new Font("Arial", Font.BOLD, 14));  // Nastavení fontu
        backButton.setBackground(new Color(220, 53, 69));  // Červená barva pozadí
        backButton.setForeground(Color.WHITE);  // Bílé písmo
        backButton.setFocusPainted(false);  // Zamezení změně vzhledu při zaměření
        backButton.setBorder(BorderFactory.createEmptyBorder(5, 15, 5, 15));  // Nastavení okrajů
        gbc.gridx = 1;
        loginPanel.add(backButton, gbc);  // Přidání tlačítka

        // Přidání panelu do hlavního okna
        multiplayer.loginFrame.add(loginPanel, BorderLayout.CENTER);

        // Akce tlačítka "Back"
        backButton.addActionListener(e -> {
            // Skrytí přihlašovacího okna a zobrazení hlavního okna
            multiplayer.loginFrame.setVisible(false);
            multiplayer.loginFrame.dispose();
            multiplayer.mainWindow.setVisible(true);
            multiplayer.disconnect();  // Odpojení od serveru
        });

        // Akce tlačítka "Login"
        loginButton.addActionListener(e -> {
            // Získání jména hráče z textového pole
            String playerName = enterName.getText().trim();
            if (!playerName.isEmpty()) {
                try {
                    // Odeslání přihlašovací zprávy na server
                    multiplayer.getConnection().sendMessage(new Client_Login(playerName));
                    multiplayer.getClient().setName(playerName);  // Nastavení jména klienta
                } catch (Exception ex) {
                    // Ošetření chyby při přihlášení
                    JOptionPane.showMessageDialog(multiplayer.loginFrame,
                            "An error occurred while trying to login. Please try again later.",
                            "Login Error", JOptionPane.ERROR_MESSAGE);
                    multiplayer.loginFrame.dispose();
                    multiplayer.mainWindow.setVisible(true);
                }
            } else {
                // Zobrazení chybové zprávy, pokud jméno není zadáno
                JOptionPane.showMessageDialog(multiplayer.loginFrame, "Please enter your name first.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        multiplayer.loginFrame.setVisible(true);  // Zobrazení přihlašovacího okna
        return multiplayer.loginFrame;  // Vrácení přihlašovacího okna
    }
}
