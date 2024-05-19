package chess.game;

import chess.gui.Input;
import chess.gui.SoundEffects;

import javax.swing.*;
import java.awt.*;
import java.util.HashMap;
import java.util.concurrent.CopyOnWriteArrayList;

public class Board extends JPanel {
    public static final String START_FEN = "rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1";
    public static final int FIELD_SIZE = 110;
    public static final int FILES = 8;
    public static final int RANKS = 8;

    static final Color lightField = new Color(232, 148, 148);
    static final Color darkField = new Color(78, 9, 9);
    static final Color highlight = new Color(0, 255, 0, 100);
    static final Color capture = new Color(255, 0, 0, 150);
    static final Color check = new Color(29, 38, 248, 150);
    public static Piece selectedPiece;

    static CopyOnWriteArrayList<Piece> pieceList;
    public static Piece[] kings; // white, black
    Input input;
    public static int colorToMove = Pieces.White;

    public Board(String fen) {
        input = new Input(this);
        this.setPreferredSize(new Dimension(RANKS * FIELD_SIZE, FILES * FIELD_SIZE));
        this.addMouseListener(input);
        this.addMouseMotionListener(input);
        pieceList = new CopyOnWriteArrayList<>();
        kings = new Piece[2];
        loadFromFen(fen);
        GameStateHandler.checkGameStatus();
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
                if (Character.isDigit(symbol))
                    file += Character.getNumericValue(symbol);
                else {
                    int color = (Character.isLowerCase(symbol)) ? Pieces.White : Pieces.Black;
                    int piece = pieceMap.get(Character.toLowerCase(symbol));
                    Piece newPiece = new Piece(piece | color, file, rank);
                    pieceList.add(newPiece);
                    if (newPiece.isType(Pieces.King))
                        kings[color == Pieces.White ? 0 : 1] = newPiece;
                    file++;
                }
            }
        }
        colorToMove = fen.split(" ")[1].equals("w") ? Pieces.White : Pieces.Black;
    }

    public static void makeMove(Move move, JFrame owner) {
        if (move.isCastling)
            handleCastle(move);
        else
            handleMove(move, owner);

        colorToMove = colorToMove == Pieces.White ? Pieces.Black : Pieces.White;
        GameStateHandler.updateDoubleStepFlag(move);
        GameStateHandler.checkGameStatus();
    }

    public static void handleMove(Move move, JFrame owner) {
        move.piece.setPos(move.targetFile, move.targetRank);
        SoundEffects.playSound(move.piece.getColor() == Pieces.White ? SoundEffects.MOVE1 : SoundEffects.MOVE2);
        capture(move);
        move.piece.checkPromotion(owner);
    }

    public static void handleCastle(Move move) {
        int direction = move.targetFile > move.startFile ? 1 : -1;
        int rookStartFile = (direction == 1) ? 7 : 0;
        int rookEndFile = (direction == 1) ? move.targetFile - 1 : move.targetFile + 1;
        move.piece.setPos(move.targetFile, move.targetRank); // king
        Piece rook = getPiece(rookStartFile, move.startRank);
        assert rook != null;
        rook.setPos(rookEndFile, move.startRank); // rook
        SoundEffects.playSound(SoundEffects.CASTLE);
    }

    public static void capture(Move move) {
        if (move.capture == null && move.piece.isType(Pieces.Pawn) && Math.abs(move.startFile - move.targetFile) == 1) {
            pieceList.remove(getPiece(move.targetFile, move.startRank)); // en passant capture
            SoundEffects.playSound(SoundEffects.CAPTURE);
        } else if (move.capture != null) {
            pieceList.remove(move.capture);
            SoundEffects.playSound(SoundEffects.CAPTURE);
        }
    }

    public static boolean isValid(Move move) {
        if (!move.piece.isColor(move.capture) && move.piece.isValidMove(move.targetFile, move.targetRank, move))
            return GameStateHandler.simulateAndCheck(move); // simulate the move and check if the king will be in check
        return false;
    }

    public static void paintSquare(Graphics2D view, int file, int rank, Color color) {
        view.setColor(color);
        view.fillRect(file * FIELD_SIZE, rank * FIELD_SIZE, FIELD_SIZE, FIELD_SIZE);
    }

    public static Piece getPiece(int file, int rank) {
        for (Piece piece : pieceList)
            if (piece.file == file && piece.rank == rank)
                return piece;
        return null;
    }

    public void paintComponent(Graphics graphics) {
        Graphics2D view = (Graphics2D) graphics;

        // board
        for (int r = 0; r < RANKS; r++)
            for (int f = 0; f < FILES; f++)
                paintSquare(view, f, r, (f + r) % 2 == 0 ? lightField : darkField);

        // highlight
        if (selectedPiece != null)
            for (int r = 0; r < RANKS; r++)
                for (int f = 0; f < FILES; f++) {
                    Move move = new Move(selectedPiece, f, r);
                    if (isValid(move))
                        paintSquare(view, f, r, move.capture != null || move.isEnPassant ? capture : highlight);
                }

        // check
        if (GameStateHandler.isKingChecked(colorToMove)) {
            Piece king = Board.kings[colorToMove == Pieces.White ? 0 : 1];
            assert king != null;
            paintSquare(view, king.file, king.rank, check);
        }

        // pieces
        for (Piece piece : pieceList)
            piece.paint(view);
    }
}
