package com.equinox.game.entities.enemies;

import com.equinox.game.entities.EnemyBullet;
import java.awt.Image;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import com.equinox.game.utils.AssetLoader;
import com.equinox.game.utils.Constants;

// Extracted into its own file
public class Miniboss extends SpecialEnemy{
    private int patternCooldown = 100;
    private int currentPatternCooldown = 0;

    // Constructor chain inheriting from SpecialEnemy
    public Miniboss(int x, int y, int width, int height, Image img, int enemyVelocityX, Image specialEnemyImg) {
        super(x, y, width, height, img, enemyVelocityX, specialEnemyImg);
    }
    public Miniboss(int x, int y, int width, int height, Image img, int enemyVelocityX, Image specialEnemyImg, int hitpoints, int shootCooldown, int specialEnemyVelocityY) {
        super(x, y, width, height, img, enemyVelocityX, specialEnemyImg, hitpoints, shootCooldown, specialEnemyVelocityY);
    }
    public Miniboss(int x, int y, int width, int height, Image img, int enemyVelocityX, Image specialEnemyImg, int hitpoints, int shootCooldown, int specialEnemyVelocityY, String enemyBossName) {
        super(x, y, width, height, img, enemyVelocityX, specialEnemyImg, hitpoints, shootCooldown, specialEnemyVelocityY, enemyBossName);
    }
    // Miniboss specific logic can be added here if needed later

    // Override shoot to use List
    @Override
    public void shoot(List<EnemyBullet> enemyBulletArray, Image enemyBulletImg, int bulletWidth, int bulletHeight) {
        if (currentCooldown <= 0) {
            int defaultVelocityX = 0;
            int defaultVelocityY = 5; // Default enemy bullet speed
            // Create a new bullet at the enemy's position
            EnemyBullet bullet = new EnemyBullet(getX() + getWidth() / 2 - bulletWidth / 2, // Center bullet horizontally
                                             getY() + getHeight() / 2 - bulletHeight / 2, // Center bullet vertically
                                             bulletWidth, bulletHeight,
                                             enemyBulletImg,
                                             defaultVelocityX,
                                             defaultVelocityY);
            enemyBulletArray.add(bullet);
            currentCooldown = shootCooldown; // Access protected field
        }
    }

    public void executeAttackPattern(List<EnemyBullet> enemyBulletArray, AssetLoader assetLoader) {
        if (currentPatternCooldown <= 0) {
            fireSpreadShot(enemyBulletArray, assetLoader);
            currentPatternCooldown = patternCooldown;
        } else {
            currentPatternCooldown--;
        }
        if (currentCooldown > 0) {
            currentCooldown--;
        }
    }

    private void fireSpreadShot(List<EnemyBullet> enemyBulletArray, AssetLoader assetLoader) {
        Image bulletImg = assetLoader.getImage(Constants.ENEMY_LASER_IMG_KEY);
        int bulletWidth = Constants.ENEMY_BULLET_WIDTH;
        int bulletHeight = Constants.ENEMY_BULLET_HEIGHT;
        int bulletSpeedY = 6; // Increased from 5

        // Fire 5 bullets in a wider spread
        EnemyBullet bullet_neg2 = new EnemyBullet(getX() + getWidth() / 2 - bulletWidth / 2,
                                            getY() + getHeight(),
                                            bulletWidth, bulletHeight, bulletImg,
                                            -2, bulletSpeedY); // Wider angle left
        enemyBulletArray.add(bullet_neg2);

        EnemyBullet bullet_neg1 = new EnemyBullet(getX() + getWidth() / 2 - bulletWidth / 2,
                                            getY() + getHeight(),
                                            bulletWidth, bulletHeight, bulletImg,
                                            -1, bulletSpeedY); // Slightly left
        enemyBulletArray.add(bullet_neg1);

        EnemyBullet bullet_0 = new EnemyBullet(getX() + getWidth() / 2 - bulletWidth / 2,
                                            getY() + getHeight(),
                                            bulletWidth, bulletHeight, bulletImg,
                                            0, bulletSpeedY); // Straight down
        enemyBulletArray.add(bullet_0);

        EnemyBullet bullet_pos1 = new EnemyBullet(getX() + getWidth() / 2 - bulletWidth / 2,
                                            getY() + getHeight(),
                                            bulletWidth, bulletHeight, bulletImg,
                                            1, bulletSpeedY); // Slightly right
        enemyBulletArray.add(bullet_pos1);

         EnemyBullet bullet_pos2 = new EnemyBullet(getX() + getWidth() / 2 - bulletWidth / 2,
                                            getY() + getHeight(),
                                            bulletWidth, bulletHeight, bulletImg,
                                            2, bulletSpeedY); // Wider angle right
        enemyBulletArray.add(bullet_pos2);

        System.out.println("Miniboss fired enhanced spread shot!"); // Debug
    }
} 