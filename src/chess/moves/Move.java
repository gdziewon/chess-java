package chess.moves;

import chess.board.Field;

public class Move {
    private final int[] start;
    private final int[] end;
    private final Field startField;
    private final Field endField;
    private final boolean isWhite;

    private ValidationResult validationResult;


    public Move(int[] start, int[] end, Field startField, Field endField, boolean isWhite) {
        this.start = start;
        this.end = end;
        this.isWhite = isWhite;
        this.startField = startField;
        this.endField = endField;
    }

    public int[] getStart() {
        return start;
    }

    public int[] getEnd() {
        return end;
    }

    public boolean getIsWhite() {
        return isWhite;
    }

    public Field getStartField() {
        return startField;
    }

    public Field getEndField() {
        return endField;
    }

    public ValidationResult getValidationResult() {
        return validationResult;
    }

    public void setValidationResult(ValidationResult validationResult) {
        this.validationResult = validationResult;
    }
}
