package chess.game;

import chess.board.Board;
import chess.moves.Move;


public class Game {
    private final Board board;
    private final Player[] players;
    private final UserInterface ui;


    public Game() {
        ui = new UserInterface();
        board = new Board(ui);
        players = new Player[]{new Player(true, board.getField(7, 4)),
                new Player(false, board.getField(0, 4))};
    }

    public void startGame() {
        Player currentPlayer = players[0];
        while (true) {
            ui.showTurn(board, currentPlayer.isWhite());
            while (true) {
                if (turn(currentPlayer)) {
                    currentPlayer = currentPlayer == players[0] ? players[1] : players[0];
                    break;
                }
            }
        }
    }

    private boolean turn(Player player) {
        String move = ui.getMoveInput();
        int[][] parsedMove = parseMove(move);
        if (parsedMove == null) {
            ui.invalidInput();
            return false;
        }

        Move boardMove = new Move(parsedMove[0], parsedMove[1], player);
        return board.move(boardMove);
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