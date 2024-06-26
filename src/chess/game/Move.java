package chess.game;

public class Move {
    int startFile, startRank, targetFile, targetRank;

    Piece piece, capture;

    boolean isCastling, isEnPassant = false;

    public Move(Piece piece, int targetFile, int targetRank) {
        this.startFile = piece.file;
        this.startRank = piece.rank;

        this.targetFile = targetFile;
        this.targetRank = targetRank;

        this.piece = piece;
        this.capture = Board.getPiece(targetFile, targetRank);

        this.isCastling = piece.isType(Pieces.King) && Math.abs(targetFile - startFile) == 2 && startRank == targetRank;
    }
}
