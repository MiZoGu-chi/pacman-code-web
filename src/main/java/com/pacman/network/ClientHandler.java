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
                // On attend de recevoir un objet (ChangeDirection) du client
                Object obj = in.readObject();
                if (obj instanceof ChangeDirection) {
                    ChangeDirection cd = (ChangeDirection) obj;
                    // On applique le changement via ton ServerMain
                    ServerMain.applyDirectionChange(clientId, cd.getDirection());
                }
            }
        } catch (Exception e) {
            System.out.println("Client " + clientId + " déconnecté.");
        } finally {
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