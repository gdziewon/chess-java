package chess.board;

import chess.pieces.Piece;

public class Field {
    private Piece piece;
    private final boolean isFieldWhite;

    public Field(Piece piece, boolean isFieldWhite) {
        this.piece = piece;
        this.isFieldWhite = isFieldWhite;
    }

    public void printField() {
        if (isEmpty()) {
            System.out.print(isFieldWhite() ? "   " : ":::");
        } else {
            System.out.print(" " + piece.getSymbol() + " ");
        }
    }

    public boolean isFieldWhite() {
        return isFieldWhite;
    }

    public Piece getPiece() {
        return piece;
    }

    public void setPiece(Piece piece) {
        this.piece = piece;
    }

    public boolean isEmpty() {
        return piece == null;
    }

    public boolean checkMove(int[] start, int[] end) {
        return piece.checkMove(start, end);
    }

    public boolean isPieceWhite() {
        return piece.getIsWhite();
    }
}
