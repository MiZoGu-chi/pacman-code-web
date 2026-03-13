package controller;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import view.SoundManager;

public class SoundObserver implements PropertyChangeListener {

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        String event = evt.getPropertyName();

        switch (event) {
            case "FoodEaten" -> SoundManager.getInstance().playSoundExclusive("sounds/pacman_chomp.wav");
            case "GameStart" -> SoundManager.getInstance().playSound("sounds/pacman_beginning.wav");
            case "PacmanDeath" -> SoundManager.getInstance().playSound("sounds/pacman_death.wav");
            case "GhostEaten" -> SoundManager.getInstance().playSound("sounds/pacman_eatghost.wav");
        }
    }  
} 