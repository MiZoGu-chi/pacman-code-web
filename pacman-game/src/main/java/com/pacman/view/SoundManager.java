package view;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import javax.sound.sampled.*;

public class SoundManager {

    private static SoundManager instance;

    private Clip exclusiveClip;

    // On stocke les sons déjà ouverts 
    // Sans ça on a des problèmes de freeze
    private final Map<String, Clip> Clips = new HashMap<>();
    
    private SoundManager() {}

    public static synchronized  SoundManager getInstance() {
        if(instance == null) {
            instance = new SoundManager();
        }
        return instance;
    }

    public synchronized void playSound(String filename) {
        new Thread(() -> {
            try {
                AudioInputStream audioIn = AudioSystem.getAudioInputStream(new File(filename));
                Clip clip = AudioSystem.getClip();
                clip.open(audioIn);
                clip.start();
                clip.addLineListener(e -> {
                    if (e.getType() == LineEvent.Type.STOP) clip.close();
                });
            } catch (Exception e) {
                System.err.println("Erreur son : " + filename);
            }
        }).start();
    }

    // On utilisera cette méthode pour les sons "chomp"
    // afin d'éviter qu'ils s'accumulent lorsque la vitesse du jeu est accélérée
    public synchronized void playSoundExclusive(String filename) {
        
        // Si n'importe quel son tourne, on bloque 
        for(Clip c : Clips.values()) {
            if(c.isRunning()) return; 
        }

        try {
            if(!Clips.containsKey(filename)) {
                Clip nouveauClip = AudioSystem.getClip();
                nouveauClip.open(AudioSystem.getAudioInputStream(new File(filename)));
                Clips.put(filename, nouveauClip);
            }

            Clip clip = Clips.get(filename);
            clip.setFramePosition(0);
            clip.start();

        } catch (Exception e) {
            System.err.println("Erreur son : " + filename);
        }
    }
}