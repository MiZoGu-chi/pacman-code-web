package com.pacman.model.factories;

import com.pacman.model.PositionAgent;
import com.pacman.model.agents.Agent;

public interface AgentFactory {
    public Agent createAgent(PositionAgent pos);
}
