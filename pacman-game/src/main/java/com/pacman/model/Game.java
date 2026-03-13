package com.pacman.model;

import java.beans.*;

public abstract class Game implements Runnable {

    protected int turn;
    protected int maxTurn;
    protected boolean isRunning;
    protected Thread thread;
    protected long time; 

    protected final PropertyChangeSupport support = new PropertyChangeSupport(this);

    public Game(int maxTurn) {
        this.maxTurn = maxTurn;
        this.turn = 0;
        this.isRunning = false;
        this.time = 1000;
    }

    public int getTurn() {
        return turn;
    }

    public void setTurn(int turn) {
        this.turn = turn;
    }

    public void setMaxTurn(int maxTurn) {
        this.maxTurn = maxTurn;
    }

    public int getMaxTurn() {
        return maxTurn;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public boolean isRunning() {
        return isRunning;
    }

    public abstract void initializeGame();

    public void init() {
        this.turn = 0;

        // Le jeu est prêt, mais arrêté
        this.isRunning = false; 
        // On notifie que le jeu est arrêté
        support.firePropertyChange("isRunning", true, false);

        this.initializeGame();

        // On prévient la vue que tout est revenu à zéro (pour rafraîchir l'écran)
        support.firePropertyChange("turn", -1, 0); 
    }

   public abstract void takeTurn();

   public abstract boolean gameContinue();

    public void step() {
        if((turn < maxTurn) && gameContinue()) { 
            int oldTurn = turn;
            turn++;
            takeTurn();
            support.firePropertyChange("turn", oldTurn, turn);
        } else {
            this.isRunning = false;
        }
    }

    public void pause() { this.isRunning = false; }

    @Override
    public void run() { 
        while(this.isRunning()) {
            step();
            try {
                Thread.sleep(this.time);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public void resume() {
        this.isRunning = true;
        this.thread = new Thread(this);
        this.thread.start();
    }

    public void launch() {
        init();

        isRunning = true;

        // Pour la musique d'intro & le message de départ
        support.firePropertyChange("GameStart", null, null);

        // notifie que le jeu tourne
        support.firePropertyChange("isRunning", false, true);

        // on attend la fin de la musique d'intro qui dure 4sec avant de lancer le jeu
        // à corriger, bug lorsque l'user cliquer sur pause pendant ce temps (le jeu ne se met pas en pause)
        new Thread(() -> {
            try {
                Thread.sleep(4000);
                // correction du cas ou l'utilisateur appuie sur pause pendant le sleep
                if (isRunning) {
                    // pour enlevé le message de départ
                    support.firePropertyChange("GameStartFinished", null, null);
                    
                    resume(); 
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }).start();
    }

    public void addPropertyChangeListener(PropertyChangeListener listener) {
        support.addPropertyChangeListener(listener);
    }
}
