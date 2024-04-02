package chess.pieces;

import chess.utils.PieceUtils;

public class Rook extends Piece {

    public Rook(boolean isWhite) {
        super(isWhite);
    }

    @Override
    public boolean checkMove(int[] start, int[] end) {
        int[] distance = PieceUtils.calculateDistance(start, end);
        return PieceUtils.isHorizontalMove(distance);
    }

    @Override
    public char getSymbol() {
        return this.getIsWhite() ? 'R' : 'r';
    }

    @Override
    public Piece copyPiece() {
        Rook newRook = new Rook(this.getIsWhite());
        newRook.setHasMoved(this.getHasMoved());
        return newRook;
    }
}
