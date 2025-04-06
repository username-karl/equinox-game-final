package com.equinox.game.entities.enemies;

import com.equinox.game.entities.EnemyBullet;

import java.awt.Image;
import java.util.ArrayList;
import java.util.List;

// Extracted into its own file
public class SpecialEnemy extends Enemy{
    Image specialEnemyImg; // Often the same as img, maybe remove?
    // Default specialEnemy values
    private String enemyBossName="";
    private int maxHitpoints = 20; // Keep max separately
    protected int shootCooldown = 100; // Ticks between shots - PROTECTED
    protected int currentCooldown = 0; // PROTECTED
    private int specialEnemyVelocityY = 1; // Vertical movement speed

    // Constructor chain for flexibility
    public SpecialEnemy(int x, int y, int width, int height, Image img, int enemyVelocityX, Image specialEnemyImg) {
        super(x, y, width, height, img, enemyVelocityX, 20); // Pass default HP to parent
        this.specialEnemyImg=specialEnemyImg; // Consider if needed
        this.maxHitpoints = this.hitpoints; // Assign from inherited hitpoints
    }

    public SpecialEnemy(int x, int y, int width, int height, Image img, int enemyVelocityX, Image specialEnemyImg,int hitpoints,int shootCooldown, int specialEnemyVelocityY) {
        super(x, y, width, height, img, enemyVelocityX, hitpoints); // Pass HP to parent
        this.specialEnemyImg=specialEnemyImg;
        this.shootCooldown=shootCooldown;
        this.maxHitpoints=hitpoints; // Assign max from initial
        this.specialEnemyVelocityY=specialEnemyVelocityY;
    }

    public SpecialEnemy(int x, int y, int width, int height, Image img, int enemyVelocityX, Image specialEnemyImg,int hitpoints,int shootCooldown, int specialEnemyVelocityY, String enemyBossName) {
        super(x, y, width, height, img, enemyVelocityX, hitpoints); // Pass HP to parent
        this.enemyBossName=enemyBossName;
        this.specialEnemyImg=specialEnemyImg;
        this.shootCooldown=shootCooldown;
        this.maxHitpoints=hitpoints; // Assign max from initial
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
        return super.getHitpoints(); // Access inherited hitpoints
    }
    public int getMaxHitpoints() {
        return maxHitpoints;
    }
    public void setHitpoints(int hitpoints) {
        // Prevent HP from going below 0
        super.hitpoints = Math.max(0, hitpoints); // Set inherited protected field directly 
                                                  // (Alternatively, add a proper setter in Enemy)
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

    // Shooting logic (similar to ShootingEnemy) - Use List
    public void shoot(List<EnemyBullet> enemyBulletArray, Image enemyBulletImg, int bulletWidth, int bulletHeight) {
        if (currentCooldown <= 0) {
            int defaultVelocityX = 0;
            int defaultVelocityY = 5; // Default enemy bullet speed
            EnemyBullet bullet = new EnemyBullet(getX() + getWidth() / 2 - bulletWidth / 2, // Center bullet
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