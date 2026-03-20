package com.pacman.web.websocket;

import javax.json.JsonObject;
import javax.websocket.*;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;

import com.pacman.web.entity.User;
import com.pacman.web.service.GameManager;
import com.pacman.web.service.GameSession;

@ServerEndpoint(value = "/ws/game/{gameId}/{playerId}", configurator = HttpSessionConfigurator.class)
public class GameWebSocket implements GameSession.GameStateListener {
    
    private Session wsSession;
    private String gameId;

    @OnOpen
    public void onOpen(Session session, 
                       @PathParam("gameId") String gameId, 
                       @PathParam("playerId") String playerId) { // Ajoute playerId ici
        this.wsSession = session;
        this.gameId = gameId;
        
        User user = (User) session.getUserProperties().get("currentUser");
        GameSession gameSession = GameManager.getInstance().getGame(gameId);
        
        // Si l'un des deux est null, rien ne sera envoyé au JS !
        if (gameSession != null && user != null) {
            gameSession.setUserId(user.getId().intValue());
            gameSession.addStateListener(this);

            // Construction du message d'initialisation
            String initMsg = javax.json.Json.createObjectBuilder()
                .add("type", "init")
                .add("pacmanIndex", 0) 
                .add("state", javax.json.Json.createReader(new java.io.StringReader(gameSession.getStateAsJson())).readObject().getJsonObject("state"))
                .build().toString();
            
            try { session.getBasicRemote().sendText(initMsg); } catch (Exception e) { e.printStackTrace(); }
        } else {
            // Ajoute ces logs pour déboguer dans ta console Java
            if (user == null) System.out.println("ERREUR : User null dans WebSocket !");
            if (gameSession == null) System.out.println("ERREUR : GameSession null !");
        }
    }

    @OnMessage
    public void onMessage(String message) {
        GameSession session = GameManager.getInstance().getGame(gameId);
        
        // On parse le JSON reçu du JS : {action: 'move', direction: 'UP'}
        JsonObject json = javax.json.Json.createReader(new java.io.StringReader(message)).readObject();
     
        String action = json.getString("action");

        switch (action) {
            case "move"   -> session.handleMove(0, json.getString("direction"));
            case "start"  -> session.start();
            case "pause"  -> session.pause();
            case "resume" -> session.resume();
            default       -> System.out.println("Action inconnue : " + action);
        }
    }	

    @Override
    public void onStateChange(String jsonState) {
        try { wsSession.getBasicRemote().sendText(jsonState); } catch (Exception e) {}
    }
}