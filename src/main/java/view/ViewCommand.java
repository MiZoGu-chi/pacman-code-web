package view;

import controller.AbstractController;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.beans.*;
import javax.swing.*;
import javax.swing.event.ChangeEvent;
import model.Game;
import view.states.State;
import view.states.StateGameEnded;
import view.states.StateInit;


public class ViewCommand implements PropertyChangeListener {

    private JSlider slider;

    private final JButton pauseButton;
    private final JButton restartButton;
    private final JButton runButton;
    private final JButton stepButton;

    private final Icon pauseIcon;
    private final Icon restartIcon;
    private final Icon runIcon;
    private final Icon stepIcon;

    private final JPanel mainPanel;
    private final JPanel topPanel;
    private final JPanel bottomPanel;
    private final JPanel leftBottomPanel;

    private final JLabel label;

    private final JFrame frame;

    private Game game;
    private AbstractController controller;

    private State state;

    public ViewCommand(Game game, AbstractController controller) {

        this.game = game;
        this.controller = controller;

        frame = new JFrame("Commande");
        frame.setResizable(false);
        frame.setSize(700, 400);

        mainPanel = new JPanel(new GridLayout(2, 1));
        topPanel = new JPanel(new GridLayout(1, 4));
        bottomPanel = new JPanel(new GridLayout(1, 2));
        leftBottomPanel = new JPanel(new GridLayout(2,0));

        slider = new JSlider(JSlider.HORIZONTAL, 1, 10, 1);
        slider.setPaintTicks(true);
        slider.setMajorTickSpacing(1);
        slider.setPaintLabels(true);
        

        pauseIcon = new ImageIcon("icons/icon_pause.png");
        restartIcon = new ImageIcon("icons/icon_restart.png");
        runIcon = new ImageIcon("icons/icon_run.png");
        stepIcon = new ImageIcon("icons/icon_step.png");

        pauseButton = new JButton(pauseIcon);
        restartButton = new JButton(restartIcon);
        runButton = new JButton(runIcon);
        stepButton = new JButton(stepIcon);

        label = new JLabel("turn " + game.getTurn(), JLabel.CENTER);

        topPanel.add(restartButton);
        topPanel.add(runButton);
        topPanel.add(stepButton);
        topPanel.add(pauseButton);

        leftBottomPanel.add(new JLabel("Number of turns per second", JLabel.CENTER));
        leftBottomPanel.add(slider);

        bottomPanel.add(leftBottomPanel);
        bottomPanel.add(label);

        mainPanel.add(topPanel);
        mainPanel.add(bottomPanel);

        frame.add(mainPanel);

        setState(new StateInit(this));

        restartButton.addActionListener((ActionEvent event) -> {
            controller.restart();
            state.restart();
        });

        pauseButton.addActionListener((ActionEvent event) -> {
            controller.pause();
            state.pause();
        });

        runButton.addActionListener((ActionEvent event) -> {
            // Si on est déjà en cours de partie (donc en pause), on resume. 
            // Sinon (si turn == 0 ou StateInit), on play.
            if (game.getTurn() > 0 && !game.isRunning()) {
                controller.resume(); 
            } else {
                controller.play();
            }
            state.run();
        });

        stepButton.addActionListener((ActionEvent event) -> {
            state.step();
            controller.step();
        });

        slider.addChangeListener((ChangeEvent event) -> {
            controller.setSpeed(slider.getValue());
        });

        this.frame.setVisible(true);

        game.addPropertyChangeListener(this);
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        if ("turn".equals(evt.getPropertyName())) {
            this.label.setText("turn " + evt.getNewValue());
        }
        if ("Victory".equals(evt.getPropertyName()) || "GameOver".equals(evt.getPropertyName())) {
            controller.pause();
            setState(new StateGameEnded(this));
        }
    }

    public void setState(State s) { 
        state = s;
        s.updateButtons();
    }

    public JButton getPauseButton() {
        return pauseButton;
    }

    public JButton getRestartButton() {
        return restartButton;
    }

    public JButton getRunButton() {
        return runButton;
    }

    public JButton getStepButton() {
        return stepButton;
    }
}
