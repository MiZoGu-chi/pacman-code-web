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

        this.viewPacman = new ViewPacmanGame(game, this);
        this.viewCommand = new ViewCommand(game, this);
        
        // On instancie le gestionnaire de clavier
        this.inputHandler = new InputHandler();
        // On le branche sur la vue (le Panel)
        this.viewPacman.setKeyListener(this.inputHandler);
        
        // CRUCIAL : Le controleur doit écouter le JEU pour entendre "PacmanCreated"
        // Si cette ligne manquait, la méthode propertyChange n'était jamais appelée par le Modèle.
        game.addPropertyChangeListener(this); 

        // On écoute aussi la vue (pour le chargement de layout par exemple)
        this.viewPacman.addPropertyChangeListener(this);

        SoundObserver soundObserver = new SoundObserver();
        game.addPropertyChangeListener(soundObserver);
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        if("layoutChanged".equals(evt.getPropertyName())) {
            String newLayout = (String) evt.getNewValue();
            System.out.println("ControllerPacmaneGame : new Layout : " + newLayout);
            ((PacmanGame) getGame()).loadNewMaze(newLayout);
        }
        if("PacmanCreated".equals(evt.getPropertyName())) {
            Pacman p = (Pacman) evt.getNewValue();
            
            // On considère que le premier Pacman créé est le Joueur 1
            if (((PacmanGame) getGame()).getPacmansPosition().size() == 1 || ((PacmanGame) getGame()).getPacmans().indexOf(p) == 0) {
                
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
