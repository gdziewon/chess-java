package chess.game;

import chess.gui.PromotionPanel;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Objects;

public class Piece {
    public int file;
    public int rank;
    public int x;
    public int y;
    public int value;
    Image sprite;

    public boolean justMadeDoubleStep = false;
    public boolean hasMoved = false;

    public static BufferedImage sheet;
    public static int sheetScale;
    static {
        try {
            sheet = ImageIO.read(Objects.requireNonNull(ClassLoader.getSystemResourceAsStream("pieces.png")));
            sheetScale = sheet.getWidth() / 6;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Piece(int value, int file, int rank) {
        this.value = value;
        this.file = file;
        this.rank = rank;
        updatePosition();
        this.sprite = sheet.getSubimage(((value - 1) & 0b111) * sheetScale, (value >> 4) == 0 ? 0 : sheetScale, sheetScale, sheetScale).getScaledInstance(Board.FIELD_SIZE, Board.FIELD_SIZE, BufferedImage.SCALE_SMOOTH);
    }

    public void paint(Graphics2D view) {
        view.drawImage(sprite, x, y, null);
    }

    public void setPos(int file, int rank) {
        this.file = file;
        this.rank = rank;
        this.hasMoved = true;
        updatePosition();
    }

    public void setPosSim(int file, int rank) {
        this.file = file;
        this.rank = rank;
    }

    public void updatePosition() {
        this.y = rank * Board.FIELD_SIZE;
        this.x = file * Board.FIELD_SIZE;
    }

    public boolean isType(int type) {
        return (value & 7) == type;
    }

    public int getType() {
        return value & 0b111;
    }

    public boolean isColor(int color) {
        return (value & color) != 0;
    }

    public boolean isColor(Piece piece) {
        if (piece == null)
            return false;

        return (value >> 4) == (piece.value >> 4);
    }

    public int getColor() {
        return value & 0b11000;
    }

    public boolean isValidMove(int targetFile, int targetRank) {
        return switch (this.getType()) {
            case Pieces.Pawn -> isValidPawnMove(targetFile, targetRank);
            case Pieces.Knight -> isValidKnightMove(targetFile, targetRank);
            case Pieces.Bishop, Pieces.Rook, Pieces.Queen -> isValidSlidingMove(targetFile, targetRank);
            case Pieces.King -> isValidKingMove(targetFile, targetRank);
            default -> false;
        };
    }

    public boolean isValidPawnMove(int targetFile, int targetRank) {
        int direction = this.isColor(Pieces.White) ? -1 : 1;
        int fileDifference = targetFile - this.file;
        int rankDifference = targetRank - this.rank;
        Piece capture = Board.getPiece(targetFile, targetRank);

        // En Passant
        if (Math.abs(targetFile - this.file) == 1 && targetRank - this.rank == direction) {
            Piece potentialEnPassantPawn = Board.getPiece(targetFile, this.rank);
            if (potentialEnPassantPawn != null && potentialEnPassantPawn.isType(Pieces.Pawn) &&
                    potentialEnPassantPawn.justMadeDoubleStep && potentialEnPassantPawn.isColor(Pieces.White) != this.isColor(Pieces.White))
                return true;
        }

        // single step forward
        if (fileDifference == 0 && rankDifference == direction)
            return capture == null;

        // double step forward from initial position
        if (fileDifference == 0 && rankDifference == 2 * direction && (this.rank == (this.isColor(Pieces.White) ? 6 : 1)))
            return Board.getPiece(targetFile, this.rank + direction) == null && capture == null;

        // diagonal capture
        if (Math.abs(fileDifference) == 1 && rankDifference == direction)
            return capture != null && !this.isColor(capture);

        return false;
    }

    public boolean isValidKnightMove(int targetFile, int targetRank) {
        return (Math.abs(targetFile - this.file) == 1 && Math.abs(targetRank - this.rank) == 2) ||
                (Math.abs(targetFile - this.file) == 2 && Math.abs(targetRank - this.rank) == 1);
    }

    public boolean isValidSlidingMove(int targetFile, int targetRank) {
        boolean isStraight = (file - targetFile == 0 || rank - targetRank == 0);
        boolean isDiagonal = (Math.abs(file - targetFile) == Math.abs(rank - targetRank));

        if ((this.isType(Pieces.Rook) && isStraight) ||
                (this.isType(Pieces.Bishop) && isDiagonal) ||
                (this.isType(Pieces.Queen) && (isStraight || isDiagonal))) {

            int stepFile = Integer.compare(targetFile, file);
            int stepRank = Integer.compare(targetRank, rank);
            int checkFile = file + stepFile;
            int checkRank = rank + stepRank;

            while (checkFile != targetFile || checkRank != targetRank) {
                if (Board.getPiece(checkFile, checkRank) != null)
                    return false; // block by a piece
                checkFile += stepFile;
                checkRank += stepRank;
            }

            return true;
        }

        return false;
    }

    public boolean isValidKingMove(int targetFile, int targetRank) {
        if (Math.abs(targetFile - this.file) == 2 && targetRank == this.rank && !this.hasMoved)
            return canCastle(targetFile);

        return Math.abs(targetFile - file) <= 1 && Math.abs(targetRank - rank) <= 1;
    }

    private boolean canCastle(int targetFile) {
        int direction = (targetFile > this.file) ? 1 : -1;
        int rookFile = (direction == 1) ? 7 : 0;
        Piece rook = Board.getPiece(rookFile, this.rank);

        if (rook == null || rook.hasMoved)// rook is present and has not moved
            return false;

        for (int f = this.file + direction; f != targetFile; f += direction) // check that the path is clear
            if (Board.getPiece(f, this.rank) != null)
                return false;

        for (int f = this.file; f != targetFile + direction; f += direction) // check that the king is not going through check
            if (CheckHandler.isSquareAttacked(f, this.rank, this.getColor()))
                return false;

        return true;
    }

    public void checkPromotion(JFrame owner) {
        if (this.isType(Pieces.Pawn) && (this.rank == 0 || this.rank == 7)) {
            PromotionPanel promotionPanel = new PromotionPanel(owner, this.isColor(Pieces.White));
            int newPieceType = promotionPanel.getChosenPiece();
            if (newPieceType != Pieces.None) {
                this.value = newPieceType;
                this.sprite = Piece.sheet.getSubimage(((newPieceType - 1) & 0b111) * Piece.sheetScale, (newPieceType >> 4) == 0 ? 0 : Piece.sheetScale, Piece.sheetScale, Piece.sheetScale).getScaledInstance(Board.FIELD_SIZE, Board.FIELD_SIZE, BufferedImage.SCALE_SMOOTH);
            }
        }
    }
}
