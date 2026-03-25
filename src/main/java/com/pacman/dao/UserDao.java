package com.pacman.dao;

import com.pacman.web.entity.User;

public interface UserDao {
    void saveUser(User user) throws DAOException;

    User findUser(String email) throws DAOException;
    
    void updateColor(long userId, String color) throws DAOException;
}
