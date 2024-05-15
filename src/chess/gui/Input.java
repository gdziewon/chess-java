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
        Piece piece = Board.getPiece(e.getX() / Board.fieldSize, e.getY() / Board.fieldSize);
        if (piece != null && piece.isColor(Board.colorToMove)) {
            board.selectedPiece = piece;
        }
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        if (board.selectedPiece != null) {
            Move move = new Move(board.selectedPiece, e.getX() / Board.fieldSize, e.getY() / Board.fieldSize);
            if (Board.isValid(move)) {
                Board.makeMove(move, (JFrame) SwingUtilities.getWindowAncestor(board));
            } else {
                board.selectedPiece.updatePosition();
            }
            board.selectedPiece = null;
        }
        board.repaint();
    }

    @Override
    public void mouseDragged(MouseEvent e) {
        if (board.selectedPiece != null) {
            board.selectedPiece.x = e.getX() - Board.fieldSize / 2;
            board.selectedPiece.y = e.getY() - Board.fieldSize / 2;
            board.repaint();
        }
    }
}
