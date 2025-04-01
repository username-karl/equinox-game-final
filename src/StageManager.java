import javax.swing.*;
import java.awt.*;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;

public class StageManager {

    private EquinoxGameLogic gameLogic;
    private CutscenePanel cutscenePanel;
    private ShopPanel shopPanel;
    private JFrame frame;
    private Image shopBackground;
    private Map<Integer, CutsceneData> cutsceneDataMap; // New: Map to store cutscene data

    public StageManager(EquinoxGameLogic gameLogic, JFrame frame) {
        this.gameLogic = gameLogic;
        this.frame = frame;
        //Load Background SHOP
        shopBackground = new ImageIcon(getClass().getResource("./img/shopbg.png")).getImage();
        // Create panels
        cutscenePanel = new CutscenePanel(this, null); // New: Pass null for default background
        shopPanel = new ShopPanel(this, shopBackground);
        // New: Load cutscene data
        loadCutsceneData();
    }

    private void loadCutsceneData() {
        cutsceneDataMap = new HashMap<>();

        // Cutscene data for Stage 1 (INTRO)
        List<String> stage1Narrations = new ArrayList<>();
        stage1Narrations.add("Narrator: You are Captain Nova");
        stage1Narrations.add("Narrator: You have a mission...");
        stage1Narrations.add("Narrator: You must begin your journey");
        stage1Narrations.add("Narrator: Enemies approaching...");
        stage1Narrations.add("Narrator: Get ready all hands on deck and prepare for battle");
        Image stage1Portrait = new ImageIcon(getClass().getResource("./img/captainnova.png")).getImage();   //LOAD CHARACTER PORTRAIT
        Image stage1Background = new ImageIcon(getClass().getResource("./img/stage1cg1.png")).getImage(); // LOAD CG / BG
        CutsceneData stage1Data = new CutsceneData(stage1Portrait, stage1Narrations, stage1Background); // PASS PORTRAIT,NARRATIONS,BG
        cutsceneDataMap.put(1, stage1Data);

        // Cutscene data for Stage 2 SIMPLY FOLLOW TEMPLATE 
        List<String> stage2Narrations = new ArrayList<>();
        stage2Narrations.add("Narrator: You have bested The Collector");
        stage2Narrations.add("Narrator: The enemies here are stronger.");
        stage2Narrations.add("Narrator: Recruit new allies to help you.");
        Image stage2Portrait = new ImageIcon(getClass().getResource("./img/captainnova.png")).getImage();
        Image stage2Background = new ImageIcon(getClass().getResource("./img/stage2cg1.png")).getImage(); 
        CutsceneData stage2Data = new CutsceneData(stage2Portrait, stage2Narrations, stage2Background); 
        cutsceneDataMap.put(2, stage2Data);
        // ADD MORE
        List<String> stage3Narrations = new ArrayList<>();
        stage3Narrations.add("Narrator: You have bested this area");
        stage3Narrations.add("Narrator: You grow ever stronger");
        stage3Narrations.add("Narrator: Recruit more allies to help you.");
        Image stage3Portrait = new ImageIcon(getClass().getResource("./img/captainnova.png")).getImage();
        Image stage3Background = new ImageIcon(getClass().getResource("./img/stage2cg1.png")).getImage(); 
        CutsceneData stage3Data = new CutsceneData(stage3Portrait, stage3Narrations, stage3Background); 
        cutsceneDataMap.put(3, stage3Data);
    }

    public void startCutscene() {
        int currentStage = gameLogic.getGameState().currentStage.getStageNumber();
        CutsceneData data = cutsceneDataMap.get(currentStage);
        if (data != null) {
            cutscenePanel.setCutsceneData(data);
            frame.remove(gameLogic);
            frame.add(cutscenePanel);
            frame.pack(); // Pack after adding cutscenePanel
            frame.revalidate();
            frame.repaint();
            cutscenePanel.startCutscene();
        } else {
            System.err.println("No cutscene data found for stage: " + currentStage);
            showShop();
        }
    }

    public void showShop() {
        frame.remove(cutscenePanel);
        frame.add(shopPanel);
        frame.pack(); // Pack after adding shopPanel
        frame.revalidate();
        frame.repaint();
        shopPanel.showShop();
    }

    public void startGameLoop() {
        frame.remove(shopPanel);
        frame.add(gameLogic);
        frame.pack(); // Pack after adding gameLogic
        frame.revalidate();
        frame.repaint();
        gameLogic.startGameLoop();
        gameLogic.requestFocus();
    }
}
