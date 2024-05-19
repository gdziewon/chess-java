package chess.gui;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import java.util.Objects;

public class SoundEffects {
    public static final int START = 0;
    public static final int MOVE1 = 1;
    public static final int MOVE2 = 2;
    public static final int CAPTURE = 3;
    public static final int CASTLE = 4;
    public static final int CHECK = 5;
    public static final int CHECKMATE = 6;
    public static final int STALEMATE = 7;

    private static final Clip[] sounds;

    static {
        sounds = new Clip[8];
        sounds[START] = loadSound("start");
        sounds[MOVE1] = loadSound("move1");
        sounds[MOVE2] = loadSound("move2");
        sounds[CAPTURE] = loadSound("capture");
        sounds[CASTLE] = loadSound("castle");
        sounds[CHECK] = loadSound("check");
        sounds[CHECKMATE] = loadSound("checkmate");
        sounds[STALEMATE] = loadSound("stalemate");
    }


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

    public static void playSound(int sound) {
        if (sounds[sound] != null) {
            sounds[sound].setFramePosition(0);
            sounds[sound].start();
        }
    }
}
