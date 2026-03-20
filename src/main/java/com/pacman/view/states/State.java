package com.pacman.view.states;

import com.pacman.view.ViewCommand;

public abstract class State {

    ViewCommand viewCommand;

    public State(ViewCommand v) {
        viewCommand = v;
    }

    abstract public void pause();
    abstract public void restart();
    abstract public void run();
    abstract public void step();

    abstract public void updateButtons();

    public void gameEnd() {
        viewCommand.setState(new StateGameEnded(viewCommand)); 
    }
}
