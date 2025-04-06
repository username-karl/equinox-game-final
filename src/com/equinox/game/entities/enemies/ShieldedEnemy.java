package com.equinox.game.entities.enemies;

import com.equinox.game.utils.Constants;

import java.awt.Image;

public class ShieldedEnemy extends Enemy {

    private int shieldHitpoints;
    private final int maxShieldHitpoints; // To potentially redraw shield bar later
    // private Image shieldDownImage; // Optional: For visual feedback when shield breaks

    public ShieldedEnemy(int x, int y, int width, int height, Image image, int hitpoints, int shieldHitpoints, double speed /*, Image shieldDownImage */) {
        super(x, y, width, height, image, (int) speed, hitpoints);
        this.shieldHitpoints = shieldHitpoints;
        this.maxShieldHitpoints = shieldHitpoints;
        // this.shieldDownImage = shieldDownImage;
    }

    @Override
    public void takeDamage(int amount) {
        if (amount <= 0) return;

        if (shieldHitpoints > 0) {
            shieldHitpoints -= amount;
            if (shieldHitpoints <= 0) {
                System.out.println("Shield broken!"); // Placeholder feedback
                shieldHitpoints = 0;
                // Optional: Change image to shieldDownImage if implemented
                // if (shieldDownImage != null) { this.img = shieldDownImage; }
            }
        } else {
            // Shield is down, apply damage to main hitpoints
            super.takeDamage(amount);
        }
    }

    public boolean isShieldActive() {
        return shieldHitpoints > 0;
    }

    public int getShieldHitpoints() {
        return shieldHitpoints;
    }

    public int getMaxShieldHitpoints() {
        return maxShieldHitpoints;
    }

    // Basic movement, inherits from Enemy. Override if needed.
    // @Override
    // public void move(int boardWidth, int enemyWidth, int enemyHeight) { super.move(boardWidth, enemyWidth, enemyHeight); }
} 