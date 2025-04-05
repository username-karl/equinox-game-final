package com.equinox.game.entities;

import java.awt.Image;
import com.equinox.game.utils.Constants; // Add import for Constants

// Extracted into its own file
public class WaveBlast extends Bullet{
    // Update constructor to match new Bullet constructor
    public WaveBlast(int x, int y, int width, int height, Image img) {
        // Call super with velocities (WaveBlast moves straight up)
        super(x, y, width, height, img, 0, Constants.WAVE_BLAST_VELOCITY_Y);
    }
    // Add specific behavior for WaveBlast if needed
} 