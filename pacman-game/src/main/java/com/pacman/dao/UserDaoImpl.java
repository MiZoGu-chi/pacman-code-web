package com.pacman.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import com.pacman.web.User;

public class UserDaoImpl implements UserDao {

    private final DAOFactory daoFactory;

    public UserDaoImpl(DAOFactory daoFactory) {
        this.daoFactory = daoFactory;
    }

    @Override
    public void saveUser(User user) throws DAOException {
        String sql = "INSERT INTO users (pseudo, email, mot_de_passe) VALUES (?, ?, ?)";

        try (Connection conn = daoFactory.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            pstmt.setString(1, user.getName());
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
        return null;
    }
}
