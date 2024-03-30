package chess.moves;

import chess.board.Board;
import chess.board.Field;

public class MoveValidator {
    private final Board board;

    public MoveValidator(Board board) {
        this.board = board;
    }

    public ValidationResult validateMove(Move move) {
        ValidationResult result = basicMoveValidation(move);
        if (result.getMoveType() == MoveType.ILLEGAL) {
            return result;
        }

        // castle move
        result = isCastle(move);
        if (result.getMoveType() == MoveType.CASTLE) {
            return result;
        }

        // pawn attack
        result = isPawnAttack(move);
        if (result.getMoveType() == MoveType.LEGAL) {
            return new ValidationResult(MoveType.LEGAL, "Pawn attack is legal.");
        }

        // normal move
        result = isMoveLegal(move);
        return result;
    }

    private ValidationResult basicMoveValidation(Move move) {
        Field startField = move.getStartField();
        if (startField.isEmpty()) {
            return new ValidationResult(MoveType.ILLEGAL, "Start field is empty.");
        }
        if (startField.getPiece().getIsWhite() != move.getPlayer().isWhite()) {
            return new ValidationResult(MoveType.ILLEGAL, "Cannot move the opponent's piece.");
        }
        return new ValidationResult(MoveType.LEGAL, "");
    }

    private ValidationResult isCastle(Move move) {
        if (move.getStartField().getPiece().getName().equals("King") &&
                !move.getStartField().getPiece().getHasMoved() &&
                isRookEligibleForCastle(move) &&
                isPathClear(move.getStart(), move.getEnd())){
            return new ValidationResult(MoveType.CASTLE, "Castling is legal.");
        }
        return new ValidationResult(MoveType.ILLEGAL, "Invalid castling move.");
    }

    private boolean isRookEligibleForCastle(Move move) {
        Field endField = move.getEndField();
        return endField.getPiece() != null &&
                endField.getPiece().getName().equals("Rook") &&
                endField.getPiece().getIsWhite() == move.getStartField().getPiece().getIsWhite() &&
                !endField.getPiece().getHasMoved();
    }

    private ValidationResult isMoveLegal(Move move) {
        if (!move.getStartField().checkMove(move.getStart(), move.getEnd())) {
            return new ValidationResult(MoveType.ILLEGAL, "Piece " + move.getStartField().getPiece().getName() + " cannot move like that.");
        }
        if (!isPathClear(move.getStart(), move.getEnd()) && !move.getStartField().getPiece().getName().equals("Knight")) {
            return new ValidationResult(MoveType.ILLEGAL, "Path is not clear.");
        }
        return new ValidationResult(MoveType.LEGAL, "Move is legal.");
    }

    private ValidationResult isPawnAttack(Move move) {
        if (move.getStartField().getPiece().getName().equals("Pawn")) {
            if (isPawnAttackValid(move)) {
                return new ValidationResult(MoveType.LEGAL, "Pawn attack is legal.");
            }
            return new ValidationResult(MoveType.ILLEGAL, "Pawn attack is not legal.");
        }
        return new ValidationResult(MoveType.ILLEGAL, "Not a pawn move.");
    }

    private boolean isPawnAttackValid(Move move) {
        int[] start = move.getStart();
        int[] end = move.getEnd();
        boolean isEnemy = move.getEndField().getPiece() != null &&
                move.getEndField().getPiece().getIsWhite() != move.getStartField().getPiece().getIsWhite();
        int deltaX = Math.abs(start[1] - end[1]);
        int deltaY = Math.abs(start[0] - end[0]);

        return isEnemy && deltaX == 1 && deltaY == 1;
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
}

