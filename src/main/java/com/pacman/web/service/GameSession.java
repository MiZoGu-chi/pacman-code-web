package com.pacman.web.service;

import com.pacman.model.PacmanGame;
import com.pacman.dao.DAOFactory;
import com.pacman.model.AgentAction;
import com.pacman.model.Maze;
import com.pacman.model.agents.Ghost;
import com.pacman.model.agents.Pacman;
import com.pacman.web.entity.Score;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.List;
import javax.json.Json;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;

public class GameSession implements PropertyChangeListener {
    private final PacmanGame game;
    private int userId;
    private final List<GameStateListener> listeners = new ArrayList<>();

    public GameSession(String gameId, String layoutPath) {
        this.game = new PacmanGame(1000, layoutPath);
        this.game.addPropertyChangeListener(this);
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        switch (evt.getPropertyName()) {
            case "GameOver", "Victory" -> {
                saveScoreToDatabase();
            }
        }
        notifyStateChange();
    }

    private void saveScoreToDatabase() {
    	System.out.println("Tentative d'enregistrement - Player: " + this.userId + " Score: " + game.getScore());
        try {
            Score s = new Score();
            s.setUserId(this.userId);
            s.setScore(game.getScore());
            s.setGameDate(new java.util.Date());
            DAOFactory.getInstance().getScoreDao().saveScore(s);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void handleMove(int pacmanIdx, String dir) {
        if (pacmanIdx >= game.getPacmans().size()) return;
        
        int actionInt;
        switch (dir.toUpperCase()) {
            case "UP":    actionInt = AgentAction.NORTH; break;
            case "DOWN":  actionInt = AgentAction.SOUTH; break;
            case "LEFT":  actionInt = AgentAction.WEST;  break;
            case "RIGHT": actionInt = AgentAction.EAST;  break;
            default:      actionInt = AgentAction.STOP;  break;
        }
        
        game.getPacmans().get(pacmanIdx).setNextAction(actionInt);
    }

    public void start() { game.launch(); }
    public void pause() { game.pause(); }
    public void resume() { game.launch(); } 

    public void addStateListener(GameStateListener l) { listeners.add(l); }
    public void removeStateListener(GameStateListener l) { listeners.remove(l); }

    private void notifyStateChange() {
        String state = getStateAsJson();
        for (GameStateListener l : listeners) {
            l.onStateChange(state);
        }
    }

    public String getStateAsJson() {
        Maze maze = game.getMaze();
        JsonObjectBuilder stateBuilder = Json.createObjectBuilder();
        
        stateBuilder.add("mazeWidth", maze.getSizeX());
        stateBuilder.add("mazeHeight", maze.getSizeY());
        stateBuilder.add("score", game.getScore());
        stateBuilder.add("lives", game.getLife());
        stateBuilder.add("turn", game.getTurn());

        JsonArrayBuilder pacmansBuilder = Json.createArrayBuilder();
        for (Pacman p : game.getPacmans()) {
            pacmansBuilder.add(Json.createObjectBuilder()
                .add("x", p.getPos().getX())
                .add("y", p.getPos().getY())
                .add("dir", p.getPos().getDir())
                .add("alive", p.isAlive())
                .add("invincible", p.isInvincible()));
        }
        stateBuilder.add("pacmans", pacmansBuilder);

        JsonArrayBuilder ghostsBuilder = Json.createArrayBuilder();
        for (Ghost g : game.getGhosts()) {
            ghostsBuilder.add(Json.createObjectBuilder()
                .add("x", g.getPos().getX())
                .add("y", g.getPos().getY())
                .add("alive", g.isAlive()));
        }
        stateBuilder.add("ghosts", ghostsBuilder);

        JsonArrayBuilder wallsBuilder = Json.createArrayBuilder();
        JsonArrayBuilder foodBuilder = Json.createArrayBuilder();
        JsonArrayBuilder capsulesBuilder = Json.createArrayBuilder();
        
        for (int x = 0; x < maze.getSizeX(); x++) {
            for (int y = 0; y < maze.getSizeY(); y++) {
                if (maze.isWall(x, y)) 
                    wallsBuilder.add(Json.createObjectBuilder().add("x", x).add("y", y));
                if (maze.isFood(x, y)) 
                    foodBuilder.add(Json.createObjectBuilder().add("x", x).add("y", y));
                if (maze.isCapsule(x, y)) 
                    capsulesBuilder.add(Json.createObjectBuilder().add("x", x).add("y", y));
            }
        }
        
        stateBuilder.add("walls", wallsBuilder);
        stateBuilder.add("food", foodBuilder);
        stateBuilder.add("capsules", capsulesBuilder);

        JsonObject stateObject = stateBuilder.build();
        
        return Json.createObjectBuilder()
        		.add("type", "state")
        		.add("state", stateObject)
        		.build()
        		.toString();
    }

    public PacmanGame getGame() { return game; }

    public interface GameStateListener {
        void onStateChange(String jsonState);
    }
}