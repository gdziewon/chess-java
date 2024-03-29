package chess.pieces;

import chess.utils.PieceUtils;

public class Bishop extends Piece {

    public Bishop(boolean isWhite) {
        super(isWhite);
    }

    @Override
    public boolean checkMove(int[] start, int[] end) {
        int[] distance = PieceUtils.calculateDistance(start, end);
        return PieceUtils.isDiagonalMove(distance);
    }

    @Override
    public char getSymbol() {
        return this.getIsWhite() ? 'B' : 'b';
    }
}
