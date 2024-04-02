package chess.game;

import chess.board.Board;
import chess.board.Field;
import chess.moves.Move;
import chess.moves.MoveType;
import chess.moves.MoveValidator;
import chess.moves.ValidationResult;
import chess.pieces.Piece;

public class GameStateManager {
    private final Board board;
    private final MoveValidator moveValidator;

    GameStateManager(Board board) {
        this.board = board;
        this.moveValidator = board.getMoveValidator();
    }

    public boolean isInCheck(boolean isWhite) {
        boolean isOppenentWhite = !isWhite;

        int[] kingPosition = findKingPosition(isWhite);
        Field kingField = board.getField(kingPosition[0], kingPosition[1]);
        for (int i = 0; i < Board.BOARD_SIZE; i++) {
            for (int j = 0; j < Board.BOARD_SIZE; j++) {
                Field startField = board.getField(i, j);
                Piece piece = startField.getPiece();
                if (piece != null && piece.getIsWhite() != isWhite) {
                    Move potentialMove = new Move(new int[]{i, j}, kingPosition, startField, kingField, isOppenentWhite);
                    ValidationResult validationResult = moveValidator.validateMove(potentialMove);
                    if (validationResult.moveType() == MoveType.LEGAL) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    private int[] findKingPosition(boolean isWhite) {
        for (int i = 0; i < Board.BOARD_SIZE; i++) {
            for (int j = 0; j < Board.BOARD_SIZE; j++) {
                Piece piece = board.getField(i, j).getPiece();
                if (piece != null && piece.getName().equals("King") && piece.getIsWhite() == isWhite) {
                    return new int[]{i, j};
                }
            }
        }
        throw new IllegalStateException("King not found, which should never happen.");
    }

}
