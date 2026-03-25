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
                    
                    // 1. EXÉCUTION DES COMMANDES : On vide la liste des actions reçues
                    synchronized (actions) {
                        for (Runnable r : actions) {
                            r.run();
                        }
                        actions.clear();
                    }

                    // 2. Avancement du jeu
                    ServerMain.game.takeTurn();

                    // 3. PRÉPARATION DE L'ENVELOPPE : On remplit le GameState
                    GameState state = new GameState();
                    state.setPacmanPositions(ServerMain.game.getPacmansPosition());
                    state.setGhostPositions(ServerMain.game.getGhostsPosition());
                    state.setScore(ServerMain.game.getScore());
                    state.setLives(ServerMain.game.getLife());
                    state.setGameOver(ServerMain.game.isGameOver()); //
                    state.setVictory(ServerMain.game.isVictory());

                    // 4. DIFFUSION : On envoie à tous les clients
                    ServerMain.broadcastState(state);
                }

                // Vitesse de la boucle (modifiable via le slider de ViewCommand)
                Thread.sleep(200); 

                if (ServerMain.game.isGameOver()) {
                    System.out.println("Fin de partie sur le serveur.");
                    break;
                }
            }
        } catch (Exception e) { e.printStackTrace(); }
    }
}