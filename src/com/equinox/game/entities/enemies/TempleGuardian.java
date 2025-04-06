package com.equinox.game.entities.enemies;

import java.awt.Image;
import java.util.List;
import com.equinox.game.data.GameState;
import com.equinox.game.entities.EnemyBullet;
import com.equinox.game.utils.AssetLoader;
import com.equinox.game.utils.Constants;

public class TempleGuardian extends MainBoss {

    private int wideSpreadCooldown = 150; // Longer cooldown for wide attack
    private int targetedShotCooldown = 50;  // Shorter cooldown for targeted shot
    private int currentWideSpreadCooldown = 0;
    private int currentTargetedShotCooldown = 0;

    // Inherit constructors
     public TempleGuardian(int x, int y, int width, int height, Image img, int enemyVelocityX, Image specialEnemyImg, int hitpoints, int shootCooldown, int specialEnemyVelocityY, String enemyBossName) {
        super(x, y, width, height, img, enemyVelocityX, specialEnemyImg, hitpoints, shootCooldown, specialEnemyVelocityY, enemyBossName);
        this.wideSpreadCooldown = Math.max(100, shootCooldown * 2);
        this.targetedShotCooldown = Math.max(30, shootCooldown / 2);
        // Randomize start times
        this.currentWideSpreadCooldown = random.nextInt(wideSpreadCooldown / 2);
        this.currentTargetedShotCooldown = random.nextInt(targetedShotCooldown); 
    }

    @Override
    public void executeAttackPattern(GameState gameState, AssetLoader assetLoader) {
        // Decrement both cooldowns
        if (currentWideSpreadCooldown > 0) currentWideSpreadCooldown--;
        if (currentTargetedShotCooldown > 0) currentTargetedShotCooldown--;

        // Fire wide spread if ready
        if (currentWideSpreadCooldown <= 0) {
            fireSlowWideSpread(gameState, assetLoader);
            currentWideSpreadCooldown = wideSpreadCooldown;
        }

        // Fire targeted shot if ready
        if (currentTargetedShotCooldown <= 0) {
            fireFastTargetedSingle(gameState, assetLoader);
            currentTargetedShotCooldown = targetedShotCooldown;
        }
    }

    private void fireSlowWideSpread(GameState gameState, AssetLoader assetLoader) {
        System.out.println("TempleGuardian fired Slow Wide Spread");
        Image bulletImg = assetLoader.getImage(Constants.ENEMY_LASER_IMG_KEY);
        if (bulletImg == null) return;

        int bulletWidth = Constants.ENEMY_BULLET_WIDTH;
        int bulletHeight = Constants.ENEMY_BULLET_HEIGHT;
        double bulletSpeedY = 4.0; // Slower base speed
        int numBullets = 7; // Fewer bullets than MainBoss spread
        double spreadAngleDeg = 60.0; // Total angle

        int baseX = getX() + getWidth() / 2;
        int baseY = getY() + getHeight();

        double angleStepRad = Math.toRadians(spreadAngleDeg / (numBullets - 1));
        double startAngleRad = Math.toRadians(-spreadAngleDeg / 2.0); // Angle relative to straight down

        for (int i = 0; i < numBullets; i++) {
            double currentAngleRad = startAngleRad + i * angleStepRad;
            double velX = bulletSpeedY * Math.sin(currentAngleRad);
            double velY = bulletSpeedY * Math.cos(currentAngleRad);

            gameState.enemyBulletArray.add(new EnemyBullet(baseX - bulletWidth / 2, baseY,
                                                       bulletWidth, bulletHeight, bulletImg,
                                                       (int)velX, (int)Math.max(1, velY)));
        }
    }

    private void fireFastTargetedSingle(GameState gameState, AssetLoader assetLoader) {
         System.out.println("TempleGuardian fired Fast Targeted Single");
         Image bulletImg = assetLoader.getImage(Constants.ENEMY_LASER_IMG_KEY);
         if (bulletImg == null || gameState.ship == null) return;

        int bulletWidth = Constants.ENEMY_BULLET_WIDTH;
        int bulletHeight = Constants.ENEMY_BULLET_HEIGHT;
        int bulletSpeed = 10; // Faster targeted shot

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
            velY = Math.max(1, velY); 
        }

        gameState.enemyBulletArray.add(new EnemyBullet((int)baseX - bulletWidth / 2, (int)baseY,
                                                  bulletWidth, bulletHeight, bulletImg,
                                                  velX, velY));
    }
} 