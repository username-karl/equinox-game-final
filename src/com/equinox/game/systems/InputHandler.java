package com.equinox.game.systems;

import com.equinox.game.ui.EquinoxGameLogic; // Need reference to trigger actions
import com.equinox.game.ui.EquinoxGameLogic.GameStateEnum; // Import the enum

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class InputHandler implements KeyListener {

    private EquinoxGameLogic gameLogic; // Reference to the game logic

    // Input state flags (moved from EquinoxGameLogic)
    private boolean moveLeft = false;
    private boolean moveRight = false;

    public InputHandler(EquinoxGameLogic gameLogic) {
        this.gameLogic = gameLogic;
    }

    // Getters for movement state
    public boolean isMoveLeft() {
        return moveLeft;
    }

    public boolean isMoveRight() {
        return moveRight;
    }

    @Override
    public void keyTyped(KeyEvent e) {
        // Not typically used in action games
    }

    @Override
    public void keyPressed(KeyEvent e) {
        int keyCode = e.getKeyCode();
        GameStateEnum currentState = gameLogic.getCurrentState(); // Get current state

        switch (currentState) {
            case MENU:
                handleMenuInputPressed(keyCode);
                break;
            case PLAYING:
                handlePlayingInputPressed(keyCode);
                break;
            case GAME_OVER:
                handleGameOverInputPressed(keyCode);
                break;
            // Add cases for CUTSCENE etc. if needed
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
        int keyCode = e.getKeyCode();
        GameStateEnum currentState = gameLogic.getCurrentState(); // Get current state

        switch (currentState) {
            case MENU:
                // No specific release actions needed for menu
                break;
            case PLAYING:
                handlePlayingInputReleased(keyCode);
                break;
            case GAME_OVER:
                handleGameOverInputReleased(keyCode);
                break;
            // Add cases for CUTSCENE etc. if needed
        }
    }

    // Helper methods for state-specific input
    private void handleMenuInputPressed(int keyCode) {
        if (keyCode == KeyEvent.VK_UP || keyCode == KeyEvent.VK_W) {
            gameLogic.menuUp();
        } else if (keyCode == KeyEvent.VK_DOWN || keyCode == KeyEvent.VK_S) {
            gameLogic.menuDown();
        } else if (keyCode == KeyEvent.VK_ENTER) {
            gameLogic.menuSelect();
        }
    }

    private void handlePlayingInputPressed(int keyCode) {
        // Ship movement flags
        if (keyCode == KeyEvent.VK_A || keyCode == KeyEvent.VK_LEFT) {
            moveLeft = true;
        } else if (keyCode == KeyEvent.VK_D || keyCode == KeyEvent.VK_RIGHT) {
            moveRight = true;
        }
    }

    private void handleGameOverInputPressed(int keyCode) {
        // No specific press actions needed for game over? Restart is on release.
        // Maybe add ESC to go back to menu?
    }

    // Helper methods for state-specific input release
    private void handlePlayingInputReleased(int keyCode) {
        // Ship movement flag released
        if (keyCode == KeyEvent.VK_A || keyCode == KeyEvent.VK_LEFT) {
            moveLeft = false;
        } else if (keyCode == KeyEvent.VK_D || keyCode == KeyEvent.VK_RIGHT) {
            moveRight = false;
        } 
        // Ship action triggers
        else if (keyCode == KeyEvent.VK_SPACE) {
            gameLogic.fireBullet(); 
        } 
        else if (keyCode == KeyEvent.VK_Q) {
            gameLogic.fireWaveBlast();
        } 
        else if (keyCode == KeyEvent.VK_E) {
            gameLogic.fireLaserBeam();
        } 
        else if (keyCode == KeyEvent.VK_R) { // R is now Phase Shift in PLAYING state
            gameLogic.firePhaseShift();
        }
    }

    private void handleGameOverInputReleased(int keyCode) {
        if (keyCode == KeyEvent.VK_R) {
            gameLogic.restartLevel(); // Restart on R release in GAME_OVER state
        } 
        // Optional: Add ESC key to return to Main Menu from Game Over
        /* else if (keyCode == KeyEvent.VK_ESCAPE) { 
            gameLogic.goToMenu(); // Need to add goToMenu() method in EquinoxGameLogic
        } */
    }
} 