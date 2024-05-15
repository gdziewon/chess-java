package chess.gui;

import chess.game.Pieces;

import javax.sound.sampled.*;
import java.util.Objects;

public class SoundEffects {
    private final static Clip start = loadSound("start");
    private final static Clip move1 = loadSound("move1");
    private final static Clip move2 = loadSound("move2");
    private final static Clip capture = loadSound("capture");
    private final static Clip castle = loadSound("castle");
    private final static Clip check = loadSound("check");
    private final static Clip checkmate = loadSound("checkmate");
    private final static Clip stalemate = loadSound("stalemate");


    private static Clip loadSound(String filename) {
        try {
            Clip clip = AudioSystem.getClip();
            AudioInputStream inputStream = AudioSystem.getAudioInputStream(Objects.requireNonNull(SoundEffects.class.getResourceAsStream("/sounds/" + filename)));
            clip.open(inputStream);
            return clip;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static void playStart() {
        assert start != null;
        start.setFramePosition(0);
        start.start();
    }

    public static void playMove(int color) {
        if (color == Pieces.White) {
            assert move1 != null;
            move1.setFramePosition(0);
            move1.start();
        } else {
            assert move2 != null;
            move2.setFramePosition(0);
            move2.start();
        }
    }

    public static void playCapture() {
        assert capture != null;
        capture.setFramePosition(0);
        capture.start();
    }

    public static void playCastle() {
        assert castle != null;
        castle.setFramePosition(0);
        castle.start();
    }

    public static void playCheck() {
        assert check != null;
        check.setFramePosition(0);
        check.start();
    }

    public static void playCheckmate() {
        assert checkmate != null;
        checkmate.setFramePosition(0);
        checkmate.start();
    }

    public static void playStalemate() {
        assert stalemate != null;
        stalemate.setFramePosition(0);
        stalemate.start();
    }
}
