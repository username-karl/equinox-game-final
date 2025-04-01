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
    private CutsceneData cutsceneData; // New: Cutscene data

    public CutscenePanel(StageManager stageManager, Image background) {
        this.stageManager = stageManager;
        this.background = background;
        setPreferredSize(new Dimension(1280, 720)); // 16:9 ratio
        setBackground(Color.BLACK);
        setFocusable(true);
        addKeyListener(this);
        narrationIndex = 0;
    }

    public void setCutsceneData(CutsceneData cutsceneData) {
        this.cutsceneData = cutsceneData;
        this.characterPortrait = cutsceneData.getCharacterPortrait();
        this.narrations = cutsceneData.getNarrations();
        this.background = cutsceneData.getBackground(); // New: Set the background
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
        if (background != null) {
            g.drawImage(background, 0, 0, getWidth(), getHeight(), this); // New: Draw the background
        }
        // Draw character portrait
        if (characterPortrait != null) {
            g.drawImage(characterPortrait, 50, 200, 400, 400, this); // Adjust position as needed
        }

        // Draw narration box
        g.setColor(Color.DARK_GRAY);
        g.fillRect(50, 550, 1180, 150); // Adjust position as needed

        // Draw narration text
        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.PLAIN, 20));
        drawWrappedText(g, currentNarration, 70, 580, 1140);
    }

    private void drawWrappedText(Graphics g, String text, int x, int y, int maxWidth) {
        FontMetrics fm = g.getFontMetrics();
        String[] words = text.split(" ");
        String currentLine = "";
        int currentY = y;

        for (String word : words) {
            if (fm.stringWidth(currentLine + word) < maxWidth) {
                currentLine += word + " ";
            } else {
                g.drawString(currentLine, x, currentY);
                currentLine = word + " ";
                currentY += fm.getHeight();
            }
        }
        g.drawString(currentLine, x, currentY);
    }

    @Override
    public void keyTyped(KeyEvent e) {
    }

    @Override
    public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_ENTER) {
            narrationIndex++;
            if (narrationIndex < narrations.size()) {
                currentNarration = narrations.get(narrationIndex);
                repaint();
            } else {
                stageManager.showShop();
            }
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
    }
}

class CutsceneData {
    private Image characterPortrait;
    private List<String> narrations;
    private Image background; // New: Background image

    public CutsceneData(Image characterPortrait, List<String> narrations, Image background) {
        this.characterPortrait = characterPortrait;
        this.narrations = narrations;
        this.background = background; // New: Set the background
    }

    public Image getCharacterPortrait() {
        return characterPortrait;
    }

    public List<String> getNarrations() {
        return narrations;
    }

    public Image getBackground() {
        return background; // New: Getter for background
    }

    public void addNarration(String narration) {
        narrations.add(narration);
    }
}