package com.pacman.dao;

import java.util.List;

import com.pacman.web.Score;

public interface ScoreDao {
    void saveScore(Score score) throws DAOException;

    List<Score> getTopScores(int limit) throws DAOException;

    void clearScores() throws DAOException;

    int getScoreCount() throws DAOException;
}
