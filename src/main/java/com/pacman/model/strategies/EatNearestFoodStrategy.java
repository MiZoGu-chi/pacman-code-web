package com.pacman.model.strategies;

import com.pacman.model.AgentAction;
import com.pacman.model.Maze;
import com.pacman.model.PositionAgent;
import com.pacman.model.agents.Agent;

public class EatNearestFoodStrategy implements Strategy {

    @Override
    public AgentAction chooseAction(Agent agent, Maze maze) {
        int agentX = agent.getPos().getX();
        int agentY = agent.getPos().getY();

        // recherche de la cible la plus proche (Nourriture ou Capsule)
        int targetX = -1;
        int targetY = -1;
        double minDistanceSquared = Double.MAX_VALUE;

        for (int x = 0; x < maze.getSizeX(); x++) {
            for(int y = 0; y < maze.getSizeY(); y++) {
                // On cherche les gommes et les capsules
                if(maze.isFood(x, y) || maze.isCapsule(x, y)) {

                    double distance = Math.pow(x - agentX, 2) + Math.pow(y - agentY, 2);
                    
                    if(distance < minDistanceSquared) {
                        minDistanceSquared = distance;
                        targetX = x;
                        targetY = y;
                    }
                }
            }
        }

        AgentAction bestAction = new AgentAction(AgentAction.STOP);
        double minDistanceAfterMove = Double.MAX_VALUE;

        int[] directions = {AgentAction.NORTH, AgentAction.SOUTH, AgentAction.EAST, AgentAction.WEST};
        int currentDirection = agent.getPos().getDir();

        for (int dir : directions) {
            AgentAction action = new AgentAction(dir);

            if (maze.isLegalMove(agent, action)) {
                 
                // On cherche à eviter les va-et-vient inutiles entre deux cases.
                if (isOpposite(dir, currentDirection) && countAvailableMoves(agent, maze) > 1) {
                    continue;
                }

                int nextX = agentX + action.get_vx();
                int nextY = agentY + action.get_vy();
                
                double dist = Math.pow(nextX - targetX, 2) + Math.pow(nextY - targetY, 2);

                if (dist < minDistanceAfterMove) {
                    minDistanceAfterMove = dist;
                    bestAction = action;
                }
            }
        }

        return bestAction;
    }

    // compte le nombre de directions possibles 
    private int countAvailableMoves(Agent agent, Maze maze) {
        int count = 0;
        int[] directions = {AgentAction.NORTH, AgentAction.SOUTH, AgentAction.EAST, AgentAction.WEST};
        
        for(int dir : directions) {
            AgentAction action = new AgentAction(dir);
            if (maze.isLegalMove(agent, action)) {
                count++;
            }
        }
        return count;
    }

    // détermine si deux direction sont opposées
    private boolean isOpposite(int dir1, int dir2) {
        if(dir1 == AgentAction.NORTH && dir2 == AgentAction.SOUTH) return true;
        if(dir1 == AgentAction.SOUTH && dir2 == AgentAction.NORTH) return true;
        if(dir1 == AgentAction.EAST && dir2 == AgentAction.WEST) return true;
        return dir1 == AgentAction.WEST && dir2 == AgentAction.EAST;
    }
}