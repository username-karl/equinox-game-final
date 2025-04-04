package com.equinox.game.entities;

import java.awt.Image;
import com.equinox.game.utils.Constants; // Add import for Constants

// Extracted into its own file
public class LaserBeam extends Bullet{
    // Update constructor to match new Bullet constructor
    public LaserBeam(int x, int y, int width, int height, Image img) {
        // Call super with velocities (LaserBeam moves straight up)
        super(x, y, width, height, img, 0, Constants.LASER_BEAM_VELOCITY_Y);
    }
     // Add specific behavior for LaserBeam if needed
} 