package com.pacman.network;

import java.net.ServerSocket;

import java.net.Socket;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.SwingUtilities;

import com.pacman.controller.AbstractController;
import com.pacman.controller.ControllerPacmanGame;
import com.pacman.model.PacmanGame;
import com.pacman.model.agents.Pacman;
import com.pacman.model.commands.Command;
import com.pacman.view.ViewCommand;
import com.pacman.model.commands.ChangeDirectionCommand;



public class ServerMain {
	
	private static int MAX_NUMBER_OF_PLAYERS = 4; // arbitraire
	
	private static final ClientHandler[] clients = new ClientHandler[MAX_NUMBER_OF_PLAYERS];
	
	private static final int PORT = 12345; // ajouter le choix du port au lacnement
    static PacmanGame game;
    static AbstractController gameController;

    public static void main(String[] args) {
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            System.out.println("SERVEUR PACMAN : Prêt sur le port " + PORT);

            game = new PacmanGame(100, "src/main/webapp/layouts/bigSearch_twoPacmans_oneGhost.lay");
            game.initializeGame();

            gameController = new ControllerPacmanGame(game);
            
            // 3. AFFICHAGE : Uniquement la télécommande admin
            SwingUtilities.invokeLater(() -> {
            	ViewCommand vCmd = new ViewCommand(game, gameController); 
                ((ControllerPacmanGame)gameController).setViewCommand(vCmd);
            });

            // 4. Lancement de la boucle de jeu
            new Thread(new GameLoop()).start();

            while (true) {
                Socket clientSocket = serverSocket.accept();
                
                synchronized (clients) {
                    int slot = -1;
                    // On cherche le premier emplacement vide
                    for (int i = 0; i < clients.length; i++) {
                        if (clients[i] == null) {
                            slot = i;
                            break;
                        }
                    }

                    if (slot != -1) {
                        ClientHandler handler = new ClientHandler(clientSocket, slot);
                        clients[slot] = handler;
                        new Thread(handler).start();
                        System.out.println("Joueur connecté au slot : " + slot);
                    } else {
                        System.out.println("Serveur plein, connexion refusée.");
                        clientSocket.close();
                    }
                }
            }            
        } catch (IOException e) {
            System.err.println("Erreur Serveur : " + e.getMessage());
        }
    }

    public static void broadcastState(GameState state) {
        synchronized (clients) {
            for (ClientHandler c : clients) {
                if (c != null) c.sendState(state);
            }
        }
    }
    
    public static void removeClient(int clientId) {
        synchronized (clients) {
            if (clientId >= 0 && clientId < clients.length) {
                clients[clientId] = null;
                System.out.println("Slot " + clientId + " libéré.");
            }
        }
    }

    public static void applyDirectionChange(int clientId, int direction) {
        List<Pacman> pacmans = game.getPacmans();
        if (clientId < pacmans.size()) {
            Pacman p = pacmans.get(clientId);
            
            Command cmd = new ChangeDirectionCommand(p, direction);
            
            GameLoop.queueAction(() -> {
                if (p.isAlive()) {
                    cmd.execute(); 
                }
            });
        }
    }

    public static void shutdown() {
        System.out.println("Arrêt du serveur...");
        System.exit(0);
    }
}