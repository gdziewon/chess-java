package chess.pieces;

public abstract class Piece {
    private final boolean isWhite;
    private boolean didntMove = true;

    public Piece(boolean isWhite) {
        this.isWhite = isWhite;
    }

    public boolean getIsWhite() {
        return isWhite;
    }

    public boolean getDidntMove() {
        return didntMove;
    }

    public void setHasMoved(boolean hasMoved) {
        this.didntMove = hasMoved;
    }

    public abstract char getSymbol();


    public abstract boolean checkMove(int[] start, int[] end);
}
