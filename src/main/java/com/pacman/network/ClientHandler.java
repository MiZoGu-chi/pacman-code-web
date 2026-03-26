package com.pacman.network;

import java.io.*;
import java.net.Socket;

public class ClientHandler implements Runnable {
    private Socket socket;
    private ObjectInputStream in;
    private ObjectOutputStream out;
    private int clientId;

    public ClientHandler(Socket socket, int id) {
        this.socket = socket;
        this.clientId = id;
        try {
            out = new ObjectOutputStream(socket.getOutputStream());
            in = new ObjectInputStream(socket.getInputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        try {
            while (true) {
                Object obj = in.readObject();
                if (obj instanceof ChangeDirection cd) {
                    ServerMain.applyDirectionChange(clientId, cd.getDirection());
                }
            }
        } catch (Exception e) {
            System.out.println("Déconnexion du client " + clientId);
        } finally {
            // TRÈS IMPORTANT : On prévient le serveur que la place est libre
            ServerMain.removeClient(clientId);
            close();
        }
    }

    public void sendState(GameState state) {
        try {
            out.writeObject(state); // Envoi de l'enveloppe
            out.reset(); // Nettoyage pour le prochain envoi
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void close() {
        try {
            if (socket != null) socket.close(); // Fermeture du socket [cite: 139]
        } catch (IOException e) { }
    }
}