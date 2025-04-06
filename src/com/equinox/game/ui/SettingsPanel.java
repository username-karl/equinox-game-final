package com.equinox.game.ui;

import com.equinox.game.utils.Constants;
import com.equinox.game.data.GameState;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;

public class SettingsPanel extends JPanel {

    private JButton backButton;
    private GameState gameState;
    // Add placeholders for settings components later
    private JLabel volumeLabel;
    // private JSlider volumeSlider; 
    private JLabel graphicsLabel;
    private JCheckBox cheatsCheckbox;

    public SettingsPanel(ActionListener backAction, GameState gameState) {
        this.gameState = gameState;
        setLayout(new BorderLayout());
        setBackground(Color.BLACK); // Or load a background image later

        // Title
        JLabel titleLabel = new JLabel("Settings", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 48));
        titleLabel.setForeground(Constants.MENU_TITLE_COLOR);
        add(titleLabel, BorderLayout.NORTH);

        // Settings Area (Use GridBagLayout for better control)
        JPanel settingsArea = new JPanel(new GridBagLayout());
        settingsArea.setOpaque(false); // Make transparent to see background
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.WEST;

        volumeLabel = new JLabel("Volume:");
        volumeLabel.setFont(new Font("Arial", Font.PLAIN, 24));
        volumeLabel.setForeground(Color.WHITE);
        settingsArea.add(volumeLabel, gbc);

        // Placeholder for slider
        gbc.gridx = 1;
        JLabel sliderPlaceholder = new JLabel("[ Slider Placeholder ]");
        sliderPlaceholder.setFont(new Font("Arial", Font.ITALIC, 20));
        sliderPlaceholder.setForeground(Color.GRAY);
        settingsArea.add(sliderPlaceholder, gbc);

        gbc.gridx = 0;
        gbc.gridy++;
        graphicsLabel = new JLabel("Graphics:");
        graphicsLabel.setFont(new Font("Arial", Font.PLAIN, 24));
        graphicsLabel.setForeground(Color.WHITE);
        settingsArea.add(graphicsLabel, gbc);

        // Placeholder for checkbox
        gbc.gridx = 1;
        JLabel checkboxPlaceholder = new JLabel("[ Fullscreen Placeholder ]");
        checkboxPlaceholder.setFont(new Font("Arial", Font.ITALIC, 20));
        checkboxPlaceholder.setForeground(Color.GRAY);
        settingsArea.add(checkboxPlaceholder, gbc);

        // Cheats (Row 2)
        gbc.gridx = 0;
        gbc.gridy++;
        JLabel cheatsLabel = new JLabel("Cheats:");
        cheatsLabel.setFont(new Font("Arial", Font.PLAIN, 24));
        cheatsLabel.setForeground(Color.WHITE);
        settingsArea.add(cheatsLabel, gbc);
        
        gbc.gridx = 1;
        cheatsCheckbox = new JCheckBox("Enable Cheats");
        cheatsCheckbox.setFont(new Font("Arial", Font.PLAIN, 20));
        cheatsCheckbox.setForeground(Color.WHITE);
        cheatsCheckbox.setOpaque(false);
        cheatsCheckbox.setFocusPainted(false);
        cheatsCheckbox.addActionListener(e -> {
            if (gameState != null) {
                gameState.cheatsEnabled = cheatsCheckbox.isSelected();
                System.out.println("Cheats Toggled via Settings: " + gameState.cheatsEnabled);
            }
        });
        settingsArea.add(cheatsCheckbox, gbc);

        add(settingsArea, BorderLayout.CENTER);

        // Back Button Area
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        bottomPanel.setOpaque(false);
        backButton = new JButton("Back");
        backButton.setFont(new Font("Arial", Font.BOLD, 24));
        backButton.setForeground(Constants.MENU_ITEM_DEFAULT_COLOR);
        backButton.setBackground(Color.DARK_GRAY);
        backButton.setFocusPainted(false);
        backButton.setBorderPainted(false);
        backButton.addActionListener(backAction);
        bottomPanel.add(backButton);
        add(bottomPanel, BorderLayout.SOUTH);
    }

    // Method to update UI state based on GameState
    public void updateSettingsState() {
        if (gameState != null && cheatsCheckbox != null) {
            cheatsCheckbox.setSelected(gameState.cheatsEnabled);
        }
        // Update other settings components (volume slider, etc.) here later
    }

    // Add methods later to get/set actual settings values
} 