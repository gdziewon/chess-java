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
        setMinimumSize(new Dimension(Board.FIELD_SIZE * Board.FILES + 18, Board.FIELD_SIZE * Board.FILES + 42));
        setLocationRelativeTo(null);

        Board board = new Board(Board.START_FEN);

        add(board);
        setVisible(true);
        SoundEffects.playSound(SoundEffects.START);
    }
}
