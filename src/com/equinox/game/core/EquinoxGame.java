package com.equinox.game.core;

import com.equinox.game.ui.EquinoxGameLogic;
import com.equinox.game.systems.StageManager; // Assuming StageManager will be in systems

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
        // Note: EquinoxGameLogic is now in com.equinox.game.ui
        EquinoxGameLogic equinox = new EquinoxGameLogic();
        // Note: StageManager is assumed to be in com.equinox.game.systems
        StageManager stageManager = new StageManager(equinox, frame);
        equinox.setStageManager(stageManager);
        
        // Start the cutscene immediately
        stageManager.startCutscene();

        // Center the window on the screen
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        int centerX = (screenSize.width - frame.getWidth()) / 2;
        int centerY = (screenSize.height - frame.getHeight()) / 2;
        frame.setLocation(centerX, centerY);
    
        frame.setVisible(true);
    }
} 