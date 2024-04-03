package chess.utils;

import static chess.utils.Constants.*;

public class GeneralUtils {
    public static int[][] parseMove(String move) {
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

    public static int[] parsePosition(String position) {
        try {
            int x = Character.getNumericValue(position.charAt(1)) - 1;
            int y = position.charAt(0) - 'A';
            if (x < 0 || x >= BOARD_SIZE || y < 0 || y >= BOARD_SIZE) {
                return null;
            }
            return new int[]{x, y};
        } catch (Exception e) {
            return null;
        }
    }

    public static boolean isValidPromotionInput(String input) {
        String upperCaseInput = input.toUpperCase();
        return (upperCaseInput.length() == 1 && PROMOTION_PIECES_SHORT.contains(upperCaseInput)) ||
                (PROMOTION_PIECES_LONG.contains(upperCaseInput));
    }
}
