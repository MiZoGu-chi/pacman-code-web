package com.pacman.network;

import com.pacman.model.commands.Command;
import java.io.ObjectOutputStream;
import java.io.IOException;

public class NetworkCommand implements Command {
    private ObjectOutputStream out;
    private int direction;

    public NetworkCommand(ObjectOutputStream out, int direction) {
        this.out = out;
        this.direction = direction;
    }

    @Override
    public void execute() {
        try {
            out.writeObject(new ChangeDirection(direction));
            out.flush(); 
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}