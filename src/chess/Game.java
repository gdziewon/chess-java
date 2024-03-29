package chess;

import chess.board.Board;
import java.util.Scanner;


public class Game {
    private final Board board;
    private boolean isWhiteTurn = true;
    private final Scanner scanner = new Scanner(System.in);


    public Game() {
        board = new Board();
    }

    public void startGame() {
        while (true) {
            board.printBoard(isWhiteTurn);
            System.out.println(isWhiteTurn ? "White turn" : "Black turn");
            while (true) {
                System.out.println("Enter move:");
                String move = scanner.nextLine();
                if (move.equals("exit")) {
                    return;
                }
                int[][] parsedMove = parseMove(move);
                if (parsedMove == null) {
                    System.out.println("Invalid input");
                    continue;
                }
                if (board.movePiece(parsedMove[0], parsedMove[1], isWhiteTurn)) {
                    isWhiteTurn = !isWhiteTurn;
                    break;
                }
            }
        }
    }

    private static int[][] parseMove(String move) {
        move = move.toUpperCase().replaceAll("\\s","");
        if (move.length() != 4) {
            return null;
        }
        int[] start = parsePosition(move.substring(0, 2));
        int[] end = parsePosition(move.substring(2, 4));
        if (start == null || end == null) {
            return null;
        }
        return new int[][]{start, end};
    }

    private static int[] parsePosition(String position) {
        try {
            int x = Character.getNumericValue(position.charAt(1)) - 1;
            int y = position.charAt(0) - 'A';
            if (x < 0 || x > 7 || y < 0 || y > 7) {
                return null;
            }
            return new int[]{x, y};
        } catch (Exception e) {
            return null;
        }
    }
}