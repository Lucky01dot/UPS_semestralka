package Windows;

import Constants.Constants;
import utils.Chessboard;

import javax.swing.*;
import java.awt.*;

public class SingleplayerWindow {

    public void createSingleplayerWindow(JFrame sGame) {
        // Vytvoření okna pro singleplayer hru
        JFrame frame = new JFrame(Constants.SingG_title); // Titulek okna
        frame.setMinimumSize(new Dimension(Constants.SingW_WIDTH, Constants.SingW_HEIGHT)); // Minimální velikost okna
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // Akce při zavření okna
        frame.setLocationRelativeTo(null); // Umístění okna na střed obrazovky
        frame.setLayout(new BorderLayout()); // Použití BorderLayout pro rozložení komponent

        frame.setExtendedState(JFrame.MAXIMIZED_BOTH); // Nastavení okna na celou obrazovku

        // Vytvoření šachovnice (Chessboard)
        Chessboard board = new Chessboard(null, null); // Předání 'null' parametrů (možná bude potřeba upravit)
        frame.add(board, BorderLayout.CENTER); // Přidání šachovnice do středu okna

        // Vytvoření tlačítka pro návrat zpět
        JButton backButton = new JButton(Constants.BackButton_title); // Tlačítko pro návrat
        frame.add(backButton, BorderLayout.SOUTH); // Tlačítko je umístěno ve spodní části okna

        // Akce při kliknutí na tlačítko 'Back'
        backButton.addActionListener(e -> {
            frame.setVisible(false); // Skrytí aktuálního okna
            frame.dispose(); // Uvolnění prostředků spojených s oknem
            sGame.setVisible(true); // Zobrazení původního okna
        });

        frame.setVisible(true); // Nastavení okna jako viditelné
    }
}
