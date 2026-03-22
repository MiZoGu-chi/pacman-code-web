package com.pacman.dao;

import java.sql.Connection;
import java.sql.SQLException;

public abstract class DAOFactory {
	
	public static final int MYSQL = 1;
	
	public abstract Connection getConnection() throws SQLException;
	
	public abstract UserDao getUserDao();
	public abstract ScoreDao getScoreDao();
	
	public static DAOFactory getDAOFactory(int whichFactory) throws DAOConfigurationException {
		switch (whichFactory) {
			case MYSQL:	
				return MySQLDAOFactory.getInstance();
			default:
				return null;
		}
	}
}
