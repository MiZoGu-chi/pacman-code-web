package com.pacman.model.commands;

import com.pacman.model.agents.Agent; 

public class ChangeDirectionCommand implements Command {

    private final Agent targetAgent; 
    private final int direction;

    public ChangeDirectionCommand(Agent agent, int direction) {
        this.targetAgent = agent;
        this.direction = direction;
    }

    @Override
    public void execute() {
        targetAgent.setNextAction(direction);
    }
}