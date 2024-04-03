package chess.utils;

import chess.moves.Move;
import chess.pieces.Pawn;

public class BoardUtils {
    public static boolean isPromotion(Move move) {
        return (move.getEndField().getPiece() instanceof Pawn) &&
                (move.getEnd()[0] == 0 || move.getEnd()[0] == 7);
    }

    public static boolean isKingsideCastle(int[] rookStartPos, int[] kingStartPos) {
        return rookStartPos[1] > kingStartPos[1];
    }

    public static int calculateKingEndColumn(int[] kingStartPos, boolean isKingside) {
        return isKingside ? kingStartPos[1] + 2 : kingStartPos[1] - 2;
    }

    public static int calculateRookEndColumn(int kingEndColumn, boolean isKingside) {
        return isKingside ? kingEndColumn - 1 : kingEndColumn + 1;
    }
}
