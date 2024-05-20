package chess.game;

import chess.gui.Input;
import static chess.game.Pieces.*;
import static chess.gui.SoundEffects.*;
import static chess.game.GameStateHandler.*;

import javax.swing.*;
import java.awt.*;
import java.util.HashMap;
import java.util.concurrent.CopyOnWriteArrayList;

public class Board extends JPanel {
    public static final String START_FEN = "rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1";
    public static final int FIELD_SIZE = 110;
    public static final int FILES = 8;
    public static final int RANKS = 8;

    Input input;

    static final Color lightField = new Color(232, 148, 148);
    static final Color darkField = new Color(78, 9, 9);
    static final Color highlight = new Color(0, 255, 0, 100);
    static final Color capture = new Color(255, 0, 0, 150);
    static final Color check = new Color(29, 38, 248, 150);

    static CopyOnWriteArrayList<Piece> pieceList;
    public static Piece[] kings; // white, black
    public static Piece selectedPiece;
    public static int colorToMove = White;

    public Board(String fen) {
        input = new Input(this);
        this.setPreferredSize(new Dimension(RANKS * FIELD_SIZE, FILES * FIELD_SIZE));
        this.addMouseListener(input);
        this.addMouseMotionListener(input);
        pieceList = new CopyOnWriteArrayList<>();
        kings = new Piece[2];
        loadFromFen(fen);
        init(600, 600, this); // 10 minutes
        checkGameState();
    }

    public static void loadFromFen(String fen) {
        HashMap<Character, Integer> pieceMap = new HashMap<>();
        pieceMap.put('p', Pawn);
        pieceMap.put('n', Knight);
        pieceMap.put('b', Bishop);
        pieceMap.put('r', Rook);
        pieceMap.put('q', Queen);
        pieceMap.put('k', King);

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
                    int color = (Character.isLowerCase(symbol)) ? White : Black;
                    int piece = pieceMap.get(Character.toLowerCase(symbol));
                    Piece newPiece = new Piece(piece | color, file, rank);
                    pieceList.add(newPiece);
                    if (newPiece.isType(King))
                        kings[color == White ? 0 : 1] = newPiece;
                    file++;
                }
            }
        }
        colorToMove = fen.split(" ")[1].equals("w") ? White : Black;
    }

    public static void makeMove(Move move, JFrame owner) {
        if (move.isCastling)
            handleCastle(move);
        else
            handleMove(move, owner);

        switchTurns();
        updateFlags(move);
        checkGameState();
    }

    public static void handleMove(Move move, JFrame owner) {
        move.piece.setPos(move.targetFile, move.targetRank);
        playSound(move.piece.getColor() == White ? MOVE1_SOUND : MOVE2_SOUND);
        capture(move);
        move.piece.checkPromotion(owner);
    }

    public static void handleCastle(Move move) {
        int direction = move.targetFile > move.startFile ? 1 : -1;
        int rookStartFile = (direction == 1) ? 7 : 0;
        int rookEndFile = (direction == 1) ? move.targetFile - 1 : move.targetFile + 1;
        // king
        move.piece.setPos(move.targetFile, move.targetRank);
        Piece rook = getPiece(rookStartFile, move.startRank);
        assert rook != null;
        // rook
        rook.setPos(rookEndFile, move.startRank);
        playSound(CASTLE_SOUND);
    }

    public static void capture(Move move) {
        if (move.capture == null && move.piece.isType(Pawn) && Math.abs(move.startFile - move.targetFile) == 1) {
            pieceList.remove(getPiece(move.targetFile, move.startRank)); // en passant capture
            playSound(CAPTURE_SOUND);
        } else if (move.capture != null) {
            pieceList.remove(move.capture);
            playSound(CAPTURE_SOUND);
        }
    }

    public static boolean isValid(Move move) {
        if (!move.piece.isColor(move.capture) && move.piece.isValidMove(move.targetFile, move.targetRank, move))
            return simulateAndCheck(move); // simulate the move and check if the king will be in check
        return false;
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
        if (isKingChecked(colorToMove)) {
            Piece king = Board.kings[colorToMove == White ? 0 : 1];
            assert king != null;
            paintSquare(view, king.file, king.rank, check);
        }

        // pieces
        for (Piece piece : pieceList)
            piece.paint(view);

        // clocks
        clocks[0].paintComponent(view);
        clocks[1].paintComponent(view);
    }

    public static void paintSquare(Graphics2D view, int file, int rank, Color color) {
        view.setColor(color);
        view.fillRect(file * FIELD_SIZE, rank * FIELD_SIZE, FIELD_SIZE, FIELD_SIZE);
    }
}