package com.equinox.game.entities;

import java.awt.Image;

// Extracted into its own file - extend Entity
public class Bullet extends Entity{
    private boolean used = false;
    protected int velocityX = 0; // Add velocityX
    protected int velocityY = 0; // Add velocityY

    // Updated constructor - removed initialPierce
    public Bullet(int x, int y, int width, int height, Image img, int velocityX, int velocityY) {
        super(x, y, width, height, img);
        this.velocityX = velocityX;
        this.velocityY = velocityY;
    }

    // Getters for velocity (optional, but can be useful)
    public int getVelocityX() {
        return velocityX;
    }

    public int getVelocityY() {
        return velocityY;
    }

    // Setters for velocity (optional, allow changing direction mid-flight)
    public void setVelocityX(int velocityX) {
        this.velocityX = velocityX;
    }

    public void setVelocityY(int velocityY) {
        this.velocityY = velocityY;
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

    // Updated move method to use both velocityX and velocityY
    public void move() { // Removed parameter
         setX(getX() + velocityX); // Move horizontally
         setY(getY() + velocityY); // Move vertically
    }
} 