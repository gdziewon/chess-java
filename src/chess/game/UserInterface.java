package chess.game;

import chess.board.Board;

import java.util.Scanner;

public class UserInterface {
    private final Scanner scanner = new Scanner(System.in);

    public void showTurn(Board board, boolean isWhiteTurn) {
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

    public void invalidMove(String message) {
        System.out.println("Invalid move: " + message);
    }

    public void showMessage(String message) {
        System.out.println(message);
    }

    public void showErrorMessage(String message) {
        System.err.println("Error: " + message);
    }
}
