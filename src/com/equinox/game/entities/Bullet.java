package com.equinox.game.entities;

import java.awt.Image;

// Extracted into its own file - extend Entity
public class Bullet extends Entity{
    private boolean used = false;

    public Bullet(int x, int y, int width, int height, Image img) {
        super(x, y, width, height, img);
    }

    // Override setUsed and isUsed from Block to manage bullet state
    @Override
    public void setUsed(boolean used) {
        this.used = used;
    }

    @Override
    public boolean isUsed() {
        return used;
    }

    // Basic vertical movement (can be overridden by subclasses)
    public void move(int velocityY) {
         setY(getY() + velocityY);
    }
} 