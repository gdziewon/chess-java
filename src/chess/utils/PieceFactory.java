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

    public static Piece promotePawn(String piece, boolean isWhite) {
        if (piece.length() == 1) {
            return switch (piece.toUpperCase()) {
                case "N" -> new Knight(isWhite);
                case "B" -> new Bishop(isWhite);
                case "R" -> new Rook(isWhite);
                case "Q" -> new Queen(isWhite);
                default -> null;
            };
        }
        return switch (piece.toUpperCase()) {
            case "KNIGHT" -> new Knight(isWhite);
            case "BISHOP" -> new Bishop(isWhite);
            case "ROOK" -> new Rook(isWhite);
            case "QUEEN" -> new Queen(isWhite);
            default -> null;
        };
    }
}
