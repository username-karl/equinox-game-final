package com.equinox.game.core;

import com.equinox.game.ui.EquinoxGameLogic;
import com.equinox.game.systems.StageManager;

import java.awt.Dimension;
import java.awt.Toolkit;

import javax.swing.*;

public class EquinoxGame {
    public static void main(String[] args) {

        //JFRAME VARIABLES
        int tileSize = 32;
        int rows = 24;
        int columns = 24;
        int boardWidth = tileSize * columns;
        int boardHeight = tileSize * rows;

        //JFrame main
        JFrame frame = new JFrame("EquinoxGame");
        frame.setLocationRelativeTo(null);
        frame.setResizable(false);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        //Game Screen
        EquinoxGameLogic equinox = new EquinoxGameLogic();
        StageManager stageManager = new StageManager(equinox, frame);
        equinox.setStageManager(stageManager);

        // Add the game logic panel to the frame's content pane
        frame.add(equinox);
        frame.pack(); // Adjust frame size to fit the preferred size of the panel

        // Center the window on the screen
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        // Get frame size *after* packing
        int frameWidth = frame.getWidth(); 
        int frameHeight = frame.getHeight();
        int centerX = (screenSize.width - frameWidth) / 2;
        int centerY = (screenSize.height - frameHeight) / 2;
        frame.setLocation(centerX, centerY);
    
        frame.setVisible(true);
    }
} 