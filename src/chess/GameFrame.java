package chess;

import javax.swing.*;
import java.awt.*;

public class GameFrame extends JFrame {
    public GameFrame() {
        setTitle("Chess Game");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        getContentPane().setBackground(Color.darkGray);
        setLayout(new GridBagLayout());
        setMinimumSize(new Dimension(1000, 1000));
        setLocationRelativeTo(null);

        Board board = new Board(Board.startFen);

        add(board);
        setVisible(true);
    }
}
