package chess.game;

import chess.board.Board;
import chess.moves.Move;

import java.util.Scanner;

public class UserInterface {
    private final Scanner scanner = new Scanner(System.in);
    private final Board board;


    public UserInterface(Board board) {
        this.board = board;
    }

    public void showTurn(boolean isWhiteTurn) {
        board.getPrinter().printBoard(isWhiteTurn);
        System.out.println((isWhiteTurn ? "White" : "Black") + "'s turn");
    }

    public String getMoveInput() {
        System.out.println("Enter your move (e.g., E2 E4):");
        return scanner.nextLine();
    }

    public void invalidInput() {
        System.out.println("Invalid input");
    }

    public String getPromotionInput() {
        System.out.println("Pawn promotion! Choose a piece (Q, R, B, N):");
        return scanner.nextLine();
    }

    public void inCheck() {
        System.out.println("You are in check!");
    }

    public void illegalMove(String message) {
        System.out.println("Invalid move: " + message);
    }

    public void showCheckmate(boolean isWhite) {
        System.out.println((isWhite ? "Black" : "White") + " wins by checkmate!");
    }

    public void castleThroughCheck() {
        System.out.println("You cannot castle through check.");
    }

    public void moveRejected(Move move) {
        System.out.println("Move rejected: " + move.getValidationResult().message());
    }
    public void moveIntoCheck() {
        System.out.println("You cannot move into check.");
    }
}
