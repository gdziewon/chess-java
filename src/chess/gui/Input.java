package chess.gui;

import chess.game.Board;
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
        Piece piece = Board.getPiece(e.getX() / Board.FIELD_SIZE, e.getY() / Board.FIELD_SIZE);
        if (piece != null && piece.isColor(Board.colorToMove))
            Board.selectedPiece = piece;

    }

    @Override
    public void mouseReleased(MouseEvent e) {
        if (Board.selectedPiece != null) {
            Move move = new Move(Board.selectedPiece, e.getX() / Board.FIELD_SIZE, e.getY() / Board.FIELD_SIZE);

            if (Board.isValid(move))
                Board.makeMove(move, (JFrame) SwingUtilities.getWindowAncestor(board));
            else
                Board.selectedPiece.updatePosition();

            Board.selectedPiece = null;
        }
        board.repaint();
    }

    @Override
    public void mouseDragged(MouseEvent e) {
        if (Board.selectedPiece != null) {
            Board.selectedPiece.x = e.getX() - Board.FIELD_SIZE / 2;
            Board.selectedPiece.y = e.getY() - Board.FIELD_SIZE / 2;
            board.repaint();
        }
    }
}
