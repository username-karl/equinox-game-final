package com.equinox.game.entities.enemies;

import java.awt.Image;

// Extracted into its own file
public class Miniboss extends SpecialEnemy{
    // Constructor chain inheriting from SpecialEnemy
    public Miniboss(int x, int y, int width, int height, Image img, int enemyVelocityX, Image specialEnemyImg) {
        super(x, y, width, height, img, enemyVelocityX, specialEnemyImg);
    }
    public Miniboss(int x, int y, int width, int height, Image img, int enemyVelocityX, Image specialEnemyImg, int hitpoints, int shootCooldown, int specialEnemyVelocityY) {
        super(x, y, width, height, img, enemyVelocityX, specialEnemyImg, hitpoints, shootCooldown, specialEnemyVelocityY);
    }
    public Miniboss(int x, int y, int width, int height, Image img, int enemyVelocityX, Image specialEnemyImg, int hitpoints, int shootCooldown, int specialEnemyVelocityY,String enemyBossName) {
        super(x, y, width, height, img, enemyVelocityX, specialEnemyImg, hitpoints, shootCooldown, specialEnemyVelocityY,enemyBossName);
    }
    // Miniboss specific logic can be added here if needed later
} 