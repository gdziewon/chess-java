package chess.game;

import chess.board.Board;


public class Game {
    private final UserInterface ui;
    private final GameplayManager gameplayManager;

    private final Player[] players;


    public Game() {
        players = new Player[]{new Player(true),
                new Player(false)};

        Board board = new Board();
        ui = new UserInterface(board);
        gameplayManager = new GameplayManager(board, ui, players);
    }

    public void startGame() {
        boolean isWhiteTurn = true;
        while (true) {
            ui.showTurn(isWhiteTurn);

            if (players[isWhiteTurn ? 0 : 1].isInCheck()) {
                ui.inCheck();
            }

            turn(isWhiteTurn);

            isWhiteTurn = !isWhiteTurn;
        }
    }

    private void turn(boolean isWhiteTurn) {
        while (true) {
            String move = ui.getMoveInput();
            if (gameplayManager.move(move, isWhiteTurn)) {
                return;
            }
        }
    }
}