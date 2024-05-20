package chess.gui;

import chess.game.Board;
import static chess.game.Board.*;
import chess.game.Move;
import chess.game.Piece;

import javax.swing.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class Input extends MouseAdapter {

    final Board board;

    public Input(Board board) {
        this.board = board;
    }

    @Override
    public void mousePressed(MouseEvent e) {
        Piece piece = getPiece(e.getX() / FIELD_SIZE, e.getY() / FIELD_SIZE);
        if (piece != null && piece.isColor(colorToMove))
            selectedPiece = piece;

    }

    @Override
    public void mouseReleased(MouseEvent e) {
        if (selectedPiece != null) {
            Move move = new Move(selectedPiece, e.getX() / FIELD_SIZE, e.getY() / FIELD_SIZE);

            if (isValid(move))
                makeMove(move, (JFrame) SwingUtilities.getWindowAncestor(board));
            else
                selectedPiece.updatePosition();

            selectedPiece = null;
        }
        board.repaint();
    }

    @Override
    public void mouseDragged(MouseEvent e) {
        if (selectedPiece != null) {
            selectedPiece.x = e.getX() - FIELD_SIZE / 2;
            selectedPiece.y = e.getY() - FIELD_SIZE / 2;
            board.repaint();
        }
    }
}
