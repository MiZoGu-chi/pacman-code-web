package com.pacman.network;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.Socket;
import java.net.URL;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

import com.pacman.model.PacmanGame;
import com.pacman.view.ViewPacmanGame;
import com.pacman.controller.InputHandler;

public class ClientMain {
    
    private static String currentPlayerName;
    private static Socket socket;
    private static ObjectOutputStream out;
    private static ObjectInputStream in;
    private static ViewPacmanGame view;
    private static PacmanGame localGame;

    public static void main(String[] args) {
        // 1. Initialisation du modèle local
        localGame = new PacmanGame(100, "src/main/webapp/layouts/bigSearch_twoPacmans_oneGhost.lay");

        SwingUtilities.invokeLater(() -> {
            // 2. Création de la vue (qui affiche le login par défaut)
            view = new ViewPacmanGame(localGame, null);
            
            // 3. Gestion du clic sur "Connexion"
            view.getViewLogin().addLoginListener(e -> {
                String user = view.getViewLogin().getUsername(); 
                String pass = view.getViewLogin().getPassword(); 

                // A. Vérification HTTP auprès de Tomcat
                if (verifyUser(user, pass)) {
                    System.out.println("Login réussi pour : " + user);
                    currentPlayerName = user; // On stocke le nom pour le score final

                    // B. Connexion au serveur de jeu (TCP) seulement si le login est OK
                    if (connectToGameServer()) {
                        // C. On switch l'affichage vers le labyrinthe
                        view.launchGame(); 
                        
                        // D. On branche les touches du clavier sur le réseau
                        setupInputHandler();
                        
                        // E. On lance l'écoute des messages du serveur
                        new Thread(new Receiver()).start();
                    }
                } else {
                    JOptionPane.showMessageDialog(view, "Erreur : Identifiants invalides sur Tomcat.");
                }
            });
            
            view.setVisible(true);
        });
    }

    // Méthode pour vérifier l'utilisateur via le Web (HTTP)
    private static boolean verifyUser(String email, String pass) {
        try {
            // 1. Correction de l'URL (/api/user) et du paramètre (email)
            URL url = new URL("http://localhost:8080/pacman-game/api/user?email=" + email);
            
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");

            // 2. Si le code est 200, l'utilisateur a été trouvé par le UserDao
            int responseCode = conn.getResponseCode();
            
            if (responseCode == 200) {
                // Optionnel : on pourrait lire le JSON ici pour récupérer la couleur du Pacman !
                return true;
            } else {
                System.out.println("Utilisateur introuvable (Code: " + responseCode + ")");
                return false;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    // Méthode pour ouvrir le tunnel de jeu (TCP)
    private static boolean connectToGameServer() {
        try {
            socket = new Socket("localhost", 12345);
            out = new ObjectOutputStream(socket.getOutputStream());
            in = new ObjectInputStream(socket.getInputStream());
            return true;
        } catch (IOException e) {
            JOptionPane.showMessageDialog(view, "Le serveur de jeu est éteint !");
            return false;
        }
    }

    // Configuration des commandes réseau
    private static void setupInputHandler() {
        InputHandler inputHandler = new InputHandler();
        inputHandler.setArrowControls(
            new NetworkCommand(out, 0), // Haut
            new NetworkCommand(out, 1), // Bas
            new NetworkCommand(out, 2), // Gauche
            new NetworkCommand(out, 3)  // Droite
        );
        view.setKeyListener(inputHandler);
    }

    static class Receiver implements Runnable {
        @Override
        public void run() {
            try {
                while (true) {
                	GameState state = (GameState) in.readObject(); 
                    
                    localGame.updateFromState(state);
                    
                    view.update(); 
                    
                    view.repaint();

                    if (state.isGameOver()) {
                        System.out.println("Fin de la partie !");
                        sendScoreToWeb(currentPlayerName, localGame.getScore());
                        break; 
                    }
                }
            } catch (Exception e) {
                System.out.println("Déconnecté du serveur.");
            }
        }
    }
    
    private static void sendScoreToWeb(String name, int finalScore) {
        try {
            URL url = new URL("http://localhost:8080/pacman-game/getScores"); //
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setDoOutput(true);

            String params = "playerName=" + name + "&score=" + finalScore; //
            
            try (OutputStream os = conn.getOutputStream()) {
                os.write(params.getBytes());
                os.flush();
            }

            if (conn.getResponseCode() == 200) {
                JOptionPane.showMessageDialog(view, "Partie finie ! Score de " + finalScore + " enregistré.");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}