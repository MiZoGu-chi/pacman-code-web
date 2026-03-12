package model.factories;

import model.PositionAgent;
import model.agents.Agent;

public interface AgentFactory {
    public Agent createAgent(PositionAgent pos);
}
