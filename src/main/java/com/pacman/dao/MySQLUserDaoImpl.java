package com.pacman.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import com.pacman.web.entity.User;

public class MySQLUserDaoImpl implements UserDao {

    private final DAOFactory daoFactory;

    public MySQLUserDaoImpl(DAOFactory daoFactory) {
        this.daoFactory = daoFactory;
    }

    @Override
    public void saveUser(User user) throws DAOException {
        String sql = "INSERT INTO users (username, email, password) VALUES (?, ?, ?)";

        try (Connection conn = daoFactory.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            pstmt.setString(1, user.getUsername());
            pstmt.setString(2, user.getEmail());
            pstmt.setString(3, user.getPassword());

            pstmt.executeUpdate();

            try (ResultSet rs = pstmt.getGeneratedKeys()) {
                if (rs.next()) {
                    user.setId(rs.getLong(1));
                }
            }
        } catch (SQLException e) {
            throw new DAOException("Registration error", e);
        }
    }

    @Override
    public User findUser(String email) throws DAOException {
    	User user = null;
        String sql = "SELECT id, email, password, username, color, victories FROM users WHERE email = ?";

        try (Connection conn = daoFactory.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, email);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    user = new User();
                    
                    user.setId(rs.getLong("id"));
                    user.setEmail(rs.getString("email"));
                    user.setPassword(rs.getString("password"));
                    user.setUsername(rs.getString("username"));
                    user.setColor(rs.getString("color"));
                    user.setVictories(rs.getInt("victories"));
                }
            }
        } catch (SQLException e) {
            throw new DAOException("Error finding user", e);
        }
        return user;
    }

	@Override
	public void updateColor(long userId, String color) throws DAOException {
		String sql = "UPDATE users SET color = ? Where id = ?";
		
        try (Connection conn = daoFactory.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {          
               pstmt.setString(1, color);
               pstmt.setLong(2, userId);
               pstmt.executeUpdate();

           } catch (SQLException e) {
               throw new DAOException("Error updating color", e);
           }
	}
}
