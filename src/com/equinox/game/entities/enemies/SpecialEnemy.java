package com.equinox.game.entities.enemies;

import com.equinox.game.entities.EnemyBullet;

import java.awt.Image;
import java.util.ArrayList;

// Extracted into its own file
public class SpecialEnemy extends Enemy{
    Image specialEnemyImg; // Often the same as img, maybe remove?
    // Default specialEnemy values
    private String enemyBossName="";
    private int hitpoints = 20;
    private int maxHitpoints = 20;
    private int shootCooldown = 100; // Ticks between shots
    private int currentCooldown = 0;
    private int specialEnemyVelocityY = 1; // Vertical movement speed

    // Constructor chain for flexibility
    public SpecialEnemy(int x, int y, int width, int height, Image img, int enemyVelocityX, Image specialEnemyImg) {
        super(x, y, width, height, img, enemyVelocityX);
        this.specialEnemyImg=specialEnemyImg; // Consider if needed
        this.maxHitpoints = this.hitpoints; // Default max HP
    }

    public SpecialEnemy(int x, int y, int width, int height, Image img, int enemyVelocityX, Image specialEnemyImg,int hitpoints,int shootCooldown, int specialEnemyVelocityY) {
        super(x, y, width, height, img, enemyVelocityX);
        this.specialEnemyImg=specialEnemyImg;
        this.hitpoints=hitpoints;
        this.shootCooldown=shootCooldown;
        this.maxHitpoints=hitpoints;
        this.specialEnemyVelocityY=specialEnemyVelocityY;
    }

    public SpecialEnemy(int x, int y, int width, int height, Image img, int enemyVelocityX, Image specialEnemyImg,int hitpoints,int shootCooldown, int specialEnemyVelocityY, String enemyBossName) {
        super(x, y, width, height, img, enemyVelocityX);
        this.enemyBossName=enemyBossName;
        this.specialEnemyImg=specialEnemyImg;
        this.hitpoints=hitpoints;
        this.shootCooldown=shootCooldown;
        this.maxHitpoints=hitpoints;
        this.specialEnemyVelocityY=specialEnemyVelocityY;
    }

    // Getters and Setters
    public String getEnemyBossName() {
        return enemyBossName;
    }
    public void setEnemyBossName(String enemyBossName) {
        this.enemyBossName = enemyBossName;
    }
    public int getHitpoints() {
        return hitpoints;
    }
    public int getMaxHitpoints() {
        return maxHitpoints;
    }
    public void setHitpoints(int hitpoints) {
        // Prevent HP from going below 0
        this.hitpoints = Math.max(0, hitpoints);
    }

    // Vertical movement logic (oscillates between top and Y=300)
    public void moveY(){
        setY(getY() + specialEnemyVelocityY);
        // Reverse direction at boundaries (Y=0 and Y=300)
        if(getY() <= 0 || getY() + getHeight() >= 300){
            specialEnemyVelocityY *= -1;
            setY(getY() + specialEnemyVelocityY); // Adjust slightly after reversing
        }
    }

    // Shooting logic (similar to ShootingEnemy)
    public void shoot(ArrayList<EnemyBullet> enemyBulletArray, Image enemyBulletImg, int bulletWidth, int bulletHeight) {
        if (currentCooldown <= 0) {
            EnemyBullet bullet = new EnemyBullet(getX() + getWidth() / 2 - bulletWidth / 2, // Center bullet
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