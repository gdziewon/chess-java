package chess.utils;

public class PieceUtils {
    public static int[] calculateDistance(int[] start, int[] end) {
        int dx = Math.abs(end[1] - start[1]); // (A-H)
        int dy = Math.abs(end[0] - start[0]); // (1-8)
        return new int[]{dx, dy};
    }

    public static int[] calculateDistancePawn(int[] start, int[] end) {
        int dx = end[1] - start[1]; // (A-H)
        int dy = end[0] - start[0]; // (1-8)
        return new int[]{dx, dy};
    }


    public static boolean isHorizontalMove(int[] distance) {
        return distance[0] == 0 || distance[1] == 0;
    }

    public static boolean isDiagonalMove(int[] distance) {
        return distance[0] == distance[1];
    }

    public static boolean isKnightMove(int[] distance) {
        return (distance[0] == 2 && distance[1] == 1) || (distance[0] == 1 && distance[1] == 2);
    }

    public static boolean isSingleSquareMove(int[] distance) {
        return distance[0] <= 1 && distance[1] <= 1;
    }
}
