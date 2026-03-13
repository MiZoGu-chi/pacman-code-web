package com.pacman.model.strategies;

import java.util.List;

import com.pacman.model.AgentAction;
import com.pacman.model.Maze;
import com.pacman.model.PositionAgent;
import com.pacman.model.agents.Agent;
import com.pacman.model.agents.Pacman;
import com.pacman.model.agents.Ghost;

public class EscapeStrategy implements Strategy {

    private List<Pacman> pacmans;
    private List<Ghost> ghosts; 

    public EscapeStrategy(List<Pacman> pacmans, List<Ghost> ghosts) {
        this.pacmans = pacmans;
        this.ghosts = ghosts;
    }

    @Override
    public AgentAction chooseAction(Agent agent, Maze maze) {
        PositionAgent myPos = agent.getPos();
        
        // on cherche le Pacman vivant le plus proche

        PositionAgent target = null;
        double minDst = Double.MAX_VALUE;

        for(Pacman p : pacmans) {
            if(p == null || !p.isAlive()) 
                continue;
            
            double d = getDistanceSquared(myPos, p.getPos());
            if(d < minDst) { 
                minDst = d; 
                target = p.getPos(); 
            }
        }

        if (target == null) return new RandomStrategy().chooseAction(agent, maze);

        AgentAction bestAction = new AgentAction(AgentAction.STOP);
        double maxDistance = -1.0;
        
        // on teste chaque directions
        for(int i = 0; i < 4; i++) {
            AgentAction action = new AgentAction(i);
            
            int nx = myPos.getX() + action.get_vx();
            int ny = myPos.getY() + action.get_vy();

            if (!maze.isLegalMove(agent, action) || isOccupiedByGhost(nx, ny, agent)) {
                continue;
            }

            double dist = getDistanceSquared(new PositionAgent(nx, ny, i), target);
            if (dist > maxDistance) {
                maxDistance = dist;
                bestAction = action;
            }
        }
        return bestAction;
    }

    private double getDistanceSquared(PositionAgent p1, PositionAgent p2) {
        return Math.pow(p1.getX() - p2.getX(), 2) + Math.pow(p1.getY() - p2.getY(), 2);
    }

    private boolean isOccupiedByGhost(int x, int y, Agent current) {
        for(Ghost g : ghosts) {
            if (g != current && g.isAlive() && g.getPos().getX() == x && g.getPos().getY() == y) {
                return true;
            }
        }
        return false;
    }
}
