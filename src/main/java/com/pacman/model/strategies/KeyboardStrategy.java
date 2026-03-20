package com.pacman.model.strategies;

import com.pacman.model.AgentAction;
import com.pacman.model.Maze;
import com.pacman.model.agents.Agent;

public class KeyboardStrategy implements Strategy {

    @Override
    public AgentAction chooseAction(Agent agent, Maze maze) {
        
        AgentAction desiredAction = new AgentAction(agent.getNextAction());
        
        if (desiredAction.get_direction() == AgentAction.STOP || desiredAction.get_direction() == -1) {
            return new AgentAction(AgentAction.STOP);
        }

        if (maze.isLegalMove(agent, desiredAction)) {
            return desiredAction;
        }

        AgentAction lastLegalMove = new AgentAction(agent.getPos().getDir());
        
        if (maze.isLegalMove(agent, lastLegalMove)) {
            return lastLegalMove;
        }

        return new AgentAction(AgentAction.STOP);
    }
}