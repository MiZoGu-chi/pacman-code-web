package com.pacman.web;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.websocket.CloseReason;
import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;

/**
 * WebSocket endpoint for real-time Pacman game communication.
 * Handles player connections, movements, and broadcasts game state updates.
 */
@ServerEndpoint("/ws/game/{gameId}/{playerId}")
public class GameWebSocket implements GameManager.GameStateListener {
    
    private static final Logger LOGGER = Logger.getLogger(GameWebSocket.class.getName());
    
    // Map to track all sessions by gameId -> playerId -> session
    private static final Map<String, Map<String, Session>> gameSessions = new ConcurrentHashMap<>();
    
    private String gameId;
    private String playerId;
    private Session session;
    
    @OnOpen
    public void onOpen(Session session, 
                       @PathParam("gameId") String gameId,
                       @PathParam("playerId") String playerId) {
        this.session = session;
        this.gameId = gameId;
        this.playerId = playerId;
        
        LOGGER.info("WebSocket opened: gameId=" + gameId + ", playerId=" + playerId);
        
        // Add session to tracking map
        gameSessions.computeIfAbsent(gameId, k -> new ConcurrentHashMap<>()).put(playerId, session);
        
        // Get or create game session
        GameManager manager = GameManager.getInstance();
        GameManager.GameSession gameSession = manager.getGame(gameId);
        
        if (gameSession == null) {
            // Create new game with default layout if it doesn't exist
            String layoutPath = session.getRequestParameterMap().getOrDefault("layout", 
                java.util.List.of("layouts/mediumClassic.lay")).get(0);
            gameSession = manager.createGame(gameId, layoutPath);
        }
        
        // Add player to game
        int pacmanIndex = gameSession.addPlayer(playerId);
        
        // Register this WebSocket as a listener for game state changes
        gameSession.addStateListener(this);
        
        // Send initial game state to the new player
        try {
            String initMessage = "{\"type\":\"init\",\"pacmanIndex\":" + pacmanIndex + 
                ",\"state\":" + gameSession.getStateAsJson() + "}";
            session.getBasicRemote().sendText(initMessage);
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Error sending init message", e);
        }
        
        // Notify other players about new player
        broadcastToGame(gameId, "{\"type\":\"playerJoined\",\"playerId\":\"" + playerId + "\"}");
    }
    
    @OnMessage
    public void onMessage(String message, Session session) {
        LOGGER.fine("Received message from " + playerId + ": " + message);
        
        try {
            // Parse simple JSON message format: {"action":"move","direction":"UP"}
            // For simplicity, we parse manually without external JSON library
            if (message.contains("\"action\"")) {
                if (message.contains("\"move\"")) {
                    String direction = extractValue(message, "direction");
                    handleMove(direction);
                } else if (message.contains("\"start\"")) {
                    handleStart();
                } else if (message.contains("\"pause\"")) {
                    handlePause();
                } else if (message.contains("\"resume\"")) {
                    handleResume();
                }
            }
        } catch (Exception e) {
            LOGGER.log(Level.WARNING, "Error processing message: " + message, e);
        }
    }
    
    private String extractValue(String json, String key) {
        String searchKey = "\"" + key + "\":\"";
        int start = json.indexOf(searchKey);
        if (start == -1) return null;
        start += searchKey.length();
        int end = json.indexOf("\"", start);
        if (end == -1) return null;
        return json.substring(start, end);
    }
    
    private void handleMove(String direction) {
        GameManager.GameSession gameSession = GameManager.getInstance().getGame(gameId);
        if (gameSession != null) {
            gameSession.handlePlayerMove(playerId, direction);
        }
    }
    
    private void handleStart() {
        GameManager.GameSession gameSession = GameManager.getInstance().getGame(gameId);
        if (gameSession != null) {
            gameSession.start();
            broadcastToGame(gameId, "{\"type\":\"gameStarted\"}");
        }
    }
    
    private void handlePause() {
        GameManager.GameSession gameSession = GameManager.getInstance().getGame(gameId);
        if (gameSession != null) {
            gameSession.pause();
            broadcastToGame(gameId, "{\"type\":\"gamePaused\"}");
        }
    }
    
    private void handleResume() {
        GameManager.GameSession gameSession = GameManager.getInstance().getGame(gameId);
        if (gameSession != null) {
            gameSession.resume();
            broadcastToGame(gameId, "{\"type\":\"gameResumed\"}");
        }
    }
    
    @OnClose
    public void onClose(Session session, CloseReason closeReason) {
        LOGGER.info("WebSocket closed: playerId=" + playerId + ", reason=" + closeReason);
        
        // Remove session from tracking
        Map<String, Session> players = gameSessions.get(gameId);
        if (players != null) {
            players.remove(playerId);
            if (players.isEmpty()) {
                gameSessions.remove(gameId);
                // Optionally remove the game session when all players leave
                // GameManager.getInstance().removeGame(gameId);
            }
        }
        
        // Remove player from game
        GameManager.GameSession gameSession = GameManager.getInstance().getGame(gameId);
        if (gameSession != null) {
            gameSession.removePlayer(playerId);
            gameSession.removeStateListener(this);
            
            // Notify other players
            broadcastToGame(gameId, "{\"type\":\"playerLeft\",\"playerId\":\"" + playerId + "\"}");
        }
    }
    
    @OnError
    public void onError(Session session, Throwable throwable) {
        LOGGER.log(Level.SEVERE, "WebSocket error for player " + playerId, throwable);
    }
    
    /**
     * Called when game state changes - sends only to THIS player's session.
     * Each WebSocket instance is its own listener and sends to its own session.
     */
    @Override
    public void onStateChange(String jsonState) {
        if (session != null && session.isOpen()) {
            String message = "{\"type\":\"state\",\"state\":" + jsonState + "}";
            try {
                session.getBasicRemote().sendText(message);
            } catch (IOException e) {
                LOGGER.log(Level.WARNING, "Error sending state to player " + playerId, e);
            }
        }
    }
    
    /**
     * Broadcasts a message to all players in a specific game.
     */
    private void broadcastToGame(String gameId, String message) {
        Map<String, Session> players = gameSessions.get(gameId);
        if (players == null) return;
        
        for (Session playerSession : players.values()) {
            if (playerSession.isOpen()) {
                try {
                    playerSession.getBasicRemote().sendText(message);
                } catch (IOException e) {
                    LOGGER.log(Level.WARNING, "Error broadcasting to session", e);
                }
            }
        }
    }
}
