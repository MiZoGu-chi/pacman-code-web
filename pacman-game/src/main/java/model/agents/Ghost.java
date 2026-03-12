package model.agents;

import java.util.Timer;
import java.util.TimerTask;

import model.PositionAgent;

public class Ghost extends Agent {
    
    private static final int RESPAWN_DELAY = 3000; 

    public Ghost(PositionAgent pos) {
        super(pos);
    }

    @Override 
    public void die() {
        super.die(); 

        System.out.println("Ghost died! Respawning in 3s...");

        Timer timer = new Timer();
        
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                respawn();
                System.out.println("Ghost respawned!");  
                timer.cancel(); 
            }
        }, RESPAWN_DELAY);
    }
}

