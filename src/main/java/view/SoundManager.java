package view;

import java.io.File;
import javax.sound.sampled.*;

public class SoundManager {

    private static SoundManager instance;

    private SoundManager() {}

    public static SoundManager getInstance() {
        if (instance == null) {
            instance = new SoundManager();
        }
        return instance;
    }

    public void playSound(String filename) {
        new Thread(() -> {
            try {
                AudioInputStream audioIn = AudioSystem.getAudioInputStream(new File(filename));
                Clip clip = AudioSystem.getClip();
                clip.open(audioIn);
                clip.start();
                // On libère la mémoire une fois fini
                clip.addLineListener(e -> {
                    if (e.getType() == LineEvent.Type.STOP) clip.close();
                });
            } catch (Exception e) {
                System.err.println("Erreur son : " + filename);
            }
        }).start();
    }
}