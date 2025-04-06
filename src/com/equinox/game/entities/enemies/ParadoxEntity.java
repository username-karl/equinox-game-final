package com.equinox.game.entities.enemies;

import java.awt.Image;
import java.util.List;
import com.equinox.game.data.GameState;
import com.equinox.game.entities.EnemyBullet;
import com.equinox.game.entities.LaserBeam; // Need this for laser
import com.equinox.game.utils.AssetLoader;
import com.equinox.game.utils.Constants;

public class ParadoxEntity extends MainBoss {

    private int patternIndex = 0; // 0: Targeted, 1: Laser
    private int patternSwitchTimer = 180; // Frames before switching pattern
    private int currentTimer = patternSwitchTimer;

    private int targetedShotCooldown = 30; 
    private int laserBurstCooldown = 90; 
    private int currentAttackCooldown = 0;

    // Inherit constructors
    public ParadoxEntity(int x, int y, int width, int height, Image img, int enemyVelocityX, Image specialEnemyImg, int hitpoints, int shootCooldown, int specialEnemyVelocityY, String enemyBossName) {
        super(x, y, width, height, img, enemyVelocityX, specialEnemyImg, hitpoints, shootCooldown, specialEnemyVelocityY, enemyBossName);
        // Set initial cooldowns based on shootCooldown?
        this.targetedShotCooldown = Math.max(20, shootCooldown / 2); 
        this.laserBurstCooldown = Math.max(60, shootCooldown * 2);
        this.currentTimer = random.nextInt(patternSwitchTimer / 2) + patternSwitchTimer / 2; // Random start time
    }

    @Override
    public void executeAttackPattern(GameState gameState, AssetLoader assetLoader) {
        currentTimer--;
        if (currentTimer <= 0) {
            patternIndex = (patternIndex + 1) % 2; // Switch between 0 and 1
            currentTimer = patternSwitchTimer;
            currentAttackCooldown = 0; // Reset attack cooldown on pattern switch
            System.out.println("ParadoxEntity switched to pattern: " + (patternIndex == 0 ? "Targeted" : "Laser"));
        }

        if (currentAttackCooldown > 0) {
            currentAttackCooldown--;
            return; // Wait for cooldown
        }

        // Execute attack based on pattern
        if (patternIndex == 0) {
            fireTargetedSingle(gameState, assetLoader);
            currentAttackCooldown = targetedShotCooldown;
        } else {
            fireLaserBurst(gameState, assetLoader);
            currentAttackCooldown = laserBurstCooldown;
        }
    }

    private void fireTargetedSingle(GameState gameState, AssetLoader assetLoader) {
        Image bulletImg = assetLoader.getImage(Constants.ENEMY_LASER_IMG_KEY);
        if (bulletImg == null || gameState.ship == null) return;

        int bulletWidth = Constants.ENEMY_BULLET_WIDTH;
        int bulletHeight = Constants.ENEMY_BULLET_HEIGHT;
        int bulletSpeed = 7; 

        double baseX = getX() + getWidth() / 2.0;
        double baseY = getY() + getHeight();
        double targetX = gameState.ship.getX() + gameState.ship.getWidth() / 2.0;
        double targetY = gameState.ship.getY() + gameState.ship.getHeight() / 2.0;
        double dx = targetX - baseX;
        double dy = targetY - baseY;
        double magnitude = Math.sqrt(dx * dx + dy * dy);

        int velX = 0;
        int velY = bulletSpeed; 
        if (magnitude > 0) {
            velX = (int) Math.round((dx / magnitude) * bulletSpeed);
            velY = (int) Math.round((dy / magnitude) * bulletSpeed);
            velY = Math.max(1, velY); // Ensure some downward movement
        }

        gameState.enemyBulletArray.add(new EnemyBullet((int)baseX - bulletWidth / 2, (int)baseY,
                                                  bulletWidth, bulletHeight, bulletImg,
                                                  velX, velY));
        System.out.println("ParadoxEntity fired Targeted Single");
    }

    private void fireLaserBurst(GameState gameState, AssetLoader assetLoader) {
        // This is tricky as enemies don't usually fire Lasers (player ability)
        // We might need to add a new Entity type or repurpose EnemyBullet visually
        // For now, let's simulate with a burst of 3 fast bullets horizontally
         Image bulletImg = assetLoader.getImage(Constants.ENEMY_LASER_IMG_KEY);
         if (bulletImg == null) return;

        int bulletWidth = Constants.TILE_SIZE; // Wider like a laser segment
        int bulletHeight = Constants.ENEMY_BULLET_HEIGHT / 2; // Thinner
        int bulletSpeedX = 10;

        int spawnY = getY() + getHeight() / 2 - bulletHeight / 2; // Fire from middle
        int spawnXLeft = getX() - bulletWidth;
        int spawnXRight = getX() + getWidth();

        // Fire left
        gameState.enemyBulletArray.add(new EnemyBullet(spawnXLeft, spawnY,
                                                  bulletWidth, bulletHeight, bulletImg,
                                                  -bulletSpeedX, 0)); 
        // Fire right
        gameState.enemyBulletArray.add(new EnemyBullet(spawnXRight, spawnY,
                                                  bulletWidth, bulletHeight, bulletImg,
                                                  bulletSpeedX, 0)); 

        System.out.println("ParadoxEntity fired Laser Burst (simulated)");
    }

} 