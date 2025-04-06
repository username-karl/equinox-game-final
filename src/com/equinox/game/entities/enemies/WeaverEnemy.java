package com.equinox.game.entities.enemies;

import java.awt.Image;
import com.equinox.game.utils.Constants;

public class WeaverEnemy extends Enemy {

    private final double verticalSpeed;
    private final double horizontalAmplitude;
    private final double horizontalFrequency;
    private double initialX; // Store the initial X to calculate weave offset
    private double weaveTimer = 0; // Timer for sine wave calculation

    public WeaverEnemy(int x, int y, int width, int height, Image image, int hitpoints, 
                         double verticalSpeed, double horizontalAmplitude, double horizontalFrequency) {
        // Pass 0 for base enemyVelocityX as we control X directly
        super(x, y, width, height, image, 0, hitpoints);
        this.initialX = x;
        this.verticalSpeed = verticalSpeed;
        this.horizontalAmplitude = horizontalAmplitude;
        this.horizontalFrequency = horizontalFrequency;
        // Optional: Randomize starting point in weave cycle
        // this.weaveTimer = random.nextDouble() * Math.PI * 2 / this.horizontalFrequency;
    }

    @Override
    public void move(int boardWidth, int enemyWidth, int enemyHeight) {
        // Vertical Movement
        y += verticalSpeed;

        // Horizontal Weaving Movement (Sine Wave)
        weaveTimer += 1.0;
        double horizontalOffset = horizontalAmplitude * Math.sin(weaveTimer * horizontalFrequency);
        x = (int) (initialX + horizontalOffset);

        // Keep within bounds horizontally
        if (x < 0) {
            x = 0;
            initialX = -horizontalOffset;
        } else if (x + width > boardWidth) {
            x = boardWidth - width;
            initialX = x - horizontalOffset;
        }
        
        // Note: This enemy doesn't use the base Enemy moveDown logic.
        // If it goes off the bottom, it will be handled by the main game loop's off-screen check.
    }

     // Weavers don't use the base moveDown logic
    @Override
    public void moveDown(int enemyHeight) { 
        // Do nothing
    }
} 