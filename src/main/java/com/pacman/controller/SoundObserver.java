package com.pacman.controller;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import com.pacman.view.SoundManager;

public class SoundObserver implements PropertyChangeListener {

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        String event = evt.getPropertyName();

        switch (event) {
            case "FoodEaten" -> SoundManager.getInstance().playSoundExclusive("src/main/webapp/sounds/pacman_chomp.wav");
            case "GameStart" -> SoundManager.getInstance().playSound("ssrc/main/webapp/ounds/pacman_beginning.wav");
            case "PacmanDeath" -> SoundManager.getInstance().playSound("src/main/webapp/sounds/pacman_death.wav");
            case "GhostEaten" -> SoundManager.getInstance().playSound("src/main/webapp/sounds/pacman_eatghost.wav");
        }
    }  
} 