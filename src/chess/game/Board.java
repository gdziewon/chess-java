package chess.game;

import chess.gui.GameFinishedPanel;
import chess.gui.Input;
import chess.gui.SoundEffects;

import javax.swing.*;
import java.awt.*;
import java.util.HashMap;
import java.util.concurrent.CopyOnWriteArrayList;

public class Board extends JPanel {
    public static final String startFen = "rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1";
    public static final int fieldSize = 110;
    public static final int files = 8;
    public static final int ranks = 8;

    static final Color light = new Color(232, 148, 148);
    static final Color dark = new Color(78, 9, 9);
    static final Color highlight = new Color(0, 255, 0, 100);
    static final Color capture = new Color(255, 0, 0, 150);
    static final Color check = new Color(29, 38, 248, 150);

    static CopyOnWriteArrayList<Piece> pieceList;
    Input input = new Input(this);

    public Piece selectedPiece;
    public static int colorToMove = Pieces.White;

    public Board(String fen) {
        this.setPreferredSize(new Dimension(ranks * fieldSize, files * fieldSize));
        this.addMouseListener(input);
        this.addMouseMotionListener(input);
        pieceList = new CopyOnWriteArrayList<>();
        loadFromFen(fen);
    }

    public static void loadFromFen(String fen) {
        HashMap<Character, Integer> pieceMap = new HashMap<>();
        pieceMap.put('p', Pieces.Pawn);
        pieceMap.put('n', Pieces.Knight);
        pieceMap.put('b', Pieces.Bishop);
        pieceMap.put('r', Pieces.Rook);
        pieceMap.put('q', Pieces.Queen);
        pieceMap.put('k', Pieces.King);

        String fenBoard = fen.split(" ")[0];
        int file = 0;
        int rank = 7;

        for (char symbol : fenBoard.toCharArray()) {
            if (symbol == '/') {
                file = 0;
                rank--;
            } else {
                if (Character.isDigit(symbol)) {
                    file += Character.getNumericValue(symbol);
                } else {
                    int color = (Character.isLowerCase(symbol)) ? Pieces.White : Pieces.Black;
                    int piece = pieceMap.get(Character.toLowerCase(symbol));
                    pieceList.add(new Piece(piece | color, file, rank));
                    file++;
                }
            }
        }
    }

    public static Piece getPiece(int file, int rank) {
        for (Piece piece : pieceList) {
            if (piece.file == file && piece.rank == rank) {
                return piece;
            }
        }
        return null;
    }

    public static void makeMove(Move move, JFrame owner) {
        updateDoubleStepFlag(move);

        if (move.isCastling) {
            handleCastle(move);
        } else {
            handleMove(move, owner);
        }

        colorToMove = colorToMove == Pieces.White ? Pieces.Black : Pieces.White;
        checkGameStatus();
    }

    public static void checkGameStatus() {
        boolean isInCheck = CheckHandler.isKingChecked(colorToMove);
        boolean hasValidMoves = CheckHandler.hasLegalMoves(colorToMove);
        if (!hasValidMoves) {
            if (isInCheck) { // checkmate
                GameFinishedPanel.displayCheckmate(colorToMove);
            } else { // stalemate
                GameFinishedPanel.displayStalemate();
            }
        } else if (isInCheck) { // check
            SoundEffects.playCheck();
        }
    }

    public static void updateDoubleStepFlag(Move move) {
        for (Piece piece : pieceList) {
            if (piece != move.piece) {
                piece.justMadeDoubleStep = false;
            }
        }
        move.piece.justMadeDoubleStep = Math.abs(move.targetRank - move.startRank) == 2 && move.piece.isType(Pieces.Pawn);
    }

    public static void handleMove(Move move, JFrame owner) {
        move.piece.setPos(move.targetFile, move.targetRank);
        SoundEffects.playMove(move.piece.getColor());
        capture(move);
        move.piece.checkPromotion(owner);
    }

    public static void handleCastle(Move move) {
        int direction = move.targetFile > move.startFile ? 1 : -1;
        int rookStartFile = (direction == 1) ? 7 : 0;
        int rookEndFile = (direction == 1) ? move.targetFile - 1 : move.targetFile + 1;
        // king
        move.piece.setPos(move.targetFile, move.targetRank);
        // rook
        Piece rook = getPiece(rookStartFile, move.startRank);
        if (rook != null) {
            rook.setPos(rookEndFile, move.startRank);
        }
        SoundEffects.playCastle();
    }

    public static void capture(Move move) {
        if (move.capture == null && move.piece.isType(Pieces.Pawn) && Math.abs(move.startFile - move.targetFile) == 1) {
            pieceList.remove(getPiece(move.targetFile, move.startRank)); // en passant capture
            SoundEffects.playCapture();
        } else if (move.capture != null) {
            pieceList.remove(move.capture);
            SoundEffects.playCapture();
        }
    }

    public static boolean isValid(Move move) {
        if (!move.piece.isColor(move.capture) && move.piece.isValidMove(move.targetFile, move.targetRank)) {
            return CheckHandler.simulateAndCheck(move); // simulate the move and check if the king will be in check
        }
        return false;
    }

    public static Piece findKing(int color) {
        for (Piece piece : pieceList) {
            if (piece.isType(Pieces.King) && piece.isColor(color)) {
                return piece;
            }
        }
        return null;
    }

    public void paintComponent(Graphics graphics) {
        Graphics2D view = (Graphics2D) graphics;

        // board
        for (int r = 0; r < ranks; r++) {
            for (int f = 0; f < files; f++) {
                paintSquare(view, f, r, (f + r) % 2 == 0 ? light : dark);
            }
        }

        // highlight
        if (selectedPiece != null) {
            for (int r = 0; r < ranks; r++) {
                for (int f = 0; f < files; f++) {
                    Move move = new Move(selectedPiece, f, r);
                    if (isValid(move)) {
                        paintSquare(view, f, r, move.capture != null ? capture : highlight);
                    }
                }
            }
        }

        // check
        if (CheckHandler.isKingChecked(colorToMove)) {
            Piece king = findKing(colorToMove);
            assert king != null;
            paintSquare(view, king.file, king.rank, check);
        }

        // pieces
        for (Piece piece : pieceList) {
            piece.paint(view);
        }
    }

    public static void paintSquare(Graphics2D view, int file, int rank, Color color) {
        view.setColor(color);
        view.fillRect(file * fieldSize, rank * fieldSize, fieldSize, fieldSize);
    }
}
