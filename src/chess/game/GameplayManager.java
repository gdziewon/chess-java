package chess.game;

import chess.board.Board;
import chess.moves.Move;
import chess.moves.MoveType;
import chess.moves.ValidationResult;

public class GameplayManager {
    private final Player[] players;
    private final Board board;
    private final GameStateManager gameStateManager;
    private final UserInterface ui;


    GameplayManager(Board board, UserInterface ui, Player[] players) {
        this.board = board;
        this.players = players;
        this.ui = ui;

        gameStateManager = new GameStateManager(board);
    }

    public boolean move(String move, boolean isWhite) {
        Player currentPlayer = isWhite ? players[0] : players[1];
        int[][] parsedMove = parseMove(move);
        if (parsedMove == null) {
            return false;
        }

        Move boardMove = new Move(parsedMove[0], parsedMove[1], board.getField(parsedMove[0]), board.getField(parsedMove[1]), isWhite);

        ValidationResult validationResult = board.move(boardMove);
        if (validationResult.moveType() == MoveType.ILLEGAL) {
            ui.illegalMove(validationResult.message());
            return false;
        }

        if (gameStateManager.isInCheck(currentPlayer.isWhite())) {
            ui.illegalMove("You cannot move into check.");
            board.undoMove(boardMove);
            return false;
        }

        if (boardMove.getMoveType() == MoveType.PROMOTION) {
            promotion(boardMove);
        }

        updateGameStates();
        return true;
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

    public void updateGameStates() {
        players[0].setInCheck(gameStateManager.isInCheck(players[0].isWhite()));
        players[1].setInCheck(gameStateManager.isInCheck(players[1].isWhite()));
    }

    private void promotion(Move move) {
        while (true) {
            String piece = ui.getPromotionInput();

            if (!isValidPromotionInput(piece)) {
                ui.invalidInput();
                continue;
            }

            board.promotePawn(piece, move);
            break;
        }
    }

    public boolean isValidPromotionInput(String input) {
        return input.length() == 1 && "QRBN".contains(input);
    }
}
