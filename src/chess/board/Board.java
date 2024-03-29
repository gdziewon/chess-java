package chess.board;
import chess.utils.PieceFactory;
import chess.utils.Pieces;

public class Board {
    private final int boardSize = 8;
    private final Field[][] fields = new Field[boardSize][boardSize];

    public Board() {
        for (int i = 0; i < boardSize; i++) {
            for (int j = 0; j < boardSize; j++) {
                fields[i][j] = new Field(null, (i + j) % 2 == 0);
            }
        }
        setPieces(true);
        setPieces(false);
    }

    private void setPieces(boolean isWhite) {
        int pawnRow = isWhite ? 1 : 6;
        int figureRow = isWhite ? 0 : 7;
        for (int i = 0; i < boardSize; i++) {
            fields[pawnRow][i].setPiece(PieceFactory.createPiece(Pieces.PAWN, isWhite));
        }
        fields[figureRow][0].setPiece(PieceFactory.createPiece(Pieces.ROOK, isWhite));
        fields[figureRow][1].setPiece(PieceFactory.createPiece(Pieces.KNIGHT, isWhite));
        fields[figureRow][2].setPiece(PieceFactory.createPiece(Pieces.BISHOP, isWhite));
        fields[figureRow][3].setPiece(PieceFactory.createPiece(Pieces.QUEEN, isWhite));
        fields[figureRow][4].setPiece(PieceFactory.createPiece(Pieces.KING, isWhite));
        fields[figureRow][5].setPiece(PieceFactory.createPiece(Pieces.BISHOP, isWhite));
        fields[figureRow][6].setPiece(PieceFactory.createPiece(Pieces.KNIGHT, isWhite));
        fields[figureRow][7].setPiece(PieceFactory.createPiece(Pieces.ROOK, isWhite));
    }

    public void printBoard(boolean isWhiteTurn) {
        printLetterRow();
        int start = isWhiteTurn ? boardSize - 1 : 0;
        int end = isWhiteTurn ? -1 : boardSize;
        int step = isWhiteTurn ? -1 : 1;

        for (int i = start; isWhiteTurn ? i > end : i < end; i += step) {
            printLine();
            System.out.print((i + 1) + " |");
            for (int j = 0; j < boardSize; j++) {
                fields[i][j].printField();
                System.out.print("|");
            }
            System.out.println(" " + (i + 1));
        }
        printLine();
        printLetterRow();
    }

    private void printLetterRow() {
        System.out.print("  ");
        for (int i = 0; i < boardSize; i++) {
            System.out.print("  " + (char)('A' + i) + " ");
        }
        System.out.println();
    }

    private void printLine() {
        System.out.print("  +");
        System.out.print("---+".repeat(boardSize));
        System.out.println();
    }

    public Field getField(int x, int y) {
        return fields[x][y];
    }

    public void setField(int x, int y, Field field) {
        fields[x][y] = field;
    }

    public boolean movePiece(int[] start, int[] end, boolean isWhiteTurn) {
        Field startField = getField(start[0], start[1]);
        Field endField = getField(end[0], end[1]);

        if (!isMoveValid(startField, start, end, isWhiteTurn)) {
            return false;
        }

        endField.setPiece(startField.getPiece());
        startField.getPiece().setHasMoved(true);
        startField.setPiece(null);
        return true;
    }

    private boolean isMoveValid(Field startField, int[] start, int[] end, boolean isWhiteTurn) {
        if (startField.isEmpty()) {
            System.out.println("There is no piece on the start field.");
            return false;
        }
        if (isWhiteTurn != startField.isPieceWhite()) {
            System.out.printf("You can only move %s pieces.%n",
                    isWhiteTurn ? "white" : "black");
            return false;
        }
        if (!startField.checkMove(start, end)) {
            System.out.printf("Piece '%s' can't move like that.%n",
                    startField.getPiece().getClass().getSimpleName());
            return false;
        }
        return true;
    }
}

