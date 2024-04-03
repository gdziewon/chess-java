package chess.game;

import chess.board.Board;
import chess.moves.Move;
import chess.moves.MoveValidator;
import chess.moves.ValidationResult;
import chess.utils.BoardUtils;
import chess.utils.GeneralUtils;

import static chess.utils.Constants.BLACK_PLAYER;
import static chess.utils.Constants.WHITE_PLAYER;

public class GameplayManager {
    private final Player[] players;
    private final Board board;
    private final CheckHandler checkHandler;
    private final UserInterface ui;
    private final MoveValidator moveValidator;

    private final Board.Executor executor;

    private Move lastMove;

    GameplayManager(Board board, UserInterface ui) {
        players = new Player[]{new Player(true),
                new Player(false)};

        this.board = board;
        this.ui = ui;
        this.moveValidator = new MoveValidator(board);
        this.checkHandler = new CheckHandler(board, moveValidator);
        this.executor = board.getExecutor();
    }

    public boolean move(String move, boolean isWhite) {
        Move boardMove = createMove(move, isWhite);

        if (boardMove == null || !executeMove(boardMove)) {
            return false;
        }

        if (checkHandler.isInCheck(isWhite)) {
            ui.inCheck();
            executor.undoMove(boardMove);
            return false;
        }

        if (BoardUtils.isPromotion(boardMove)) {
            handlePromotion(boardMove);
        }

        lastMove = boardMove;
        updateGameStates();
        return true;
    }

    public Player getCurrentPlayer(boolean isWhite) {
        return isWhite ? players[WHITE_PLAYER] : players[BLACK_PLAYER];
    }

    private Move createMove(String move, boolean isWhite) {
        int[][] parsedMove = GeneralUtils.parseMove(move);
        if (parsedMove == null) {
            return null;
        }

        return new Move(parsedMove[0], parsedMove[1], board.getField(parsedMove[0]), board.getField(parsedMove[1]), isWhite);
    }

    private boolean executeMove(Move move) {
        moveValidator.validateMove(move);
        ValidationResult validationResult = move.getValidationResult();

        return switch (validationResult.moveType()) {
            case ILLEGAL -> {
                ui.illegalMove(validationResult.message());
                yield false;
            }

            case CASTLE -> {
                if (checkHandler.isInCheckWhileCastling(move)) {
                    ui.castleThroughCheck();
                    yield false;
                }
                executor.executeMove(move);
                yield true;
            }

            case EN_PASSANT -> {
                if (!MoveValidator.isDoublePawnPush(move, lastMove)) {
                    ui.illegalMove("Not a valid en passant move.");
                    yield false;
                }
                executor.executeMove(move);
                yield true;
            }

            case STANDARD -> {
                executor.executeMove(move);
                yield true;
            }
        };

    }

    private void handlePromotion(Move move) {
        while (true) {
            String piece = ui.getPromotionInput();
            if (GeneralUtils.isValidPromotionInput(piece)) {
                executor.promotePawn(piece, move);
                break;
            }
            ui.invalidInput();
        }
    }

    public void updateGameStates() {
        Player whitePlayer = players[WHITE_PLAYER];
        Player blackPlayer = players[BLACK_PLAYER];

        whitePlayer.setInCheck(checkHandler.isInCheck(whitePlayer.isWhite()));
        blackPlayer.setInCheck(checkHandler.isInCheck(blackPlayer.isWhite()));
    }

    public boolean isInCheckmate(boolean isWhite) {
        return checkHandler.isInCheckmate(isWhite);
    }
}