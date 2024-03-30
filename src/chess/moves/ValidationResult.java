package chess.moves;

import chess.moves.MoveType;

public class ValidationResult {
    private final MoveType moveType;
    private final String message;

    public ValidationResult(MoveType moveType, String message) {
        this.moveType = moveType;
        this.message = message;
    }

    public MoveType getMoveType() {
        return moveType;
    }

    public String getMessage() {
        return message;
    }
}
