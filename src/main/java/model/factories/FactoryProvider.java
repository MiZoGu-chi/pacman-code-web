package model.factories;

import model.AgentType;

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
