package chess;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;

public class Board extends JPanel {
    public static final String startFen = "rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1";
    public static final int fieldSize = 85;

    public static final int files = 8;
    public static final int ranks = 8;

    static ArrayList<Piece> pieceList;

    public Piece selectedPiece;

    static int colorToMove = Pieces.White;

    Input input = new Input(this);

    public Board(String fen) {
        this.setPreferredSize(new Dimension(ranks * fieldSize, files * fieldSize));
        this.addMouseListener(input);
        this.addMouseMotionListener(input);
        pieceList = new ArrayList<>();
        loadFromFen(fen);
    }

    public static void loadFromFen(String fen) {
        HashMap<Character, Integer> pieceMap = new HashMap<Character, Integer>();
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
            move.piece.setPos(move.targetFile, move.targetRank);
            capture(move);
            move.piece.checkPromotion(owner);
        }

        colorToMove = colorToMove == Pieces.White ? Pieces.Black : Pieces.White;
        checkGameStatus();
    }

    public static void checkGameStatus() {
        boolean isInCheck = CheckHandler.isKingChecked(colorToMove);
        boolean hasValidMoves = CheckHandler.hasLegalMoves(colorToMove);
        if (!hasValidMoves) {
            if (isInCheck) {
                GameFinishedPanel.displayEndGame("Checkmate. " + (colorToMove == Pieces.White ? "Black wins!" : "White wins!"));
            } else {
                GameFinishedPanel.displayEndGame("Stalemate. It's a draw!");
            }
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
    }

    public static void capture(Move move) {
        if (move.capture == null && move.piece.isType(Pieces.Pawn) && Math.abs(move.startFile - move.targetFile) == 1) {
            // en passant capture
            int pawnRank = move.startRank;
            pieceList.remove(getPiece(move.targetFile, pawnRank));
        } else if (move.capture != null) {
            pieceList.remove(move.capture);
        }
    }

    public static boolean isValid(Move move) {
        if (!move.piece.isColor(move.capture) && move.piece.isValidMove(move.targetFile, move.targetRank)) {
            // simulate the move
            return CheckHandler.simulateAndCheck(move);
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
                view.setColor((f + r) % 2 == 0 ? new Color(232, 148, 148) : new Color(78, 9, 9) );
                view.fillRect(f * fieldSize, r * fieldSize, fieldSize, fieldSize);
            }
        }

        // highlight
        if (selectedPiece != null) {
            view.setColor(new Color(233, 84, 238, 185));
            view.fillRect(selectedPiece.file * fieldSize, selectedPiece.rank * fieldSize, fieldSize, fieldSize);
            for (int r = 0; r < ranks; r++) {
                for (int f = 0; f < files; f++) {
                    if (isValid(new Move(selectedPiece, f, r))) {
                        view.setColor(new Color(0, 255, 0, 100));
                        view.fillRect(f * fieldSize, r * fieldSize, fieldSize, fieldSize);
                    }

                }
            }
        }

        if (CheckHandler.isKingChecked(colorToMove)) {
            Piece king = findKing(colorToMove);
            view.setColor(new Color(255, 0, 0, 255));
            view.fillRect(king.file * fieldSize, king.rank * fieldSize, fieldSize, fieldSize);
        }

        for (Piece piece : pieceList) {
            piece.paint(view);
        }
    }
}
