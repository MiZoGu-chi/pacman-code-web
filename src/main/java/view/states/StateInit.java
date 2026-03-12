package view.states;

import view.ViewCommand;

public class StateInit extends State {

    public StateInit(ViewCommand v) {
        super(v);
    }

    @Override
    public void pause() { }

    @Override
    public void restart() { }
        

    @Override
    public void run() {
        viewCommand.setState(new StateRun(viewCommand));
    }

    @Override
    public void step() { }     

    @Override
    public void updateButtons() {
        viewCommand.getPauseButton().setEnabled(false);
        viewCommand.getRestartButton().setEnabled(false);
        viewCommand.getRunButton().setEnabled(true);
        viewCommand.getStepButton().setEnabled(true);
    }
}
