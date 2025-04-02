package com.equinox.game.systems;

import com.equinox.game.ui.EquinoxGameLogic;
import com.equinox.game.ui.CutscenePanel;
import com.equinox.game.ui.ShopPanel;
import com.equinox.game.data.GameState;
import com.equinox.game.systems.CutsceneData;

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
    private static Image shopBackground;
    private Map<Integer, CutsceneData> cutsceneDataMap;

    public StageManager(EquinoxGameLogic gameLogic, JFrame frame) {
        this.gameLogic = gameLogic;
        this.frame = frame;
        if (shopBackground == null) {
            shopBackground = new ImageIcon(getClass().getResource("/assets/bg_shop.png")).getImage();
        }
        cutscenePanel = new CutscenePanel(this, null);
        shopPanel = new ShopPanel(this, shopBackground,gameLogic.getGameState());
        loadCutsceneData();
    }

    private void loadCutsceneData() {
        cutsceneDataMap = new HashMap<>();

        List<String> stage1Narrations = new ArrayList<>();
        stage1Narrations.add("Narrator: You are Captain Nova");
        stage1Narrations.add("Narrator: You have a mission...");
        stage1Narrations.add("Narrator: You must begin your journey");
        stage1Narrations.add("Narrator: Enemies approaching...");
        stage1Narrations.add("Narrator: Get ready all hands on deck and prepare for battle");
        Image stage1Portrait = new ImageIcon(getClass().getResource("/assets/captainnova.png")).getImage();
        Image stage1Background = new ImageIcon(getClass().getResource("/assets/cg_stage1_scene1.png")).getImage();
        CutsceneData stage1Data = new CutsceneData(stage1Portrait, stage1Narrations, stage1Background);
        cutsceneDataMap.put(1, stage1Data);

        List<String> stage2Narrations = new ArrayList<>();
        stage2Narrations.add("Narrator: You have bested The Collector");
        stage2Narrations.add("Narrator: The enemies here are stronger.");
        stage2Narrations.add("Narrator: Recruit new allies to help you.");
        Image stage2Portrait = new ImageIcon(getClass().getResource("/assets/captainnova.png")).getImage();
        Image stage2Background = new ImageIcon(getClass().getResource("/assets/cg_stage2_scene1.png")).getImage();
        CutsceneData stage2Data = new CutsceneData(stage2Portrait, stage2Narrations, stage2Background);
        cutsceneDataMap.put(2, stage2Data);
        List<String> stage3Narrations = new ArrayList<>();
        stage3Narrations.add("Narrator: You have bested this area");
        stage3Narrations.add("Narrator: You grow ever stronger");
        stage3Narrations.add("Narrator: Recruit more allies to help you.");
        Image stage3Portrait = new ImageIcon(getClass().getResource("/assets/captainnova.png")).getImage();
        Image stage3Background = new ImageIcon(getClass().getResource("/assets/cg_stage2_scene1.png")).getImage();
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
            frame.pack();
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
        frame.pack();
        frame.revalidate();
        frame.repaint();
        shopPanel.showShop();
    }

    public void startGameLoop() {
        frame.remove(shopPanel);
        frame.add(gameLogic);
        frame.pack();
        frame.revalidate();
        frame.repaint();
        gameLogic.startGameLoop();
        gameLogic.requestFocus();
    }
} 