package model.strategies;

import java.util.List;

import model.AgentAction;
import model.Maze;
import model.PositionAgent;
import model.agents.Agent;
import model.agents.Pacman;

public class EscapeStrategy implements Strategy {

    private List<Pacman> pacmans; 

    public EscapeStrategy(List<Pacman> pacmans) {
        this.pacmans = pacmans;
    }

    @Override
    public AgentAction chooseAction(Agent agent, Maze maze) {
        PositionAgent myPos = agent.getPos();
        
        PositionAgent targetPos = null;
        double minDst = Double.MAX_VALUE;

        for (Pacman p : pacmans) {
             if(p == null) continue;

             double dist = getDistanceSquared(myPos, p.getPos());
             if (dist < minDst) {
                 minDst = dist;
                 targetPos = p.getPos();
             }
        }

        if (targetPos == null) {
            return new RandomStrategy().chooseAction(agent, maze);
        }

        AgentAction bestAction = new AgentAction(AgentAction.STOP);
        double maxDistance = -1.0;
        
        // On teste les 4 directions cardinales
        int[] dirs = {AgentAction.NORTH, AgentAction.SOUTH, AgentAction.EAST, AgentAction.WEST};
        
        for (int dir : dirs) {
            AgentAction action = new AgentAction(dir);
            
            if (maze.isLegalMove(agent, action)) {
                int nextX = myPos.getX() + action.get_vx();
                int nextY = myPos.getY() + action.get_vy();
                PositionAgent nextPos = new PositionAgent(nextX, nextY, dir);
                
                double dist = getDistanceSquared(nextPos, targetPos);
                
                if (dist > maxDistance) {
                    maxDistance = dist;
                    bestAction = action;
                }
            }
        }

        return bestAction;
    }

    private double getDistanceSquared(PositionAgent p1, PositionAgent p2) {
        return Math.pow(p1.getX() - p2.getX(), 2) + Math.pow(p1.getY() - p2.getY(), 2);
    }
}
