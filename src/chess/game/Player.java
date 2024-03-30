package chess.game;

import chess.board.Field;

public class Player {
    private final boolean isWhite;
    private Field kingField;

    public Player(boolean isWhite, Field kingField) {
        this.isWhite = isWhite;
        this.kingField = kingField;
    }

    public boolean isWhite() {
        return isWhite;
    }

    public Field getKingField() {
        return kingField;
    }

    public void setKingField(Field kingField) {
        this.kingField = kingField;
    }
}
