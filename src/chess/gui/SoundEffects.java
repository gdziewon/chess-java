package chess.gui;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import java.util.Objects;

public class SoundEffects {
    public static final int START_SOUND = 0;
    public static final int MOVE1_SOUND = 1;
    public static final int MOVE2_SOUND = 2;
    public static final int CAPTURE_SOUND = 3;
    public static final int CASTLE_SOUND = 4;
    public static final int CHECK_SOUND = 5;
    public static final int CHECKMATE_SOUND = 6;
    public static final int STALEMATE_SOUND = 7;

    private static final Clip[] sounds;

    static {
        sounds = new Clip[8];
        sounds[START_SOUND] = loadSound("start");
        sounds[MOVE1_SOUND] = loadSound("move1");
        sounds[MOVE2_SOUND] = loadSound("move2");
        sounds[CAPTURE_SOUND] = loadSound("capture");
        sounds[CASTLE_SOUND] = loadSound("castle");
        sounds[CHECK_SOUND] = loadSound("check");
        sounds[CHECKMATE_SOUND] = loadSound("checkmate");
        sounds[STALEMATE_SOUND] = loadSound("stalemate");
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
