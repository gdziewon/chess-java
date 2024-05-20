package chess.gui;

import chess.game.Board;
import static chess.game.Board.*;
import static chess.gui.SoundEffects.*;

import javax.swing.*;
import java.awt.*;

public class GameFrame extends JFrame {
    public GameFrame() {
        setTitle("Chess Game");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        getContentPane().setBackground(Color.darkGray);
        setLayout(new GridBagLayout());
        setMinimumSize(new Dimension(FIELD_SIZE * FILES + 18, FIELD_SIZE * FILES + 42));
        setLocationRelativeTo(null);

        Board board = new Board(Board.START_FEN);
        GameFinishedPanel.gameFrame = this;

        add(board);
        setVisible(true);
        playSound(START_SOUND);
    }
}
