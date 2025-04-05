package com.equinox.game.entities.enemies;

import com.equinox.game.entities.EnemyBullet;
import java.awt.Image;
import java.util.ArrayList;
import java.util.List;
import com.equinox.game.data.GameState;
import com.equinox.game.utils.AssetLoader;
import com.equinox.game.utils.Constants;
import java.util.Random; // Import Random for patterns

// Extracted into its own file
public class MainBoss extends SpecialEnemy{
    // Attack Pattern State
    private int patternDuration = 120; // Reduced from 150
    private int patternTimer = patternDuration;
    private int currentPattern = 0; // 0: Rapid, 1: Spread, 2: Targeted
    private Random random = new Random();

    // Attack Cooldowns (adjust these values for difficulty)
    private int rapidShotCooldown = 6; // Reduced from 8
    private int spreadShotCooldown = 50; // Reduced from 60
    private int targetedBurstCooldown = 65; // Reduced from 80
    private int currentRapidShotCooldown = 0;
    private int currentSpreadShotCooldown = 0;
    private int currentTargetedBurstCooldown = 0;

    private boolean hasFiredInitialShot = false; // Flag for the first shot

     // Constructor chain inheriting from SpecialEnemy
    public MainBoss(int x, int y, int width, int height, Image img, int enemyVelocityX, Image specialEnemyImg) {
        super(x, y, width, height, img, enemyVelocityX, specialEnemyImg);
    }
    public MainBoss(int x, int y, int width, int height, Image img, int enemyVelocityX, Image specialEnemyImg, int hitpoints, int shootCooldown, int specialEnemyVelocityY) {
        super(x, y, width, height, img, enemyVelocityX, specialEnemyImg, hitpoints, shootCooldown, specialEnemyVelocityY);
    }
    public MainBoss(int x, int y, int width, int height, Image img, int enemyVelocityX, Image specialEnemyImg, int hitpoints, int shootCooldown, int specialEnemyVelocityY, String enemyBossName) {
        super(x, y, width, height, img, enemyVelocityX, specialEnemyImg, hitpoints, shootCooldown, specialEnemyVelocityY, enemyBossName);
    }
    
    // Override shoot to use List
    @Override
    public void shoot(List<EnemyBullet> enemyBulletArray, Image enemyBulletImg, int bulletWidth, int bulletHeight) {
        if (currentCooldown <= 0) {
            int shotWidth = bulletWidth;
            int shotHeight = bulletHeight;
            int shotVelocityY = 5; // Default normal speed

            // Check if this is the very first shot
            if (!hasFiredInitialShot) {
                System.out.println("MainBoss firing HUGE initial shot!"); // Debug
                shotWidth = Constants.ENEMY_BULLET_WIDTH * 3; // Make it wider
                shotHeight = Constants.ENEMY_BULLET_HEIGHT * 3; // Make it taller
                shotVelocityY = 3; // Make the huge shot slightly slower?
                hasFiredInitialShot = true; // Set flag so subsequent shots are normal
            }

            int defaultVelocityX = 0;
            // Create a new bullet at the enemy's position using potentially modified dimensions/speed
            EnemyBullet bullet = new EnemyBullet(getX() + getWidth() / 2 - shotWidth / 2, // Center bullet horizontally
                                             getY() + getHeight(), // Fire from bottom center
                                             shotWidth, 
                                             shotHeight, 
                                             enemyBulletImg,
                                             defaultVelocityX,
                                             shotVelocityY); // Use potentially modified Y velocity
            enemyBulletArray.add(bullet);
            currentCooldown = shootCooldown; // Reset cooldown regardless of shot type
        }
    }
    
    // TODO: Implement Main Boss attack pattern
    public void executeAttackPattern(GameState gameState, AssetLoader assetLoader) {
        // Update pattern timer and switch patterns
        patternTimer--;
        if (patternTimer <= 0) {
            currentPattern = (currentPattern + 1) % 3; // Cycle through 3 patterns
            patternTimer = patternDuration;
            System.out.println("MainBoss switched to pattern: " + currentPattern); // Debug
            // Reset attack cooldowns when switching patterns to avoid instant firing
            currentRapidShotCooldown = rapidShotCooldown;
            currentSpreadShotCooldown = spreadShotCooldown;
            currentTargetedBurstCooldown = targetedBurstCooldown;
        }

        // Decrement attack cooldowns
        if (currentRapidShotCooldown > 0) currentRapidShotCooldown--;
        if (currentSpreadShotCooldown > 0) currentSpreadShotCooldown--;
        if (currentTargetedBurstCooldown > 0) currentTargetedBurstCooldown--;

        // Execute attack based on current pattern
        switch (currentPattern) {
            case 0: // Rapid Shot
                if (currentRapidShotCooldown <= 0) {
                    fireRapidShot(gameState, assetLoader);
                    currentRapidShotCooldown = rapidShotCooldown;
                }
                break;
            case 1: // Wide Spread Shot
                if (currentSpreadShotCooldown <= 0) {
                    fireWideSpread(gameState, assetLoader);
                    currentSpreadShotCooldown = spreadShotCooldown;
                }
                break;
            case 2: // Targeted Burst
                if (currentTargetedBurstCooldown <= 0) {
                    fireTargetedBurst(gameState, assetLoader);
                    currentTargetedBurstCooldown = targetedBurstCooldown;
                }
                break;
        }
         // Also decrement the base shoot cooldown if it's used by any patterns implicitly
         if (currentCooldown > 0) {
            currentCooldown--;
        }
    }

    // --- Attack Pattern Helper Methods ---

    private void fireRapidShot(GameState gameState, AssetLoader assetLoader) {
        // Uses the existing shoot method, but triggered more frequently by the pattern logic
        Image bulletImg = assetLoader.getImage(Constants.ENEMY_LASER_IMG_KEY);
        int bulletWidth = Constants.ENEMY_BULLET_WIDTH;
        int bulletHeight = Constants.ENEMY_BULLET_HEIGHT;
        shoot(gameState.enemyBulletArray, bulletImg, bulletWidth, bulletHeight); // Call the overridden shoot
         System.out.println("MainBoss fired Rapid Shot"); // Debug
    }

    private void fireWideSpread(GameState gameState, AssetLoader assetLoader) {
        System.out.println("MainBoss fired MAX CHAOS Wide Spread"); // Debug
        Image bulletImg = assetLoader.getImage(Constants.ENEMY_LASER_IMG_KEY);
        int bulletWidth = Constants.ENEMY_BULLET_WIDTH / 2 ;
        int bulletHeight = Constants.ENEMY_BULLET_HEIGHT / 2;
        int bulletSpeedY = 6; // Increased from 5
        int numBullets = 15; // Increased from 13
        double angleStep = Math.PI / (numBullets + 1);

        for (int i = 1; i <= numBullets; i++) {
            // Simplified fixed velocities based on index
             int vx;
             if (i <= numBullets / 2) {
                 vx = -(numBullets / 2 - i + 1);
             } else if (i == (numBullets / 2) + 1) {
                 vx = 0;
             } else {
                 vx = i - (numBullets / 2 + 1);
             }
             // Decrease base Y speed for wider shots, add more randomness
             int vy = Math.max(1, bulletSpeedY - Math.abs(vx) / 2) + random.nextInt(5) - 2; // +/- 2 random variation

             // Add more random X offset to start position
             int startX = getX() + getWidth() / 2 - bulletWidth / 2 + random.nextInt(11) - 5; // +/- 5px random offset

            EnemyBullet bullet = new EnemyBullet(startX,
                                             getY() + getHeight(),
                                             bulletWidth, bulletHeight, bulletImg,
                                             vx, Math.max(1, vy)); // Ensure vy is at least 1
            gameState.enemyBulletArray.add(bullet);
        }
    }

    private void fireTargetedBurst(GameState gameState, AssetLoader assetLoader) {
         System.out.println("MainBoss fired MAX CHAOS Targeted Burst"); // Debug
         Image bulletImg = assetLoader.getImage(Constants.ENEMY_LASER_IMG_KEY);
         int bulletWidth = Constants.ENEMY_BULLET_WIDTH;
         int bulletHeight = Constants.ENEMY_BULLET_HEIGHT;
         int bulletSpeed = 9; // Increased from 8

         if (gameState.ship == null) return;

         // Calculate base direction vector towards the player ship
         double baseX = getX() + getWidth() / 2;
         double baseY = getY() + getHeight();
         double targetX = gameState.ship.getX() + gameState.ship.getWidth() / 2;
         double targetY = gameState.ship.getY() + gameState.ship.getHeight() / 2;
         double dx = targetX - baseX;
         double dy = targetY - baseY;
         double magnitude = Math.sqrt(dx * dx + dy * dy);

         // Fire a burst of 4 bullets
         for (int i = 0; i < 4; i++) { // Increased burst count from 3 to 4
             int velX = 0;
             int velY = bulletSpeed; // Default

             if (magnitude > 0) {
                 double currentDx = dx;
                 double currentDy = dy;
                 // Add slight random offset for 2nd, 3rd, and 4th bullets
                 if (i > 0) {
                     // Introduce a random angle offset (e.g., +/- 10 degrees)
                     double angleOffset = (random.nextDouble() - 0.5) * Math.toRadians(20); // Increased from 15 to 20
                     double originalAngle = Math.atan2(dy, dx);
                     double newAngle = originalAngle + angleOffset;
                     currentDx = magnitude * Math.cos(newAngle);
                     currentDy = magnitude * Math.sin(newAngle);
                 }

                double currentMagnitude = Math.sqrt(currentDx * currentDx + currentDy * currentDy);
                if(currentMagnitude > 0){
                     velX = (int) Math.round((currentDx / currentMagnitude) * bulletSpeed);
                     velY = (int) Math.max(1, Math.round((currentDy / currentMagnitude) * bulletSpeed));
                } else {
                     velX = 0;
                     velY = bulletSpeed;
                }
             }

             EnemyBullet bullet = new EnemyBullet((int)baseX - bulletWidth / 2,
                                                 (int)baseY,
                                                 bulletWidth, bulletHeight, bulletImg,
                                                 velX, velY);
             gameState.enemyBulletArray.add(bullet);
         }
    }

    // MainBoss specific logic can be added here if needed later
} 