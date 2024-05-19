package chess.game;

import chess.gui.GameFinishedPanel;
import chess.gui.SoundEffects;

import java.util.HashMap;
import java.util.Map;

import static chess.game.Board.*;

public class GameStateHandler {
    private static final Map<String, Integer> gameStateHistory = new HashMap<>();

    public static boolean isKingChecked(int color) {
        Piece king = kings[color == Pieces.White ? 0 : 1];
        assert king != null;
        return isSquareAttacked(king.file, king.rank, color);
    }

    private static boolean hasLegalMoves(int color) {
        for (Piece piece : pieceList)
            if (piece.isColor(color))
                for (int f = 0; f < Board.FILES; f++)
                    for (int r = 0; r < Board.RANKS; r++)
                        if (piece.isValidMove(f, r, null)) {
                            Move move = new Move(piece, f, r);
                            if (Board.isValid(move))
                                return true;
                        }
        return false;
    }

    public static boolean isSquareAttacked(int file, int rank, int color) {
        int oppositeColor = color == Pieces.White ? Pieces.Black : Pieces.White;
        for (Piece piece : pieceList)
            if (piece.isColor(oppositeColor) && piece.isValidMove(file, rank, null))
                return true;
        return false;
    }

    public static boolean simulateAndCheck(Move move) {
        Piece originalPiece = move.piece;
        Piece capturedPiece = Board.getPiece(move.targetFile, move.targetRank);
        int originalFile = originalPiece.file;
        int originalRank = originalPiece.rank;

        movePieceSim(move); // simulate the move
        boolean isSafe = !GameStateHandler.isKingChecked(colorToMove); // check if king in check
        undoMove(originalPiece, capturedPiece, originalFile, originalRank); // undo

        return isSafe;
    }

    private static void movePieceSim(Move move) {
        move.piece.setPosSim(move.targetFile, move.targetRank);
        if (move.capture != null)
            pieceList.remove(move.capture);
    }

    private static void undoMove(Piece piece, Piece capturedPiece, int originalFile, int originalRank) {
        piece.setPosSim(originalFile, originalRank);
        if (capturedPiece != null)
            pieceList.add(capturedPiece);
    }

    public static void updateDoubleStepFlag(Move move) {
        for (Piece piece : pieceList)
            if (piece != move.piece)
                piece.justMadeDoubleStep = false;

        move.piece.justMadeDoubleStep = Math.abs(move.targetRank - move.startRank) == 2 && move.piece.isType(Pieces.Pawn);
    }

    public static void checkGameStatus() {
        String gameState = generateGameState();
        gameStateHistory.put(gameState, gameStateHistory.getOrDefault(gameState, 0) + 1);
        boolean isInCheck = GameStateHandler.isKingChecked(colorToMove);
        boolean hasValidMoves = GameStateHandler.hasLegalMoves(colorToMove);
        if (!hasValidMoves) {
            if (isInCheck) // checkmate
                GameFinishedPanel.displayCheckmate(colorToMove);
            else // stalemate
                GameFinishedPanel.displayStalemate();
        } else if (isInCheck) // check
            SoundEffects.playSound(SoundEffects.CHECK);

        if (isInsufficientMaterial() || gameStateHistory.get(gameState) >= 3) // draw
            GameFinishedPanel.displayStalemate();
    }

    private static boolean isInsufficientMaterial() {
        if (pieceList.size() > 4)
            return false;

        if (pieceList.size() == 2) // K vs K
            return true;

        int[] knightCounts = new int[2]; // white, black
        int[] bishopCounts = new int[2];
        boolean[] bishopOnLightSquare = new boolean[2];
        boolean[] bishopOnDarkSquare = new boolean[2];

        for (Piece piece : pieceList) {
            if (piece.isType(Pieces.Pawn) || piece.isType(Pieces.Rook) || piece.isType(Pieces.Queen))
                return false;

            if (piece.isType(Pieces.Knight))
                knightCounts[piece.isColor(Pieces.White) ? 0 : 1]++;

            if (piece.isType(Pieces.Bishop)) {
                bishopCounts[piece.isColor(Pieces.White) ? 0 : 1]++;
                if (piece.isOnLightSquare())
                    bishopOnLightSquare[piece.isColor(Pieces.White) ? 0 : 1] = true;
                else
                    bishopOnDarkSquare[piece.isColor(Pieces.White) ? 0 : 1] = true;
            }
        }

        return (pieceList.size() == 3 && (bishopCounts[0] == 1 || bishopCounts[1] == 1)) || // K&B vs. K
                (pieceList.size() == 3 && (knightCounts[0] == 1 || knightCounts[1] == 1)) || // K&N vs. K
                ((pieceList.size() == 4 && bishopCounts[0] == 1 && bishopCounts[1] == 1) && // K&B vs. K&B (same color)
                        ((bishopOnLightSquare[0] && bishopOnLightSquare[1]) || (bishopOnDarkSquare[0] && bishopOnDarkSquare[1])));
    }

    private static String generateGameState() {
        StringBuilder gameState = new StringBuilder();
        for (Piece piece : pieceList) {
            gameState.append(piece.file);
            gameState.append(piece.rank);
            gameState.append(piece.value);
            if (piece.justMadeDoubleStep && piece.isType(Pieces.Pawn))
                gameState.append("DS");
            if (piece.isType(Pieces.Rook) || piece.isType(Pieces.King))
                gameState.append(piece.hasMoved ? "1" : "0");
        }

        gameState.append(colorToMove);
        return gameState.toString();
    }
}
