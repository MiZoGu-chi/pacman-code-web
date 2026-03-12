package model.factories;

import model.PositionAgent;
import model.agents.Agent;
import model.agents.Pacman;

public class PacmanFactory implements AgentFactory{

    @Override
    public Agent createAgent(PositionAgent pos) {
        return new Pacman(pos);
    }
}
