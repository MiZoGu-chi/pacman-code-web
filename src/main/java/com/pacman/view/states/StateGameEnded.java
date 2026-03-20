package com.pacman.view.states;

import com.pacman.view.ViewCommand;

public class StateGameEnded extends State {

    public StateGameEnded(ViewCommand v) {
        super(v);
    }

    @Override
    public void pause() { 

    }

    @Override
    public void restart() {
        viewCommand.setState(new StateInit(viewCommand));
    }

    @Override
    public void run() { 

    }

    @Override
    public void step() { 

    }

    @Override
    public void updateButtons() {
        viewCommand.getPauseButton().setEnabled(false);
        viewCommand.getRestartButton().setEnabled(true);
        viewCommand.getRunButton().setEnabled(false);
        viewCommand.getStepButton().setEnabled(false);
    }
}