package com.equinox.game.entities;

import java.awt.Image;

// Extracted into its own file
public class WaveBlast extends Bullet{
    // Currently just uses Bullet constructor
    public WaveBlast(int x, int y, int width, int height, Image img) {
        super(x, y, width, height, img);
    }
    // Add specific behavior for WaveBlast if needed
} 