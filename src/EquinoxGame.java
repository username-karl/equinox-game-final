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
