package chess.board;

import chess.moves.Move;
import chess.moves.MoveType;
import chess.pieces.Piece;
import chess.utils.BoardUtils;
import chess.utils.PieceFactory;
import chess.utils.Pieces;
import static chess.utils.Constants.BOARD_SIZE;

public class Board {
    private final Field[][] fields = new Field[BOARD_SIZE][BOARD_SIZE];

    private final Printer printer = new Printer();

    private final Executor executor = new Executor();

    private Piece capturedPiece;
    private Piece movedPiece;
    private int[] capturedPawnPosition;



    public Board() {
        Initializer initializer = new Initializer();
        initializer.initializeBoard();
    }

    class Initializer {
        public void initializeBoard() {
            for (int i = 0; i < BOARD_SIZE; i++) {
                for (int j = 0; j < BOARD_SIZE; j++) {
                    fields[i][j] = new Field(null, (i + j) % 2 == 0);
                }
            }

            setPiecesOnBoard();
        }

        private void setPiecesOnBoard() {
            setupPieceRow(0, true);  // white pieces
            setupPieceRow(7, false); // black pieces
            setupPawnRow(1, true);   // white pawns
            setupPawnRow(6, false);  // black pawns
        }

        private void setupPieceRow(int row, boolean isWhite) {
            Pieces[] pieces = {Pieces.ROOK, Pieces.KNIGHT, Pieces.BISHOP,
                    Pieces.QUEEN, Pieces.KING, Pieces.BISHOP,
                    Pieces.KNIGHT, Pieces.ROOK};

            for (int i = 0; i < BOARD_SIZE; i++) {
                fields[row][i].setPiece(PieceFactory.createPiece(pieces[i], isWhite));
            }
        }

        private void setupPawnRow(int row, boolean isWhite) {
            for (int i = 0; i < BOARD_SIZE; i++) {
                fields[row][i].setPiece(PieceFactory.createPiece(Pieces.PAWN, isWhite));
            }
        }
    }

    public class Printer {
        public void printBoard(boolean isWhiteTurn) {
            printLetterRow();
            if (isWhiteTurn) {
                for (int i = BOARD_SIZE - 1; i >= 0; i--) {
                    printFields(i);
                }
            } else {
                for (int i = 0; i < BOARD_SIZE; i++) {
                    printFields(i);
                }
            }
            printLine();
            printLetterRow();
        }

        private void printFields(int row) {
            printLine();
            System.out.print((row + 1) + " |");
            for (int j = 0; j < BOARD_SIZE; j++) {
                fields[row][j].printField();
                System.out.print("|");
            }
            System.out.println(" " + (row + 1));
        }

        private void printLetterRow() {
            System.out.print("  ");
            for (int i = 0; i < BOARD_SIZE; i++) {
                System.out.print("  " + (char)('A' + i) + " ");
            }
            System.out.println();
        }

        private void printLine() {
            System.out.print("  +");
            System.out.print("---+".repeat(BOARD_SIZE));
            System.out.println();
        }
    }

    public class Executor {
        public void executeMove(Move move) {
            movedPiece = move.getStartField().getPiece().copyPiece();
            capturedPiece = move.getEndField().getPiece() != null ? move.getEndField().getPiece().copyPiece() : null;

            switch (move.getValidationResult().moveType()) {
                case STANDARD -> movePiece(move);
                case EN_PASSANT -> executeEnPassant(move);
                case CASTLE -> executeCastleMove(move);
            }
        }

        public void undoMove(Move move) {
            if (move.getValidationResult().moveType() == MoveType.EN_PASSANT) {
                getField(capturedPawnPosition).setPiece(capturedPiece);
            } else {
                move.getEndField().setPiece(capturedPiece);
            }

            move.getStartField().setPiece(movedPiece);

            capturedPiece = null;
            movedPiece = null;
        }


        public void movePiece(Move move) {
            move.getEndField().setPiece(move.getStartField().getPiece());
            move.getStartField().setPiece(null);
            move.getEndField().getPiece().setHasMoved(true);

        }

        public void promotePawn(String piece, Move move) {
            Piece promotedPiece = PieceFactory.promotePawn(piece, move.getIsWhite());
            move.getEndField().setPiece(promotedPiece);
        }

        public void executeEnPassant(Move move) {
            movePiece(move);

            capturedPawnPosition = new int[]{move.getStart()[0], move.getEnd()[1]};
            capturedPiece = getField(capturedPawnPosition).getPiece().copyPiece();
            getField(capturedPawnPosition).setPiece(null);
        }


        public void executeCastleMove(Move move) {
            int[] kingStartPos = move.getStart();
            int[] rookStartPos = move.getEnd();

            Field kingStartField = move.getStartField();
            Field rookStartField = move.getEndField();

            boolean isKingside = BoardUtils.isKingsideCastle(rookStartPos, kingStartPos);

            int kingEndColumn = BoardUtils.calculateKingEndColumn(kingStartPos, isKingside);
            int rookEndColumn = BoardUtils.calculateRookEndColumn(kingEndColumn, isKingside);

            int[] kingEndPos = {kingStartPos[0], kingEndColumn};
            int[] rookEndPos = {rookStartPos[0], rookEndColumn};

            Field kingEndField = getField(kingEndPos);
            Field rookEndField = getField(rookEndPos);

            Move kingMove = new Move(kingStartPos, kingEndPos, kingStartField, kingEndField, move.getIsWhite());
            Move rookMove = new Move(rookStartPos, rookEndPos, rookStartField, rookEndField, move.getIsWhite());

            movePiece(kingMove);
            movePiece(rookMove);
        }
    }

    public Printer getPrinter() {
        return printer;
    }

    public Executor getExecutor() {
        return executor;
    }

    public Field getField(int x, int y) {
        return fields[x][y] ;
    }

    public Field getField(int[] position) {
        return fields[position[0]][position[1]];
    }
}

