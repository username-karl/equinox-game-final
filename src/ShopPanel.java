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

        // CHARACTER PANNEL FOR BUTTONS
        JPanel characterPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 50, 50)); // FlowLayout for buttons
        characterPanel.setOpaque(false); // Make the panel transparent
        characterButtons = new ArrayList<>();

        //Example
        createCharacterButton(characterPanel, "Captain Nova", new ImageIcon(getClass().getResource("./img/temp.png")).getImage(), "A trusty old Ally");
        createCharacterButton(characterPanel, "Character 2", new ImageIcon(getClass().getResource("./img/temp.png")).getImage(), "A mysterious and powerful ally.");
        createCharacterButton(characterPanel, "Character 3", new ImageIcon(getClass().getResource("./img/temp.png")).getImage(), "A skilled and loyal companion.");

        add(characterPanel, BorderLayout.CENTER); // Add character panel to the center
    }

    private void createCharacterButton(JPanel panel, String name, Image portrait, String description) {
        JButton button = new JButton(name);
        button.setPreferredSize(new Dimension(200, 250));
        button.setLayout(new BorderLayout());
        button.setBackground(new Color(50, 50, 50)); // Darker background for buttons
        button.setForeground(Color.WHITE);
        button.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 16)); // Changed font

        JLabel portraitLabel = new JLabel(new ImageIcon(portrait.getScaledInstance(150, 150, Image.SCALE_SMOOTH)));
        portraitLabel.setHorizontalAlignment(JLabel.CENTER);
        button.add(portraitLabel, BorderLayout.CENTER);

        // Create the description label
        JLabel descriptionLabel = new JLabel("<html><center>" + description + "</center></html>"); // HTML for text wrapping
        descriptionLabel.setHorizontalAlignment(JLabel.CENTER);
        descriptionLabel.setForeground(Color.LIGHT_GRAY); // Lighter gray for description
        descriptionLabel.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 14)); // Changed font
        button.add(descriptionLabel, BorderLayout.SOUTH);

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
