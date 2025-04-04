package com.equinox.game.entities;

import java.awt.Image;

// Extracted into its own file
public class EnemyBullet extends Bullet{
    // Removed private velocity field - inherited from Bullet
    // private int bulletVelocityY = 10;

    // Updated constructor to accept velocities and pass to super
    public EnemyBullet(int x, int y, int width, int height, Image img, int velocityX, int velocityY) {
        super(x, y, width, height, img, velocityX, velocityY);
    }

    // Removed move() override - inherits updated move() from Bullet
    /*
    public void move(){
        // Calls the generic move from the parent Bullet class with its velocity
        super.move(bulletVelocityY); 
    }
    */
}