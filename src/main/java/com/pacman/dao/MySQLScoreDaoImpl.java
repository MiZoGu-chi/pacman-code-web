package com.pacman.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import com.pacman.web.entity.Score;

public class MySQLScoreDaoImpl implements ScoreDao {

    private final DAOFactory daoFactory;

    MySQLScoreDaoImpl(DAOFactory daoFactory) {
        this.daoFactory = daoFactory;
    }

    @Override
    public void saveScore(Score score) throws DAOException {
        String sql = "INSERT INTO scores (id_user, score, achieved_date) VALUES (?, ?, ?)";

        try (Connection conn = daoFactory.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            pstmt.setInt(1, score.getUserId());
            pstmt.setInt(2, score.getScore());
            pstmt.setTimestamp(3, new Timestamp(score.getGameDate().getTime()));

            pstmt.executeUpdate();

            try (ResultSet rs = pstmt.getGeneratedKeys()) {
                if (rs.next()) {
                    score.setId(rs.getInt(1));
                }
            }
        } catch (SQLException e) {
            throw new DAOException("Erreur lors de l'enregistrement du score", e);
        }
    }

    @Override
    public List<Score> getTopScores(int limit) throws DAOException {
        List<Score> topScores = new ArrayList<>();
        String sql = "SELECT s.id, u.username, s.score, s.achieved_date " +
                "FROM scores s JOIN users u ON s.id_user = u.id " +
                "ORDER BY s.score DESC LIMIT ?";

        try (Connection conn = daoFactory.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, limit);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Score s = new Score();
                    s.setId(rs.getInt("id"));
                    s.setPlayerName(rs.getString("username")); 
                    s.setScore(rs.getInt("score"));
                    s.setGameDate(new java.util.Date(rs.getTimestamp("achieved_date").getTime()));
                    topScores.add(s);
                }
            }
        } catch (SQLException e) {
            throw new DAOException("Error retrieving scores", e);
        }
        return topScores;
    }

    @Override
    public void clearScores() throws DAOException {
        String sql = "DELETE FROM scores";
        try (Connection conn = daoFactory.getConnection();
                Statement stmt = conn.createStatement()) {
            stmt.executeUpdate(sql);
        } catch (SQLException e) {
            throw new DAOException("Erreur lors de l'effacement des scores", e);
        }
    }
}