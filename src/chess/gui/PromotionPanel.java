package chess.gui;

import chess.game.Piece;
import static chess.game.Pieces.*;

import javax.swing.*;
import java.awt.*;

public class PromotionPanel extends JDialog {
    private int chosenPiece = None;

    public PromotionPanel(JFrame owner, boolean isWhite) {
        super(owner, "Promote Pawn", true);
        setLayout(new GridLayout(1, 4));
        setModal(true);
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE); // disable close operation
        int baseValue = isWhite ? White : Black;

        // buttons for promotion options
        addPieceButton(Queen | baseValue);
        addPieceButton(Rook | baseValue);
        addPieceButton(Bishop | baseValue);
        addPieceButton(Knight | baseValue);

        pack();
        setLocationRelativeTo(owner);
        setVisible(true);
    }

    private void addPieceButton(int pieceValue) {
        JButton button = new JButton(new ImageIcon(Piece.getSprite(pieceValue).getScaledInstance(64, 64, Image.SCALE_SMOOTH)));
        button.addActionListener(e -> {
            chosenPiece = pieceValue;
            setVisible(false);
            dispose();
        });
        add(button);
    }

    public int getChosenPiece() {
        return chosenPiece;
    }
}
