package com.equinox.game.ui;

import com.equinox.game.systems.StageManager;
import com.equinox.game.data.CutsceneData; // Correct import
import com.equinox.game.utils.AssetLoader; // Added import
import com.equinox.game.utils.Constants; // Added import for potential UI constants

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
// import java.util.List; // No longer directly needed for narrations list

public class CutscenePanel extends JPanel implements KeyListener {

    // Removed direct image/text fields, get from CutsceneFrame
    // private Image characterPortrait;
    // private String currentNarration;
    // private List<String> narrations;
    // private int narrationIndex;
    // private Image background;

    private StageManager stageManager;
    private AssetLoader assetLoader; // Store AssetLoader
    private CutsceneData currentCutsceneData; // Store the active cutscene data
    private CutsceneData.CutsceneFrame currentFrame; // Store the current frame being displayed

    // Updated Constructor
    public CutscenePanel(StageManager stageManager, AssetLoader assetLoader) {
        this.stageManager = stageManager;
        this.assetLoader = assetLoader; // Store it
        // this.background = background; // Removed - background comes from frame
        setPreferredSize(new Dimension(Constants.BOARD_WIDTH, Constants.BOARD_HEIGHT)); // Use Constants
        setBackground(Color.BLACK); // Default background
        setFocusable(true);
        addKeyListener(this);
        // narrationIndex = 0; // Removed
    }

    // Updated to store the data object
    public void setCutsceneData(CutsceneData cutsceneData) {
        this.currentCutsceneData = cutsceneData;
        this.currentFrame = null; // Reset frame when new data is set
    }

    // Renamed and updated logic
    public void startCutsceneDisplay() {
        if (currentCutsceneData != null) {
            currentCutsceneData.start(); // Activate the cutscene data
            currentFrame = currentCutsceneData.getCurrentFrame(); // Get the first frame
        } else {
             currentFrame = null; // Ensure no frame if data is null
             System.err.println("Error: Attempted to start cutscene with null data.");
             // Optionally, create a default error frame
             // currentFrame = new CutsceneData.CutsceneFrame("Error loading cutscene!", null, null);
        }
        requestFocusInWindow();
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        if (currentFrame == null) {
            // Nothing to display or show an error/loading state
            g.setColor(Color.BLACK);
            g.fillRect(0, 0, getWidth(), getHeight());
            g.setColor(Color.RED);
            g.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 24));
            g.drawString("Loading Cutscene...", getWidth()/2 - 100, getHeight()/2);
            return; // Nothing more to draw
        }

        // Draw Background from current frame
        Image frameBackground = currentFrame.background;
        if (frameBackground != null) {
             g.drawImage(frameBackground, 0, 0, getWidth(), getHeight(), this);
        } else {
            // Draw default black background if frame has none
            g.setColor(Color.BLACK);
            g.fillRect(0, 0, getWidth(), getHeight());
        }

        // Draw character portrait from current frame
        Image framePortrait = currentFrame.characterPortrait;
        if (framePortrait != null) {
             int portraitWidth = getWidth() / 3;
             int portraitHeight = getHeight() / 2;
             int portraitX = 50;
             int portraitY = (getHeight() - portraitHeight) / 2;
            g.drawImage(framePortrait, portraitX, portraitY, portraitWidth, portraitHeight, this);
        }

        // Draw narration box
        int boxHeight = getHeight() / 4;
        int boxY = getHeight() - boxHeight - 20;
        g.setColor(new Color(30, 30, 30, 200));
        g.fillRect(50, boxY, getWidth() - 100, boxHeight);

        // Draw narration text from current frame
        g.setColor(Color.WHITE);
        g.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 18));
        String frameText = currentFrame.text;
        if (frameText != null) {
             drawText(g, frameText, 70, boxY + 30, getWidth() - 140);
        }

        // Draw 'continue' prompt if the cutscene is active (has more frames)
        if (currentCutsceneData != null && currentCutsceneData.isActive()) {
             String continuePrompt = "[Space/Enter]";
             g.setFont(new Font(Font.SANS_SERIF, Font.ITALIC, 14));
             g.setColor(Color.LIGHT_GRAY);
             FontMetrics fm = g.getFontMetrics();
             int promptWidth = fm.stringWidth(continuePrompt);
             int promptX = getWidth() - 50 - 10 - promptWidth;
             int promptY = boxY + boxHeight - 10;
             g.drawString(continuePrompt, promptX, promptY);
        }
    }

    // Basic text drawing - WITH line wrapping (Keep as is)
    private void drawText(Graphics g, String text, int x, int y, int maxWidth) {
        FontMetrics fm = g.getFontMetrics();
        int lineHeight = fm.getHeight();

        if (text == null || text.isEmpty()) {
            return;
        }

        String[] words = text.split(" ");
        StringBuilder currentLine = new StringBuilder();
        int currentY = y;

        for (String word : words) {
            String testLine = (currentLine.length() > 0) ? currentLine + " " + word : word;
            if (fm.stringWidth(testLine) <= maxWidth) {
                if (currentLine.length() > 0) {
                    currentLine.append(" ");
                }
                currentLine.append(word);
            } else {
                g.drawString(currentLine.toString(), x, currentY);
                currentY += lineHeight;
                currentLine = new StringBuilder(word);
                if (fm.stringWidth(currentLine.toString()) > maxWidth) {
                     g.drawString(currentLine.toString(), x, currentY);
                     currentLine = new StringBuilder();
                     currentY += lineHeight;
                }
            }
        }
        if (currentLine.length() > 0) {
            g.drawString(currentLine.toString(), x, currentY);
        }
    }

    @Override
    public void keyTyped(KeyEvent e) {
    }

    @Override
    public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_ENTER || e.getKeyCode() == KeyEvent.VK_SPACE) {
            advanceFrame(); // Changed method name
        }
    }

    // Updated logic to advance frame - Make public
    public void advanceFrame() {
         if (currentCutsceneData == null || !currentCutsceneData.isActive()) return;

         currentCutsceneData.nextFrame(); // Tell data object to advance
         currentFrame = currentCutsceneData.getCurrentFrame(); // Get the new frame (might be null if ended)

         if (currentFrame != null) {
              repaint(); // Redraw with the new frame
         } else {
             // Cutscene finished, transition via StageManager
             stageManager.showShop(); // Or whatever the next step is
         }
    }

    @Override
    public void keyReleased(KeyEvent e) {
    }
}

// CutsceneData class removed from here, should be imported from StageManager's package 