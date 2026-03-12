package model.strategies;

import model.AgentAction;
import model.Maze;
import model.agents.Agent;

public class KeyboardStrategy implements Strategy {

    @Override
    public AgentAction chooseAction(Agent agent, Maze maze) {
        
        // 1. Récupérer la dernière touche pressée par l'utilisateur
        int reqDir = agent.getNextAction();
        
        // Si une touche a été pressée, on tente d'abord d'aller dans cette direction
        if (reqDir != -1 && reqDir != AgentAction.STOP) {
            AgentAction actionDemande = new AgentAction(reqDir);
            if (maze.isLegalMove(agent, actionDemande)) {
                return actionDemande;
            }
        }

        // 2. Si l'action demandée est illégale (mur), on essaie de continuer tout droit
        // On récupère la direction dans laquelle l'agent regarde actuellement
        int currentDir = agent.getPos().getDir();
        AgentAction actionInertie = new AgentAction(currentDir);

        // Si continuer tout droit est possible, on ignore l'input illégal et on avance
        if (maze.isLegalMove(agent, actionInertie)) {
            return actionInertie;
        }

        // 3. Si même l'inertie est bloquée par un mur, alors seulement on s'arrête
        return new AgentAction(AgentAction.STOP);
    }
}