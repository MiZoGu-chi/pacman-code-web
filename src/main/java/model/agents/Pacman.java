package model.agents;

import model.PositionAgent;

public class Pacman extends Agent {

    private boolean invincible;
    
    public Pacman(PositionAgent pos) {
        super(pos);
        setStrategy(null);
        invincible = false;
    }

    public boolean isInvincible() {
        return invincible;
    }

    public void setInvicible() {
        invincible = true;
    }

    public void setNotInvicible() {
        invincible = false;
    }
}
