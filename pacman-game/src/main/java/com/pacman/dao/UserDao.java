package com.pacman.dao;

import com.pacman.web.User;

public interface UserDao {
    void saveUser(User user) throws DAOException;

    User findUser(String email) throws DAOException;
}
