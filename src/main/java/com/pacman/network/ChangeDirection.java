package com.pacman.network;

import java.io.Serializable;

public class ChangeDirection implements Serializable {
    private static final long serialVersionUID = 1L;
    private int direction; // 0, 1, 2, 3

    public ChangeDirection(int direction) {
        this.direction = direction;
    }

    public int getDirection() {
        return direction;
    }
}