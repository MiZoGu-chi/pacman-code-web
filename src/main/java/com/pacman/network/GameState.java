package com.pacman.network;

import java.io.Serializable;
import java.util.List;
import com.pacman.model.PositionAgent;

// DTO (DATA TRANSFERT OBJECT)
public class GameState implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private List<PositionAgent> pacmanPositions;
    private List<PositionAgent> ghostPositions;
   
    private boolean[][] foods;
    private boolean[][] capsules;
    
    private int score; 
    private int lives;
    
    private boolean gameOver;
    private boolean victory;

    public List<PositionAgent> getPacmanPositions() { return pacmanPositions; }
    public void setPacmanPositions(List<PositionAgent> pos) { pacmanPositions = pos; }

    public List<PositionAgent> getGhostPositions() { return ghostPositions; }
    public void setGhostPositions(List<PositionAgent> pos) { ghostPositions = pos; }

    public int getScore() { return score; }
    public void setScore(int score) { this.score = score; }

    public int getLives() { return lives; }
    public void setLives(int lives) { this.lives = lives; }

    public boolean isGameOver() { return gameOver; }
    public void setGameOver(boolean gameOver) { this.gameOver = gameOver; }

    public boolean isVictory() { return victory; }
    public void setVictory(boolean victory) { this.victory = victory; }
    
    public void setFoods(boolean[][] foods) { this.foods = foods; }
    public boolean[][] getFoods() { return foods; }
    
    public void setCapsules(boolean[][] capsules) { this.capsules = capsules; }
    public boolean[][] getCapsules() { return capsules; }
}