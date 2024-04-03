package chess.moves;

import chess.board.Board;
import chess.board.Field;
import chess.pieces.*;

public class MoveValidator {
    private final Board board;

    public MoveValidator(Board board) {
        this.board = board;
    }

    public void validateMove(Move move) {
        if (!basicMoveValidation(move)) {
            setMovesValidationResult(move, MoveType.ILLEGAL, "Invalid move.");
        } else if (isEnPassant(move)) {
            validateEnPassant(move);
        } else if (isCastle(move)) {
            validateCastleMove(move);
        } else if (isPawnAttack(move)) {
            validatePawnAttack(move);
        } else {
            validateStandardMove(move);
        }
    }

    public void setMovesValidationResult(Move move, MoveType moveType, String message) {
        move.setValidationResult(new ValidationResult(moveType, message));
    }

    private boolean basicMoveValidation(Move move) {
        Field startField = move.getStartField();

        if (startField.isEmpty()) {
            return false;
        }
        return startField.getPiece().getIsWhite() == move.getIsWhite();
    }

    private void validateStandardMove(Move move) {
        Field endField = move.getEndField();
        Field startField = move.getStartField();

        if (!endField.isEmpty() &&
                endField.getPiece().getIsWhite() == move.getIsWhite()) {
            setMovesValidationResult(move, MoveType.ILLEGAL, "Cannot capture own piece.");

        } else if (!startField.checkMove(move.getStart(), move.getEnd())) {
            setMovesValidationResult(move, MoveType.ILLEGAL,
                    String.format("Piece %s cannot move like that.", move.getStartField().getPiece().getName()));

        } else if (!isPathClear(move.getStart(), move.getEnd()) &&
                !(startField.getPiece() instanceof Knight)) {
            setMovesValidationResult(move, MoveType.ILLEGAL, "Path is not clear.");

        } else {
            setMovesValidationResult(move, MoveType.STANDARD, "Standard move is legal.");
        }
    }

    private boolean isPathClear(int[] start, int[] end) {
        int dx = Integer.compare(end[0], start[0]);
        int dy = Integer.compare(end[1], start[1]);

        int x = start[0] + dx;
        int y = start[1] + dy;

        while (x != end[0] || y != end[1]) {
            if (!board.getField(x, y).isEmpty()) {
                return false;
            }
            x += dx;
            y += dy;
        }

        return true;
    }

    private boolean isEnPassant(Move move) {
        Piece startPiece = move.getStartField().getPiece();
        Field endField = move.getEndField();

        if (!(startPiece instanceof Pawn pawn)) {
            return false;
        }

        boolean isAttack = pawn.checkAttack(move.getStart(), move.getEnd());

        return isAttack && endField.isEmpty();
    }

    private void validateEnPassant(Move move) {
        Field enemysPawnField = board.getField(move.getStart()[0], move.getEnd()[1]);

        if (enemysPawnField.getPiece() == null ||
                !(enemysPawnField.getPiece() instanceof Pawn) ||
                enemysPawnField.getPiece().getIsWhite() == move.getIsWhite()) {
            setMovesValidationResult(move, MoveType.ILLEGAL, "No pawn to capture by en passant.");

        } else {
            setMovesValidationResult(move, MoveType.EN_PASSANT, "En passant is legal.");
        }
    }


    private boolean isCastle(Move move) {
        Piece startPiece = move.getStartField().getPiece();
        Piece endPiece = move.getEndField().getPiece();

        if (endPiece == null) {
            return false;
        }

        return (startPiece instanceof King) && (endPiece instanceof Rook) &&
                endPiece.getIsWhite() == move.getIsWhite();
    }

    private void validateCastleMove(Move move) {
        Piece king = move.getStartField().getPiece();
        Piece rook = move.getEndField().getPiece();

        if (king.getHasMoved()) {
            setMovesValidationResult(move, MoveType.ILLEGAL, "King has moved.");

        } else if (rook.getHasMoved()) {
            setMovesValidationResult(move, MoveType.ILLEGAL, "Rook has moved.");

        } else if (!isPathClear(move.getStart(), move.getEnd())) {
            setMovesValidationResult(move, MoveType.ILLEGAL, "Path is not clear for castle.");

        } else {
            setMovesValidationResult(move, MoveType.CASTLE, "Castle is legal.");
        }
    }

    private boolean isPawnAttack(Move move) {
        Piece startPiece = move.getStartField().getPiece();
        Piece endPiece = move.getEndField().getPiece();

        return (startPiece instanceof Pawn) &&
                endPiece != null &&
                endPiece.getIsWhite() != move.getIsWhite();
    }

    private void validatePawnAttack(Move move) {
        int[] start = move.getStart();
        int[] end = move.getEnd();
        Pawn pawn = (Pawn) move.getStartField().getPiece();

        if (!pawn.checkAttack(start, end)) {
            setMovesValidationResult(move, MoveType.ILLEGAL, "Pawn cannot attack like that.");

        } else {
            setMovesValidationResult(move, MoveType.STANDARD, "Pawn attack is legal.");
        }
    }

    public static boolean isDoublePawnPush(Move move, Move lastMove) {
        if (lastMove == null) {
            return false;
        }

        int[] lastMoveStart = lastMove.getStart();
        int[] lastMoveEnd = lastMove.getEnd();

        int[] moveStart = move.getStart();
        int[] moveEnd = move.getEnd();

        boolean isLastMoveDoublePawnPush = lastMove.getEndField().getPiece().getName().equals("Pawn") &&
                Math.abs(lastMoveStart[0] - lastMoveEnd[0]) == 2;

        return isLastMoveDoublePawnPush &&
                lastMoveEnd[0] == moveStart[0] &&
                lastMoveEnd[1] == moveEnd[1] &&
                Math.abs(lastMoveEnd[0] - moveEnd[0]) == 1;
    }
}

