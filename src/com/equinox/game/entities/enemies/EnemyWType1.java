package com.equinox.game.entities.enemies;

import com.equinox.game.entities.EnemyBullet;
import com.equinox.game.entities.Entity;
import com.equinox.game.utils.Constants;
import com.equinox.game.utils.AssetLoader;
import com.equinox.game.data.GameState;

import java.awt.Image;
import java.util.Random;

public class EnemyWType1 extends Enemy {

    private long lastFireTime;
    private long fireDelay;
    private boolean movingRight = true;
    private GameState gameState; // Store GameState to access bullet array
    private Image bulletImage; // Store bullet image
    private static final Random random = new Random(); // Add a static random instance
    private final int numberOfShots; // Add field for number of shots
    private static final double SPREAD_ANGLE_DEG = 15.0; // Angle between side shots for multi-shot

    public EnemyWType1(int x, int y, int width, int height, Image image, int hitpoints, double speed, long fireDelay, GameState gameState, Image bulletImage, int numberOfShots) {
        super(x, y, width, height, image, (int)speed, hitpoints);
        
        // Apply random variation to fireDelay (+/- 15%)
        double variation = (random.nextDouble() * 0.30) - 0.15; // Range -0.15 to +0.15
        this.fireDelay = (long) (fireDelay * (1.0 + variation));
        this.fireDelay = Math.max(500, this.fireDelay); // Ensure delay doesn't become too short (e.g., min 0.5s)

        // Add random offset to lastFireTime (0 to half of fireDelay)
        long initialDelayOffset = (this.fireDelay > 0) ? random.nextInt((int)this.fireDelay / 2 + 1) : 0; 
        this.lastFireTime = System.currentTimeMillis() - initialDelayOffset; // Set last fire time in the past by offset
        
        this.enemyVelocityX = (int)speed; 
        this.gameState = gameState;
        this.bulletImage = bulletImage;
        this.numberOfShots = Math.max(1, numberOfShots); // Store number of shots (at least 1)
    }

    @Override
    public void move(int boardWidth, int enemyWidth, int enemyHeight) {
        if (movingRight) {
            x += enemyVelocityX;
            if (x + width >= boardWidth) {
                x = boardWidth - width;
                movingRight = false;
                enemyVelocityX = -Math.abs(enemyVelocityX);
            }
        } else {
            x += enemyVelocityX;
            if (x <= 0) {
                x = 0;
                movingRight = true;
                enemyVelocityX = Math.abs(enemyVelocityX);
            }
        }

        tryFireBullet();
    }

    private void tryFireBullet() {
        long currentTime = System.currentTimeMillis();
        if (currentTime - lastFireTime > fireDelay) {
            fireBullet();
            lastFireTime = currentTime;
        }
    }

    private void fireBullet() {
        if (gameState != null && gameState.enemyBulletArray != null && bulletImage != null) {
            // Calculate base spawn position (center bottom of the enemy)
            int baseX = x + width / 2;
            int baseY = y + height;
            double baseSpeed = Constants.ENEMY_BULLET_SPEED;

            for (int i = 0; i < numberOfShots; i++) {
                double angleRad = 0; // Angle in radians relative to straight down (0 degrees)
                int spawnOffsetX = 0;

                if (numberOfShots == 1) {
                    // Single shot: straight down
                    angleRad = Math.toRadians(0);
                    spawnOffsetX = -Constants.ENEMY_BULLET_WIDTH / 2;
                } else if (numberOfShots == 3) {
                    // Three shots: center, left, right
                    if (i == 0) { // Left shot
                        angleRad = Math.toRadians(SPREAD_ANGLE_DEG);
                        spawnOffsetX = -Constants.ENEMY_BULLET_WIDTH / 2 - width / 4; // Offset slightly left
                    } else if (i == 1) { // Center shot
                        angleRad = Math.toRadians(0);
                        spawnOffsetX = -Constants.ENEMY_BULLET_WIDTH / 2;
                    } else { // Right shot (i == 2)
                        angleRad = Math.toRadians(-SPREAD_ANGLE_DEG);
                         spawnOffsetX = -Constants.ENEMY_BULLET_WIDTH / 2 + width / 4; // Offset slightly right
                    }
                } 
                // Can add logic for other numberOfShots values if needed
                else { // Default fallback for > 1 shot (e.g., two parallel slightly offset)
                     if (i == 0) spawnOffsetX = -Constants.ENEMY_BULLET_WIDTH / 2 - 4;
                     else spawnOffsetX = -Constants.ENEMY_BULLET_WIDTH / 2 + 4;
                     angleRad = Math.toRadians(0);
                }

                // Calculate velocity components based on angle and speed
                // Angle 0 is straight down (positive Y)
                double velX = baseSpeed * Math.sin(angleRad);
                double velY = baseSpeed * Math.cos(angleRad);

                EnemyBullet newBullet = new EnemyBullet(
                    baseX + spawnOffsetX, 
                    baseY, 
                    Constants.ENEMY_BULLET_WIDTH, 
                    Constants.ENEMY_BULLET_HEIGHT, 
                    bulletImage, 
                    (int)velX, 
                    (int)velY);
                gameState.enemyBulletArray.add(newBullet);
            }
        }
    }
} 