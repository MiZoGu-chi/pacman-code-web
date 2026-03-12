package model.strategies;

import model.AgentAction;
import model.Maze;
import model.agents.Agent;

public interface Strategy {
    public AgentAction chooseAction(Agent agent, Maze maze);
}
