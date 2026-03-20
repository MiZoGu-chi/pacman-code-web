package com.pacman.model.strategies;

import com.pacman.model.AgentAction;
import com.pacman.model.Maze;
import com.pacman.model.agents.Agent;

public interface Strategy {
    public AgentAction chooseAction(Agent agent, Maze maze);
}
