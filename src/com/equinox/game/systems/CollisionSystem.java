package com.equinox.game.systems;

import com.equinox.game.entities.Entity;
import com.equinox.game.entities.Bullet;
import com.equinox.game.entities.EnemyBullet;
import com.equinox.game.entities.WaveBlast;
import com.equinox.game.entities.LaserBeam;
import com.equinox.game.entities.ShipUser;
import com.equinox.game.entities.enemies.Enemy;
import com.equinox.game.ui.EquinoxGameLogic; 

import java.util.ArrayList;

public class CollisionSystem {

    private EquinoxGameLogic gameLogic; // Reference to main logic

    public CollisionSystem(EquinoxGameLogic gameLogic) {
        this.gameLogic = gameLogic;
    }

    // Basic AABB collision detection - can be static or instance method
    private boolean detectCollision(Entity a, Entity b) {
        return a.getX() < b.getX() + b.getWidth() &&   // A's left edge is left of B's right edge
               a.getX() + a.getWidth() > b.getX() &&   // A's right edge is right of B's left edge
               a.getY() < b.getY() + b.getHeight() &&  // A's top edge is above B's bottom edge
               a.getY() + a.getHeight() > b.getY();    // A's bottom edge is below B's top edge
    }

    // Check player bullets against enemies
    public void checkPlayerBulletCollisions(ArrayList<Bullet> bulletArray, ArrayList<Enemy> enemyArray) {
        for (Bullet bullet : bulletArray) {
            if (!bullet.isUsed()) {
                for (Enemy enemy : enemyArray) {
                    if (enemy.isAlive() && detectCollision(bullet, enemy)) {
                        bullet.setUsed(true);
                        gameLogic.handleEnemyHit(enemy); // Call method on gameLogic
                         break; // Bullet hits one enemy and is used
                    }
                }
            }
        }
    }

    // Check player tactical abilities against enemies
    public void checkTacticalCollisions(ArrayList<WaveBlast> waveBlastArray, ArrayList<LaserBeam> laserBeamArray, ArrayList<Enemy> enemyArray) {
        // WaveBlast (Q)
        for (WaveBlast waveBlast : waveBlastArray) {
            if (!waveBlast.isUsed()) {
                for (Enemy enemy : enemyArray) {
                    if (enemy.isAlive() && detectCollision(waveBlast, enemy)) {
                        gameLogic.handleEnemyHit(enemy); 
                        // waveBlast.setUsed(true); // Uncomment if WaveBlast is single hit
                    }
                }
            }
        }
        // LaserBeam (E)
        for (LaserBeam laserBeam : laserBeamArray) {
            if (!laserBeam.isUsed()) {
                for (Enemy enemy : enemyArray) {
                    if (enemy.isAlive() && detectCollision(laserBeam, enemy)) {
                        laserBeam.setUsed(true);
                        gameLogic.handleEnemyHit(enemy);
                        break; // LaserBeam hits one enemy and is used
                    }
                }
            }
        }
    }

    // Check enemy bullets against player ship
    public void checkEnemyBulletCollisions(ArrayList<EnemyBullet> enemyBulletArray, ShipUser ship) {
        // Skip collision check if ship is null, dead, phase shift is active, OR CHEATS ARE ENABLED
        if (ship == null || !ship.isAlive() || gameLogic.isPhaseShiftActive() || gameLogic.areCheatsEnabled()) return; 
        
        for (EnemyBullet bullet : enemyBulletArray) {
             if (!bullet.isUsed()) { 
                if(detectCollision(bullet, ship)){ 
                    bullet.setUsed(true); 
                    ship.takeDamage(1); // Player takes 1 damage
                    
                    // Check if player died from this hit
                    if (!ship.isAlive()) {
                        // Call the method in GameLogic to handle player death
                        gameLogic.playerDied();
                        // Optionally break here if game over should happen immediately
                        // break; 
                    }
                    // Consider if bullet should disappear after hitting player, or if multiple bullets can hit
                    // break; // Uncomment if one bullet hit is enough per frame
                }
            }
        }
    }
} 