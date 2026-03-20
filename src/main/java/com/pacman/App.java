package com.pacman;

import com.pacman.controller.ControllerPacmanGame;
import com.pacman.model.PacmanGame;

public class App {
    public static void main(String[] args) {

        PacmanGame game = new PacmanGame(1000,"src/main/webapp/layouts/originalClassic_twoPacmans.lay");

        @SuppressWarnings("unused")
        ControllerPacmanGame controller = new ControllerPacmanGame(game);

        game.initializeGame();
        
        System.out.println("Client Java lancé. En attente de connexion...");
    }
}