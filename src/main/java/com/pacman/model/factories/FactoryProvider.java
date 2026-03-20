package com.pacman.model.factories;

import com.pacman.model.agents.AgentType;

public class FactoryProvider {
    
    public static AgentFactory getFactory(AgentType typeAgent) {
        AgentFactory agentFactory = null;
        
        switch (typeAgent) {
            case PACMAN -> agentFactory = new PacmanFactory();
            case GHOST -> agentFactory = new GhostFactory();
        }
        return agentFactory;
    }
}
