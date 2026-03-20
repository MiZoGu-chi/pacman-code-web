package com.pacman.web.entity;

import java.io.Serializable;
import java.util.Date;

/**
 * Simple Score class for storing high scores.
 * Uses in-memory storage for simplicity (no database required).
 */
public class Score implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    private int id;
    private int userId;
    private String playerName;
    private int score;
    private Date gameDate;
    private String gameId;
    
    public Score() {
        this.gameDate = new Date();
    }
    
    public Score(String playerName, int score) {
        this.playerName = playerName;
        this.score = score;
        this.gameDate = new Date();
    }
    
    public Score(String playerName, int score, String gameId) {
        this.playerName = playerName;
        this.score = score;
        this.gameId = gameId;
        this.gameDate = new Date();
    }
    
    // Getters and Setters
    
    public int getId() {
        return id;
    }
    
    public void setId(int id) {
        this.id = id;
    }
    
    public int getUserId() {
    	return userId;
    }
    
    public void setUserId(int userId) {
    	this.userId = userId;
    }
    
    public String getPlayerName() {
        return playerName;
    }
    
    public void setPlayerName(String playerName) {
        this.playerName = playerName;
    }
    
    public int getScore() {
        return score;
    }
    
    public void setScore(int score) {
        this.score = score;
    }
    
    public Date getGameDate() {
        return gameDate;
    }
    
    public void setGameDate(Date gameDate) {
        this.gameDate = gameDate;
    }
    
    public String getGameId() {
        return gameId;
    }
    
    public void setGameId(String gameId) {
        this.gameId = gameId;
    }
    
    @Override
    public String toString() {
        return "Score{playerName='" + playerName + "', score=" + score + ", date=" + gameDate + "}";
    }
}
