package com.pacman.dao;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class DAOFactory {
    private static final String FICHIER_PROPERTIES = "com/pacman/dao/dao.properties";
    private final String url;
    private final String username;
    private final String password;

    DAOFactory(String url, String username, String password) {
        this.url = url;
        this.username = username;
        this.password = password;
    }

    public static DAOFactory getInstance() throws DAOConfigurationException {
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
            return new DAOFactory(url, nomUtilisateur, motDePasse);
        } catch (IOException | ClassNotFoundException e) {
            throw new DAOConfigurationException("Erreur configuration DAOFactory", e);
        }
    }

    Connection getConnection() throws SQLException {
        return DriverManager.getConnection(url, username, password);
    }

    public ScoreDao getScoreDao() {
        return new ScoreDaoImpl(this);
    }
}