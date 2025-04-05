package com.equinox.game.leaderboard;

import com.equinox.game.ui.EquinoxGameLogic; // To switch back
import com.equinox.game.utils.Constants;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

public class LeaderboardPanel extends JPanel implements ActionListener {

    private EquinoxGameLogic gameLogic; // Reference to main logic to switch back
    private JButton backButton;
    private List<LeaderboardEntry> scores;

    public LeaderboardPanel(EquinoxGameLogic gameLogic) {
        this.gameLogic = gameLogic;
        setPreferredSize(new Dimension(Constants.BOARD_WIDTH, Constants.BOARD_HEIGHT));
        setBackground(Color.BLACK);
        setLayout(new BorderLayout()); // Use BorderLayout for positioning

        // Title Label
        JLabel titleLabel = new JLabel("Leaderboard", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 48));
        titleLabel.setForeground(Color.CYAN);
        add(titleLabel, BorderLayout.NORTH);

        // Back Button
        backButton = new JButton("Back to Menu");
        backButton.setFont(new Font("Arial", Font.BOLD, 24));
        backButton.setForeground(Color.BLACK);
        backButton.setBackground(Color.LIGHT_GRAY);
        backButton.setFocusable(false); // Prevent stealing focus
        backButton.addActionListener(this);

        // Panel for the button at the bottom
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        buttonPanel.setOpaque(false); // Make transparent
        buttonPanel.add(backButton);
        add(buttonPanel, BorderLayout.SOUTH);

        setFocusable(true);
    }

    // Method to load scores and trigger repaint
    public void loadAndDisplayScores() {
        this.scores = LeaderboardManager.loadEntries();
        repaint(); // Trigger paintComponent to draw the scores
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g); // Draw background etc.
        drawScores(g);
    }

    private void drawScores(Graphics g) {
        if (scores == null) {
            return;
        }

        g.setColor(Color.WHITE);
        g.setFont(new Font("Monospaced", Font.PLAIN, 18));
        FontMetrics fm = g.getFontMetrics();

        int startY = 120; // Starting Y position for the first entry
        int lineHeight = fm.getHeight() + 10; // Line height + padding
        int rankX = 50;
        int nameX = 100;
        int scoreX = 300;
        int moneyX = 450;
        int timeX = 600;

        // Draw Header
        g.setFont(new Font("Monospaced", Font.BOLD, 20));
        g.drawString("Rank", rankX, startY);
        g.drawString("Name", nameX, startY);
        g.drawString("Score", scoreX, startY);
        g.drawString("Max $ ", moneyX, startY);
        g.drawString("Time", timeX, startY);
        startY += lineHeight;
        g.setFont(new Font("Monospaced", Font.PLAIN, 18)); // Reset font for entries
        
        int rank = 1;
        for (LeaderboardEntry entry : scores) {
            if (rank > LeaderboardManager.MAX_ENTRIES) break; // Should not happen if load/save trim correctly

            int currentY = startY + (rank - 1) * lineHeight;
            
            g.drawString(String.format("%2d.", rank), rankX, currentY);
            g.drawString(String.format("%-15s", entry.playerName()), nameX, currentY); // Pad name
            g.drawString(String.format("%,9d", entry.score()), scoreX, currentY); // Format score with commas
            g.drawString(String.format("%,7d", entry.maxMoney()), moneyX, currentY); // Format money
            g.drawString(entry.getFormattedTime(), timeX, currentY);
            
            rank++;
        }
        
        if (scores.isEmpty()){
             g.drawString("No scores recorded yet!", nameX, startY);
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == backButton) {
            // Switch back to the main menu state
             gameLogic.showMainMenu(); // Need to add this method to EquinoxGameLogic
        }
    }
} 