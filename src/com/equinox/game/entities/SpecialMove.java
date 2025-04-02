package com.equinox.game.entities;

import java.awt.Image;

// Extracted into its own file
public class SpecialMove extends Bullet{
     // Currently just uses Bullet constructor
    public SpecialMove(int x, int y, int width, int height, Image img) {
        super(x, y, width, height, img);
    }
     // Add specific behavior for SpecialMove if needed
} 