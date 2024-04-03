package chess.game;

import chess.board.Board;


public class Game {
    private final UserInterface ui;
    private final GameplayManager gameplayManager;


    public Game() {
        Board board = new Board();
        ui = new UserInterface(board);
        gameplayManager = new GameplayManager(board, ui);
    }

    public void startGame() {
        boolean isWhiteTurn = true;
        while (true) {
            ui.showTurn(isWhiteTurn);


            if (gameplayManager.getCurrentPlayer(isWhiteTurn).isInCheck()) {
                if (gameplayManager.isInCheckmate(isWhiteTurn)) {
                    ui.showCheckmate(isWhiteTurn);
                    return;
                } else {
                    ui.inCheck();
                }
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