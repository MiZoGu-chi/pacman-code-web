package controller;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;
import model.commands.Command;

public class InputHandler extends KeyAdapter {

    private final Map<Integer, Command> keyBindings = new HashMap<>();

    private static final Logger LOGGER = Logger.getLogger(AbstractController.class.getName());

    public void setBinding(int keyCode, Command command) {
        keyBindings.put(keyCode, command);
    }

    public void removeBinding(int keyCode) {
        keyBindings.remove(keyCode);
    }

    /**
     * Méthode utilitaire pour configurer rapidement les 4 flèches.
     */
    public void setArrowControls(Command up, Command down, Command left, Command right) {
        setBinding(KeyEvent.VK_UP, up);
        setBinding(KeyEvent.VK_DOWN, down);
        setBinding(KeyEvent.VK_LEFT, left);
        setBinding(KeyEvent.VK_RIGHT, right);
    }

    @Override
    public void keyPressed(KeyEvent e) {
        int key = e.getKeyCode();
        String keyText = KeyEvent.getKeyText(key);

        //LOGGER.info(() -> "Touche pressée : " + keyText + " (Code: " + key + ")");

        if (keyBindings.containsKey(key)) {
            LOGGER.info(() -> "Commande :  " + keyText);
            keyBindings.get(key).execute();
        } 
    }
}