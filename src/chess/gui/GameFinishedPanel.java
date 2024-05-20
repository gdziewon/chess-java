package chess.gui;

import static chess.game.Pieces.*;
import static chess.gui.SoundEffects.*;

import javax.swing.*;
import java.awt.*;

public class GameFinishedPanel extends JPanel {
    public static GameFrame gameFrame;

    public static final String INSUFFICIENT_MATERIAL = " Insufficient material!";
    public static final String THREEFOLD_REPETITION = " Threefold repetition!";
    public static final String FIFTY_MOVE_RULE = " Fifty-move rule!";
    public static final String STALEMATE = " Stalemate!";

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
            gameFrame.dispose();
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
        playSound(CHECKMATE_SOUND);
        displayEndGame(color == White ? "Checkmate! Black wins!" : "Checkmate! White wins!");
    }

    public static void displayTimeOut(int color) {
        playSound(CHECKMATE_SOUND);
        displayEndGame(color == White ? "Time out! Black wins!" : "Time out! White wins!");
    }

    public static void displayDraw(String cause) {
        playSound(STALEMATE_SOUND);
        displayEndGame(cause + " It's a draw!");
    }
}
