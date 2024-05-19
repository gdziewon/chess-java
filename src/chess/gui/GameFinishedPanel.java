package chess.gui;

import chess.game.Pieces;

import javax.swing.*;
import java.awt.*;

public class GameFinishedPanel extends JPanel {
    public GameFinishedPanel(JFrame owner, String result) {
        setLayout(new BorderLayout());
        JLabel resultLabel = new JLabel(result, SwingConstants.CENTER);
        resultLabel.setFont(new Font("Serif", Font.BOLD, 20));
        add(resultLabel, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new FlowLayout());
        JButton rematchButton = new JButton("Rematch");
        JButton quitButton = new JButton("Quit");

        rematchButton.addActionListener(e -> {
            owner.dispose();
            SwingUtilities.invokeLater(GameFrame::new); // start a new game
        });

        quitButton.addActionListener(e -> System.exit(0)); // exit the game

        buttonPanel.add(rematchButton);
        buttonPanel.add(quitButton);
        add(buttonPanel, BorderLayout.SOUTH);
    }

    public static void displayEndGame(String result) {
        JFrame endGameFrame = new JFrame("Game Over");
        endGameFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        endGameFrame.setSize(300, 200);
        endGameFrame.setLocationRelativeTo(null);
        endGameFrame.add(new GameFinishedPanel(endGameFrame, result));
        endGameFrame.setVisible(true);
    }

    public static void displayCheckmate(int color) {
        SoundEffects.playSound(SoundEffects.CHECKMATE);
        displayEndGame(color == Pieces.White ? "Checkmate! Black wins!" : "Checkmate! White wins!");
    }

    public static void displayStalemate() {
        SoundEffects.playSound(SoundEffects.STALEMATE);
        displayEndGame("Stalemate! It's a draw!");
    }
}
