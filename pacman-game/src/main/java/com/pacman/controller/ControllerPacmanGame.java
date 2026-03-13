package controller;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import model.AgentAction;
import model.PacmanGame;
import model.agents.Pacman;
import model.commands.ChangeDirectionCommand;
import view.ViewCommand;
import view.ViewPacmanGame;

public class ControllerPacmanGame extends AbstractController implements PropertyChangeListener  {

    private final ViewPacmanGame viewPacman;
    private final ViewCommand viewCommand;
    private final InputHandler inputHandler;

    public ControllerPacmanGame(PacmanGame game) {
        super(game);

        viewPacman = new ViewPacmanGame(game, this);
        viewCommand = new ViewCommand(game, this);
        
        inputHandler = new InputHandler();
        viewPacman.setKeyListener(inputHandler);
        
 
        game.addPropertyChangeListener(this); 
        viewPacman.addPropertyChangeListener(this);

        SoundObserver soundObserver = new SoundObserver();
        game.addPropertyChangeListener(soundObserver);
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        if("layoutChanged".equals(evt.getPropertyName())) {
            String newLayout = (String) evt.getNewValue();
            System.out.println("ControllerPacmaneGame : new Layout : " + newLayout);
            ((PacmanGame) getGame()).loadNewMaze(newLayout);
            viewCommand.getState().restart();
        }
        if("PacmanCreated".equals(evt.getPropertyName())) {
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

    public ViewPacmanGame getView() {
        return this.viewPacman;
    }
}
