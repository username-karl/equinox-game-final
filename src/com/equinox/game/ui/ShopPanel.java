package com.equinox.game.ui;

import com.equinox.game.systems.StageManager;
import com.equinox.game.data.GameState;
import com.equinox.game.shop.Upgrade;
import com.equinox.game.shop.UpgradeManager;
import com.equinox.game.utils.Constants;
import com.equinox.game.utils.AssetLoader;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import javax.swing.border.*; // Import border classes

public class ShopPanel extends JPanel {

    private StageManager stageManager;
    private Image background;
    private JLabel lblTitle;    // Title Label
    private JLabel lblMoney; // Money Label
    private GameState gameState;
    private JPanel upgradesPanel; // Panel to hold upgrade components
    private Map<String, JPanel> upgradePanelMap; // Map to easily update specific upgrade displays
    private JButton continueButton;
    private AssetLoader assetLoader;

    // References to UI components for updates
    private JLabel healthLevelLabel, healthCostLabel;
    private JButton healthBuyButton;
    private JLabel damageLevelLabel, damageCostLabel;
    private JButton damageBuyButton;
    private JLabel speedLevelLabel, speedCostLabel;
    private JButton speedBuyButton;
    
    // Panel that holds the three upgrade options
    private JPanel upgradeOptionsPanel;

    public ShopPanel(StageManager stageManager, Image background, GameState gameState, AssetLoader assetLoader) {
        this.stageManager = stageManager;
        this.background = background; // Note: background is not currently used/loaded by StageManager
        this.gameState = gameState;
        this.assetLoader = assetLoader;
        setPreferredSize(new Dimension(Constants.BOARD_WIDTH, Constants.BOARD_HEIGHT)); 
        setLayout(new BorderLayout(10, 10)); // Add gaps
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20)); // Add more padding

        // Title Label (Optional, can be part of background)
        lblTitle = new JLabel("Upgrade Shop"); // Changed Title
        lblTitle.setFont(new Font("Monospaced", Font.BOLD, 48)); // Retro font?
        lblTitle.setForeground(Color.CYAN);
        lblTitle.setHorizontalAlignment(JLabel.CENTER);
        add(lblTitle, BorderLayout.NORTH);

        // Central panel for the three upgrade options
        upgradeOptionsPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 40, 20)); // Centered flow with gaps
        upgradeOptionsPanel.setOpaque(false); // Make transparent to see background
        add(upgradeOptionsPanel, BorderLayout.CENTER);

        // Create the three specific upgrade panels in the desired order
        upgradeOptionsPanel.add(createSpecificUpgradePanel("HEALTH")); // Hull Engineering
        upgradeOptionsPanel.add(createSpecificUpgradePanel("DAMAGE")); // Weapons Bay
        upgradeOptionsPanel.add(createSpecificUpgradePanel("SPEED"));  // Engine Mechanic

        // Bottom Panel for Money and Continue Button
        JPanel bottomPanel = new JPanel(new BorderLayout(10, 5));
        bottomPanel.setOpaque(false);

        // Money Label
        lblMoney = new JLabel("Money: $" + gameState.getMoney());
        lblMoney.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 24));
        lblMoney.setForeground(Color.ORANGE);
        lblMoney.setHorizontalAlignment(JLabel.LEFT);
        bottomPanel.add(lblMoney, BorderLayout.WEST);

        // Continue Button
        continueButton = new JButton("Continue");
        continueButton.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 24));
        continueButton.addActionListener(e -> stageManager.startGameLoop());
        JPanel continueButtonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT)); // Align right
        continueButtonPanel.setOpaque(false);
        continueButtonPanel.add(continueButton);
        bottomPanel.add(continueButtonPanel, BorderLayout.EAST);

        add(bottomPanel, BorderLayout.SOUTH);

        upgradePanelMap = new HashMap<>();
    }

    // Call this when the shop needs to be shown/refreshed
    public void setupShopUI() {
        lblMoney.setText("Money: $" + gameState.getMoney());
        // Update the display for each of the three fixed panels
        updateSpecificUpgradeDisplay("HEALTH");
        updateSpecificUpgradeDisplay("DAMAGE");
        updateSpecificUpgradeDisplay("SPEED");
        revalidate();
        repaint();
        requestFocusInWindow();
    }

    // Creates a specific upgrade panel (Hull, Gun, Engine)
    private JPanel createSpecificUpgradePanel(String upgradeId) {
        Upgrade upgrade = UpgradeManager.getUpgrade(upgradeId);
        if (upgrade == null) return new JPanel(); // Should not happen if IDs are correct

        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setPreferredSize(new Dimension(200, 300)); // Fixed size for panels
        panel.setBackground(new Color(30, 30, 60, 200)); // Semi-transparent dark blueish
        // Retro border
        Border outerBorder = BorderFactory.createLineBorder(new Color(0, 100, 255), 3); // Dark blue, 3px thick
        Border innerBorder = BorderFactory.createEmptyBorder(10, 10, 10, 10); // Padding
        panel.setBorder(BorderFactory.createCompoundBorder(outerBorder, innerBorder));
        panel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        // Title Label (e.g., "Hull", "Guns", "Engines")
        JLabel titleLabel = new JLabel(upgrade.crewLine()); // Using crewLine as panel title
        titleLabel.setFont(new Font("Monospaced", Font.BOLD, 20));
        titleLabel.setForeground(Color.CYAN);
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.add(titleLabel);
        panel.add(Box.createRigidArea(new Dimension(0, 10))); // Spacer
        
        // Icon
        Image iconImg = assetLoader.getImage(Constants.PLACEHOLDER_ICON_KEY); // Use specific icons later?
        JLabel iconLabel = new JLabel();
        if (iconImg != null) {
            iconLabel.setIcon(new ImageIcon(iconImg.getScaledInstance(80, 80, Image.SCALE_SMOOTH)));
        } else {
            iconLabel.setText("[ICON]"); iconLabel.setForeground(Color.RED);
        }
        iconLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.add(iconLabel);
        panel.add(Box.createRigidArea(new Dimension(0, 10))); // Spacer

        // Add Description Label
        JTextArea descArea = new JTextArea(upgrade.description());
        descArea.setEditable(false);
        descArea.setLineWrap(true);
        descArea.setWrapStyleWord(true);
        descArea.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 12));
        descArea.setForeground(Color.LIGHT_GRAY);
        descArea.setOpaque(false);
        descArea.setBorder(null);
        descArea.setAlignmentX(Component.CENTER_ALIGNMENT);
        // Set preferred size to constrain width and allow wrapping
        descArea.setMaximumSize(new Dimension(180, 40)); // Adjust height as needed
        panel.add(descArea);
        panel.add(Box.createRigidArea(new Dimension(0, 10))); // Spacer

        // Add Effect Per Level Label
        JLabel effectLabel = new JLabel("Effect: " + upgrade.effectPerLevelText());
        effectLabel.setFont(new Font(Font.SANS_SERIF, Font.ITALIC, 12));
        effectLabel.setForeground(Color.GREEN); // Green for positive effects
        effectLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.add(effectLabel);
        panel.add(Box.createRigidArea(new Dimension(0, 10))); // Spacer

        // Level Label
        JLabel levelLabel = new JLabel(); 
        levelLabel.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 14));
        levelLabel.setForeground(Color.WHITE);
        levelLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.add(levelLabel);
        
        // Cost Label
        JLabel costLabel = new JLabel(); 
        costLabel.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 14));
        costLabel.setForeground(Color.YELLOW);
        costLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.add(costLabel);
        panel.add(Box.createRigidArea(new Dimension(0, 15))); // Spacer

        // Buy Button
        JButton buyButton = new JButton("BUY");
        buyButton.setFont(new Font("Monospaced", Font.BOLD, 18));
        buyButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        buyButton.addActionListener(e -> purchaseUpgrade(upgrade));
        panel.add(buyButton);

        // Store references to components that need updating
        switch(upgradeId) {
            case "HEALTH":
                healthLevelLabel = levelLabel;
                healthCostLabel = costLabel;
                healthBuyButton = buyButton;
                break;
            case "DAMAGE":
                damageLevelLabel = levelLabel;
                damageCostLabel = costLabel;
                damageBuyButton = buyButton;
                break;
            case "SPEED":
                speedLevelLabel = levelLabel;
                speedCostLabel = costLabel;
                speedBuyButton = buyButton;
                break;
        }

        // Initial update is handled by setupShopUI calling updateSpecificUpgradeDisplay
        return panel;
    }
    
    // Updates the display for one of the three specific upgrade panels
    private void updateSpecificUpgradeDisplay(String upgradeId) {
        Upgrade upgrade = UpgradeManager.getUpgrade(upgradeId);
        if (upgrade == null) return;

        int currentLevel = getCurrentUpgradeLevel(upgrade.id());
        int nextCost = UpgradeManager.calculateCost(upgrade, currentLevel);
        boolean maxLevelReached = currentLevel >= upgrade.maxLevel();

        JLabel levelLabel = null, costLabel = null;
        JButton buyButton = null;

        switch(upgradeId) {
            case "HEALTH": levelLabel = healthLevelLabel; costLabel = healthCostLabel; buyButton = healthBuyButton; break;
            case "DAMAGE": levelLabel = damageLevelLabel; costLabel = damageCostLabel; buyButton = damageBuyButton; break;
            case "SPEED":  levelLabel = speedLevelLabel;  costLabel = speedCostLabel;  buyButton = speedBuyButton; break;
        }

        if (levelLabel == null || costLabel == null || buyButton == null) return; // Safety check

        levelLabel.setText(String.format("Level: %d / %d", currentLevel, upgrade.maxLevel()));
        
        if (maxLevelReached) {
            costLabel.setText("MAX LEVEL");
            buyButton.setText("Maxed");
            buyButton.setEnabled(false);
        } else {
            costLabel.setText(String.format("Cost: $%d", nextCost));
            buyButton.setText("BUY");
            buyButton.setEnabled(gameState.getMoney() >= nextCost); 
        }
    }

    // Helper to get current level from GameState based on ID
    private int getCurrentUpgradeLevel(String upgradeId) {
        // Only return levels for the 3 displayed upgrades
        switch (upgradeId) {
            case "HEALTH": return gameState.healthUpgradeLevel;
            case "SPEED": return gameState.speedUpgradeLevel;
            case "DAMAGE": return gameState.damageUpgradeLevel;
            // Return 0 for others, though they shouldn't be requested here
            default: return 0; 
        }
    }

    // Logic to handle purchasing an upgrade
    private void purchaseUpgrade(Upgrade upgrade) {
        if (upgrade == null) return;
        String upgradeId = upgrade.id();
        int currentLevel = getCurrentUpgradeLevel(upgradeId);
        int cost = UpgradeManager.calculateCost(upgrade, currentLevel);

        if (cost <= 0 || currentLevel >= upgrade.maxLevel()) {
            System.out.println("Upgrade already maxed: " + upgradeId);
            return;
        }

        if (gameState.getMoney() >= cost) {
            gameState.addMoney(-cost); 
            incrementUpgradeLevel(upgradeId); 

            System.out.println("Purchased " + upgrade.name() + " Level " + getCurrentUpgradeLevel(upgradeId));
            
            // Update the UI
            lblMoney.setText("Money: $" + gameState.getMoney());
            // Update the specific panel that was clicked
            updateSpecificUpgradeDisplay(upgradeId); 
            // Update affordability of all buttons
            updateAllUpgradeButtonsAffordability(); 

        } else {
            JOptionPane.showMessageDialog(this, 
                "Not enough money to purchase " + upgrade.name() + "!", 
                "Insufficient Funds", 
                JOptionPane.WARNING_MESSAGE);
        }
    }
    
    // Helper to increment the correct level in GameState
    private void incrementUpgradeLevel(String upgradeId) {
         switch (upgradeId) {
            case "HEALTH": gameState.healthUpgradeLevel++; break;
            case "SPEED": gameState.speedUpgradeLevel++; break;
            case "DAMAGE": gameState.damageUpgradeLevel++; break;
            // Other upgrades not handled by this specific shop screen
        }
    }
    
    // Helper to refresh the enabled state of all buy buttons based on current money
    private void updateAllUpgradeButtonsAffordability(){
         updateSpecificUpgradeDisplay("HEALTH");
         updateSpecificUpgradeDisplay("DAMAGE");
         updateSpecificUpgradeDisplay("SPEED");
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        // Draw the background image
        if (background != null) {
            g.drawImage(background, 0, 0, getWidth(), getHeight(), this);
        } else { // Fallback if background failed to load
             g.setColor(Color.BLACK);
             g.fillRect(0, 0, getWidth(), getHeight());
        }
    }
} 