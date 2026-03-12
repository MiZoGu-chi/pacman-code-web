package model.strategies;

import java.util.List;

import model.AgentAction;
import model.Maze;
import model.PositionAgent;
import model.agents.Agent;
import model.agents.Ghost;

public class FollowNearestGhostStrategy implements Strategy {

    private final List<Ghost> ghosts;

    public FollowNearestGhostStrategy(List<Ghost> ghosts) {
        this.ghosts = ghosts;
    }

    @Override
    public AgentAction chooseAction(Agent agent, Maze maze) {
        PositionAgent myPos = agent.getPos();

        PositionAgent target = null;
        double minGlobalDist = Double.MAX_VALUE;

        for (Ghost g : ghosts) {
            // dans le cas ou un fantôme à cette stratégie même si ce n'est pas sensé arriver
            if (g == agent) continue; 
            
            double d = Math.pow(myPos.getX() - g.getPos().getX(), 2) + 
                       Math.pow(myPos.getY() - g.getPos().getY(), 2);
            
            if (d < minGlobalDist) {
                minGlobalDist = d;
                target = g.getPos();
            }
        }

        if (target == null) {
            return new AgentAction(AgentAction.STOP);
        }

        AgentAction bestAction = new AgentAction(AgentAction.STOP);
        double minMoveDist = Double.MAX_VALUE;
        
        int[] dirs = {AgentAction.NORTH, AgentAction.SOUTH, AgentAction.EAST, AgentAction.WEST};

        for (int dir : dirs) {
            AgentAction action = new AgentAction(dir);

            if (maze.isLegalMove(agent, action)) {
                int nextX = myPos.getX() + action.get_vx();
                int nextY = myPos.getY() + action.get_vy();

                double d = Math.pow(nextX - target.getX(), 2) + 
                           Math.pow(nextY - target.getY(), 2);
                if (d < minMoveDist) {
                    minMoveDist = d;
                    bestAction = action;
                }
            }
        }

        return bestAction;
    }
}
