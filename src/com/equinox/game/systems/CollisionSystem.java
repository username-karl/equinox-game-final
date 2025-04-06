package com.equinox.game.systems;

import com.equinox.game.data.GameState;
import com.equinox.game.entities.Entity;
import com.equinox.game.entities.Bullet;
import com.equinox.game.entities.EnemyBullet;
import com.equinox.game.entities.WaveBlast;
import com.equinox.game.entities.LaserBeam;
import com.equinox.game.entities.ShipUser;
import com.equinox.game.entities.enemies.Enemy;
import com.equinox.game.ui.EquinoxGameLogic; 

import java.util.List;

public class CollisionSystem {

    // Dependencies
    private GameState gameState; 
    private GameUpdateSystem gameUpdateSystem; // Needed to call handleEnemyHit
    private EquinoxGameLogic gameLogic; // Needed for signalGameOver (Temporary)

    // Updated Constructor
    public CollisionSystem(GameState gameState, GameUpdateSystem gameUpdateSystem, EquinoxGameLogic gameLogic) {
        this.gameState = gameState;
        this.gameUpdateSystem = gameUpdateSystem;
        this.gameLogic = gameLogic;
    }

    // Basic AABB collision detection - can be static or instance method
    private boolean detectCollision(Entity a, Entity b) {
        return a.getX() < b.getX() + b.getWidth() &&   // A's left edge is left of B's right edge
               a.getX() + a.getWidth() > b.getX() &&   // A's right edge is right of B's left edge
               a.getY() < b.getY() + b.getHeight() &&  // A's top edge is above B's bottom edge
               a.getY() + a.getHeight() > b.getY();    // A's bottom edge is below B's top edge
    }

    // Check player bullets against enemies (Uses GameState)
    public void checkPlayerBulletCollisions() { 
        // Get lists from GameState
        List<Bullet> bulletArray = gameState.bulletArray;
        List<Enemy> enemyArray = gameState.enemyArray;
        if (bulletArray == null || enemyArray == null || gameUpdateSystem == null) return;

        for (Bullet bullet : bulletArray) {
            if (!bullet.isUsed()) {
                for (Enemy enemy : enemyArray) {
                    // Check collision only if enemy is alive AND bullet hasn't been used up by pierce
                    if (enemy.isAlive() && !bullet.isUsed() && detectCollision(bullet, enemy)) {
                        // --- DEBUG --- 
                        // System.out.println("  Collision Detected! Bullet Pierce Before Hit: " + bullet.getRemainingPierce() + " | Bullet Used Before Hit: " + bullet.isUsed()); // REMOVED
                        // ------------- 
                        gameUpdateSystem.handleEnemyHit(enemy); // Apply damage (damage calc is in handleEnemyHit)
                        // bullet.consumePierce(); // REMOVED - Bullets are used after one hit now
                         // --- DEBUG --- 
                        // System.out.println("  Collision Handled! Bullet Pierce After Hit:  " + bullet.getRemainingPierce() + " | Bullet Used After Hit:  " + bullet.isUsed()); // REMOVED
                        // ------------- 
                        bullet.setUsed(true); // Mark bullet as used after hitting one enemy
                        break; // Exit inner loop once bullet hits an enemy
                    }
                }
            }
        }
    }

    // Check player tactical abilities against enemies (Uses GameState)
    public void checkTacticalCollisions() {
        // Get lists from GameState
        List<WaveBlast> waveBlastArray = gameState.waveBlastArray;
        List<LaserBeam> laserBeamArray = gameState.laserBeamArray;
        List<Enemy> enemyArray = gameState.enemyArray;
        if (gameUpdateSystem == null) return;

        if (waveBlastArray != null && enemyArray != null) {
            for (WaveBlast waveBlast : waveBlastArray) {
                if (!waveBlast.isUsed()) {
                    for (Enemy enemy : enemyArray) {
                        if (enemy.isAlive() && detectCollision(waveBlast, enemy)) {
                            gameUpdateSystem.handleEnemyHit(enemy); 
                        }
                    }
                }
            }
        }
        
        if (laserBeamArray != null && enemyArray != null) {
            for (LaserBeam laserBeam : laserBeamArray) {
                if (!laserBeam.isUsed()) {
                    for (Enemy enemy : enemyArray) {
                        if (enemy.isAlive() && detectCollision(laserBeam, enemy)) {
                            laserBeam.setUsed(true);
                            gameUpdateSystem.handleEnemyHit(enemy);
                            break;
                        }
                    }
                }
            }
        }
    }

    // Check enemy bullets against player ship (Uses GameState)
    public void checkEnemyBulletCollisions() {
        // Get state from GameState
        List<EnemyBullet> enemyBulletArray = gameState.enemyBulletArray;
        ShipUser ship = gameState.ship;
        boolean isPhaseShiftActive = gameState.isPhaseShiftActive;
        boolean cheatsEnabled = gameState.cheatsEnabled;

        if (ship == null || !ship.isAlive() || isPhaseShiftActive || cheatsEnabled || enemyBulletArray == null || gameLogic == null) return; 
        
        for (EnemyBullet bullet : enemyBulletArray) {
             if (!bullet.isUsed()) { 
                if(detectCollision(bullet, ship)){ 
                    bullet.setUsed(true); 
                    ship.takeDamage(1); 
                    
                    if (!ship.isAlive()) {
                        gameLogic.signalGameOver(); // Call on EquinoxGameLogic
                        return; // Stop checking collisions if player is dead
                    }
                }
            }
        }
    }
} 