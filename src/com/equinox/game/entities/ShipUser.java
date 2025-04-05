package com.equinox.game.entities;

import java.awt.Image;
import com.equinox.game.data.GameState;
import com.equinox.game.utils.Constants;

// Extracted into its own file - extends Entity
public class ShipUser extends Entity {

    private boolean isDev;
    private int maxHealth;
    private int health;

    public ShipUser(int x, int y, int width, int height, Image img, GameState gameState) {
        super(x, y, width, height, img);
        updateMaxHealth(gameState);
        this.health = this.maxHealth;
    }
    
    //SETTERS AND GETTERS
    public boolean isDev() {
        return isDev;
    }
    public void setDev(boolean dev) {
        isDev = dev;
    }
    
    public int getMaxHealth() {
        return maxHealth;
    }
    
    public int getHealth() {
        return health;
    }
    
    public void updateMaxHealth(GameState gameState) {
        if (gameState == null) {
            this.maxHealth = Constants.SHIP_INITIAL_HEALTH;
            return;
        }
        this.maxHealth = Constants.SHIP_INITIAL_HEALTH + gameState.healthUpgradeLevel * 1;
    }
    
    public void takeDamage(int damage) {
        setHealth(this.health - damage);
    }
    
    public void setHealth(int health) {
        this.health = Math.max(0, Math.min(health, this.maxHealth));
    }
    
    public void resetHealth(){
        this.health = this.maxHealth;
    }
    
    public boolean isAlive() {
        return this.health > 0;
    }
    
    // Method to recalculate stats based on current GameState upgrades
    public void recalculateStats(GameState gameState) {
        int oldMaxHealth = this.maxHealth;
        updateMaxHealth(gameState); // Recalculate max health
        // Optionally adjust current health if max health increased
        if (this.maxHealth > oldMaxHealth) {
            // Give the player the health they just bought
             setHealth(this.health + (this.maxHealth - oldMaxHealth));
        } else {
             // Ensure current health doesn't exceed new max if max was somehow lowered (unlikely)
            setHealth(this.health); // Re-clamp health to new max
        }
        System.out.println("Ship stats recalculated. New Max HP: " + this.maxHealth);
    }
} 