package com.pacman.web.service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;

public class GameManager {
    
    private static final Logger LOGGER = Logger.getLogger(GameManager.class.getName());
    private static GameManager instance;
    
    private final Map<String, GameSession> gameSessions = new ConcurrentHashMap<>();

    private GameManager() {
 	
    }

    public static synchronized GameManager getInstance() {
        if (instance == null) {
            instance = new GameManager();
        }
        return instance;
    }

    public GameSession createGame(String gameId, String layoutPath) {
        GameSession session = new GameSession(gameId, layoutPath);
        gameSessions.put(gameId, session);
        LOGGER.info("Game registered in GameManager: " + gameId);
        return session;
    }

    public GameSession getGame(String gameId) {
        return gameSessions.get(gameId);
    }

    public void removeGame(String gameId) {
        gameSessions.remove(gameId);
        LOGGER.info("Game removed from GameManager: " + gameId);
    }
}