import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

public class ShopPanel extends JPanel {

    private StageManager stageManager;
    private List<JButton> characterButtons;
    private Image background;
    private JLabel titleLabel; // New: Shop title label

    public ShopPanel(StageManager stageManager, Image background) {
        this.stageManager = stageManager;
        this.background = background;
        setPreferredSize(new Dimension(1280, 720)); // 16:9 ratio
        setLayout(new BorderLayout()); // Use BorderLayout for better layout control

        // Create the title label
        titleLabel = new JLabel("Welcome to the trading hub: Recruit your crew");
        titleLabel.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 28)); // Font
        titleLabel.setForeground(Color.ORANGE); // Brighter orange
        titleLabel.setHorizontalAlignment(JLabel.CENTER);
        add(titleLabel, BorderLayout.NORTH); // Add title to the top

        // Create the money label

        titleLabel = new JLabel("Current Money: ");
        titleLabel.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 28)); // Font
        titleLabel.setForeground(Color.ORANGE); // Brighter orange
        titleLabel.setHorizontalAlignment(JLabel.LEFT);
        add(titleLabel, BorderLayout.NORTH); // Add title to the top

        // CHARACTER PANEL FOR BUTTONS
        JPanel characterPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 50, 50)); // FlowLayout for buttons
        characterPanel.setOpaque(false); // Make the panel transparent
        characterButtons = new ArrayList<>();

        //Example
        createCharacterButton(characterPanel, "Captain Nova", new ImageIcon(getClass().getResource("./img/temp.png")).getImage(), "A trusty old Ally", "Increase Guns");
        createCharacterButton(characterPanel, "Character 2", new ImageIcon(getClass().getResource("./img/temp.png")).getImage(), "A mysterious and powerful ally.", "Increase Health");
        createCharacterButton(characterPanel, "Character 3", new ImageIcon(getClass().getResource("./img/temp.png")).getImage(), "A skilled and loyal companion.", "Increase Speed");

        add(characterPanel, BorderLayout.CENTER); // Add character panel to the center
    }

    private void createCharacterButton(JPanel panel, String name, Image portrait, String description, String upgradeDescription) {
        JButton button = new JButton();
        button.setPreferredSize(new Dimension(200, 300)); // Increased height to accommodate new label
        button.setLayout(new BorderLayout());
        button.setBackground(new Color(50, 50, 50)); // Darker background for buttons
        button.setForeground(Color.WHITE);
        button.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 16)); // Changed font

        // Create the title label for the character name
        JLabel nameLabel = new JLabel(name);
        nameLabel.setHorizontalAlignment(JLabel.CENTER);
        nameLabel.setForeground(Color.WHITE);
        nameLabel.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 18)); // Larger font for name
        button.add(nameLabel, BorderLayout.NORTH); // Add name to the top of the button

        JLabel portraitLabel = new JLabel(new ImageIcon(portrait.getScaledInstance(150, 150, Image.SCALE_SMOOTH)));
        portraitLabel.setHorizontalAlignment(JLabel.CENTER);
        button.add(portraitLabel, BorderLayout.CENTER);

        // Create the description label
        JLabel descriptionLabel = new JLabel("<html><center>" + description + "</center></html>"); // HTML for text wrapping
        descriptionLabel.setHorizontalAlignment(JLabel.CENTER);
        descriptionLabel.setForeground(Color.LIGHT_GRAY); // Lighter gray for description
        descriptionLabel.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 14)); // Changed font
        button.add(descriptionLabel, BorderLayout.SOUTH);

        // Create the upgrade description label
        JLabel upgradeLabel = new JLabel("<html><center>Upgrade: " + upgradeDescription + "</center></html>");
        upgradeLabel.setHorizontalAlignment(JLabel.CENTER);
        upgradeLabel.setForeground(Color.CYAN); // Different color for upgrade
        upgradeLabel.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 12));
        // Create a panel to hold the description and upgrade labels
        JPanel textPanel = new JPanel(new GridLayout(2, 1)); // 2 rows, 1 column
        textPanel.setOpaque(false);
        textPanel.add(descriptionLabel);
        textPanel.add(upgradeLabel);
        button.add(textPanel, BorderLayout.SOUTH);

        button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Handle character selection
                System.out.println(name + " selected!");
                stageManager.startGameLoop();
            }
        });
        characterButtons.add(button);
        panel.add(button);
    }

    public void showShop() {
        revalidate();
        repaint();
        requestFocusInWindow();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (background != null) {
            g.drawImage(background, 0, 0, getWidth(), getHeight(), this);
        }
    }
}
