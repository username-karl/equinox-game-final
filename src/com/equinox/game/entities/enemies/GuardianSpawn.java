package com.equinox.game.entities.enemies;

import java.awt.Image;
import java.util.List;

import com.equinox.game.entities.EnemyBullet;
import com.equinox.game.utils.AssetLoader;
import com.equinox.game.utils.Constants;

public class GuardianSpawn extends Miniboss {

    private int tighterSpreadCooldown = 120; // Cooldown specific to this pattern

    // Inherit constructors - provide specific ones if needed, otherwise rely on super()
     public GuardianSpawn(int x, int y, int width, int height, Image img, int enemyVelocityX, Image specialEnemyImg, int hitpoints, int shootCooldown, int specialEnemyVelocityY, String enemyBossName) {
        super(x, y, width, height, img, enemyVelocityX, specialEnemyImg, hitpoints, shootCooldown, specialEnemyVelocityY, enemyBossName);
        // Initialize specific cooldown based on passed shootCooldown or set default
        this.tighterSpreadCooldown = Math.max(60, shootCooldown); // Use shootCooldown, min 60
        this.currentPatternCooldown = random.nextInt(tighterSpreadCooldown / 2); // Randomize start
    }


    @Override
    public void executeAttackPattern(List<EnemyBullet> enemyBulletArray, AssetLoader assetLoader) {
        if (currentPatternCooldown <= 0) {
            fireTighterSpread(enemyBulletArray, assetLoader);
            currentPatternCooldown = tighterSpreadCooldown; // Use specific cooldown
        } else {
            currentPatternCooldown--;
        }
        // Decrement base cooldown if used implicitly (though not in this pattern)
        if (currentCooldown > 0) {
            currentCooldown--;
        }
    }

    private void fireTighterSpread(List<EnemyBullet> enemyBulletArray, AssetLoader assetLoader) {
        Image bulletImg = assetLoader.getImage(Constants.ENEMY_LASER_IMG_KEY);
        if (bulletImg == null) return;

        int bulletWidth = Constants.ENEMY_BULLET_WIDTH;
        int bulletHeight = Constants.ENEMY_BULLET_HEIGHT;
        int bulletSpeedY = 7; // Slightly faster Y speed
        double bulletSpeedX = 0.75; // Tighter horizontal speed

        // Fire 3 bullets in a tight spread
        int centerX = getX() + getWidth() / 2;
        int spawnY = getY() + getHeight();

        // Left
        enemyBulletArray.add(new EnemyBullet(centerX - bulletWidth / 2, spawnY,
                                            bulletWidth, bulletHeight, bulletImg,
                                            (int)(-bulletSpeedX), bulletSpeedY)); 
        // Center
        enemyBulletArray.add(new EnemyBullet(centerX - bulletWidth / 2, spawnY,
                                            bulletWidth, bulletHeight, bulletImg,
                                            0, bulletSpeedY)); 
        // Right
         enemyBulletArray.add(new EnemyBullet(centerX - bulletWidth / 2, spawnY,
                                            bulletWidth, bulletHeight, bulletImg,
                                            (int)(bulletSpeedX), bulletSpeedY));

        System.out.println("GuardianSpawn fired tighter spread!"); // Debug
    }
} 