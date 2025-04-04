package com.equinox.game.systems;

import com.equinox.game.ui.EquinoxGameLogic;
import com.equinox.game.ui.CutscenePanel;
import com.equinox.game.ui.ShopPanel;
import com.equinox.game.data.GameState;
import com.equinox.game.data.CutsceneData;
import com.equinox.game.utils.AssetLoader;
import com.equinox.game.utils.Constants;

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
    private AssetLoader assetLoader;
    private static Image shopBackground;
    private Map<Integer, CutsceneData> cutsceneDataMap;

    public StageManager(EquinoxGameLogic gameLogic, JFrame frame, AssetLoader assetLoader) {
        this.gameLogic = gameLogic;
        this.frame = frame;
        this.assetLoader = assetLoader;

        if (shopBackground == null) {
            try {
                shopBackground = new ImageIcon(getClass().getResource("/assets/bg_shop.png")).getImage();
            } catch (Exception e) { System.err.println("Failed to load shop background"); }
        }
        cutscenePanel = new CutscenePanel(this, assetLoader);
        shopPanel = new ShopPanel(this, shopBackground, gameLogic.getGameState(), assetLoader);
        loadCutsceneData();
    }

    private void loadCutsceneData() {
        cutsceneDataMap = new HashMap<>();

        CutsceneData stage1Data = new CutsceneData();
        stage1Data.addFrame(new CutsceneData.CutsceneFrame(
            "Narrator: You are Captain Nova",
            assetLoader.getImage(Constants.BG_CUTSCENE_1_KEY),
            assetLoader.getImage(Constants.CAPTAIN_PORTRAIT_KEY)
        ));
        stage1Data.addFrame(new CutsceneData.CutsceneFrame(
            "Narrator: You have a mission...",
             assetLoader.getImage(Constants.BG_CUTSCENE_1_KEY),
             assetLoader.getImage(Constants.CAPTAIN_PORTRAIT_KEY)
        ));
         stage1Data.addFrame(new CutsceneData.CutsceneFrame(
            "Narrator: You must begin your journey",
             assetLoader.getImage(Constants.BG_CUTSCENE_1_KEY),
             assetLoader.getImage(Constants.CAPTAIN_PORTRAIT_KEY)
        ));
         stage1Data.addFrame(new CutsceneData.CutsceneFrame(
            "Narrator: Enemies approaching...",
             assetLoader.getImage(Constants.BG_CUTSCENE_1_KEY),
             null
        ));
         stage1Data.addFrame(new CutsceneData.CutsceneFrame(
            "Narrator: Get ready all hands on deck and prepare for battle",
             assetLoader.getImage(Constants.BG_CUTSCENE_1_KEY),
             assetLoader.getImage(Constants.CAPTAIN_PORTRAIT_KEY)
        ));
        cutsceneDataMap.put(1, stage1Data);

        CutsceneData stage2Data = new CutsceneData();
        stage2Data.addFrame(new CutsceneData.CutsceneFrame(
            "Narrator: You have bested The Collector",
             assetLoader.getImage(Constants.BG_CUTSCENE_2_KEY),
             assetLoader.getImage(Constants.CAPTAIN_PORTRAIT_KEY)
        ));
        stage2Data.addFrame(new CutsceneData.CutsceneFrame(
            "Narrator: The enemies here are stronger.",
             assetLoader.getImage(Constants.BG_CUTSCENE_2_KEY),
             assetLoader.getImage(Constants.CAPTAIN_PORTRAIT_KEY)
        ));
        stage2Data.addFrame(new CutsceneData.CutsceneFrame(
            "Narrator: Recruit new allies to help you.",
             assetLoader.getImage(Constants.BG_CUTSCENE_2_KEY),
             assetLoader.getImage(Constants.CAPTAIN_PORTRAIT_KEY)
        ));
        cutsceneDataMap.put(2, stage2Data);

        CutsceneData stage3Data = new CutsceneData();
        stage3Data.addFrame(new CutsceneData.CutsceneFrame(
            "Narrator: You have bested this area",
             assetLoader.getImage(Constants.BG_DEFAULT_KEY),
             assetLoader.getImage(Constants.CAPTAIN_PORTRAIT_KEY)
        ));
         stage3Data.addFrame(new CutsceneData.CutsceneFrame(
            "Narrator: You grow ever stronger",
             assetLoader.getImage(Constants.BG_DEFAULT_KEY),
             assetLoader.getImage(Constants.CAPTAIN_PORTRAIT_KEY)
        ));
         stage3Data.addFrame(new CutsceneData.CutsceneFrame(
            "Narrator: Recruit more allies to help you.",
             assetLoader.getImage(Constants.BG_DEFAULT_KEY),
             assetLoader.getImage(Constants.CAPTAIN_PORTRAIT_KEY)
        ));
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
            cutscenePanel.startCutsceneDisplay();
            cutscenePanel.requestFocusInWindow();
        } else {
            System.err.println("No cutscene data found for stage: " + currentStage);
            showShop();
        }
    }

    public void showShop() {
        frame.remove(cutscenePanel);
        gameLogic.setCurrentState(EquinoxGameLogic.GameStateEnum.MENU);
        frame.add(shopPanel);
        frame.pack();
        frame.revalidate();
        frame.repaint();
        shopPanel.setupShopUI();
    }

    public void startGameLoop() {
        frame.remove(shopPanel);
        gameLogic.setCurrentState(EquinoxGameLogic.GameStateEnum.PLAYING);
        frame.add(gameLogic);
        frame.pack();
        frame.revalidate();
        frame.repaint();
        // Recalculate ship stats based on any upgrades purchased
        if (gameLogic.gameState != null && gameLogic.gameState.ship != null) {
            gameLogic.gameState.ship.recalculateStats(gameLogic.gameState);
        }
        gameLogic.startGameLoop();
        gameLogic.requestFocus();
    }
} 