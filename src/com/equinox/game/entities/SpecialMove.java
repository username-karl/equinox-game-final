package com.equinox.game.entities;

import java.awt.Image;
// Assuming SpecialMove might have its own velocity or Constants needed later
// import com.equinox.game.utils.Constants;

// Extracted into its own file
public class SpecialMove extends Bullet{
     // Update constructor to match new Bullet constructor
    public SpecialMove(int x, int y, int width, int height, Image img) {
        // Call super with velocities (Using 0, 0 as placeholder)
        super(x, y, width, height, img, 0, 0); // Removed pierce argument
    }
     // Add specific behavior for SpecialMove if needed
} 