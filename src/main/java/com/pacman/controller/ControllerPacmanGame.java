package com.pacman.controller;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import com.pacman.model.AgentAction;
import com.pacman.model.PacmanGame;
import com.pacman.model.agents.Pacman;
import com.pacman.model.commands.ChangeDirectionCommand;
import com.pacman.view.ViewCommand;
import com.pacman.view.ViewPacmanGame;

public class ControllerPacmanGame extends AbstractController implements PropertyChangeListener {

    private ViewPacmanGame viewPacman; // N'est plus final
    private ViewCommand viewCommand;   // N'est plus final
    private InputHandler inputHandler;

    public ControllerPacmanGame(PacmanGame game) {
        super(game);
        // On ne crée plus les vues ici !
        
        inputHandler = new InputHandler();
        game.addPropertyChangeListener(this); 

        SoundObserver soundObserver = new SoundObserver();
        game.addPropertyChangeListener(soundObserver);
    }

    public void setViewPacman(ViewPacmanGame view) {
        viewPacman = view;
        viewPacman.setKeyListener(inputHandler);
        viewPacman.addPropertyChangeListener(this);
    }

    public void setViewCommand(ViewCommand view) {
        this.viewCommand = view;
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        if ("layoutChanged".equals(evt.getPropertyName())) {
            String newLayout = (String) evt.getNewValue();
            ((PacmanGame) getGame()).loadNewMaze(newLayout);
            
            if (viewCommand != null) {
                viewCommand.getState().restart();
            }
        }
        
        if ("PacmanCreated".equals(evt.getPropertyName())) {
            Pacman p = (Pacman) evt.getNewValue();
            if (p.isControlled()) { 
                inputHandler.setArrowControls(
                    new ChangeDirectionCommand(p, AgentAction.NORTH),
                    new ChangeDirectionCommand(p, AgentAction.SOUTH),
                    new ChangeDirectionCommand(p, AgentAction.WEST),
                    new ChangeDirectionCommand(p, AgentAction.EAST)
                );
            }
        }
    }
}