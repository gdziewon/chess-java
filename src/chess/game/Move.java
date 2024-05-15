package chess.game;

public class Move {

    int startFile;
    int startRank;
    int targetFile;
    int targetRank;

    Piece piece;
    Piece capture;

    boolean isCastling;

    public Move(Piece piece, int targetFile, int targetRank) {
        this.startFile = piece.file;
        this.startRank = piece.rank;

        this.targetFile = targetFile;
        this.targetRank = targetRank;

        this.piece = piece;
        this.capture = Board.getPiece(targetFile, targetRank);

        this.isCastling = determineIfCastling();
    }

    private boolean determineIfCastling() {
        return piece.isType(Pieces.King) && Math.abs(targetFile - startFile) == 2 && startRank == targetRank;
    }
}
