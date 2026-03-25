package com.pacman.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Logger;

import com.pacman.model.agents.Agent;
import com.pacman.model.agents.Ghost;
import com.pacman.model.agents.Pacman;
import com.pacman.model.agents.AgentType;

import com.pacman.model.factories.AgentFactory;
import com.pacman.model.factories.FactoryProvider;

import com.pacman.model.strategies.EatNearestFoodStrategy;
import com.pacman.model.strategies.EscapeStrategy;
import com.pacman.model.strategies.KeyboardStrategy;
import com.pacman.model.strategies.RandomStrategy;
import com.pacman.network.GameState;

public class PacmanGame extends Game {

    private Maze maze;
    private String layout;

    private List<Pacman> pacmans = new ArrayList<>();
    private List<Ghost> ghosts = new ArrayList<>();

    private int score = 0;
    private int life = 3;
    private int up = 0;

    private static final int POINT_PER_FOOD = 10;

    private boolean ghostMoveToggle = true;

    private static final Logger LOGGER = Logger.getLogger(PacmanGame.class.getName());

    public PacmanGame(int maxTurn, String layout) {  
        super(maxTurn);
        
        this.layout = layout;

        try {
            this.maze = new Maze(this.layout);   
        } catch (Exception e) {
            throw new RuntimeException("error loadint maze " + e.getMessage(), e);
        }
    }
    
    @Override
    public void initializeGame() {
        System.out.println("**Pacman : call Initialize()**");

        this.score = 0;
        this.life = 3;

        support.firePropertyChange("ScoreChanged", null, this.score);
        support.firePropertyChange("LifeChanged", null, this.life);

        this.turn = 0; 

        maze.resetFoodsAndCapsules();

        pacmans.clear();
        ghosts.clear();

        List<PositionAgent> pacmanStart = maze.getPacman_start();

        for(int i = 0; i < pacmanStart.size(); i++) {
            PositionAgent p = pacmanStart.get(i);
            AgentFactory factory = FactoryProvider.getFactory(AgentType.PACMAN);
            Pacman pacman = (Pacman) factory.createAgent(p);
            pacmans.add(pacman);

            // on considére arbitrairement que le premier pacman est toujours le pacman controllé par l'utilisateur
            if(i == 0) {
                pacman.setStrategy(new KeyboardStrategy());
            } else {
                pacman.setStrategy(new EatNearestFoodStrategy());
            }

            System.out.println("Pacman created at position: " + p);
            
            support.firePropertyChange("PacmanCreated", null, pacman);
        }



        for(PositionAgent p : maze.getGhosts_start()) {
            AgentFactory factory = FactoryProvider.getFactory(AgentType.GHOST);
            Ghost ghost = (Ghost) factory.createAgent(p);
            ghosts.add(ghost);
            System.out.println("Ghost created at position: " + p);
        }
    }

    @Override
    public void takeTurn() {
        // On sauveragde les positions avant le mouvement 
        // afin de pouvoir gérer les collisions
        Map<Agent, PositionAgent> oldPositions = new HashMap<>();

        for (Ghost g : ghosts) oldPositions.put(g, g.getPos());
        for (Pacman p : pacmans) oldPositions.put(p, p.getPos());

        ghostMoveToggle = !ghostMoveToggle;

        for(Ghost g : ghosts) {
            if(g.isAlive() && g.geStrategy() != null) {

                // Lorsque les fantôme sont en mode fuite, ils sont ralentis
                // (ilS ne jouent pas un tour sur deux)
                if(g.geStrategy() instanceof EscapeStrategy && !ghostMoveToggle) {
                    continue; 
                }   
                AgentAction action = g.play(maze);
                LOGGER.info(() -> "Ghost at " + g.getPos() + " plays " + action);
                moveAgent(g, action);
            }
        }

        for(Pacman p : pacmans) {
            if(p.isAlive() && p.geStrategy() != null) {
                AgentAction action = p.play(maze);
                LOGGER.info(() -> "Pacman at " + p.getPos() + " plays " + action);
                moveAgent(p, action);   
            }         
        }

        checkCollisions(oldPositions);

        if(isFoodEmpty()) {
            LOGGER.info("Victory you ate alle the food !");
            support.firePropertyChange("Victory", null, null);
            return;
        }

        if(isGameOver()) {
            gameOver();
        }
    }

    @Override
    public boolean gameContinue() {
        System.out.println("**Pacman : call gameContinue()**");
        return !isFoodEmpty() && !isGameOver();
    }

    public void setMaze(Maze maze) {
        this.maze = maze;
    }

    public void loadNewMaze(String layout) {
        try {
            this.layout = layout;
            this.maze = new Maze(this.layout);
            
            this.init(); 

            System.out.println("Maze reloaded from " + layout);

            support.firePropertyChange("layoutLoaded", null, layout);

        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("error loading maze in PacmanGame: " + e.getMessage());
        }
    }
    
    public void moveAgent(Agent agent, AgentAction action) {
        PositionAgent oldPos = agent.getPos();
        int newX = oldPos.getX() + action.get_vx();
        int newY = oldPos.getY() + action.get_vy();

        PositionAgent newPos = new PositionAgent(newX, newY, action.get_direction());

        agent.setPos(newPos);

        if(agent instanceof Pacman pacman) {

            if(maze.isFood(newX, newY)) {
                support.firePropertyChange("FoodEaten", null, null);
                maze.setFood(newX, newY, false);
                score += POINT_PER_FOOD;
                // 1up
                if(score >= 1000) {
                    life++;
                    score = 0;
                    support.firePropertyChange("LifeChanged", null, null);
                }
                support.firePropertyChange("ScoreChanged", null, null);
            }

            else if(maze.isCapsule(newX, newY)) {
                maze.setCapsule(newX, newY, false);
                // tous les pacmans deviennent invincible en multijoueur
                for(Pacman p : pacmans) {
                    p.setInvicible();
                }
                setGhostsEscapeStrategy();
                support.firePropertyChange("PacmanInvicible", null, pacman);

                /**
                 * règle avec les capsules, qui enclenche un timer au cours 
                 * desquelles les pacmans peuvent manger les fantômes
                 */
                new Timer().schedule(new TimerTask() {
                    @Override
                    public void run() {
                        for(Pacman p : pacmans) {
                            p.setNotInvicible();
                        }
                        setGhostsRandomStrategy();
                        support.firePropertyChange("PacmanNormal", null, pacman);
                    }
                }, 10000); 
            }            
        } 
    }

    public ArrayList<PositionAgent> getPacmansPosition() {
        ArrayList<PositionAgent> positions = new ArrayList<>();
        for (Pacman p : pacmans) {
            positions.add(p.getPos());
        }
        return positions;
    }

    public ArrayList<PositionAgent> getGhostsPosition() {
        ArrayList<PositionAgent> positions = new ArrayList<>();
        for (Ghost g : ghosts) {
            positions.add(g.getPos());
        }
        return positions;
    }

    public boolean isFoodEmpty() {
        for(int i = 0; i < maze.getSizeX(); i++) {
            for(int j = 0; j < maze.getSizeY(); j++) {
                if(maze.isFood(i, j)) {
                    return false;
                }
            }
        }
        return true;
    }
    
    public void checkCollisions(Map<Agent, PositionAgent> oldPositions) {
        for(Pacman p : pacmans) {
            if(!p.isAlive()) continue;

            for(Ghost g : ghosts) {
                if (!g.isAlive()) continue;

                // Détection si les agents ont la même position
                boolean sameCell = p.getPos().equals(g.getPos());
                // Détection si les agents se sont croisés
                boolean swapped = p.getPos().equals(oldPositions.get(g)) && g.getPos().equals(oldPositions.get(p));

                if(sameCell || swapped) {
                    if(p.isInvincible()) {
                        support.firePropertyChange("GhostEaten", null, null);
                        g.die(); 
                        // on "cache" le fantôme en dehors de la vue
                        g.setPos(new PositionAgent(-1, -1, 0)); 
                    } else {
                        support.firePropertyChange("PacmanDeath", null, null);
                        if(life <= 0) {
                            for(Pacman p2 : pacmans) 
                                p2.die();
                        } else {
                            life--;
                            p.respawn(); 
                            support.firePropertyChange("LifeChanged", null, null);
                        }
                    }
                }
            }
        }
    }

    public boolean isGameOver() {
    	if (pacmans.isEmpty()) return false;

        // 2. Condition de Victoire
        if (isFoodEmpty()) return true;

        // 3. Condition de Défaite (code existant)
        for(Pacman p : pacmans) {
            if(p.isAlive()) return false;
        }
        return true;
    }
    
    /**
     * synchronise le jeu locale avec les donneés reçues par le serveur
     * pour le moment on recharge l'entièreté du jeu à chaque fois
     */
    public void updateFromState(GameState state) {
    	
    	pacmans.clear();
    	ghosts.clear();
    	
    	for(PositionAgent p : state.getPacmanPositions()) {
    		pacmans.add(new Pacman(p));
    	}
    	
    	for(PositionAgent g : state.getGhostPositions()) {
    		ghosts.add(new Ghost(g));
    	}   
    	
        score = state.getScore();
        life = state.getLives();
    }
    
    public boolean isVictory() {
    	return isFoodEmpty();
    }

    public void gameOver() {
        LOGGER.info("Game Over");
        support.firePropertyChange("GameOver", null, null);
    }

    public Maze getMaze() { return maze; }

    public void setGhostsEscapeStrategy() {
        for(Ghost g : ghosts) {
            g.setStrategy(new EscapeStrategy(pacmans, ghosts));
        }
    }

    public void setGhostsRandomStrategy() {
        for(Ghost g : ghosts) {
            g.setStrategy(new RandomStrategy());
        }
    }

    public List<Ghost> getGhosts() {
        return ghosts;
    }

    public List<Pacman> getPacmans() {
        return pacmans;
    }

    public int getScore() {
        return score;
    }

    public int getLife() {
        return life;
    }

    public int getUp() {
        return up;
    }

    public void setLayout(String layout) {
        this.layout = layout;
    }
}