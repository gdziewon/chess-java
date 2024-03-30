package chess.pieces;

public abstract class Piece {
    private final boolean isWhite;
    private boolean hasMoved = false;

    public Piece(boolean isWhite) {
        this.isWhite = isWhite;
    }

    public boolean getIsWhite() {
        return isWhite;
    }

    public boolean getHasMoved() {
        return hasMoved;
    }

    public void setHasMoved(boolean hasMoved) {
        this.hasMoved = hasMoved;
    }

    public abstract char getSymbol();

    public String getName() {
        return this.getClass().getSimpleName();
    }

    public abstract boolean checkMove(int[] start, int[] end);
}
