package chess.utils;

import chess.pieces.*;

public class PieceFactory {
    public static Piece createPiece(Pieces piece, boolean isWhite) {
        return switch (piece) {
            case PAWN -> new Pawn(isWhite);
            case KNIGHT -> new Knight(isWhite);
            case BISHOP -> new Bishop(isWhite);
            case ROOK -> new Rook(isWhite);
            case QUEEN -> new Queen(isWhite);
            case KING -> new King(isWhite);
        };
    }
}
