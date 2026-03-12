import controller.ControllerPacmanGame;
import model.PacmanGame;

public class App {
    public static void main(String[] args) {

        PacmanGame game = new PacmanGame(1000,"/layouts/originalClassic.lay");

        @SuppressWarnings("unused")
        ControllerPacmanGame controller = new ControllerPacmanGame(game);

        game.initializeGame();
    }
}