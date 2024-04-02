package chess.game;

public class Player {
    private final boolean isWhite;
    private boolean isInCheck = false;

    public Player(boolean isWhite) {
        this.isWhite = isWhite;
    }

    public boolean isWhite() {
        return isWhite;
    }

    public boolean isInCheck() {
        return isInCheck;
    }

    public void setInCheck(boolean inCheck) {
        isInCheck = inCheck;
    }

}
