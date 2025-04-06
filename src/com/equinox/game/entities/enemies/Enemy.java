package com.equinox.game.entities.enemies;

import com.equinox.game.entities.Entity;
import java.awt.Image;

// Extracted into its own file in the enemies subpackage - extend Entity
public class Enemy extends Entity{

    protected int enemyVelocityX;
    protected int hitpoints = 1; // Default health for base enemy
    private boolean alive =true;
    // These flags might be better handled by specific subclasses or interfaces
    // private boolean isMiniBoss; 
    // private boolean isBoss;
    private boolean moveDown = false;

    //SETTERS AND GETTERS
    // public boolean isBoss() {
    //     return isBoss;
    // }
    // public void setBoss(boolean boss) {
    //     isBoss = boss;
    // }
    // public boolean isMiniBoss() {
    //     return isMiniBoss;
    // }
    // public void setMiniBoss(boolean miniBoss) {
    //     isMiniBoss = miniBoss;
    // }
    public boolean isAlive() {
        return alive;
    }

    public void setAlive(boolean alive) {
        this.alive = alive;
    }
    public boolean isMoveDown() {
        return moveDown;
    }

    public void setMoveDown(boolean moveDown) {
        this.moveDown = moveDown;
    }

    // Constructor to accept initial health
    public Enemy(int x, int y, int width, int height, Image img, int enemyVelocityX, int initialHealth) {
        super(x, y, width, height, img);
        this.enemyVelocityX = enemyVelocityX;
        this.hitpoints = Math.max(1, initialHealth); // Ensure health is at least 1
    }
    
    // Overload constructor for backward compatibility or default health
     public Enemy(int x, int y, int width, int height, Image img, int enemyVelocityX) {
        this(x, y, width, height, img, enemyVelocityX, 1); // Call the other constructor with default health 1
    }

    // Method to apply damage
    public void takeDamage(int amount) {
        if (amount > 0) {
            this.hitpoints -= amount;
            if (this.hitpoints <= 0) {
                this.hitpoints = 0;
                // Don't set alive = false here, let the system handle it after checking health
            }
        }
    }
    
    // Getter for hitpoints
    public int getHitpoints() {
        return hitpoints;
    }

    // Basic horizontal movement with boundary check and triggering downward move
    public void move(int boardWidth, int enemyWidth, int enemyHeight) {
        int currentX = getX(); // Store current X before moving
        setX(currentX + enemyVelocityX);

        // Check boundaries and reverse direction
        if (getX() <= 0 || getX() + enemyWidth >= boardWidth) {
            // Only trigger moveDown when hitting the right edge (i.e., when velocity was positive before reversal)
            // boolean movingRightBeforeHit = enemyVelocityX > 0; // No longer needed
            
            enemyVelocityX *= -1; // Reverse horizontal direction
            setX(currentX + enemyVelocityX); // Adjust position slightly based on original X and new velocity
            
            // Move down whenever a horizontal boundary is hit
            setMoveDown(true);
            /*
            if (movingRightBeforeHit && getX() + enemyWidth >= boardWidth) { // Check we actually hit the right edge
                 setMoveDown(true); // Signal to move down only after hitting the right boundary
            } else {
                 setMoveDown(false); // Ensure flag is false if hitting left edge
            }
            */
        }
    }

    // Moves the enemy down one step if the moveDown flag is set
    public void moveDown(int enemyHeight){
        if(isMoveDown()){
            setY(getY() + enemyHeight);
            setMoveDown(false); // Reset the flag after moving down
        }
    }
} 