package com.pacman.network;

import java.util.ArrayList;
import java.util.List;

public class GameLoop implements Runnable {
    // Liste des changements de direction envoyés par les clients
    private static List<Runnable> actions = new ArrayList<>();

    public static synchronized void queueAction(Runnable r) {
        actions.add(r);
    }

    @Override
    public void run() {
        try {
            while (true) {
                if (ServerMain.game.isRunning()) { 
                    
                    synchronized (actions) {
                        for (Runnable r : actions) {
                            r.run();
                        }
                        actions.clear();
                    }

                    ServerMain.game.takeTurn();

                    GameState state = new GameState();
                    state.setPacmanPositions(ServerMain.game.getPacmansPosition());
                    state.setGhostPositions(ServerMain.game.getGhostsPosition());
                    state.setScore(ServerMain.game.getScore());
                    state.setLives(ServerMain.game.getLife());
                    state.setGameOver(ServerMain.game.isGameOver());
                    state.setVictory(ServerMain.game.isVictory());

                    int sizeX = ServerMain.game.getMaze().getSizeX();
                    int sizeY = ServerMain.game.getMaze().getSizeY();
                    boolean[][] currentFood = new boolean[sizeX][sizeY];
                    boolean[][] currentCapsules = new boolean[sizeX][sizeY];

                    for (int x = 0; x < sizeX; x++) {
                        for (int y = 0; y < sizeY; y++) {
                            currentFood[x][y] = ServerMain.game.getMaze().isFood(x, y);
                            currentCapsules[x][y] = ServerMain.game.getMaze().isCapsule(x, y);
                        }
                    }

                    state.setFoods(currentFood);
                    state.setCapsules(currentCapsules);
                    // --------------------------------------------------

                    ServerMain.broadcastState(state);
                }

                Thread.sleep(200); 

                if (ServerMain.game.isGameOver()) {
                    GameState finalState = new GameState();
                    finalState.setGameOver(true);
                    finalState.setVictory(ServerMain.game.isVictory());
                    finalState.setScore(ServerMain.game.getScore());

                    ServerMain.broadcastState(finalState); 
                    break; 
                }
            }
        } catch (Exception e) { 
            e.printStackTrace(); 
        }
    }
}