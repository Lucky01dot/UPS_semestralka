package Windows;

import Constants.Constants;
import utils.Chessboard;

import javax.swing.*;
import java.awt.*;

public class SingleplayerWindow {

    static JFrame mainWindow;
    SingleplayerWindow(JFrame mainWindow){
        SingleplayerWindow.mainWindow = mainWindow;
        createSingleplayerWindow(mainWindow);
    }
    private static void createSingleplayerWindow(JFrame sGame) {
        JFrame frame = new JFrame(Constants.SingG_title);
        frame.setMinimumSize(new Dimension(Constants.SingW_WIDTH, Constants.SingW_HEIGHT));
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLocationRelativeTo(null);
        frame.setLayout(new BorderLayout());

        Chessboard board = new Chessboard(null,new Multiplayer(mainWindow,"",0));
        frame.add(board, BorderLayout.CENTER);

        JButton backButton = new JButton(Constants.BackButton_title);
        frame.add(backButton, BorderLayout.SOUTH);

        backButton.addActionListener(e -> {
            frame.setVisible(false);
            sGame.setVisible(true);
        });

        frame.setVisible(true);
    }
}
