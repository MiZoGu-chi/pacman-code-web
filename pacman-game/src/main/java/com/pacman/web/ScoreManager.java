package com.pacman.web;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;


public class ScoreManager {
    
    private static final Logger LOGGER = Logger.getLogger(ScoreManager.class.getName());
    private static ScoreManager instance;
    
    // Paramètres de connexion
    private final String url = "jdbc:mysql://localhost:3306/pacman_db?useSSL=false&serverTimezone=UTC";
    private final String user = "root";
    private final String password = ""; // À remplir si tu as un mot de passe

    private ScoreManager() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            LOGGER.severe("Driver MySQL non trouvé !");
        }
    }
    
    public static synchronized ScoreManager getInstance() {
        if (instance == null) {
            instance = new ScoreManager();
        }
        return instance;
    }

    private Connection getConnection() throws SQLException {
        return DriverManager.getConnection(url, user, password);
    }
    
    public void saveScore(Score score) {
        String sql = "INSERT INTO scores (pseudo, score) VALUES (?, ?)";
        
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            pstmt.setString(1, score.getPlayerName()); 
            pstmt.setInt(2, score.getScore());
            pstmt.executeUpdate();

            // Récupérer l'ID auto-généré pour mettre à jour l'objet Score
            try (ResultSet rs = pstmt.getGeneratedKeys()) {
                if (rs.next()) {
                    score.setId(rs.getInt(1));
                }
            }
            LOGGER.info(() -> "Score saved to database: " + score);
            
        } catch (SQLException e) {
            LOGGER.severe(() -> "Error saving score: " + e.getMessage());
        }
    }
    
    public List<Score> getTopScores(int limit) {
        List<Score> topScores = new ArrayList<>();
        String sql = "SELECT id, pseudo, score FROM scores ORDER BY score DESC LIMIT ?";
        
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, limit);
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                Score s = new Score();
                s.setId(rs.getInt("id"));
                s.setPlayerName(rs.getString("pseudo"));
                s.setScore(rs.getInt("score"));
                topScores.add(s);
            }
        } catch (SQLException e) {
            LOGGER.severe(() -> "Error getting top scores: " + e.getMessage());
        }
        return topScores;
    }

    public void clearScores() {
        String sql = "DELETE FROM scores";
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement()) {
            stmt.executeUpdate(sql);
            LOGGER.info("Database scores table cleared");
        } catch (SQLException e) {
            LOGGER.severe(() -> "Error clearing scores: " + e.getMessage());
        }
    }

    public int getScoreCount() {
        String sql = "SELECT COUNT(*) FROM scores";
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            if (rs.next()) return rs.getInt(1);
        } catch (SQLException e) {
            LOGGER.severe(() -> "Error counting scores: " + e.getMessage());
        }
        return 0;
    }
}