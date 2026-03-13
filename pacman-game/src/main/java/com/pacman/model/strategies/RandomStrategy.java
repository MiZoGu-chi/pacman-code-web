package com.pacman.model.strategies;

import java.util.Random;

import model.AgentAction;
import model.Maze;
import model.agents.Agent;

public class RandomStrategy implements Strategy {

    @Override
    public AgentAction chooseAction(Agent agent, Maze maze) {
        Random random = new Random();
        AgentAction action;

        do { 
            int dir = random.nextInt(5);
            action = new AgentAction(dir);
        } while (!maze.isLegalMove(agent, action));

        return action;
    }
}
