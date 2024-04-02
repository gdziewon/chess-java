package chess.pieces;

import chess.utils.PieceUtils;

public class Knight extends Piece {

    public Knight(boolean isWhite) {
        super(isWhite);
    }

    @Override
    public boolean checkMove(int[] start, int[] end) {
        int[] distance = PieceUtils.calculateDistance(start, end);
        return PieceUtils.isKnightMove(distance);
    }

    @Override
    public char getSymbol() {
        return this.getIsWhite() ? 'N' : 'n';
    }

    @Override
    public Piece copyPiece() {
        return new Knight(this.getIsWhite());
    }
}