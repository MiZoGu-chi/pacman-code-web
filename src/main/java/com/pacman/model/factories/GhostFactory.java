package com.pacman.model.factories;

import com.pacman.model.PositionAgent;
import com.pacman.model.agents.Agent;
import com.pacman.model.agents.Ghost;

public class GhostFactory implements AgentFactory {

    @Override
    public Agent createAgent(PositionAgent pos) {
        return new Ghost(pos);
    }
}
