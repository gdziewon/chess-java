package chess.board;
import chess.moves.Move;
import chess.moves.MoveType;
import chess.moves.MoveValidator;
import chess.moves.ValidationResult;
import chess.pieces.Piece;
import chess.utils.PieceFactory;
import chess.utils.Pieces;

public class Board {
    public static final int BOARD_SIZE = 8;
    private final Field[][] fields = new Field[BOARD_SIZE][BOARD_SIZE];
    private final MoveValidator moveValidator;

    private final Printer printer = new Printer();

    private Piece capturedPiece;
    private Piece movedPiece;



    public Board() {
        moveValidator = new MoveValidator(this);

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

    public Printer getPrinter() {
        return printer;
    }

    public MoveValidator getMoveValidator() {
        return moveValidator;
    }

    public Field getField(int x, int y) {
        if (fields[x][y] == null) {
            throw new IllegalArgumentException("Field does not exist.");
        }
        return fields[x][y] ;
    }

    public Field getField(int[] position) {
        return fields[position[0]][position[1]];
    }

    public ValidationResult move(Move move) {

        ValidationResult validationResult = moveValidator.validateMove(move);
        if (validationResult.moveType() == MoveType.ILLEGAL) {
            return validationResult;
        }

        move.setMoveType(validationResult.moveType());

        executeMove(move);

        return validationResult;
    }

    public void executeMove(Move move) {
        movedPiece = move.getStartField().getPiece().copyPiece();
        capturedPiece = move.getEndField().getPiece() != null ? move.getEndField().getPiece().copyPiece() : null;

        switch (move.getMoveType()) {
            case LEGAL -> {
                executeStandardMove(move);
                if (isPromotion(move)) {
                    move.setMoveType(MoveType.PROMOTION);
                }
            }
            case CASTLE -> executeCastleMove(move);
            default -> throw new IllegalStateException("Unexpected value: " + move.getMoveType());
        }
    }

    public void undoMove(Move move) {
        move.getStartField().setPiece(movedPiece);
        move.getEndField().setPiece(capturedPiece);

        capturedPiece = null;
        movedPiece = null;
    }


    private void executeStandardMove(Move move) {
        move.getEndField().setPiece(move.getStartField().getPiece());
        move.getStartField().setPiece(null);
        move.getEndField().getPiece().setHasMoved(true);
    }

    private boolean isPromotion(Move move) {
        return move.getEndField().getPiece().getName().equals("Pawn") &&
                (move.getEnd()[0] == 0 || move.getEnd()[0] == 7);
    }

    public void promotePawn(String piece, Move move) {
        Piece promotedPiece = PieceFactory.promotePawn(piece, move.getIsWhite());
        move.getEndField().setPiece(promotedPiece);
    }



    private void executeCastleMove(Move move) {
        int[] kingStartPos = move.getStart();
        int[] rookStartPos = move.getEnd();

        Field kingStartField = move.getStartField();
        Field rookStartField = move.getEndField();

        boolean isKingside = rookStartPos[1] > kingStartPos[1];

        int kingEndColumn = isKingside ? kingStartPos[1] + 2 : kingStartPos[1] - 2;
        int rookEndColumn = isKingside ? kingEndColumn - 1 : kingEndColumn + 1;

        // get fields for the new positions
        Field kingEndField = getField(kingStartPos[0], kingEndColumn);
        Field rookEndField = getField(rookStartPos[0], rookEndColumn);

        // move the king
        kingEndField.setPiece(kingStartField.getPiece());
        kingStartField.setPiece(null);
        kingEndField.getPiece().setHasMoved(true);

        // move the rook
        rookEndField.setPiece(rookStartField.getPiece());
        rookStartField.setPiece(null);
        rookEndField.getPiece().setHasMoved(true);
    }
}

