package chess.gui;

import chess.game.Piece;
import chess.game.Pieces;

import javax.swing.*;
import java.awt.*;

public class PromotionPanel extends JDialog {
    private int chosenPiece = Pieces.None;

    public PromotionPanel(JFrame owner, boolean isWhite) {
        super(owner, "Promote Pawn", true);
        setLayout(new GridLayout(1, 4));
        setModal(true);
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE); // disable close operation
        int baseValue = isWhite ? Pieces.White : Pieces.Black;

        // buttons for promotion options
        addPieceButton(Pieces.Queen | baseValue);
        addPieceButton(Pieces.Rook | baseValue);
        addPieceButton(Pieces.Bishop | baseValue);
        addPieceButton(Pieces.Knight | baseValue);

        pack();
        setLocationRelativeTo(owner);
        setVisible(true);
    }

    private void addPieceButton(int pieceValue) {
        JButton button = new JButton(new ImageIcon(getPieceImage(pieceValue)));
        button.addActionListener(e -> {
            chosenPiece = pieceValue;
            setVisible(false);
            dispose();
        });
        add(button);
    }

    private Image getPieceImage(int value) {
        return Piece.sheet.getSubimage(((value - 1) & 0b111) * Piece.sheetScale, (value >> 4) == 0 ? 0 : Piece.sheetScale, Piece.sheetScale, Piece.sheetScale).getScaledInstance(64, 64, Image.SCALE_SMOOTH);
    }

    public int getChosenPiece() {
        return chosenPiece;
    }
}
