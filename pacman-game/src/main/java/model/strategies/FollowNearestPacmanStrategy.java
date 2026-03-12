package model.strategies;

import java.util.ArrayList;
import java.util.List;

import model.AgentAction;
import model.Maze;
import model.PositionAgent;
import model.agents.Agent;
import model.agents.Pacman;

public class FollowNearestPacmanStrategy implements Strategy {

    private List<Pacman> pacmans;
    private PositionAgent lastPos = null; // Mémoire pour éviter les demi-tours immédiats

    public FollowNearestPacmanStrategy(ArrayList<Pacman> pacmans) {
        this.pacmans = pacmans;
    }

    @Override
    public AgentAction chooseAction(Agent agent, Maze maze) {
        PositionAgent agentPos = agent.getPos();

        // 1. Trouver le Pacman le plus proche (Cible)
        PositionAgent targetPos = null;
        double minGlobalDist = Double.MAX_VALUE;

        for (Pacman p : pacmans) {
            // Optionnel : vérifier si le Pacman est vivant (selon votre implémentation)
            // if (!p.isAlive()) continue; 

            double dist = getDistanceSquared(agentPos, p.getPos());
            if (dist < minGlobalDist) {
                minGlobalDist = dist;
                targetPos = p.getPos();
            }
        }

        // Si aucun Pacman n'est trouvé, on s'arrête (ou Random)
        if (targetPos == null) {
            return new AgentAction(AgentAction.STOP);
        }

        // 2. Choisir le mouvement qui RÉDUIT la distance vers la cible
        AgentAction bestAction = new AgentAction(AgentAction.STOP);
        double minMoveDist = Double.MAX_VALUE;

        int[] directions = {AgentAction.NORTH, AgentAction.SOUTH, AgentAction.EAST, AgentAction.WEST};

        for (int dir : directions) {
            AgentAction action = new AgentAction(dir);

            if (maze.isLegalMove(agent, action)) {
                // Simuler la position future
                int nextX = agentPos.getX() + action.get_vx();
                int nextY = agentPos.getY() + action.get_vy();
                PositionAgent nextPos = new PositionAgent(nextX, nextY, dir);

                double dist = getDistanceSquared(nextPos, targetPos);

                // --- ANTI-OSCILLATION ---
                // Si la case suivante est celle d'où l'on vient, on pénalise ce mouvement
                // pour forcer le fantôme à contourner les obstacles plutôt que rebrousser chemin.
                if (lastPos != null && nextX == lastPos.getX() && nextY == lastPos.getY()) {
                    dist += 5000.0; 
                }
                // ------------------------

                // On cherche la distance MINIMALE (pour se rapprocher)
                if (dist < minMoveDist) {
                    minMoveDist = dist;
                    bestAction = action;
                }
            }
        }

        // Mise à jour de la mémoire
        lastPos = agentPos;

        return bestAction;
    }

    private double getDistanceSquared(PositionAgent p1, PositionAgent p2) {
        return Math.pow(p1.getX() - p2.getX(), 2) + Math.pow(p1.getY() - p2.getY(), 2);
    }
}
