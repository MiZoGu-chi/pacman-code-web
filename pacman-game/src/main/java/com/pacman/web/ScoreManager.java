package com.pacman.web;

import java.util.List;

import com.pacman.dao.ScoreDao;

public class ScoreManager {
    
    private ScoreDao scoreDao;
    
    public ScoreManager(ScoreDao scoreDao) {
        this.scoreDao = scoreDao;
    }
    
    public void saveScore(Score score) {
        scoreDao.saveScore(score);
    }
    
    public List<Score> getTopScores(int limit) {
        return scoreDao.getTopScores(limit);
    }

    public void clearScores() {
        scoreDao.clearScores();
    }

    public int getScoreCount() {
        return scoreDao.getScoreCount();
    }
}