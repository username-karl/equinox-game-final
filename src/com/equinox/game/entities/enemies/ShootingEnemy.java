package com.equinox.game.entities.enemies;

import com.equinox.game.entities.EnemyBullet; // Import specific bullet type

import java.awt.Image;
import java.util.ArrayList;

// Extracted into its own file
public class ShootingEnemy extends Enemy{
    private int shootCooldown = 100; // Cooldown ticks between shots
    private int currentCooldown = 0;

    public ShootingEnemy(int x, int y, int width, int height, Image img, int enemyVelocityX) {
        super(x, y, width, height, img, enemyVelocityX);
    }

    // Handles shooting logic, called within the game loop/update method
    public void shoot(ArrayList<EnemyBullet> enemyBulletArray, Image enemyBulletImg, int bulletWidth, int bulletHeight) {
        if (currentCooldown <= 0) {
            // Create a new bullet at the enemy's position
            EnemyBullet bullet = new EnemyBullet(getX() + getWidth() / 2 - bulletWidth / 2, // Center bullet horizontally
                                             getY() + getHeight(), 
                                             bulletWidth, 
                                             bulletHeight, 
                                             enemyBulletImg);
            enemyBulletArray.add(bullet);
            currentCooldown = shootCooldown; // Reset cooldown
        } else {
            currentCooldown--; // Decrease cooldown timer
        }
    }
} 