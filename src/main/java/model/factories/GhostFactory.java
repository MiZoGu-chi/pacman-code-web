package model.factories;

import model.PositionAgent;
import model.agents.Agent;
import model.agents.Ghost;

public class GhostFactory implements AgentFactory {

    @Override
    public Agent createAgent(PositionAgent pos) {
        return new Ghost(pos);
    }
}
