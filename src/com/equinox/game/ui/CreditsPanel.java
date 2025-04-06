package com.equinox.game.ui;

import com.equinox.game.utils.Constants;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;

public class CreditsPanel extends JPanel {

    private JButton backButton;
    private JTextArea creditsTextArea;

    public CreditsPanel(ActionListener backAction) {
        setLayout(new BorderLayout());
        setBackground(Color.BLACK);

        // Title
        JLabel titleLabel = new JLabel("Credits", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 48));
        titleLabel.setForeground(Constants.MENU_TITLE_COLOR);
        add(titleLabel, BorderLayout.NORTH);

        // Credits Text Area
        creditsTextArea = new JTextArea();
        creditsTextArea.setFont(new Font("Monospaced", Font.PLAIN, 16));
        creditsTextArea.setForeground(Color.WHITE);
        creditsTextArea.setBackground(Color.BLACK);
        creditsTextArea.setEditable(false);
        creditsTextArea.setLineWrap(true);
        creditsTextArea.setWrapStyleWord(true);
        creditsTextArea.setMargin(new Insets(20, 40, 20, 40)); // Add padding
        
        // Sample Credits Text
        String creditsText = "EQUINOX\n\n" +
                             "Development Lead:\n" +
                             "  [Your Name/Handle Here]\n\n" +
                             "Programming:\n" +
                             "  [Your Name/Handle Here]\n" +
                             "  Assisted by AI (Gemini/Cursor)\n\n" +
                             "Art Assets (Placeholders/Sources):\n" +
                             "  [List Asset Sources or Creators if known]\n" +
                             "  - Example Asset Pack by ...\n" +
                             "  - Player Ship by ...\n\n" +
                             "Special Thanks:\n" +
                             "  [Anyone you want to thank]\n\n" +
                             "Made with Java & Swing";
        creditsTextArea.setText(creditsText);

        // Add text area to a scroll pane
        JScrollPane scrollPane = new JScrollPane(creditsTextArea);
        scrollPane.setBorder(null);
        scrollPane.getViewport().setBackground(Color.BLACK);
        add(scrollPane, BorderLayout.CENTER);

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
} 