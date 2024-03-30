package chess.pieces;

import chess.utils.PieceUtils;

public class Pawn extends Piece {

    public Pawn(boolean isWhite) {
        super(isWhite);
    }

    @Override
    public boolean checkMove(int[] start, int[] end) {
        int[] distance = PieceUtils.calculateDistancePawn(start, end);
        boolean isWhite = this.getIsWhite();

        boolean normalMove = distance[0] == 0 && (isWhite ? distance[1] == 1 : distance[1] == -1);
        boolean initialMove = !this.getHasMoved() && distance[0] == 0 && (isWhite ? distance[1] == 2 : distance[1] == -2);

        return normalMove || initialMove;
    }

    public boolean checkAttack(int[] start, int[] end) {
        int[] distance = PieceUtils.calculateDistancePawn(start, end);

        if (this.getIsWhite()) {
            return Math.abs(distance[0]) == 1 && distance[1] == 1;
        } else {
            return Math.abs(distance[0]) == 1 && distance[1] == -1;
        }
    }

    @Override
    public char getSymbol() {
        return this.getIsWhite() ? 'P' : 'p';
    }
}
