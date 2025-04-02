package com.equinox.game.entities;

import java.awt.Image;

// Extracted into its own file - extend Entity
public class ShipUser extends Entity {

    private boolean isDev;
    private int maxHealth;
    private int health;

    public ShipUser(int x, int y, int width, int height, Image img, int maxHealth) {
        super(x, y, width, height, img);
        this.maxHealth = maxHealth;
        this.health = maxHealth; // Start with full health
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
    
    // Use this to apply damage
    public void takeDamage(int amount) {
        if (amount > 0) { // Ensure damage is positive
             this.health -= amount;
             if (this.health < 0) {
                 this.health = 0;
             }
        }
    }
    
    // Use this to heal or reset health
    public void setHealth(int health) {
        this.health = Math.max(0, Math.min(health, this.maxHealth)); // Clamp between 0 and maxHealth
    }
    
    public void resetHealth(){
        this.health = this.maxHealth;
    }
    
    public boolean isAlive() {
        return this.health > 0;
    }
} 