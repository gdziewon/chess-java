package chess.game;

import chess.board.Board;
import chess.board.Field;
import chess.moves.Move;
import chess.moves.MoveType;
import chess.moves.MoveValidator;
import chess.pieces.Piece;

import java.util.Arrays;

import static chess.utils.Constants.BOARD_SIZE;

public class CheckHandler {
    private final Board board;
    private final MoveValidator moveValidator;

    private final Board.Executor executor;

    CheckHandler(Board board, MoveValidator moveValidator) {
        this.board = board;
        this.moveValidator = moveValidator;
        this.executor = board.getExecutor();
    }

    public boolean isInCheckWhileCastling(Move move) {
        return isInCheck(move.getIsWhite()) || isCastlingThroughCheck(move);
    }

    private boolean isCastlingThroughCheck(Move move) {
        if (move.getValidationResult().moveType() == MoveType.CASTLE) {
            int[] kingStartPos = move.getStart();
            int[] rookStartPos = move.getEnd();

            int[] direction = new int[]{Integer.compare(rookStartPos[0], kingStartPos[0]), Integer.compare(rookStartPos[1], kingStartPos[1])};
            int[] fieldToCheck = new int[]{kingStartPos[0] + direction[0], kingStartPos[1] + direction[1]};

            while (!Arrays.equals(fieldToCheck, rookStartPos)) {
                if (isUnderAttack(fieldToCheck, !move.getIsWhite())) {
                    return true;
                }
                fieldToCheck[0] += direction[0];
                fieldToCheck[1] += direction[1];
            }
        }
        return false;
    }

    public boolean isInCheckmate(boolean isWhite) {
        return isInCheck(isWhite) && !hasLegalMoves(isWhite);
    }

    public boolean isInCheck(boolean isWhite) {
        int[] kingPosition = findKingPosition(isWhite);
        return isUnderAttack(kingPosition, !isWhite);
    }

    private boolean hasLegalMoves(boolean isWhite) {
        for (int i = 0; i < BOARD_SIZE; i++) {
            for (int j = 0; j < BOARD_SIZE; j++) {
                if (hasLegalMoveFromField(i, j, isWhite)) {
                    return true;
                }
            }
        }
        return false;
    }

    private boolean hasLegalMoveFromField(int startX, int startY, boolean isWhite) {
        Field startField = board.getField(startX, startY);
        Piece piece = startField.getPiece();
        if (piece != null && piece.getIsWhite() == isWhite) {
            for (int endX = 0; endX < BOARD_SIZE; endX++) {
                for (int endY = 0; endY < BOARD_SIZE; endY++) {
                    if (isLegalMove(startX, startY, endX, endY, isWhite)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    private boolean isLegalMove(int startX, int startY, int endX, int endY, boolean isWhite) {
        Move potentialMove = new Move(new int[]{startX, startY}, new int[]{endX, endY}, board.getField(startX, startY), board.getField(endX, endY), isWhite);
        moveValidator.validateMove(potentialMove);
        if (potentialMove.getValidationResult().moveType() == MoveType.STANDARD ||
                potentialMove.getValidationResult().moveType() == MoveType.EN_PASSANT) {
            executor.executeMove(potentialMove);
            boolean stillInCheck = isInCheck(isWhite);
            executor.undoMove(potentialMove);
            return !stillInCheck;
        }
        return false;
    }

    private boolean isUnderAttack(int[] position, boolean isOpponentWhite) {
        for (int i = 0; i < BOARD_SIZE; i++) {
            for (int j = 0; j < BOARD_SIZE; j++) {
                if (isFieldAttackingPosition(i, j, position, isOpponentWhite)) {
                    return true;
                }
            }
        }
        return false;
    }

    private boolean isFieldAttackingPosition(int startX, int startY, int[] position, boolean isOpponentWhite) {
        Field startField = board.getField(startX, startY);
        Piece piece = startField.getPiece();
        if (piece != null && piece.getIsWhite() == isOpponentWhite) {
            Move potentialMove = new Move(new int[]{startX, startY}, position, startField, board.getField(position[0], position[1]), isOpponentWhite);
            moveValidator.validateMove(potentialMove);
            return potentialMove.getValidationResult().moveType() == MoveType.STANDARD;
        }
        return false;
    }

    private int[] findKingPosition(boolean isWhite) {
        for (int i = 0; i < BOARD_SIZE; i++) {
            for (int j = 0; j < BOARD_SIZE; j++) {
                Piece piece = board.getField(i, j).getPiece();
                if (piece != null && piece.getName().equals("King") && piece.getIsWhite() == isWhite) {
                    return new int[]{i, j};
                }
            }
        }
        throw new IllegalStateException("King not found, which should never happen.");
    }
}