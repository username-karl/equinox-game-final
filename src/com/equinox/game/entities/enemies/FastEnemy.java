package com.equinox.game.entities.enemies;

import java.awt.Image;

// Extracted into its own file
public class FastEnemy extends Enemy{
    public FastEnemy(int x, int y, int width, int height, Image img, int enemyVelocityX) {
        super(x, y, width, height, img, enemyVelocityX);
        this.enemyVelocityX = enemyVelocityX * 2; // Double the speed
    }
} 