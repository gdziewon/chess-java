package chess.game;

import java.util.HashMap;
import java.util.Map;

import static chess.game.Board.*;
import static chess.game.Pieces.*;
import static chess.gui.GameFinishedPanel.*;
import static chess.gui.SoundEffects.*;

public class GameStateHandler {
    private static Map<String, Integer> gameStateHistory;
    private static int movesSinceLastCaptureOrPawnMove;
    public static ChessClock[] clocks; // white, black

    public static void init(int whiteTime, int blackTime, Board board) {
        setClocks(whiteTime, blackTime, board);
        gameStateHistory = new HashMap<>();
        movesSinceLastCaptureOrPawnMove = 0;
    }

    private static void setClocks(int whiteTime, int blackTime, Board board) {
        clocks = new ChessClock[2];
        clocks[0] = new ChessClock(whiteTime, true, board);
        clocks[1] = new ChessClock(blackTime, false, board);
        clocks[colorToMove == White ? 0 : 1].start();
    }

    public static void switchTurns() {
        clocks[colorToMove == White ? 0 : 1].stop();
        colorToMove = colorToMove == White ? Black : White;
        clocks[colorToMove == White ? 0 : 1].start();
    }

    public static boolean isKingChecked(int color) {
        Piece king = kings[color == White ? 0 : 1];
        assert king != null;
        return isSquareAttacked(king.file, king.rank, color);
    }

    private static boolean hasLegalMoves(int color) {
        for (Piece piece : pieceList) // check if any piece has any valid move
            if (piece.isColor(color))
                for (int f = 0; f < FILES; f++)
                    for (int r = 0; r < RANKS; r++)
                        if (piece.isValidMove(f, r, null))
                            if (isValid(new Move(piece, f, r)))
                                return true;

        return false;
    }

    public static boolean isSquareAttacked(int file, int rank, int color) {
        int oppositeColor = color == White ? Black : White;
        for (Piece piece : pieceList)
            if (piece.isColor(oppositeColor) && piece.isValidMove(file, rank, null))
                return true;
        return false;
    }

    public static boolean simulateAndCheck(Move move) {
        Piece originalPiece = move.piece;
        Piece capturedPiece = getPiece(move.targetFile, move.targetRank);
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
        for (Piece piece : pieceList) // reset all double step flags
            if (piece != move.piece)
                piece.justMadeDoubleStep = false;

        // set double step flag for pawn
        move.piece.justMadeDoubleStep = Math.abs(move.targetRank - move.startRank) == 2 && move.piece.isType(Pawn);
    }

    public static void updateMovesSinceLastCaptureOrPawnMove(Move move) {
        if (move.capture != null || move.piece.isType(Pawn))
            movesSinceLastCaptureOrPawnMove = 0;
        else
            movesSinceLastCaptureOrPawnMove++;
    }

    public static void updateFlags(Move move) {
        updateDoubleStepFlag(move);
        updateMovesSinceLastCaptureOrPawnMove(move);
    }

    public static void checkGameState() {
        String gameState = generateGameState(); // generate current game state
        gameStateHistory.put(gameState, gameStateHistory.getOrDefault(gameState, 0) + 1);

        boolean isInCheck = isKingChecked(colorToMove);
        boolean hasValidMoves = hasLegalMoves(colorToMove);
        boolean insufficientMaterial = isInsufficientMaterial();
        boolean repetition = gameStateHistory.get(gameState) >= 3;
        boolean fiftyMoveRule = movesSinceLastCaptureOrPawnMove >= 50;

        if (!hasValidMoves) {
            if (isInCheck) // checkmate
                displayCheckmate(colorToMove);
            else // stalemate
                displayDraw(STALEMATE);
        } else if (isInCheck) // check
            playSound(CHECK_SOUND);

        if (insufficientMaterial)
            displayDraw(INSUFFICIENT_MATERIAL);
        else if (repetition)
            displayDraw(THREEFOLD_REPETITION);
        else if (fiftyMoveRule)
            displayDraw(FIFTY_MOVE_RULE);
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
            if (piece.isType(Pawn) || piece.isType(Rook) || piece.isType(Queen))
                return false; // pawn, rook, queen - not insufficient material

            if (piece.isType(Knight))
                knightCounts[piece.isColor(White) ? 0 : 1]++;

            if (piece.isType(Bishop)) {
                bishopCounts[piece.isColor(White) ? 0 : 1]++;
                if (piece.isOnLightSquare())
                    bishopOnLightSquare[piece.isColor(White) ? 0 : 1] = true;
                else
                    bishopOnDarkSquare[piece.isColor(White) ? 0 : 1] = true;
            }
        }

        return (pieceList.size() == 3 && (bishopCounts[0] == 1 || bishopCounts[1] == 1)) || // K&B vs. K
                (pieceList.size() == 3 && (knightCounts[0] == 1 || knightCounts[1] == 1)) || // K&N vs. K
                ((pieceList.size() == 4 && bishopCounts[0] == 1 && bishopCounts[1] == 1) && // K&B vs. K&B (same color)
                        ((bishopOnLightSquare[0] && bishopOnLightSquare[1]) || (bishopOnDarkSquare[0] && bishopOnDarkSquare[1])));
    }

    private static String generateGameState() {
        StringBuilder gameState = new StringBuilder(); // state of the game represented as a string
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
