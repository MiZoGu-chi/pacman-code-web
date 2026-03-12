package model.strategies;

import model.AgentAction;
import model.Maze;
import model.PositionAgent;
import model.agents.Agent;

public class EatNearestFoodStrategy implements Strategy {

    @Override
    public AgentAction chooseAction(Agent agent, Maze maze) {
        PositionAgent agentPos = agent.getPos();
        
        // 1. Trouver la position de la nourriture la plus proche
        // On stocke les coordonnées cibles (x, y)
        int targetX = -1;
        int targetY = -1;
        double minGlobalDist = Double.MAX_VALUE;

        // On parcourt toutes les cases du labyrinthe
        for (int x = 0; x < maze.getSizeX(); x++) {
            for (int y = 0; y < maze.getSizeY(); y++) {
                if (maze.isFood(x, y)) { // Si c'est de la nourriture
                    double dist = getDistanceSquared(agentPos.getX(), agentPos.getY(), x, y);
                    if (dist < minGlobalDist) {
                        minGlobalDist = dist;
                        targetX = x;
                        targetY = y;
                    }
                }
            }
        }

        // Cas limite : Si plus aucune nourriture sur le plateau
        if (targetX == -1) {
            return new AgentAction(AgentAction.STOP);
        }

        // 2. Choisir le mouvement légal qui minimise la distance vers cette cible
        AgentAction bestAction = new AgentAction(AgentAction.STOP);
        double minMoveDist = Double.MAX_VALUE;

        int[] directions = {AgentAction.NORTH, AgentAction.SOUTH, AgentAction.EAST, AgentAction.WEST};

        for (int dir : directions) {
            AgentAction action = new AgentAction(dir);
            
            // On ne considère que les mouvements légaux (pas de murs)
            if (maze.isLegalMove(agent, action)) {
                
                // Calcul de la position future si on fait ce mouvement
                int nextX = agentPos.getX() + action.get_vx();
                int nextY = agentPos.getY() + action.get_vy();

                // Distance entre la position future et la nourriture cible
                double dist = getDistanceSquared(nextX, nextY, targetX, targetY);

                if (dist < minMoveDist) {
                    minMoveDist = dist;
                    bestAction = action;
                }
            }
        }

        return bestAction;
    }

    // Calcul de distance euclidienne au carré (plus performant que racine carrée)
    private double getDistanceSquared(int x1, int y1, int x2, int y2) {
        return Math.pow(x1 - x2, 2) + Math.pow(y1 - y2, 2);
    }
}