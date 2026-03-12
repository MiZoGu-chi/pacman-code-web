package model.agents;

import model.AgentAction;
import model.Maze;
import model.PositionAgent;
import model.strategies.KeyboardStrategy;
import model.strategies.RandomStrategy;
import model.strategies.Strategy;

public abstract class Agent {

    protected PositionAgent pos;

    // on ajoute ça pour pouvoir gérer de manière individuel (ce que MAZE ne permet pas) la position de départ de chaque agents (pour les respawn)
    protected PositionAgent startPos;

    protected Strategy strat;

    protected boolean alive;

    protected int nextRequestedDir = -1;

    public Agent(PositionAgent pos) {
        startPos = pos;
        this.pos = pos;
        strat = new RandomStrategy();
        alive = true;
    }

    public void setPos(PositionAgent pos) {
        this.pos = pos;
    }

    public PositionAgent getPos() {
        return pos;
    }

    public void setStrategy(Strategy strat) {
        this.strat = strat;
    }
  	
	public AgentAction play(Maze maze) {	
		return strat.chooseAction(this, maze);		
	}

    public boolean isAlive() {
        return alive;
    }

    public void setAlive(boolean alive) {
        this.alive = alive;
    }

    public void die() {
        this.alive = false;
    }

    public Strategy geStrategy() {
        return strat;
    }

    public void respawn() {
        this.pos = startPos;
    }

    public void setNextAction(int dir) {
        this.nextRequestedDir = dir;
    }

    public int getNextAction() {
        return this.nextRequestedDir;
    }

    public boolean isControlled() {
        return strat instanceof KeyboardStrategy;
    }
}
