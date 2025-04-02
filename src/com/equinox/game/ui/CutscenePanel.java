package com.equinox.game.ui;

import com.equinox.game.systems.StageManager; // Import StageManager
import com.equinox.game.systems.CutsceneData; // Import CutsceneData (assuming it's public or we extract it)

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.List;

public class CutscenePanel extends JPanel implements KeyListener {

    private Image characterPortrait;
    private String currentNarration;
    private List<String> narrations;
    private int narrationIndex;
    private StageManager stageManager;
    private Image background;
    private CutsceneData cutsceneData; 

    public CutscenePanel(StageManager stageManager, Image background) {
        this.stageManager = stageManager;
        this.background = background;
        setPreferredSize(new Dimension(768, 768)); // Adjusted size from original (was 1280x720)
        setBackground(Color.BLACK);
        setFocusable(true);
        addKeyListener(this);
        narrationIndex = 0;
    }

    public void setCutsceneData(CutsceneData cutsceneData) {
        this.cutsceneData = cutsceneData;
        if (cutsceneData != null) { // Added null check
            this.characterPortrait = cutsceneData.getCharacterPortrait();
            this.narrations = cutsceneData.getNarrations();
            this.background = cutsceneData.getBackground(); // New: Set the background
        } else {
             this.characterPortrait = null;
             this.narrations = List.of("Error: Cutscene data missing."); // Provide default
             this.background = null;
        }
    }

    public void startCutscene() {
        narrationIndex = 0;
        if (narrations != null && !narrations.isEmpty()) {
            currentNarration = narrations.get(narrationIndex);
        } else {
            currentNarration = "No narration available.";
        }
        requestFocusInWindow();
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        // Draw Background first
        if (background != null) {
             g.drawImage(background, 0, 0, getWidth(), getHeight(), this);
        } else {
            // Draw default black background if none provided
            g.setColor(Color.BLACK);
            g.fillRect(0, 0, getWidth(), getHeight());
        }

        // Draw character portrait (adjust position/size as needed)
        if (characterPortrait != null) {
            // Example: Draw portrait on the left side
             int portraitWidth = getWidth() / 3; // Example size
             int portraitHeight = getHeight() / 2; // Example size
             int portraitX = 50; // Example position
             int portraitY = (getHeight() - portraitHeight) / 2; // Center vertically
            g.drawImage(characterPortrait, portraitX, portraitY, portraitWidth, portraitHeight, this); 
        }

        // Draw narration box (adjust position/size as needed)
        int boxHeight = getHeight() / 4; // Example height
        int boxY = getHeight() - boxHeight - 20; // Position near bottom
        g.setColor(new Color(30, 30, 30, 200)); // Dark, semi-transparent box
        g.fillRect(50, boxY, getWidth() - 100, boxHeight); 

        // Draw narration text (adjust position/size as needed)
        g.setColor(Color.WHITE);
        g.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 18)); 
        if (currentNarration != null) {
             drawText(g, currentNarration, 70, boxY + 30, getWidth() - 140); // Pass width for potential wrapping
        }
        
        // Draw 'continue' prompt in the bottom right of the text box
        if (narrations != null && narrationIndex < narrations.size() -1) { // Only show if there's more text
             String continuePrompt = "[Space/Enter]";
             g.setFont(new Font(Font.SANS_SERIF, Font.ITALIC, 14)); // Smaller, italic font
             g.setColor(Color.LIGHT_GRAY); // Slightly dimmer color
             FontMetrics fm = g.getFontMetrics();
             int promptWidth = fm.stringWidth(continuePrompt);
             int promptX = getWidth() - 50 - 10 - promptWidth; // Position from right edge inside the box
             int promptY = boxY + boxHeight - 10; // Position near bottom inside the box
             g.drawString(continuePrompt, promptX, promptY);
        }
    }

    // Basic text drawing - WITH line wrapping
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
            // Check if adding the next word exceeds the max width
            String testLine = (currentLine.length() > 0) ? currentLine + " " + word : word;
            if (fm.stringWidth(testLine) <= maxWidth) {
                // Word fits, append it (with a space if needed)
                if (currentLine.length() > 0) {
                    currentLine.append(" ");
                }
                currentLine.append(word);
            } else {
                // Word doesn't fit, draw the current line and start a new one
                g.drawString(currentLine.toString(), x, currentY);
                currentY += lineHeight; // Move y down for the next line
                currentLine = new StringBuilder(word); // Start new line with the current word
                
                // Handle case where a single word is longer than maxWidth
                if (fm.stringWidth(currentLine.toString()) > maxWidth) {
                     // Optional: Implement character-level wrapping or just let it overflow slightly
                     // For now, we draw it and it might overflow
                     g.drawString(currentLine.toString(), x, currentY);
                     currentLine = new StringBuilder(); // Clear line
                     currentY += lineHeight; 
                }
            }
        }

        // Draw the last remaining line
        if (currentLine.length() > 0) {
            g.drawString(currentLine.toString(), x, currentY);
        }
    }

    @Override
    public void keyTyped(KeyEvent e) {
    }

    @Override
    public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_ENTER || e.getKeyCode() == KeyEvent.VK_SPACE) { // Allow Space too
            advanceNarration();
        }
    }

    private void advanceNarration() {
         if (narrations == null) return; // Safety check
            narrationIndex++;
            if (narrationIndex < narrations.size()) {
                currentNarration = narrations.get(narrationIndex);
                repaint();
            } else {
                // Cutscene finished, transition to shop
                stageManager.showShop();
            }
    }

    @Override
    public void keyReleased(KeyEvent e) {
    }
}

// CutsceneData class removed from here, should be imported from StageManager's package 