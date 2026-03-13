package com.pacman.controller;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import com.pacman.model.PacmanGame;


public class ControllerPacmanGame extends AbstractController implements PropertyChangeListener  {

    public ControllerPacmanGame(PacmanGame game) {
        super(game);

        game.addPropertyChangeListener(this);
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        if("layoutChanged".equals(evt.getPropertyName())) {
            String newLayout = (String) evt.getNewValue();
            System.out.println("ControllerPacmaneGame : new Layout : " + newLayout);
            ((PacmanGame) getGame()).loadNewMaze(newLayout);
        }
    }
}
