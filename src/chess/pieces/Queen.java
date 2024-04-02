package chess.pieces;

import chess.utils.PieceUtils;

public class Queen extends Piece {

        public Queen(boolean isWhite) {
            super(isWhite);
        }

        @Override
        public boolean checkMove(int[] start, int[] end) {
            int[] distance = PieceUtils.calculateDistance(start, end);
            return PieceUtils.isDiagonalMove(distance) || PieceUtils.isHorizontalMove(distance);
        }

        @Override
        public char getSymbol() {
            return this.getIsWhite() ? 'Q' : 'q';
        }

        @Override
        public Piece copyPiece() {
            return new Queen(this.getIsWhite());
        }
}
