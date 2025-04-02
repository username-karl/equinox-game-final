package com.equinox.game.systems;

import com.equinox.game.entities.Entity;
import com.equinox.game.entities.Bullet;
import com.equinox.game.entities.EnemyBullet;
import com.equinox.game.entities.TacticalQ;
import com.equinox.game.entities.TacticalE;
import com.equinox.game.entities.ShipUser;
import com.equinox.game.entities.enemies.Enemy;
import com.equinox.game.ui.EquinoxGameLogic; // To call handleEnemyHit, access GameState

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
    public void checkTacticalCollisions(ArrayList<TacticalQ> tacticalQArray, ArrayList<TacticalE> tacticalEArray, ArrayList<Enemy> enemyArray) {
        // Tactical Q (Piercing?)
        for (TacticalQ tacticalq : tacticalQArray) {
            if (!tacticalq.isUsed()) { 
                for (Enemy enemy : enemyArray) {
                    if (enemy.isAlive() && detectCollision(tacticalq, enemy)) {
                        gameLogic.handleEnemyHit(enemy); 
                        // tacticalq.setUsed(true); // Uncomment if Q is single hit
                    }
                }
            }
        }
        // Tactical E (Single Hit)
        for (TacticalE tacticale : tacticalEArray) {
            if (!tacticale.isUsed()) {
                for (Enemy enemy : enemyArray) {
                    if (enemy.isAlive() && detectCollision(tacticale, enemy)) {
                        tacticale.setUsed(true);
                        gameLogic.handleEnemyHit(enemy);
                        break; // Tactical E hits one enemy and is used
                    }
                }
            }
        }
    }

    // Check enemy bullets against player ship
    public void checkEnemyBulletCollisions(ArrayList<EnemyBullet> enemyBulletArray, ShipUser ship) {
        if (ship == null || !ship.isAlive()) return; // Don't check collisions if ship is dead or null
        
        for (EnemyBullet bullet : enemyBulletArray) {
             if (!bullet.isUsed()) { 
                if(detectCollision(bullet, ship)){ 
                    bullet.setUsed(true); 
                    ship.takeDamage(1); // Player takes 1 damage
                    
                    // Check if player died from this hit
                    if (!ship.isAlive()) {
                        // Set game over state if player health reached 0
                         if (gameLogic.getGameState() != null) { 
                            gameLogic.getGameState().gameOver = true; 
                         }
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