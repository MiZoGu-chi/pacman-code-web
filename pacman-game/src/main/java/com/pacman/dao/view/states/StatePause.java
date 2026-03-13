package com.pacman.dao.view.states;

import view.ViewCommand;

public class StatePause extends State {

    public StatePause(ViewCommand v) {
        super(v);
    }

    @Override
    public void pause() { }

    @Override
    public void restart() {
        viewCommand.setState(new StateInit(viewCommand));
    }

    @Override
    public void run() {
        viewCommand.setState(new StateRun(viewCommand));
    }

    @Override
    public void step() {
        
    }

    @Override
    public void updateButtons() {
        viewCommand.getPauseButton().setEnabled(false);
        viewCommand.getRestartButton().setEnabled(true);
        viewCommand.getRunButton().setEnabled(true);
        viewCommand.getStepButton().setEnabled(true);
    }
}
