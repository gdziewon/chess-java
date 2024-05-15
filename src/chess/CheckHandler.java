package chess;

public class CheckHandler {

    public static boolean isKingChecked(int color) {
        Piece king = Board.findKing(color);
        assert king != null;
        return isSquareAttacked(king.file, king.rank, color);
    }

    public static boolean hasLegalMoves(int color) {
        for (Piece piece : Board.pieceList) {
            if (piece.isColor(color)) {
                for (int f = 0; f < Board.files; f++) {
                    for (int r = 0; r < Board.ranks; r++) {
                        if (piece.isValidMove(f, r)) {
                            Move move = new Move(piece, f, r);
                            if (Board.isValid(move)) {
                                return true;
                            }
                        }
                    }
                }

            }
        }
        return false;
    }

    public static boolean isSquareAttacked(int file, int rank, int color) {
        int oppositeColor = color == Pieces.White ? Pieces.Black : Pieces.White;
        for (Piece piece : Board.pieceList) {
            if (piece.isColor(oppositeColor) && piece.isValidMove(file, rank) ) {
                return true;
            }
        }
        return false;
    }

    public static boolean simulateAndCheck(Move move) {
        Piece originalPiece = move.piece;
        Piece capturedPiece = Board.getPiece(move.targetFile, move.targetRank);
        int originalFile = originalPiece.file;
        int originalRank = originalPiece.rank;

        movePieceSim(move);

        // check if the move leaves the king in check
        boolean isSafe = !CheckHandler.isKingChecked(Board.colorToMove);

        // undo
        undoMove(originalPiece, capturedPiece, originalFile, originalRank);

        return isSafe;
    }

    private static void movePieceSim(Move move) {
        move.piece.setPosSim(move.targetFile, move.targetRank);
        if (move.capture != null) {
            Board.pieceList.remove(move.capture);
        }
    }

    private static void undoMove(Piece piece, Piece capturedPiece, int originalFile, int originalRank) {
        piece.setPosSim(originalFile, originalRank);
        if (capturedPiece != null) {
            Board.pieceList.add(capturedPiece);
        }
    }

}
