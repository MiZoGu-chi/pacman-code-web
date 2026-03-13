package com.pacman.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import com.pacman.web.Score;

public class ScoreDaoImpl implements ScoreDao {

    private final DAOFactory daoFactory;

    ScoreDaoImpl(DAOFactory daoFactory) {
        this.daoFactory = daoFactory;
    }

    @Override
    public void saveScore(Score score) throws DAOException {
        String sql = "INSERT INTO scores (pseudo, score, date_atteinte) VALUES (?, ?, ?)";

        try (Connection conn = daoFactory.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            pstmt.setString(1, score.getPlayerName());
            pstmt.setInt(2, score.getScore());
            // On convertit la Date java en Timestamp SQL
            pstmt.setTimestamp(3, new java.sql.Timestamp(score.getGameDate().getTime()));

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
        String sql = "SELECT id, pseudo, score, date_atteinte FROM scores ORDER BY score DESC LIMIT ?";

        try (Connection conn = daoFactory.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, limit);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    topScores.add(map(rs));
                }
            }
        } catch (SQLException e) {
            throw new DAOException("Erreur lors de la récupération des scores", e);
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

    @Override
    public int getScoreCount() throws DAOException {
        String sql = "SELECT COUNT(*) FROM scores";
        try (Connection conn = daoFactory.getConnection();
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery(sql)) {
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            throw new DAOException("Erreur lors du comptage des scores", e);
        }
        return 0;
    }

    private static Score map(ResultSet rs) throws SQLException {
        Score s = new Score();
        s.setId(rs.getInt("id"));
        s.setPlayerName(rs.getString("pseudo"));
        s.setScore(rs.getInt("score"));

        Timestamp timestamp = rs.getTimestamp("date_atteinte");
        if (timestamp != null) {
            s.setGameDate(new java.util.Date(timestamp.getTime()));
        }
        return s;
    }
}