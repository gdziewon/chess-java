package chess.moves;

import chess.board.Board;
import chess.board.Field;
import chess.pieces.Pawn;
import chess.pieces.Piece;

public class MoveValidator {
    private final Board board;

    public MoveValidator(Board board) {
        this.board = board;
    }

    public ValidationResult validateMove(Move move) {
        if (!basicMoveValidation(move)) {
            return new ValidationResult(MoveType.ILLEGAL, "Field is empty or piece is not yours.");
        }

        if (isCastle(move)) {
            return isValidCastleMove(move);
        }

        if (isPawnAttack(move)) {
            return isValidPawnAttack(move);
        }

        return isMoveLegal(move);
    }

    private boolean basicMoveValidation(Move move) {
        Field startField = move.getStartField();
        if (startField.isEmpty()) {
            return false;
        }
        return startField.getPiece().getIsWhite() == move.getIsWhite();
    }

    private ValidationResult isMoveLegal(Move move) {
        if (!move.getEndField().isEmpty() &&
                move.getEndField().getPiece().getIsWhite() == move.getIsWhite()) {
            return new ValidationResult(MoveType.ILLEGAL, "Cannot capture own piece.");
        }
        if (!move.getStartField().checkMove(move.getStart(), move.getEnd())) {
            return new ValidationResult(MoveType.ILLEGAL, "Piece " + move.getStartField().getPiece().getName() + " cannot move like that.");
        }
        if (!isPathClear(move.getStart(), move.getEnd()) &&
                !move.getStartField().getPiece().getName().equals("Knight")) {
            return new ValidationResult(MoveType.ILLEGAL, "Path is not clear.");
        }
        return new ValidationResult(MoveType.LEGAL, "Move is legal.");
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


    private boolean isCastle(Move move) {
        Piece startPiece = move.getStartField().getPiece();
        Piece endPiece = move.getEndField().getPiece();

        if (endPiece == null) {
            return false;
        }

        return startPiece.getName().equals("King") && endPiece.getName().equals("Rook") &&
                endPiece.getIsWhite() == move.getIsWhite();
    }

    private ValidationResult isValidCastleMove(Move move) {
        if (move.getStartField().getPiece().getHasMoved()) {
            return new ValidationResult(MoveType.ILLEGAL, "King has moved.");
        }
        if (move.getEndField().getPiece().getHasMoved()) {
            return new ValidationResult(MoveType.ILLEGAL, "Rook has moved.");
        }
        if (!isPathClear(move.getStart(), move.getEnd())) {
            return new ValidationResult(MoveType.ILLEGAL, "Path is not clear.");
        }
        return new ValidationResult(MoveType.CASTLE, "Castling is legal.");
    }

    private boolean isPawnAttack(Move move) {
        return move.getStartField().getPiece().getName().equals("Pawn") &&
                move.getEndField().getPiece() != null &&
                move.getEndField().getPiece().getIsWhite() != move.getStartField().getPiece().getIsWhite();
    }

    private ValidationResult isValidPawnAttack(Move move) {
        int[] start = move.getStart();
        int[] end = move.getEnd();
        Pawn pawn = (Pawn) move.getStartField().getPiece();
        if (!pawn.checkAttack(start, end)) {
            return new ValidationResult(MoveType.ILLEGAL, "Pawn cannot attack like that.");
        }
        return new ValidationResult(MoveType.LEGAL, "Pawn attack is legal.");
    }

}

