package com.equinox.game.entities.enemies;

import com.equinox.game.entities.Entity;
import java.awt.Image;

// Extracted into its own file in the enemies subpackage - extend Entity
public class Enemy extends Entity{

    protected int enemyVelocityX;
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

    public Enemy(int x, int y, int width, int height, Image img, int enemyVelocityX) {
        super(x, y, width, height, img);
        this.enemyVelocityX = enemyVelocityX;
    }

    // Basic horizontal movement with boundary check and triggering downward move
    public void move(int boardWidth, int enemyWidth, int enemyHeight) {
        int currentX = getX(); // Store current X before moving
        setX(currentX + enemyVelocityX);

        // Check boundaries and reverse direction
        if (getX() <= 0 || getX() + enemyWidth >= boardWidth) {
            // Only trigger moveDown when hitting the right edge (i.e., when velocity was positive before reversal)
            boolean movingRightBeforeHit = enemyVelocityX > 0;
            
            enemyVelocityX *= -1; // Reverse horizontal direction
            setX(currentX + enemyVelocityX); // Adjust position slightly based on original X and new velocity
            
            if (movingRightBeforeHit && getX() + enemyWidth >= boardWidth) { // Check we actually hit the right edge
                 setMoveDown(true); // Signal to move down only after hitting the right boundary
            } else {
                 setMoveDown(false); // Ensure flag is false if hitting left edge
            }
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