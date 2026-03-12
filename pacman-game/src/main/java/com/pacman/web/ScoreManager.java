package com.pacman.web;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.logging.Logger;

/**
 * Simple in-memory score manager.
 * Stores high scores without requiring a database.
 * Scores are lost when the server restarts.
 */
public class ScoreManager {
    
    private static final Logger LOGGER = Logger.getLogger(ScoreManager.class.getName());
    private static ScoreManager instance;
    
    private final List<Score> scores = new CopyOnWriteArrayList<>();
    private int nextId = 1;
    
    private ScoreManager() {}
    
    public static synchronized ScoreManager getInstance() {
        if (instance == null) {
            instance = new ScoreManager();
        }
        return instance;
    }
    
    /**
     * Saves a new score.
     */
    public synchronized void saveScore(Score score) {
        score.setId(nextId++);
        scores.add(score);
        LOGGER.info("Score saved: " + score);
    }
    
    /**
     * Gets the top scores, sorted by score descending.
     */
    public List<Score> getTopScores(int limit) {
        List<Score> sortedScores = new ArrayList<>(scores);
        sortedScores.sort(Comparator.comparingInt(Score::getScore).reversed());
        
        if (sortedScores.size() > limit) {
            return sortedScores.subList(0, limit);
        }
        return sortedScores;
    }
    
    /**
     * Gets all scores for a specific game.
     */
    public List<Score> getScoresForGame(String gameId) {
        List<Score> gameScores = new ArrayList<>();
        for (Score score : scores) {
            if (gameId.equals(score.getGameId())) {
                gameScores.add(score);
            }
        }
        gameScores.sort(Comparator.comparingInt(Score::getScore).reversed());
        return gameScores;
    }
    
    /**
     * Clears all scores.
     */
    public void clearScores() {
        scores.clear();
        LOGGER.info("All scores cleared");
    }
    
    /**
     * Gets the total number of scores.
     */
    public int getScoreCount() {
        return scores.size();
    }
}
