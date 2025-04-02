package com.equinox.game.entities;

import java.awt.Image;

// Extracted into its own file
public class EnemyBullet extends Bullet{
    private int bulletVelocityY = 10; // Default downward speed for enemy bullets

    public EnemyBullet(int x, int y, int width, int height, Image img) {
        super(x, y, width, height, img);
    }

    // Specific move method for enemy bullets (moves down)
    public void move(){
        // Calls the generic move from the parent Bullet class with its velocity
        super.move(bulletVelocityY); 
    }
}