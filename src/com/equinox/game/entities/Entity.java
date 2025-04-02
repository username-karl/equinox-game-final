package com.equinox.game.entities;

import java.awt.*;

// Renamed from Block, now the base entity class
public class Entity {

    // Made fields protected for subclasses, or keep private with public getters
    protected int x;
    protected int y;
    protected int width;
    protected int height;
    protected Image img;
    
    // Flag for bullets/effects, default false for most entities
    protected boolean used = false; 

    public Entity(int x, int y, int width, int height, Image img){
        this.x = x;
        this.y = y;
        this.width=width;
        this.height=height;
        this.img=img;
    }

    // Getters 
    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }
    
    public Image getImage() {
        return img;
    }

    // Setters
    public void setX(int x) {
        this.x = x;
    }
    public void setY(int y){
        this.y=y;
    }
    public void setWidth(int width){
        this.width=width;
    }
    public void setHeight(int height){
        this.height=height;
    }
    public void setImage(Image img){
        this.img = img;
    }

    // Basic used flag handling (mainly for bullets/effects)
    public void setUsed(boolean used) {
        this.used = used;
    }
    public boolean isUsed() {
        return used;
    }
    
    // Abstract methods or common methods like update/render could go here later
    // public abstract void update(double deltaTime);
    // public abstract void render(Graphics2D g);
}

// All subclass definitions removed 