package com.pacman.dao.view.states;

import view.ViewCommand;

public class StateRun extends State {

    public StateRun(ViewCommand v) {
        super(v);
    }

    @Override
    public void pause() {
        viewCommand.setState(new StatePause(viewCommand));
    }

    @Override
    public void restart() {
        viewCommand.setState(new StateInit(viewCommand));
    }

    @Override
    public void run() { }

    @Override
    public void step() { }

    @Override
    public void updateButtons() {
        viewCommand.getPauseButton().setEnabled(true);
        viewCommand.getRestartButton().setEnabled(true);
        viewCommand.getRunButton().setEnabled(false);
        viewCommand.getStepButton().setEnabled(false);
    }
}
