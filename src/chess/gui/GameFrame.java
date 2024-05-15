package chess.gui;

import chess.game.Board;

import javax.swing.*;
import java.awt.*;

public class GameFrame extends JFrame {
    public GameFrame() {
        setTitle("Chess Game");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        getContentPane().setBackground(Color.darkGray);
        setLayout(new GridBagLayout());
        setMinimumSize(new Dimension(Board.fieldSize * Board.files + 18, Board.fieldSize * Board.files + 42));
        setLocationRelativeTo(null);

        Board board = new Board(Board.startFen);

        add(board);
        setVisible(true);
        SoundEffects.playStart();
    }
}
