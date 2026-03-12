package com.pacman.web;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.logging.Logger;
import javax.json.Json;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObjectBuilder;
import model.AgentAction;
import model.Maze;
import model.PacmanGame;
import model.PositionAgent;
import model.agents.Ghost;
import model.agents.Pacman;
import model.strategies.KeyboardStrategy;

public class GameManager {
    private static final Logger LOGGER = Logger.getLogger(GameManager.class.getName());
    private static GameManager instance;
    private final Map<String, GameSession> gameSessions = new ConcurrentHashMap<>();

    public static synchronized GameManager getInstance() {
        if (instance == null) {
            instance = new GameManager();
        }
        return instance;
    }

    public GameSession createGame(String gameId, String layoutPath) {
        GameSession session = new GameSession(gameId, layoutPath);
        gameSessions.put(gameId, session);
        LOGGER.info("Created game session: " + gameId);
        return session;
    }
    public GameSession getGame(String gameId) {
        return gameSessions.get(gameId);
    }
    public void removeGame(String gameId) {
        GameSession session = gameSessions.remove(gameId);
        if (session != null) {
            session.stop();
            LOGGER.info("Removed game session: " + gameId);
        }
    }
    public List<String> getActiveGameIds() {
        return new ArrayList<>(gameSessions.keySet());
    }

    public static class GameSession implements PropertyChangeListener {
        private final String gameId;
        private final PacmanGame game;
        private final Map<String, Integer> playerPacmanMap = new ConcurrentHashMap<>();
        private final List<GameStateListener> listeners = new CopyOnWriteArrayList<>();
        private int nextPacmanIndex = 0;

        public GameSession(String gameId, String layoutPath) {
            this.gameId = gameId;
            this.game = new PacmanGame(1000, layoutPath);
            this.game.init();
            this.game.addPropertyChangeListener(this);
        }

        public synchronized int addPlayer(String playerId) {
            if (playerPacmanMap.containsKey(playerId)) {
                int existingIndex = playerPacmanMap.get(playerId);
                LOGGER.info("Player " + playerId + " already in game " + gameId + " with Pacman " + existingIndex);
                return existingIndex;
            }
            
            List<Pacman> pacmans = game.getPacmans();
            if (nextPacmanIndex >= pacmans.size()) {
                LOGGER.warning("No more Pacmans available for player: " + playerId);
                return -1;
            }
            
            int pacmanIndex = nextPacmanIndex++;
            playerPacmanMap.put(playerId, pacmanIndex);
            
            pacmans.get(pacmanIndex).setStrategy(new KeyboardStrategy());
            
            LOGGER.info("Player " + playerId + " joined game " + gameId + " controlling Pacman " + pacmanIndex);
            return pacmanIndex;
        }
        public void removePlayer(String playerId) {
            playerPacmanMap.remove(playerId);
            LOGGER.info("Player " + playerId + " left game " + gameId);
        }
        public void handlePlayerMove(String playerId, String direction) {
            Integer pacmanIndex = playerPacmanMap.get(playerId);
            if (pacmanIndex == null) {
                LOGGER.warning("Player " + playerId + " not in game " + gameId);
                return;
            }
            
            List<Pacman> pacmans = game.getPacmans();
            if (pacmanIndex >= pacmans.size()) {
                return;
            }
            
            Pacman pacman = pacmans.get(pacmanIndex);
            int dirCode = parseDirectionCode(direction);
            AgentAction action = parseDirection(direction);
            
            pacman.setNextAction(dirCode);
            
            if (action != null && isValidMove(pacman, action)) {
                game.moveAgent(pacman, action);
                notifyStateChange();
            }
            
            LOGGER.info("Player " + playerId + " moved " + direction + " for pacman " + pacmanIndex);
        }

        private int parseDirectionCode(String direction) {
            if (direction == null) return AgentAction.STOP;
            
            switch (direction.toUpperCase()) {
                case "UP":
                case "NORTH":
                    return AgentAction.NORTH;
                case "DOWN":
                case "SOUTH":
                    return AgentAction.SOUTH;
                case "LEFT":
                case "WEST":
                    return AgentAction.WEST;
                case "RIGHT":
                case "EAST":
                    return AgentAction.EAST;
                default:
                    return AgentAction.STOP;
            }
        }
        private AgentAction parseDirection(String direction) {
            if (direction == null) return null;
            
            switch (direction.toUpperCase()) {
                case "UP":
                case "NORTH":
                    return new AgentAction(AgentAction.NORTH);
                case "DOWN":
                case "SOUTH":
                    return new AgentAction(AgentAction.SOUTH);
                case "LEFT":
                case "WEST":
                    return new AgentAction(AgentAction.WEST);
                case "RIGHT":
                case "EAST":
                    return new AgentAction(AgentAction.EAST);
                default:
                    return new AgentAction(AgentAction.STOP);
            }
        }
        private boolean isValidMove(Pacman pacman, AgentAction action) {
            Maze maze = game.getMaze();
            PositionAgent pos = pacman.getPos();
            int newX = pos.getX() + action.get_vx();
            int newY = pos.getY() + action.get_vy();
            
            return newX >= 0 && newX < maze.getSizeX() 
                && newY >= 0 && newY < maze.getSizeY()
                && !maze.isWall(newX, newY);
        }
        public void start() {
            game.resume();
        }
        public void pause() {
            game.pause();
        }
        public void resume() {
            game.resume();
        }
        public void stop() {
            game.pause();
        }
        public String getStateAsJson() {
            Maze maze = game.getMaze();
            
            JsonObjectBuilder stateBuilder = Json.createObjectBuilder()
                .add("gameId", gameId)
                .add("score", game.getScore())
                .add("lives", game.getLife())
                .add("turn", game.getTurn())
                .add("isRunning", game.isRunning())
                .add("mazeWidth", maze.getSizeX())
                .add("mazeHeight", maze.getSizeY());
            JsonArrayBuilder pacmansBuilder = Json.createArrayBuilder();
            for (Pacman p : game.getPacmans()) {
                PositionAgent pos = p.getPos();
                pacmansBuilder.add(Json.createObjectBuilder()
                    .add("x", pos.getX())
                    .add("y", pos.getY())
                    .add("dir", pos.getDir())
                    .add("invincible", p.isInvincible())
                    .add("alive", p.isAlive()));
            }
            stateBuilder.add("pacmans", pacmansBuilder);
            JsonArrayBuilder ghostsBuilder = Json.createArrayBuilder();
            for (Ghost g : game.getGhosts()) {
                PositionAgent pos = g.getPos();
                ghostsBuilder.add(Json.createObjectBuilder()
                    .add("x", pos.getX())
                    .add("y", pos.getY())
                    .add("dir", pos.getDir())
                    .add("alive", g.isAlive()));
            }
            stateBuilder.add("ghosts", ghostsBuilder);
            
            JsonArrayBuilder wallsBuilder = Json.createArrayBuilder();
            JsonArrayBuilder foodBuilder = Json.createArrayBuilder();
            JsonArrayBuilder capsulesBuilder = Json.createArrayBuilder();
            
            for (int y = 0; y < maze.getSizeY(); y++) {
                for (int x = 0; x < maze.getSizeX(); x++) {
                    if (maze.isWall(x, y)) {
                        wallsBuilder.add(Json.createObjectBuilder().add("x", x).add("y", y));
                    }
                    if (maze.isFood(x, y)) {
                        foodBuilder.add(Json.createObjectBuilder().add("x", x).add("y", y));
                    }
                    if (maze.isCapsule(x, y)) {
                        capsulesBuilder.add(Json.createObjectBuilder().add("x", x).add("y", y));
                    }
                }
            }
            stateBuilder.add("walls", wallsBuilder);
            stateBuilder.add("food", foodBuilder);
            stateBuilder.add("capsules", capsulesBuilder);
            
            return stateBuilder.build().toString();
        }
        public PacmanGame getGame() {
            return game;
        }
        
        public int getPlayerCount() {
            return playerPacmanMap.size();
        }
        
        public void addStateListener(GameStateListener listener) {
            listeners.add(listener);
        }
        
        public void removeStateListener(GameStateListener listener) {
            listeners.remove(listener);
        }
        
        private void notifyStateChange() {
            String state = getStateAsJson();
            for (GameStateListener listener : listeners) {
                listener.onStateChange(state);
            }
        }
        @Override
        public void propertyChange(PropertyChangeEvent evt) {
            // Forward game events to all listeners
            notifyStateChange();
        }
        
    }
    public interface GameStateListener {
        void onStateChange(String jsonState);
    }
}
