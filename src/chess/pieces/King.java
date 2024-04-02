package chess.pieces;

import chess.utils.PieceUtils;

public class King extends Piece {

        public King(boolean isWhite) {
            super(isWhite);
        }

        @Override
        public boolean checkMove(int[] start, int[] end) {
            int[] distance = PieceUtils.calculateDistance(start, end);
            return PieceUtils.isSingleSquareMove(distance);
        }

        @Override
        public char getSymbol() {
            return this.getIsWhite() ? 'K' : 'k';
        }

        @Override
        public Piece copyPiece() {
            King newKing = new King(this.getIsWhite());
            newKing.setHasMoved(this.getHasMoved());
            return newKing;
        }
}
