package com.equinox.game.entities.enemies;

import com.equinox.game.entities.EnemyBullet; // Import specific bullet type

import java.awt.Image;
import java.util.ArrayList;
import java.util.List; // Import List interface

// Extracted into its own file
public class ShootingEnemy extends Enemy{
    private int shootCooldown = 60; // Cooldown ticks between shots (Decreased from 100)
    private int currentCooldown = 0;

    public ShootingEnemy(int x, int y, int width, int height, Image img, int enemyVelocityX) {
        // Call new Enemy constructor, passing slightly increased health (e.g., 2)
        super(x, y, width, height, img, enemyVelocityX, 2); // Pass 2 as initialHealth
    }

    // Handles shooting logic, accept List instead of ArrayList
    public void shoot(List<EnemyBullet> enemyBulletArray, Image enemyBulletImg, int bulletWidth, int bulletHeight) {
        if (currentCooldown <= 0) {
            // Create a new bullet at the enemy's position with default velocity
            int defaultVelocityX = 0;
            int defaultVelocityY = 5; // Default enemy bullet speed
            EnemyBullet bullet = new EnemyBullet(getX() + getWidth() / 2 - bulletWidth / 2, // Center bullet horizontally
                                             getY() + getHeight(), 
                                             bulletWidth, 
                                             bulletHeight, 
                                             enemyBulletImg,
                                             defaultVelocityX,
                                             defaultVelocityY);
            enemyBulletArray.add(bullet);
            currentCooldown = shootCooldown; // Reset cooldown
        } else {
            currentCooldown--; // Decrease cooldown timer
        }
    }
} 