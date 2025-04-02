package com.equinox.game.entities;

import java.awt.Image;

// Extracted into its own file
public class LaserBeam extends Bullet{
    // Currently just uses Bullet constructor
    public LaserBeam(int x, int y, int width, int height, Image img) {
        super(x, y, width, height, img);
    }
     // Add specific behavior for LaserBeam if needed
} 