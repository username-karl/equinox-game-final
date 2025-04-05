package com.equinox.game.data;

import com.equinox.game.entities.*;
import com.equinox.game.entities.enemies.*;
import com.equinox.game.utils.Constants;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class GameState {
    
    // Game Status
    public boolean gameOver = false;

    // Scoring & Economy
    public int score = 0;
    public int money = 500;
    public long enemySlain = 0;

    // Stage & Wave Progression
    public Stage currentStage;
    public long enemyCount = 0; // Enemies currently alive
    
    // Entities
    public ShipUser ship;
    public List<Enemy> enemyArray = new ArrayList<>();
    public List<Bullet> bulletArray = new ArrayList<>();
    public List<EnemyBullet> enemyBulletArray = new ArrayList<>();
    public List<WaveBlast> waveBlastArray = new ArrayList<>();
    public List<LaserBeam> laserBeamArray = new ArrayList<>();
    
    // Player State / Cooldowns (TODO: Maybe move to PlayerState class?)
    public long remainingWaveBlastCooldown = 0;
    public long remainingLaserBeamCooldown = 0;
    public long remainingPhaseShiftCooldown = 0;
    public long lastWaveBlastUseTime = 0;
    public long lastLaserBeamUseTime = 0;
    public long lastPhaseShiftUseTime = 0;
    public boolean isPhaseShiftActive = false;
    public long phaseShiftEndTime = 0;
    
    // Settings (TODO: Move to Settings/Config class?)
    public boolean cheatsEnabled = false;

    // Leaderboard Tracking
    public long startTimeMillis; // Time the current game started
    public int maxMoneyAchieved; // Highest money held during the run

    // Win/Loss State
    public boolean gameWon = false; // Flag to indicate if game ended in victory

    // --- Upgrade Levels --- (Initialized to 0 in reset)
    public int healthUpgradeLevel;
    public int speedUpgradeLevel;
    public int damageUpgradeLevel;
    public int fireRateUpgradeLevel;
    public int cooldownQEUpgradeLevel;
    public int bulletPierceLevel;
    public int cooldownRUpgradeLevel;
    public int moneyMultUpgradeLevel;

    // Constructor
    public GameState(){
        // Initialize Stage (Example values)
        this.currentStage = new Stage(1, 7); 
    }
    
    // --- Methods --- 

    public int getMoney() {
        return money;
    }
    public void setMoney(int money) {
        this.money = money;
    }

    // TODO: Refactor dropLoot - maybe move to an Enemy or LootSystem class
    public void dropLoot(){
        Random random = new Random();
        int rndm=random.nextInt(3);
        switch(rndm){
            case 1: money += 10; break;
            case 2: money += 5; break;
            default: break; // Case 0 or others give nothing
        }
    }
    
    // Convenience method to reset entity lists (used by GameUpdateSystem)
    public void resetEntityLists() {
        enemyArray.clear();
        bulletArray.clear();
        enemyBulletArray.clear();
        waveBlastArray.clear();
        laserBeamArray.clear();
        isPhaseShiftActive = false; // Reset phase shift state too
        phaseShiftEndTime = 0;
        enemyCount = 0; // Reset count when lists are cleared
    }
    
    // Convenience method to reset cooldowns (used by GameUpdateSystem)
    public void resetCooldowns() {
        lastWaveBlastUseTime = 0;
        lastLaserBeamUseTime = 0;
        lastPhaseShiftUseTime = 0;
        remainingWaveBlastCooldown = 0;
        remainingLaserBeamCooldown = 0;
        remainingPhaseShiftCooldown = 0;
        isPhaseShiftActive = false;
        phaseShiftEndTime = 0;
    }
    
    // Method to reset core game state for start/restart
    public void resetForNewGame() {
        // Reset core stats
        score = 0;
        money = Constants.STARTING_MONEY; 
        enemySlain = 0;
        gameOver = false;
        // Reset ship health and position (assuming ship reference is valid)
        if (ship != null) {
             ship.resetHealth();
             ship.setX(Constants.SHIP_START_X);
             ship.setY(Constants.SHIP_START_Y);
        }
       
        // Reset entity lists
        if (enemyArray != null) enemyArray.clear();
        if (bulletArray != null) bulletArray.clear();
        if (enemyBulletArray != null) enemyBulletArray.clear();
        if (waveBlastArray != null) waveBlastArray.clear();
        if (laserBeamArray != null) laserBeamArray.clear();

        // Reset stage progress
        if (currentStage != null) {
            currentStage.setStageNumber(1);
            currentStage.setCurrentWave(1);
            currentStage.setSpecialEnemySpawned(false);
        }
        enemyCount = 0; // Reset enemy count too

        // Reset cooldowns and ability states
        lastWaveBlastUseTime = 0;
        lastLaserBeamUseTime = 0;
        lastPhaseShiftUseTime = 0;
        remainingWaveBlastCooldown = 0;
        remainingLaserBeamCooldown = 0;
        remainingPhaseShiftCooldown = 0;
        isPhaseShiftActive = false;
        phaseShiftEndTime = 0;
        
        // Initialize leaderboard tracking fields
        startTimeMillis = 0; // Will be set properly when game starts
        maxMoneyAchieved = money; // Initial max money is starting money
        gameWon = false; // Ensure gameWon flag is reset

        // Initialize upgrade levels
        healthUpgradeLevel = 0;
        speedUpgradeLevel = 0;
        damageUpgradeLevel = 0;
        fireRateUpgradeLevel = 0;
        cooldownQEUpgradeLevel = 0;
        bulletPierceLevel = 0;
        cooldownRUpgradeLevel = 0;
        moneyMultUpgradeLevel = 0;

        System.out.println("GameState reset for new game.");
    }

    // Helper to update money and track max achieved
    public void addMoney(int amount) {
        if (amount < 0) {
            // Don't apply multiplier to deductions (costs)
            this.money += amount;
        } else {
            // Apply multiplier bonus (+15% per level)
            double multiplierBonus = 0.15 * this.moneyMultUpgradeLevel;
            int finalAmount = (int) Math.round(amount * (1.0 + multiplierBonus));
            this.money += finalAmount;
        }
        this.maxMoneyAchieved = Math.max(this.maxMoneyAchieved, this.money);
        // System.out.println("Money: " + this.money + " | MaxMoney: " + this.maxMoneyAchieved); // Debug
    }
} 