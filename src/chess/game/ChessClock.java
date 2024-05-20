package chess.game;

import chess.gui.GameFinishedPanel;

import javax.swing.*;
import java.awt.*;

public class ChessClock extends JLabel {
    private int timeLeft;
    private Timer timer;
    private final boolean isWhite;
    private static final Font FONT = new Font("Monospaced", Font.BOLD, 18);

    public ChessClock(int timeInSeconds, boolean isWhite, Board board) {
        this.timeLeft = timeInSeconds;
        this.isWhite = isWhite;
        this.timer = new Timer(1000, e -> {
            timeLeft--;
            board.repaint();
            if (timeLeft <= 0) {
                timer.stop();
                GameFinishedPanel.displayTimeOut(Board.colorToMove);
            }
        });
        this.setFont(FONT);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        int rectWidth = 64;
        int rectHeight = 24;
        int rectX = 0;
        int rectY = isWhite ? Board.FIELD_SIZE * Board.FILES - rectHeight : 0;

        g2d.setColor(Color.BLACK);
        g2d.fillRect(rectX, rectY, rectWidth, rectHeight);

        String timeStr = formatTime(timeLeft);
        FontMetrics fm = g2d.getFontMetrics(FONT);
        int textX = rectX + (rectWidth - fm.stringWidth(timeStr)) / 2;
        int textY = rectY + ((rectHeight - fm.getHeight()) / 2) + fm.getAscent();

        g2d.setColor(Color.WHITE);
        g2d.setFont(FONT);
        g2d.drawString(timeStr, textX, textY);
    }

    public void start() {
        timer.start();
    }

    public void stop() {
        timer.stop();
    }

    private String formatTime(int timeInSeconds) {
        int minutes = timeInSeconds / 60;
        int seconds = timeInSeconds % 60;
        return String.format("%02d:%02d", minutes, seconds);
    }
}