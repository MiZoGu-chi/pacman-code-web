package com.pacman.dao;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class MySQLDAOFactory extends DAOFactory {
    private static final String FICHIER_PROPERTIES = "com/pacman/dao/dao.properties";
    private final String url;
    private final String username;
    private final String password;

    MySQLDAOFactory(String url, String username, String password) {
        this.url = url;
        this.username = username;
        this.password = password;
    }

    public static MySQLDAOFactory getInstance() throws DAOConfigurationException {
        Properties properties = new Properties();
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        InputStream fichierProperties = classLoader.getResourceAsStream(FICHIER_PROPERTIES);

        if (fichierProperties == null) {
            throw new DAOConfigurationException("Fichier properties " + FICHIER_PROPERTIES + " introuvable.");
        }

        try {
            properties.load(fichierProperties);
            String url = properties.getProperty("url");
            String driver = properties.getProperty("driver");
            String nomUtilisateur = properties.getProperty("nomutilisateur");
            String motDePasse = properties.getProperty("motdepasse");

            Class.forName(driver);
            return new MySQLDAOFactory(url, nomUtilisateur, motDePasse);
        } catch (IOException | ClassNotFoundException e) {
            throw new DAOConfigurationException("Erreur configuration MySQLDAOFactory", e);
        }
    }

    @Override
    public Connection getConnection() throws SQLException {
        return DriverManager.getConnection(url, username, password);
    }

    @Override
    public ScoreDao getScoreDao() {
        return new MySQLScoreDaoImpl(this);
    }
    
    @Override
    public UserDao getUserDao() {
        return new MySQLUserDaoImpl(this);
    }
}