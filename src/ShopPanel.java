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
    private JLabel lbltitle;    //Title Label
    private JLabel lblmoney; // Money Label
    private GameState gameState;
    private JPanel moneyPanel; // Panel to hold the money label

    public ShopPanel(StageManager stageManager, Image background, GameState gameState) {
        this.stageManager = stageManager;
        this.background = background;
        this.gameState = gameState;
        setPreferredSize(new Dimension(1280, 720));
        setLayout(new BorderLayout());

        // Create the title label
        lbltitle = new JLabel("Welcome to the trading hub: Recruit your crew");
        lbltitle.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 28));
        lbltitle.setForeground(Color.ORANGE);
        lbltitle.setHorizontalAlignment(JLabel.CENTER);
        add(lbltitle, BorderLayout.NORTH);

        // Create the money label and panel
        moneyPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        moneyPanel.setOpaque(false); // Make the panel transparent
        lblmoney = new JLabel("Current Money: $" + gameState.getMoney());
        lblmoney.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 20));
        lblmoney.setForeground(Color.ORANGE);
        moneyPanel.add(lblmoney);
        add(moneyPanel, BorderLayout.SOUTH); // Add money panel to the bottom

        // CHARACTER PANEL FOR BUTTONS
        JPanel characterPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 50, 50));
        characterPanel.setOpaque(false);
        characterButtons = new ArrayList<>();

        // Example
        createCharacterButton(characterPanel, "Captain Nova", new ImageIcon(getClass().getResource("./img/temp.png")).getImage(), "A trusty old Ally", "Increase Guns", 100);
        createCharacterButton(characterPanel, "Character 2", new ImageIcon(getClass().getResource("./img/temp.png")).getImage(), "A mysterious and powerful ally.", "Increase Health", 150);
        createCharacterButton(characterPanel, "Character 3", new ImageIcon(getClass().getResource("./img/temp.png")).getImage(), "A skilled and loyal companion.", "Increase Speed", 200);

        add(characterPanel, BorderLayout.CENTER);
    }

    private void createCharacterButton(JPanel panel, String name, Image portrait, String description, String upgradeDescription, int cost) {
        JButton button = new JButton();
        button.setPreferredSize(new Dimension(200, 300));
        button.setLayout(new BorderLayout());
        button.setBackground(new Color(50, 50, 50));
        button.setForeground(Color.WHITE);
        button.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 16));

        // Create the title label for the character name
        JLabel nameLabel = new JLabel(name);
        nameLabel.setHorizontalAlignment(JLabel.CENTER);
        nameLabel.setForeground(Color.WHITE);
        nameLabel.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 18));
        button.add(nameLabel, BorderLayout.NORTH);

        JLabel portraitLabel = new JLabel(new ImageIcon(portrait.getScaledInstance(150, 150, Image.SCALE_SMOOTH)));
        portraitLabel.setHorizontalAlignment(JLabel.CENTER);
        button.add(portraitLabel, BorderLayout.CENTER);

        // Create the description label
        JLabel descriptionLabel = new JLabel("<html><center>" + description + "</center></html>");
        descriptionLabel.setHorizontalAlignment(JLabel.CENTER);
        descriptionLabel.setForeground(Color.LIGHT_GRAY);
        descriptionLabel.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 14));

        // Create the upgrade description label
        JLabel upgradeLabel = new JLabel("<html><center>Upgrade: " + upgradeDescription + "</center></html>");
        upgradeLabel.setHorizontalAlignment(JLabel.CENTER);
        upgradeLabel.setForeground(Color.CYAN);
        upgradeLabel.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 12));

        // Create the cost label
        JLabel costLabel = new JLabel("<html><center>Cost: $" + cost + "</center></html>");
        costLabel.setHorizontalAlignment(JLabel.CENTER);
        costLabel.setForeground(Color.YELLOW);
        costLabel.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 12));

        // Create a panel to hold the description, upgrade, and cost labels
        JPanel textPanel = new JPanel(new GridLayout(3, 1)); // 3 rows, 1 column
        textPanel.setOpaque(false);
        textPanel.add(descriptionLabel);
        textPanel.add(upgradeLabel);
        textPanel.add(costLabel);
        button.add(textPanel, BorderLayout.SOUTH);

        button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Handle character selection
                System.out.println(name + " selected!");
                // Money Check
                if (gameState.getMoney() >= cost) {
                    nameLabel.setText("RECRUITED");
                    button.setEnabled(false);
                    // Deduct money
                    gameState.setMoney(gameState.getMoney() - cost);
                    // Update the money label
                    lblmoney.setText("Current Money: $" + gameState.getMoney());
                    // Proceed to the game loop
                    stageManager.startGameLoop();
                } else {
                    // Not enough money, display a message
                    JOptionPane.showMessageDialog(ShopPanel.this, "Insufficient Funds! " + name + "!", "Insufficient Funds", JOptionPane.WARNING_MESSAGE);
                }
            }
        });
        characterButtons.add(button);
        panel.add(button);
    }

    public void showShop() {
        lblmoney.setText("Current Money: $" + gameState.getMoney());
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
