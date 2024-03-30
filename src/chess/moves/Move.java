package chess.moves;

import chess.board.Field;
import chess.game.Player;

public class Move {
    private final int[] start;
    private final int[] end;
    private Field startField;
    private Field endField;
    private final Player player;


    public Move(int[] start, int[] end, Player player) {
        this.start = start;
        this.end = end;
        this.player = player;
    }

    public int[] getStart() {
        return start;
    }

    public int[] getEnd() {
        return end;
    }

    public Player getPlayer() {
        return player;
    }

    public void setStartField(Field startField) {
        this.startField = startField;
    }

    public void setEndField(Field endField) {
        this.endField = endField;
    }

    public Field getStartField() {
        return startField;
    }

    public Field getEndField() {
        return endField;
    }
}
