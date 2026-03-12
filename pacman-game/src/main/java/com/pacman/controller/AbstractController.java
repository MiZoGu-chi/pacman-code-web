package controller;

import java.util.logging.Logger;
import model.Game;

public abstract class AbstractController {
    
    protected Game game;

    private static final Logger LOGGER = Logger.getLogger(AbstractController.class.getName());

    public AbstractController(Game game) {
        this.game = game;
    }
    
    public void restart() {
        game.pause(); 
        game.init();  
    }

    public void step() {
        LOGGER.info("Call step()");
        game.step();
    }

    public void play() {
        LOGGER.info("Call play()");
        game.launch();
    }

    public void pause() {
        LOGGER.info("Call pause()");
        game.pause();
    }

    public void resume() {
        LOGGER.info("Call resume()");
        game.resume(); 
    }

    public void setSpeed(double speed) {
        LOGGER.info("Call setSpeed()");
        long newTime = (long) (1000 / speed);
        game.setTime(newTime);
    }

    public Game getGame() {
        return game;
    }
}
 