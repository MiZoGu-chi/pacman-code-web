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
	private static final int PORT = 12345; // Assure-toi que le ClientMain utilise le même !
    private static List<ClientHandler> clients = new ArrayList<>();
    static PacmanGame game;
    static AbstractController gameController;

    public static void main(String[] args) {
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            System.out.println("SERVEUR PACMAN : Prêt sur le port " + PORT);

            // 1. Initialisation du moteur de jeu réel
            game = new PacmanGame(100, "src/main/webapp/layouts/bigSearch_twoPacmans_oneGhost.lay");
            game.initializeGame();

            // 2. Initialisation du contrôleur pour la ViewCommand
            gameController = new ControllerPacmanGame(game);
            
            // 3. AFFICHAGE : Uniquement la télécommande admin
            SwingUtilities.invokeLater(() -> {
            	ViewCommand vCmd = new ViewCommand(game, gameController); 
                ((ControllerPacmanGame)gameController).setViewCommand(vCmd);
            });

            // 4. Lancement de la boucle de jeu
            new Thread(new GameLoop()).start();

            // 5. Attente des clients
            while (true) {
                Socket clientSocket = serverSocket.accept(); 
                ClientHandler handler = new ClientHandler(clientSocket, clients.size());
                synchronized (clients) {
                    clients.add(handler);
                }
                new Thread(handler).start();
                System.out.println("Joueur " + clients.size() + " connecté.");
            }
        } catch (IOException e) {
            System.err.println("Erreur Serveur : " + e.getMessage());
        }
    }

    public static void broadcastState(GameState state) {
        synchronized (clients) {
            for (ClientHandler c : clients) {
                c.sendState(state);
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