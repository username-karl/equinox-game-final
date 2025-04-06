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

    // References to UI components for updates - Reworked to use a Map
    private Map<String, JLabel> levelLabels = new HashMap<>();
    private Map<String, JLabel> costLabels = new HashMap<>();
    private Map<String, JButton> buyButtons = new HashMap<>();
    
    // Panel that holds the upgrade options
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

        // Central panel for the upgrade options - Changed to GridLayout
        // upgradeOptionsPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 40, 20));
        // Determine rows/cols based on number of upgrades (e.g., 2 columns)
        // This needs to fetch upgrades available at max world level (e.g., 4) for initial setup
        int maxWorldLevel = 4; // Assuming world 4 is the max
        List<Upgrade> allPossibleUpgrades = UpgradeManager.getAvailableUpgrades(maxWorldLevel);
        int numUpgrades = allPossibleUpgrades.size();
        int gridCols = 3; // Let's try 3 columns
        int gridRows = (int) Math.ceil((double) numUpgrades / gridCols);
        upgradeOptionsPanel = new JPanel(new GridLayout(gridRows, gridCols, 15, 15)); // Rows, Cols, Hgap, Vgap
        
        upgradeOptionsPanel.setOpaque(false); // Make transparent to see background
        // Add a scroll pane in case content overflows
        JScrollPane scrollPane = new JScrollPane(upgradeOptionsPanel);
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);
        scrollPane.setBorder(BorderFactory.createEmptyBorder()); // No border for scrollpane itself
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        add(scrollPane, BorderLayout.CENTER); // Add scrollpane instead of panel directly

        // Create the specific upgrade panels dynamically
        // upgradeOptionsPanel.add(createSpecificUpgradePanel("HEALTH")); // REMOVED
        // upgradeOptionsPanel.add(createSpecificUpgradePanel("DAMAGE")); // REMOVED
        // upgradeOptionsPanel.add(createSpecificUpgradePanel("SPEED"));  // REMOVED
        for (Upgrade upgrade : allPossibleUpgrades) {
            upgradeOptionsPanel.add(createUpgradePanel(upgrade)); // Call new method
        }

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
        // Update the display for all panels stored in the map
        // updateSpecificUpgradeDisplay("HEALTH"); // REMOVED
        // updateSpecificUpgradeDisplay("DAMAGE"); // REMOVED
        // updateSpecificUpgradeDisplay("SPEED"); // REMOVED
        for (String upgradeId : levelLabels.keySet()) { // Iterate through the upgrades we have UI for
            updateUpgradeDisplay(upgradeId); // Call generalized update method
        }
        revalidate();
        repaint();
        requestFocusInWindow();
    }

    // Renamed from createSpecificUpgradePanel, takes Upgrade object
    private JPanel createUpgradePanel(Upgrade upgrade) {
        // Upgrade upgrade = UpgradeManager.getUpgrade(upgradeId); // No longer needed
        if (upgrade == null) return new JPanel(); 

        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        // panel.setPreferredSize(new Dimension(200, 300)); // REMOVED - Let layout manager handle size
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
        // descArea.setMaximumSize(new Dimension(180, 40)); // REMOVED - Allow natural wrapping
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

        // Store references to components that need updating using the Upgrade ID
        levelLabels.put(upgrade.id(), levelLabel);
        costLabels.put(upgrade.id(), costLabel);
        buyButtons.put(upgrade.id(), buyButton);
        /*
        switch(upgradeId) {
            case "HEALTH":
                levelLabels.put("HEALTH", levelLabel);
                costLabels.put("HEALTH", costLabel);
                buyButtons.put("HEALTH", buyButton);
                break;
            case "DAMAGE":
                levelLabels.put("DAMAGE", levelLabel);
                costLabels.put("DAMAGE", costLabel);
                buyButtons.put("DAMAGE", buyButton);
                break;
            case "SPEED":
                levelLabels.put("SPEED", levelLabel);
                costLabels.put("SPEED", costLabel);
                buyButtons.put("SPEED", buyButton);
                break;
        }
        */

        // Initial update is handled by setupShopUI calling updateUpgradeDisplay
        return panel;
    }
    
    // Renamed from updateSpecificUpgradeDisplay, uses Maps
    private void updateUpgradeDisplay(String upgradeId) {
        Upgrade upgrade = UpgradeManager.getUpgrade(upgradeId);
        if (upgrade == null) return;

        int currentLevel = getCurrentUpgradeLevel(upgrade.id());
        int nextCost = UpgradeManager.calculateCost(upgrade, currentLevel);
        boolean maxLevelReached = currentLevel >= upgrade.maxLevel();

        // Retrieve components from Maps
        JLabel levelLabel = levelLabels.get(upgradeId);
        JLabel costLabel = costLabels.get(upgradeId);
        JButton buyButton = buyButtons.get(upgradeId);
        /*
        switch(upgradeId) {
            case "HEALTH": levelLabel = levelLabels.get("HEALTH"); costLabel = costLabels.get("HEALTH"); buyButton = buyButtons.get("HEALTH"); break;
            case "DAMAGE": levelLabel = levelLabels.get("DAMAGE"); costLabel = costLabels.get("DAMAGE"); buyButton = buyButtons.get("DAMAGE"); break;
            case "SPEED":  levelLabel = levelLabels.get("SPEED");  costLabel = costLabels.get("SPEED");  buyButton = buyButtons.get("SPEED"); break;
        }
        */

        if (levelLabel == null || costLabel == null || buyButton == null) { 
             System.err.println("Warning: UI components not found for upgrade ID: " + upgradeId); // Added warning
             return; 
        }

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
        // Updated to include all relevant upgrades
        switch (upgradeId) {
            case "HEALTH": return gameState.healthUpgradeLevel;
            case "SPEED": return gameState.speedUpgradeLevel;
            case "DAMAGE": return gameState.damageUpgradeLevel;
            case "FIRE_RATE": return gameState.fireRateUpgradeLevel;
            case "COOLDOWN_QE": return gameState.cooldownQEUpgradeLevel;
            case "COOLDOWN_R": return gameState.cooldownRUpgradeLevel;
            case "MONEY_MULT": return gameState.moneyMultUpgradeLevel;
            // Return 0 for others, though they shouldn't be requested here
            default: 
                System.err.println("Warning: Unknown upgrade ID in getCurrentUpgradeLevel: " + upgradeId);
                return 0; 
        }
    }

    private void purchaseUpgrade(Upgrade upgrade) {
        int currentLevel = getCurrentUpgradeLevel(upgrade.id());
        if (currentLevel >= upgrade.maxLevel()) {
            System.out.println(upgrade.id() + " is already at max level!");
            return;
        }

        int cost = UpgradeManager.calculateCost(upgrade, currentLevel);
        if (gameState.getMoney() >= cost) {
            gameState.addMoney(-cost); // Use addMoney to handle potential multipliers correctly if costs were positive
            incrementUpgradeLevel(upgrade.id());
            // Update UI immediately after purchase
            lblMoney.setText("Money: $" + gameState.getMoney());
            updateUpgradeDisplay(upgrade.id()); // Update the specific upgrade bought
            // Update button states for potentially newly affordable upgrades
            for (String otherId : levelLabels.keySet()) {
                 if (!otherId.equals(upgrade.id())) { // Don't re-update the one just bought
                    updateUpgradeDisplay(otherId);
                 }
            }
        } else {
            System.out.println("Not enough money for " + upgrade.id());
            // Maybe add a visual/audio cue for failed purchase
        }
    }

    // Updated to include all relevant upgrades
    private void incrementUpgradeLevel(String upgradeId) {
        switch (upgradeId) {
            case "HEALTH": gameState.healthUpgradeLevel++; break;
            case "SPEED": gameState.speedUpgradeLevel++; break;
            case "DAMAGE": gameState.damageUpgradeLevel++; break;
            case "FIRE_RATE": gameState.fireRateUpgradeLevel++; break;
            case "COOLDOWN_QE": gameState.cooldownQEUpgradeLevel++; break;
            case "COOLDOWN_R": gameState.cooldownRUpgradeLevel++; break;
            case "MONEY_MULT": gameState.moneyMultUpgradeLevel++; break;
             default:
                 System.err.println("Warning: Unknown upgrade ID in incrementUpgradeLevel: " + upgradeId);
                 break;
        }
        System.out.println(upgradeId + " level incremented to: " + getCurrentUpgradeLevel(upgradeId)); // Debug
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