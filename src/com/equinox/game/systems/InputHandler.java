package com.equinox.game.systems;

import com.equinox.game.ui.EquinoxGameLogic; // Need reference to trigger actions

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

        // Ship movement flag raise
        if (keyCode == KeyEvent.VK_A || keyCode == KeyEvent.VK_LEFT) {
            moveLeft = true;
        } else if (keyCode == KeyEvent.VK_D || keyCode == KeyEvent.VK_RIGHT) {
            moveRight = true;
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
        int keyCode = e.getKeyCode();

        // Check game state for context-sensitive input
        boolean isGameOver = gameLogic.getGameState() != null && gameLogic.getGameState().gameOver;

        if (isGameOver) {
            // Only listen for Restart key when game is over
            if (keyCode == KeyEvent.VK_R) {
                gameLogic.restartLevel(); // Call restart method
            }
        } else {
            // Normal game input handling
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
                gameLogic.fireTacticalQ(); 
            } 
            else if (keyCode == KeyEvent.VK_E) {
                gameLogic.fireTacticalE(); 
            }
        }
    }
} 