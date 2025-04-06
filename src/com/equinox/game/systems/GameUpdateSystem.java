package com.equinox.game.systems;

import com.equinox.game.data.GameState;
import com.equinox.game.entities.*; // Keep entity imports
import com.equinox.game.entities.enemies.*; // Keep enemy imports
import com.equinox.game.ui.EquinoxGameLogic; // Needed for state transitions
import com.equinox.game.utils.AssetLoader;
import com.equinox.game.utils.Constants;
import com.equinox.game.leaderboard.LeaderboardManager; // Import LeaderboardManager

import java.awt.Image;
import java.util.ArrayList;
import java.util.Random;

public class GameUpdateSystem {

    // Dependencies
    private GameState gameState;
    private InputHandler inputHandler;
    private CollisionSystem collisionSystem;
    public StageManager stageManager;
    private AssetLoader assetLoader;
    private EquinoxGameLogic gameLogic; // Reference to main panel for state changes/actions
    private Random random; // Add Random instance

    // Ship state (might move to Player specific class later)
    private int shipVelocityX = 0;

    public GameUpdateSystem(GameState gameState, 
                          InputHandler inputHandler, /* CollisionSystem collisionSystem, */
                          StageManager stageManager, AssetLoader assetLoader, EquinoxGameLogic gameLogic) {
        this.gameState = gameState;
        this.inputHandler = inputHandler;
        // this.collisionSystem = collisionSystem; // Removed from constructor
        this.stageManager = stageManager;
        this.assetLoader = assetLoader;
        this.gameLogic = gameLogic;
        this.random = new Random(); // Initialize Random
    }

    // Setter for CollisionSystem dependency
    public void setCollisionSystem(CollisionSystem collisionSystem) {
        this.collisionSystem = collisionSystem;
    }

    // Added setter for StageManager dependency
    public void setStageManager(StageManager stageManager) {
        this.stageManager = stageManager;
    }

    // Main update method called by the game loop
    public void update() {
        // Ensure collisionSystem is set before using it
        if (collisionSystem == null) {
            System.err.println("ERROR: CollisionSystem not set in GameUpdateSystem!");
            return; 
        }
        
        moveEnemies();
        moveEnemyBullets();
        movePlayerBullets();
        moveTacticalAbilities();
        
        // Call parameterless collision methods
        collisionSystem.checkPlayerBulletCollisions();
        collisionSystem.checkTacticalCollisions();
        collisionSystem.checkEnemyBulletCollisions();
        
        clearOffScreenBullets();
        updateCooldowns(); // Update cooldowns first
        handlePlayerShooting(); // Then check if player can shoot
        handleStageAndWaveLogic();
        moveShip();
    }

    // --- Movement Logic ---
    private void moveEnemies() {
        if (gameState.enemyArray == null || gameState.ship == null) return;
        // Create ArrayList copies for methods expecting them - REMOVED
        // ArrayList<EnemyBullet> enemyBulletsForShoot = new ArrayList<>(gameState.enemyBulletArray);
        
        for (var enemy : new ArrayList<>(gameState.enemyArray)) { // Iterate copy for safe removal
            if (enemy.isAlive()) {
                enemy.move(Constants.BOARD_WIDTH, enemy.getWidth(), enemy.getHeight());
                if (enemy.isMoveDown()) {
                    enemy.moveDown(enemy.getHeight());
                }
                
                // Move bosses vertically regardless of shooting chance
                if (enemy instanceof com.equinox.game.entities.enemies.Miniboss) {
                    ((com.equinox.game.entities.enemies.Miniboss) enemy).moveY(); 
                }
                if (enemy instanceof com.equinox.game.entities.enemies.MainBoss) {
                    ((com.equinox.game.entities.enemies.MainBoss) enemy).moveY(); 
                }

                // Enemy shooting logic - Add random chance for regular enemies
                int shootChance = 1; // Reduced from 2 (e.g., 1% chance per frame if cooldown is ready)
                
                if (enemy instanceof com.equinox.game.entities.enemies.ShootingEnemy) {
                    if (random.nextInt(100) < shootChance) { 
                        ((com.equinox.game.entities.enemies.ShootingEnemy) enemy).shoot(gameState.enemyBulletArray, assetLoader.getImage(Constants.ENEMY_LASER_IMG_KEY),
                                                      Constants.ENEMY_BULLET_WIDTH, Constants.ENEMY_BULLET_HEIGHT);
                    }
                } else if (enemy instanceof com.equinox.game.entities.enemies.Miniboss) {
                     // Bosses handle their own attack patterns/timing
                     ((com.equinox.game.entities.enemies.Miniboss) enemy).executeAttackPattern(gameState.enemyBulletArray, assetLoader);
                 } else if (enemy instanceof com.equinox.game.entities.enemies.MainBoss) {
                     ((com.equinox.game.entities.enemies.MainBoss) enemy).executeAttackPattern(gameState, assetLoader);
                 }
                
                // Check if enemy reached the player's Y position (Game Over condition)
                // Only trigger if the bottom of the enemy reaches the top of the ship's Y coordinate
                if (enemy.getY() + enemy.getHeight() > gameState.ship.getY()) {
                    System.out.println("Game Over triggered by enemy at Y: " + enemy.getY() + " vs Ship Y: " + gameState.ship.getY()); // Debug log
                    gameLogic.signalGameOver(); 
                    return; // Exit loop immediately on game over
                }
            }
        }
        // Update the original list if shoot modified the copy - REMOVED (No longer needed)
        // If shoot adds bullets, we might need gameState.enemyBulletArray.addAll(enemyBulletsForShoot); 
        // but this depends if the copy reflects additions. Simpler if shoot takes List.
    }

    private void moveEnemyBullets() {
        if (gameState.enemyBulletArray == null) return;
        for (EnemyBullet bullet : gameState.enemyBulletArray) {
            bullet.move();
        }
    }

    private void movePlayerBullets() {
        if (gameState.bulletArray == null) return;
        for (Bullet bullet : gameState.bulletArray) {
            bullet.setY(bullet.getY() + Constants.BULLET_VELOCITY_Y);
        }
    }

    private void moveTacticalAbilities() {
         if (gameState.waveBlastArray != null) {
            for (WaveBlast waveBlast : gameState.waveBlastArray) {
                waveBlast.setY(waveBlast.getY() + Constants.WAVE_BLAST_VELOCITY_Y);
            }
        }
        if (gameState.laserBeamArray != null) {
            for (LaserBeam laserBeam : gameState.laserBeamArray) {
                laserBeam.setY(laserBeam.getY() + Constants.LASER_BEAM_VELOCITY_Y);
            }
        }
    }
    
    private void moveShip() {
        if (gameState.ship == null || inputHandler == null) return;
        boolean moveLeft = inputHandler.isMoveLeft();
        boolean moveRight = inputHandler.isMoveRight();

        // --- Calculate speed/accel based on upgrades --- 
        // Example: +0.5 max speed per level, +0.2 accel per level
        double speedBonusPerLevel = 0.5;
        double accelBonusPerLevel = 0.2;
        int currentMaxSpeed = Constants.SHIP_MAX_SPEED + (int)Math.round(gameState.speedUpgradeLevel * speedBonusPerLevel);
        double currentAcceleration = Constants.SHIP_ACCELERATION + (gameState.speedUpgradeLevel * accelBonusPerLevel);
        // Adjust deceleration proportionally? Maybe cap it?
        double currentDeceleration = Constants.SHIP_DECELERATION + (gameState.speedUpgradeLevel * accelBonusPerLevel * 0.5);
        currentDeceleration = Math.min(currentDeceleration, currentAcceleration); // Ensure decel isn't > accel
        // --------------------------------------------

        if (moveLeft) {
            // Use calculated values
            shipVelocityX = Math.max(shipVelocityX - (int)Math.round(currentAcceleration), -currentMaxSpeed);
        } else if (moveRight) {
             // Use calculated values
            shipVelocityX = Math.min(shipVelocityX + (int)Math.round(currentAcceleration), currentMaxSpeed);
        } else {
             // Use calculated values
            if (shipVelocityX > 0) {
                shipVelocityX = Math.max(0, shipVelocityX - (int)Math.round(currentDeceleration));
            } else if (shipVelocityX < 0) {
                shipVelocityX = Math.min(0, shipVelocityX + (int)Math.round(currentDeceleration));
            }
        }

        int newShipX = gameState.ship.getX() + shipVelocityX;
        gameState.ship.setX(Math.max(0, Math.min(newShipX, Constants.BOARD_WIDTH - gameState.ship.getWidth())));
    }

    // --- Game Logic & State Updates ---
    private void updateCooldowns() {
        if (gameState == null) return;
        long currentTime = System.currentTimeMillis();

        // --- Calculate effective cooldowns based on upgrades --- 
        // Bullet Cooldown Calculation
        double currentFireRateReduction = Math.min(Constants.FIRE_RATE_MAX_REDUCTION, 
                                                  gameState.fireRateUpgradeLevel * Constants.FIRE_RATE_REDUCTION_PER_LEVEL);
        long effectiveBulletCooldown = (long) (Constants.BULLET_COOLDOWN_MS * (1.0 - currentFireRateReduction));

        // Other Ability Cooldowns (QE & R)
        double reductionPerLevelQE = 0.10; // 10% reduction per level
        double reductionPerLevelR = 0.15;  // 15% reduction per level
        double maxReduction = 0.60; // Cap reduction at 60% (was 0.75)

        double currentReductionQE = Math.min(maxReduction, gameState.cooldownQEUpgradeLevel * reductionPerLevelQE);
        double currentReductionR = Math.min(maxReduction, gameState.cooldownRUpgradeLevel * reductionPerLevelR);

        long effectiveCooldownQ = (long) (Constants.WAVE_BLAST_COOLDOWN_MS * (1.0 - currentReductionQE));
        long effectiveCooldownE = (long) (Constants.LASER_BEAM_COOLDOWN_MS * (1.0 - currentReductionQE)); // QE upgrade affects both
        long effectiveCooldownR = (long) (Constants.PHASE_SHIFT_COOLDOWN_MS * (1.0 - currentReductionR));
        // ------------------------------------------------------

        // Update remaining cooldowns based on time since last use and effective values
        gameState.remainingBulletCooldown = Math.max(0, effectiveBulletCooldown - (currentTime - gameState.lastBulletFireTime));
        gameState.remainingWaveBlastCooldown = Math.max(0, effectiveCooldownQ - (currentTime - gameState.lastWaveBlastUseTime));
        gameState.remainingLaserBeamCooldown = Math.max(0, effectiveCooldownE - (currentTime - gameState.lastLaserBeamUseTime));
        gameState.remainingPhaseShiftCooldown = Math.max(0, effectiveCooldownR - (currentTime - gameState.lastPhaseShiftUseTime));

        // Handle Phase Shift duration
        if (gameState.isPhaseShiftActive && currentTime >= gameState.phaseShiftEndTime) {
            gameState.isPhaseShiftActive = false;
            System.out.println("Phase Shift Deactivated."); // Added feedback
        }
    }

    private void clearOffScreenBullets() {
        if (gameState.bulletArray != null) gameState.bulletArray.removeIf(bullet -> bullet.isUsed() || bullet.getY() < 0);
        if (gameState.waveBlastArray != null) gameState.waveBlastArray.removeIf(waveBlast -> waveBlast.isUsed() || waveBlast.getY() < -waveBlast.getHeight());
        if (gameState.laserBeamArray != null) gameState.laserBeamArray.removeIf(laserBeam -> laserBeam.isUsed() || laserBeam.getY() < 0);
        if (gameState.enemyBulletArray != null) gameState.enemyBulletArray.removeIf(enemyBullet -> enemyBullet.isUsed() || enemyBullet.getY() > Constants.BOARD_HEIGHT);
    }

    private void handleStageAndWaveLogic() {
        if (gameState == null || gameState.currentStage == null || stageManager == null) return;
        
        // DEBUG: Log current state before checking count
        // System.out.println("handleStageAndWaveLogic - Current Wave: " + gameState.currentStage.getCurrentWave() + ", Enemy Count: " + gameState.enemyCount);

        if (gameState.enemyCount == 0) { // Check if wave is cleared
            // DEBUG: Log when wave clear condition is met
            System.out.println("--- Wave Cleared! Checking logic for Wave: " + gameState.currentStage.getCurrentWave() + " ---");

            int currentStageNum = gameState.currentStage.getStageNumber();
            int currentWave = gameState.currentStage.getCurrentWave();
            int totalWaves = gameState.currentStage.getTotalWaves();
            int finalStage = 4; // Updated to 4 as per requirement

            // Check if the final wave of the final stage is completed
            if (currentStageNum == finalStage && currentWave == totalWaves) {
                System.out.println("DEBUG: Final wave of final stage cleared! Game Won!");
                gameState.gameWon = true; // Set the win flag
                // Call GameLogic to handle win sequence (prompt, save, game over)
                gameLogic.handleGameWin(); 
                return; // Stop further stage/wave processing
            }

            if (currentWave == totalWaves) {
                 System.out.println("DEBUG: End of Stage " + gameState.currentStage.getStageNumber() + ". Moving to next stage cutscene.");
                // Move to the next stage
                int nextStageNum = currentStageNum + 1;
                gameState.currentStage.setStageNumber(nextStageNum);
                gameState.currentStage.setCurrentWave(1);
                // --- SET TOTAL WAVES FOR NEXT STAGE ---
                int wavesForNextStage;
                 switch (nextStageNum) {
                     case 2: wavesForNextStage = 7; break; // Example: Stage 2 has 7 waves
                     case 3: wavesForNextStage = 8; break; // Example: Stage 3 has 8 waves
                     case 4: wavesForNextStage = 9; break; // Example: Stage 4 has 9 waves
                     default: wavesForNextStage = 7; break; // Fallback
                 }
                 gameState.currentStage.setTotalWaves(wavesForNextStage);
                 System.out.println("DEBUG: Set TotalWaves for Stage " + nextStageNum + " to " + wavesForNextStage);
                 // -------------------------------------
                gameState.currentStage.setSpecialEnemySpawned(false);
                resetEntityLists(); 
                stageManager.startCutscene(); // Trigger cutscene between stages
            } else if (currentWave == totalWaves - 1) {
                 System.out.println("DEBUG: Checking Main Boss spawn condition (CurrentWave=" + currentWave + ", Expected=" + (totalWaves - 1) + ")");
                // Spawn main boss
                if (!gameState.currentStage.isSpecialEnemySpawned()) {
                    System.out.println("DEBUG: Spawning Main Boss!");
                    gameState.currentStage.setSpecialEnemySpawned(true);
                    createMainboss();
                    gameState.currentStage.setCurrentWave(currentWave + 1);
                } else {
                     System.out.println("DEBUG: Main Boss already spawned this stage.");
                }
            } else if (currentWave == totalWaves - 2) {
                 System.out.println("DEBUG: Checking Mini Boss spawn condition (CurrentWave=" + currentWave + ", Expected=" + (totalWaves - 2) + ")");
                // Spawn miniboss
                 if (!gameState.currentStage.isSpecialEnemySpawned()) {
                    System.out.println("DEBUG: Spawning Mini Boss!");
                    gameState.currentStage.setSpecialEnemySpawned(true);
                    createMiniboss();
                    gameState.currentStage.setCurrentWave(currentWave + 1);
                    gameState.currentStage.setSpecialEnemySpawned(false); 
                 } else {
                     System.out.println("DEBUG: Mini Boss already spawned this stage.");
                 }
            } else {
                System.out.println("DEBUG: Moving to next normal wave (" + (currentWave + 1) + ")");
                // Move to the next normal wave
                gameState.currentStage.setCurrentWave(currentWave + 1);
                resetEntityLists();
                createEnemies();
            }
        }
    }

    // Called by CollisionSystem - needs access to gameState
    public void handleEnemyHit(Enemy enemy) {
         if (enemy == null || gameState == null) return;
         
         // Calculate current bullet damage based on upgrades
         int currentDamage = Constants.BULLET_BASE_DAMAGE + 
                             (gameState.damageUpgradeLevel * Constants.BULLET_DAMAGE_INCREASE_PER_LEVEL);

        if (enemy instanceof SpecialEnemy) {
            SpecialEnemy specialEnemy = (SpecialEnemy) enemy;
            // Apply damage to boss
            specialEnemy.takeDamage(currentDamage);
            if (specialEnemy.getHitpoints() <= 0) {
                if(enemy instanceof Miniboss){
                    gameState.score += Constants.SCORE_PER_MINIBOSS;
                    gameState.addMoney(Constants.MONEY_PER_MINIBOSS);
                } else if(enemy instanceof MainBoss){
                    gameState.score += Constants.SCORE_PER_MAINBOSS;
                    gameState.addMoney(Constants.MONEY_PER_MAINBOSS);
                }
                enemy.setAlive(false);
                gameState.enemySlain++;
                gameState.enemyCount--;
            }
        } else { // Regular Enemy
            // Apply damage and check if dead
            enemy.takeDamage(currentDamage);
            if (enemy.getHitpoints() <= 0) {
                enemy.setAlive(false);
                dropLoot(enemy); 
                gameState.score += Constants.SCORE_PER_ENEMY;
                gameState.enemySlain++;
                gameState.enemyCount--;
            } // If not dead (hitpoints > 0), nothing else happens yet
        }
    }

    // Method for handling loot drops (Example - Needs implementation based on your design)
    public void dropLoot(Enemy enemy) { 
         // This method needs to be implemented
         // Currently GameState calls gameState.dropLoot() which doesn't exist
         // Example: Add a small chance to drop money
         if (random.nextInt(100) < 15) { // Increased chance from 10% to 15%
            int lootAmount = 10 + random.nextInt(16); // e.g., 10-25 money
            gameState.addMoney(lootAmount);
            System.out.println("Enemy dropped " + lootAmount + " money!");
        }
    }

    // --- Entity Creation --- 
     public void createEnemies() {
        if (gameState == null || gameState.currentStage == null || gameState.enemyArray == null || assetLoader == null) return;
        
        int stageNum = gameState.currentStage.getStageNumber();
        int currentWave = gameState.currentStage.getCurrentWave();
        int maxEnemyRows = Constants.ENEMY_INITIAL_ROWS + (currentWave / 2);
        int maxEnemyColumns = Constants.ENEMY_INITIAL_COLUMNS + (currentWave);
        maxEnemyRows = Math.min(maxEnemyRows, Constants.ENEMY_MAX_ROWS);
        maxEnemyColumns = Math.min(maxEnemyColumns, Constants.ENEMY_MAX_COLS);

        gameState.enemyArray.clear(); 
        Image bulletImg = assetLoader.getImage(Constants.ENEMY_LASER_IMG_KEY); // Load bullet image once

        for (int r = 0; r < maxEnemyRows; r++) {
            for (int c = 0; c < maxEnemyColumns; c++) {
                Image enemyImg;
                Enemy enemy;
                int spawnX = Constants.ENEMY_START_X + c * Constants.ENEMY_WIDTH;
                int spawnY = Constants.ENEMY_START_Y + r * Constants.ENEMY_HEIGHT;
                int baseHealth = 1; // Default base health

                // Determine enemy type, image, and health based on stage
                switch (stageNum) {
                    case 1: 
                        enemyImg = assetLoader.getRandomEnemyImage(); 
                        // baseHealth remains 1
                        if (random.nextInt(6) == 3) { 
                             enemy = new ShootingEnemy(spawnX, spawnY, Constants.ENEMY_WIDTH, Constants.ENEMY_HEIGHT, enemyImg, Constants.ENEMY_BASE_VELOCITY_X);
                        } else {
                            enemy = new FastEnemy(spawnX, spawnY, Constants.ENEMY_WIDTH, Constants.ENEMY_HEIGHT, enemyImg, Constants.ENEMY_BASE_VELOCITY_X + 3);
                        }
                        break;
                    case 2: 
                        int randomTypeW2 = random.nextInt(10); 
                        int worldExponentW2 = stageNum - 1; 
                        enemy = null; 

                        // Spawn Weaver (30% chance: 0, 1, 2)
                        if (randomTypeW2 < 3) { 
                            int scaledHp = (int) (Constants.WEAVER_BASE_HP * Math.pow(Constants.WEAVER_HP_SCALE_FACTOR, worldExponentW2));
                            double scaledVSpeed = Constants.WEAVER_BASE_VERTICAL_SPEED * Math.pow(Constants.WEAVER_SPEED_SCALE_FACTOR, worldExponentW2);
                            double amplitude = Constants.WEAVER_HORIZONTAL_AMPLITUDE;
                            double frequency = Constants.WEAVER_HORIZONTAL_FREQUENCY;

                            enemyImg = assetLoader.getImage(Constants.WEAVER_ASSET_KEY_PLACEHOLDER); 

                            if (enemyImg != null) {
                                enemy = new WeaverEnemy(spawnX, spawnY, Constants.ENEMY_WIDTH, Constants.ENEMY_HEIGHT, 
                                                        enemyImg, scaledHp, scaledVSpeed, amplitude, frequency);
                            } else {
                                System.err.println("Warning: Missing assets for WeaverEnemy (World " + stageNum + "). Spawning fallback.");
                            }
                        } 
                        // Spawn EnemyWType1 (40% chance: 3, 4, 5, 6)
                        else if (randomTypeW2 < 7) { 
                            int scaledHp = (int) (Constants.ENEMY_W_TYPE1_BASE_HP * Math.pow(Constants.ENEMY_W_TYPE1_HP_SCALE_FACTOR, worldExponentW2));
                            double scaledSpeed = Constants.ENEMY_W_TYPE1_BASE_SPEED * Math.pow(Constants.ENEMY_W_TYPE1_SPEED_SCALE_FACTOR, worldExponentW2);
                            long baseFireDelay = Constants.ENEMY_W_TYPE1_BASE_FIRE_DELAY;
                            int shotsToFire = 1; // World 2 fires 1 shot

                            String actualAssetKey = Constants.ENEMY_W2_TYPE1_IMG_KEY; // W2 key
                            enemyImg = assetLoader.getImage(actualAssetKey);
                            
                            if (enemyImg != null && bulletImg != null) {
                                enemy = new EnemyWType1(spawnX, spawnY, Constants.ENEMY_WIDTH, Constants.ENEMY_HEIGHT, 
                                                        enemyImg, scaledHp, scaledSpeed, baseFireDelay, gameState, bulletImg, shotsToFire); // Pass shotsToFire
                            } else {
                                System.err.println("Warning: Missing assets for EnemyWType1 (World " + stageNum + "). Spawning fallback.");
                            }
                        } 
                        // Spawn Placeholders (30% chance: 7, 8, 9)
                        else { 
                            String[] keys;
                            int speedBonus;
                            keys = new String[]{Constants.ENEMY_W2_TYPE2_IMG_KEY, Constants.ENEMY_W2_TYPE3_IMG_KEY, Constants.ENEMY_W2_TYPE5_IMG_KEY }; // Avoid W2_Type4 placeholder
                            baseHealth = 2; speedBonus = 1; 
                            enemyImg = assetLoader.getImage(keys[random.nextInt(keys.length)]);
                            if (enemyImg != null) {
                                enemy = new Enemy(spawnX, spawnY, Constants.ENEMY_WIDTH, Constants.ENEMY_HEIGHT, enemyImg, Constants.ENEMY_BASE_VELOCITY_X + speedBonus, baseHealth); 
                            } else {
                                System.err.println("Warning: Missing placeholder assets (World " + stageNum + "). Spawning fallback.");
                            }
                        }
                        break;
                    case 3: 
                        int randomTypeW3 = random.nextInt(10); 
                        int worldExponentW3 = stageNum - 1; 
                        enemy = null; 

                        // Spawn Shielded (30% chance: 0, 1, 2)
                        if (randomTypeW3 < 3) {
                            int scaledHp = (int) (Constants.SHIELDED_BASE_HP * Math.pow(Constants.SHIELDED_HP_SCALE_FACTOR, worldExponentW3));
                            int scaledShieldHp = (int) (Constants.SHIELDED_BASE_SHIELD_HP * Math.pow(Constants.SHIELDED_SHIELD_HP_SCALE_FACTOR, worldExponentW3));
                            double scaledSpeed = Constants.SHIELDED_BASE_SPEED * Math.pow(Constants.SHIELDED_SPEED_SCALE_FACTOR, worldExponentW3);
                            
                            enemyImg = assetLoader.getImage(Constants.SHIELDED_ASSET_KEY_PLACEHOLDER);

                            if (enemyImg != null) {
                                enemy = new ShieldedEnemy(spawnX, spawnY, Constants.ENEMY_WIDTH, Constants.ENEMY_HEIGHT,
                                                          enemyImg, scaledHp, scaledShieldHp, scaledSpeed);
                            } else {
                                System.err.println("Warning: Missing assets for ShieldedEnemy (World " + stageNum + "). Spawning fallback.");
                            }
                        } 
                        // Spawn Weaver (30% chance: 3, 4, 5) - Replace Lunger
                        else if (randomTypeW3 < 6) { 
                             int scaledHp = (int) (Constants.WEAVER_BASE_HP * Math.pow(Constants.WEAVER_HP_SCALE_FACTOR, worldExponentW3));
                            double scaledVSpeed = Constants.WEAVER_BASE_VERTICAL_SPEED * Math.pow(Constants.WEAVER_SPEED_SCALE_FACTOR, worldExponentW3);
                            double amplitude = Constants.WEAVER_HORIZONTAL_AMPLITUDE;
                            double frequency = Constants.WEAVER_HORIZONTAL_FREQUENCY;

                            enemyImg = assetLoader.getImage(Constants.WEAVER_ASSET_KEY_PLACEHOLDER); 

                            if (enemyImg != null) {
                                enemy = new WeaverEnemy(spawnX, spawnY, Constants.ENEMY_WIDTH, Constants.ENEMY_HEIGHT, 
                                                        enemyImg, scaledHp, scaledVSpeed, amplitude, frequency);
                            } else {
                                System.err.println("Warning: Missing assets for WeaverEnemy (World " + stageNum + "). Spawning fallback.");
                            }
                        }
                        // Spawn EnemyWType1 (30% chance: 6, 7, 8)
                        else if (randomTypeW3 < 9) { 
                            int scaledHp = (int) (Constants.ENEMY_W_TYPE1_BASE_HP * Math.pow(Constants.ENEMY_W_TYPE1_HP_SCALE_FACTOR, worldExponentW3));
                            double scaledSpeed = Constants.ENEMY_W_TYPE1_BASE_SPEED * Math.pow(Constants.ENEMY_W_TYPE1_SPEED_SCALE_FACTOR, worldExponentW3);
                            long baseFireDelay = Constants.ENEMY_W_TYPE1_BASE_FIRE_DELAY;
                            int shotsToFire = 1; // World 3 fires 1 shot

                            String actualAssetKey = Constants.ENEMY_W3_TYPE1_IMG_KEY; // W3 key
                            enemyImg = assetLoader.getImage(actualAssetKey);

                            if (enemyImg != null && bulletImg != null) {
                                enemy = new EnemyWType1(spawnX, spawnY, Constants.ENEMY_WIDTH, Constants.ENEMY_HEIGHT,
                                                        enemyImg, scaledHp, scaledSpeed, baseFireDelay, gameState, bulletImg, shotsToFire); // Pass shotsToFire
                            } else {
                                System.err.println("Warning: Missing assets for EnemyWType1 (World " + stageNum + "). Spawning fallback.");
                            }
                        }
                        // Spawn Placeholders (10% chance: 9)
                        else { 
                            String[] keys = {Constants.ENEMY_W3_TYPE2_IMG_KEY, Constants.ENEMY_W3_TYPE4_IMG_KEY, Constants.ENEMY_W3_TYPE5_IMG_KEY }; // Use non-shielded W3 placeholders
                            baseHealth = 3; int speedBonus = 2;
                            enemyImg = assetLoader.getImage(keys[random.nextInt(keys.length)]);
                            if (enemyImg != null) {
                                enemy = new Enemy(spawnX, spawnY, Constants.ENEMY_WIDTH, Constants.ENEMY_HEIGHT, enemyImg, Constants.ENEMY_BASE_VELOCITY_X + speedBonus, baseHealth);
                            } else {
                                System.err.println("Warning: Missing placeholder assets (World " + stageNum + "). Spawning fallback.");
                            }
                        }
                        // Fallback if any enemy creation above failed
                        if (enemy == null) { enemy = createFallbackEnemy(spawnX, spawnY); }
                        break;
                    case 4: 
                        int randomTypeW4 = random.nextInt(10); 
                        int worldExponentW4 = stageNum - 1; 
                        enemy = null; 

                        // Spawn Weaver (30% chance: 0, 1, 2) - Replace Lunger
                        if (randomTypeW4 < 3) { 
                             int scaledHp = (int) (Constants.WEAVER_BASE_HP * Math.pow(Constants.WEAVER_HP_SCALE_FACTOR, worldExponentW4));
                            double scaledVSpeed = Constants.WEAVER_BASE_VERTICAL_SPEED * Math.pow(Constants.WEAVER_SPEED_SCALE_FACTOR, worldExponentW4);
                            double amplitude = Constants.WEAVER_HORIZONTAL_AMPLITUDE;
                            double frequency = Constants.WEAVER_HORIZONTAL_FREQUENCY;

                            enemyImg = assetLoader.getImage(Constants.WEAVER_ASSET_KEY_PLACEHOLDER); 

                            if (enemyImg != null) {
                                enemy = new WeaverEnemy(spawnX, spawnY, Constants.ENEMY_WIDTH, Constants.ENEMY_HEIGHT, 
                                                        enemyImg, scaledHp, scaledVSpeed, amplitude, frequency);
                            } else {
                                System.err.println("Warning: Missing assets for WeaverEnemy (World " + stageNum + "). Spawning fallback.");
                            }
                        } 
                        // Spawn EnemyWType1 (40% chance: 3, 4, 5, 6)
                        else if (randomTypeW4 < 7) { 
                            int scaledHp = (int) (Constants.ENEMY_W_TYPE1_BASE_HP * Math.pow(Constants.ENEMY_W_TYPE1_HP_SCALE_FACTOR, worldExponentW4));
                            double scaledSpeed = Constants.ENEMY_W_TYPE1_BASE_SPEED * Math.pow(Constants.ENEMY_W_TYPE1_SPEED_SCALE_FACTOR, worldExponentW4);
                            long baseFireDelay = Constants.ENEMY_W_TYPE1_BASE_FIRE_DELAY;
                            int shotsToFire = 3; // World 4 fires 3 shots

                            String actualAssetKey = Constants.ENEMY_W4_TYPE1_IMG_KEY; // W4 key
                            enemyImg = assetLoader.getImage(actualAssetKey);
                            
                            if (enemyImg != null && bulletImg != null) {
                                enemy = new EnemyWType1(spawnX, spawnY, Constants.ENEMY_WIDTH, Constants.ENEMY_HEIGHT, 
                                                        enemyImg, scaledHp, scaledSpeed, baseFireDelay, gameState, bulletImg, shotsToFire); // Pass shotsToFire
                            } else {
                                System.err.println("Warning: Missing assets for EnemyWType1 (World " + stageNum + "). Spawning fallback.");
                            }
                        } 
                        // Spawn Placeholders (30% chance: 7, 8, 9)
                        else { 
                            String[] keys;
                            int speedBonus;
                            keys = new String[]{Constants.ENEMY_W4_TYPE2_IMG_KEY, Constants.ENEMY_W4_TYPE3_IMG_KEY, Constants.ENEMY_W4_TYPE5_IMG_KEY }; // Avoid W4_Type4 placeholder
                            baseHealth = 4; speedBonus = 3; 
                            enemyImg = assetLoader.getImage(keys[random.nextInt(keys.length)]);
                     if (enemyImg != null) {
                                enemy = new Enemy(spawnX, spawnY, Constants.ENEMY_WIDTH, Constants.ENEMY_HEIGHT, enemyImg, Constants.ENEMY_BASE_VELOCITY_X + speedBonus, baseHealth); 
                     } else {
                                System.err.println("Warning: Missing placeholder assets (World " + stageNum + "). Spawning fallback.");
                            }
                        }
                        // Fallback if any enemy creation above failed
                        if (enemy == null) { enemy = createFallbackEnemy(spawnX, spawnY); }
                        break; 
                    default: // Fallback for any unexpected stage number
                         enemy = createFallbackEnemy(spawnX, spawnY); 
                        break;
                }
                
                // The original fallback logic is mostly covered now, but keep a final check
                if (enemy == null) { 
                     System.err.println("CRITICAL: Enemy object is null after switch. Spawning fallback.");
                     enemy = createFallbackEnemy(spawnX, spawnY);
                     if (enemy == null) { // If even fallback fails
                        System.err.println("CRITICAL: Fallback enemy creation failed. Skipping spawn.");
                         continue; 
                     }
                }

                gameState.enemyArray.add(enemy);
            }
        }
        gameState.enemyCount = gameState.enemyArray.size();
        System.out.println("Created " + gameState.enemyCount + " enemies for wave " + currentWave + " of Stage " + stageNum);
    }

    // Helper method for creating fallback enemy to reduce code duplication
    private Enemy createFallbackEnemy(int spawnX, int spawnY) {
        Image fallbackImg = assetLoader.getRandomEnemyImage(); // Use W1 random image as fallback
        if (fallbackImg != null) {
            return new Enemy(spawnX, spawnY, Constants.ENEMY_WIDTH, Constants.ENEMY_HEIGHT, fallbackImg, Constants.ENEMY_BASE_VELOCITY_X, 1);
        } else {
            return null; // Indicate failure to create even fallback
        }
    }

    public void createMiniboss(){
        if (gameState == null || gameState.enemyArray == null || assetLoader == null || gameState.currentStage == null) return;
        int stageNum = gameState.currentStage.getStageNumber();
        gameState.enemyArray.clear();
        Enemy miniboss = null;

        switch (stageNum) {
             case 1: // World 1 Miniboss (Original)
                Image minibossImg1 = assetLoader.getImage(Constants.MINIBOSS_IMG_KEY);
                miniboss = new Miniboss( 
                    Constants.BOARD_WIDTH/2 - Constants.TILE_SIZE * 2, Constants.TILE_SIZE,
                    Constants.TILE_SIZE*4, Constants.TILE_SIZE*4, minibossImg1, Constants.ENEMY_BASE_VELOCITY_X,
                    minibossImg1, 50, 100, 2, "Quantum Anomaly" );
                break;
             case 2: // World 2 Miniboss (Guardian Spawn)
                 Image minibossImg2 = assetLoader.getImage(Constants.GUARDIAN_SPAWN_IMG_KEY);
                 if (minibossImg2 != null) {
                     // Use new GuardianSpawn class
                     miniboss = new GuardianSpawn( 
                         Constants.BOARD_WIDTH/2 - Constants.TILE_SIZE * 2, Constants.TILE_SIZE,
                         Constants.TILE_SIZE*4, Constants.TILE_SIZE*4, minibossImg2, Constants.ENEMY_BASE_VELOCITY_X,
                         minibossImg2, 75, 90, 3, "Guardian Spawn" ); // Pass stats to new constructor
                 }
                break;
            // Case 3 & 4: No miniboss specified, fallback handled later
            default:
                System.out.println("No specific miniboss defined for stage " + stageNum + ". Attempting fallback.");
                break; 
        }

        // --- Fallback Logic --- 
        if (miniboss == null) {
             System.err.println("Warning: Failed to create intended miniboss for stage " + stageNum + ". Falling back to Stage 1 Miniboss.");
            Image fallbackImg = assetLoader.getImage(Constants.MINIBOSS_IMG_KEY);
            if (fallbackImg != null) {
                // Ensure fallback call matches Miniboss constructor
                miniboss = new Miniboss(
                    Constants.BOARD_WIDTH/2 - Constants.TILE_SIZE * 2, Constants.TILE_SIZE,
                    Constants.TILE_SIZE*4, Constants.TILE_SIZE*4, fallbackImg, Constants.ENEMY_BASE_VELOCITY_X,
                    fallbackImg, 50, 100, 2, "Quantum Anomaly (Fallback)" );
            }
        }
        // --------------------

        if (miniboss != null) {
            gameState.enemyArray.add(miniboss);
            gameState.enemyCount = 1;
            System.out.println("Created Miniboss for Stage " + stageNum + ": " + ((SpecialEnemy)miniboss).getEnemyBossName());
        } else {
             System.err.println("Warning: Failed to create miniboss for stage " + stageNum);
             gameState.enemyCount = 0; // Ensure count is 0 if creation failed
        }
    }

    public void createMainboss(){
        if (gameState == null || gameState.enemyArray == null || assetLoader == null || gameState.currentStage == null) return;
         int stageNum = gameState.currentStage.getStageNumber();
        gameState.enemyArray.clear();
        Enemy boss = null;
        Image bossImg;

        switch (stageNum) {
            case 1: // World 1 Main Boss (Original)
                 bossImg = assetLoader.getImage(Constants.MAINBOSS_IMG_KEY);
                boss = new MainBoss( 
                        Constants.BOARD_WIDTH/2 - Constants.TILE_SIZE * 4, Constants.TILE_SIZE,
                        Constants.TILE_SIZE*8, Constants.TILE_SIZE*8, bossImg, Constants.ENEMY_BASE_VELOCITY_X,
                        bossImg, 100, 75, 2, "The Collector" );
                break;
             case 2: // World 2 Boss (Guardian Construct)
                bossImg = assetLoader.getImage(Constants.GUARDIAN_CONSTRUCT_IMG_KEY);
                 if (bossImg != null) {
                     // TODO: Create GuardianConstruct class extending MainBoss if specific behavior needed, otherwise use MainBoss
                    boss = new MainBoss( 
                            Constants.BOARD_WIDTH/2 - Constants.TILE_SIZE * 4, Constants.TILE_SIZE,
                            Constants.TILE_SIZE*8, Constants.TILE_SIZE*8, bossImg, Constants.ENEMY_BASE_VELOCITY_X,
                            bossImg, 150, 60, 3, "Guardian Construct" ); // Ensure constructor match
                 }
                 break;
             case 3: // World 3 Boss (Paradox Entity)
                 bossImg = assetLoader.getImage(Constants.PARADOX_ENTITY_IMG_KEY);
                 if (bossImg != null) {
                    // Use new ParadoxEntity class
                    boss = new ParadoxEntity( 
                            Constants.BOARD_WIDTH/2 - Constants.TILE_SIZE * 5, Constants.TILE_SIZE, 
                            Constants.TILE_SIZE*10, Constants.TILE_SIZE*10, bossImg, Constants.ENEMY_BASE_VELOCITY_X -1, 
                            bossImg, 200, 50, 3, "Paradox Entity" ); // Pass stats to new constructor
                 }
                 break;
             case 4: // World 4 Boss (Temple Guardian - FINAL)
                 bossImg = assetLoader.getImage(Constants.TEMPLE_GUARDIAN_IMG_KEY);
                  if (bossImg != null) {
                    // Use new TempleGuardian class
                    boss = new TempleGuardian( 
                            Constants.BOARD_WIDTH/2 - Constants.TILE_SIZE * 6, Constants.TILE_SIZE, 
                            Constants.TILE_SIZE*12, Constants.TILE_SIZE*12, bossImg, Constants.ENEMY_BASE_VELOCITY_X, 
                            bossImg, 250, 40, 4, "Temple Guardian" ); // Pass stats to new constructor
                 }
                 break;
             default:
                System.err.println("Warning: Tried to create main boss for invalid stage " + stageNum);
                return; 
        }

        // --- Fallback Logic --- 
        if (boss == null) {
            System.err.println("Warning: Failed to create intended main boss for stage " + stageNum + ". Falling back to Stage 1 MainBoss.");
            Image fallbackImg = assetLoader.getImage(Constants.MAINBOSS_IMG_KEY);
            if (fallbackImg != null) {
                // Ensure fallback call matches MainBoss constructor
                boss = new MainBoss( 
                    Constants.BOARD_WIDTH/2 - Constants.TILE_SIZE * 4, Constants.TILE_SIZE,
                    Constants.TILE_SIZE*8, Constants.TILE_SIZE*8, fallbackImg, Constants.ENEMY_BASE_VELOCITY_X,
                    fallbackImg, 100, 75, 2, "The Collector (Fallback)" );
            }
        }
        // --------------------

        if (boss != null) {
             gameState.enemyArray.add(boss);
            gameState.enemyCount = 1;
             System.out.println("Created Main Boss for Stage " + stageNum + ": " + ((SpecialEnemy)boss).getEnemyBossName());
        } else {
             System.err.println("Warning: Failed to create main boss for stage " + stageNum);
             gameState.enemyCount = 0; // Ensure count is 0 if creation failed
        }
    }
    
    // --- Player Actions (Called by EquinoxGameLogic via InputHandler) ---
    // New method to handle player shooting based on input state and cooldown
    private void handlePlayerShooting() {
        if (gameState == null || gameState.ship == null || !gameState.ship.isAlive()) return;

        if (gameState.isFiring && gameState.remainingBulletCooldown <= 0) {
            createPlayerBullet(); // Fire the bullet
            gameState.lastBulletFireTime = System.currentTimeMillis(); // Record the time
            // The actual remaining cooldown is updated in updateCooldowns() based on this time
        }
    }

    // Renamed from fireBullet and made private - Now handles multi-shot
    private void createPlayerBullet() {
        if (gameState == null || gameState.ship == null || gameState.bulletArray == null || assetLoader == null) return;
        
        // --- DEBUG --- 
        System.out.println("[DEBUG] createPlayerBullet called. damageLevel = " + gameState.damageUpgradeLevel);
        // ------------- 
        
        // Determine number of shots based on damage level
        int numberOfShots = Constants.BASE_SHOTS_PER_FIRE;
        if (gameState.damageUpgradeLevel >= Constants.DAMAGE_LEVEL_FOR_MULTI_SHOT) {
            numberOfShots = Constants.MULTI_SHOT_ACTIVE_COUNT;
        }
                            
        // --- DEBUG --- 
        System.out.println("[DEBUG] Calculated numberOfShots = " + numberOfShots);
        // ------------- 
        
        Image bulletImage = assetLoader.getImage(Constants.PLAYER_LASER_IMG_KEY);
        if (bulletImage == null) { 
            System.err.println("Warning: Player bullet image not loaded!");
            return; 
        }

        // Ship center X for reference
        int shipCenterX = gameState.ship.getX() + gameState.ship.getWidth() / 2;
        int baseY = gameState.ship.getY();
        int velocityX = 0; // Parallel shots have no X velocity
        int velocityY = Constants.BULLET_VELOCITY_Y;
        
        // Remove angle calculation logic
        // double totalSpreadAngle = (numberOfShots - 1) * Constants.MULTI_SHOT_SPREAD_ANGLE_DEGREES; 
        // double startAngleRad = Math.toRadians(-90 - totalSpreadAngle / 2.0); 
        // double angleStepRad = Math.toRadians(Constants.MULTI_SHOT_SPREAD_ANGLE_DEGREES);
        // double baseSpeed = Math.abs(baseVelocityY);

        for (int i = 0; i < numberOfShots; i++) {
            int spawnX; 
            if (numberOfShots == 1) {
                // Single shot: spawn at center
                spawnX = shipCenterX - Constants.BULLET_WIDTH / 2;
            } else { // numberOfShots == 2 (or more, though currently capped at 2)
                // Multi-shot: Offset from center
                // Shot 0 (left): Center - Offset
                // Shot 1 (right): Center + Offset - BulletWidth (to align right edge with offset point)
                // Simpler: Spawn relative to center, adjusted by half the offset
                int offset = Constants.MULTI_SHOT_PARALLEL_OFFSET;
                spawnX = shipCenterX + (i == 0 ? -offset : offset) - Constants.BULLET_WIDTH / 2 ;
                // --- DEBUG --- 
                System.out.println("[DEBUG]   Multi-shot Iteration: " + i + " | SpawnX: " + spawnX);
                // ------------- 
            }
            
            // Remove velocity calculations based on angle
            // int bulletVelocityX = 0;
            // int bulletVelocityY = baseVelocityY;
            // if (numberOfShots > 1) { ... }

            Bullet bullet = new Bullet(
                spawnX, // Use calculated spawnX
                baseY,
                Constants.BULLET_WIDTH,
                Constants.BULLET_HEIGHT,
                bulletImage,
                velocityX, // Always 0 for parallel
                velocityY  // Always base Y velocity
            );
            gameState.bulletArray.add(bullet);
        }
    }

    public void fireWaveBlast() {
        if (gameState == null || gameState.ship == null || gameState.waveBlastArray == null) return;
        long currentTime = System.currentTimeMillis();
        
        // Recalculate effective cooldown here too, in case upgrades change mid-game (unlikely but safer)
        double reductionPerLevelQE = 0.10; 
        double maxReduction = 0.60; 
        double currentReductionQE = Math.min(maxReduction, gameState.cooldownQEUpgradeLevel * reductionPerLevelQE);
        long effectiveCooldownQ = (long) (Constants.WAVE_BLAST_COOLDOWN_MS * (1.0 - currentReductionQE));
        
        if (gameState.remainingWaveBlastCooldown <= 0 || gameState.cheatsEnabled) {
            WaveBlast waveBlast = new WaveBlast(
                gameState.ship.getX() + gameState.ship.getWidth() / 2 - Constants.WAVE_BLAST_WIDTH / 2,
                gameState.ship.getY() - Constants.WAVE_BLAST_HEIGHT,
                Constants.WAVE_BLAST_WIDTH,
                Constants.WAVE_BLAST_HEIGHT,
                null);
            gameState.waveBlastArray.add(waveBlast);
            gameState.lastWaveBlastUseTime = currentTime;
            if (!gameState.cheatsEnabled) {
                // Use effective cooldown when resetting
                gameState.remainingWaveBlastCooldown = effectiveCooldownQ;
            }
        } else {
            System.out.println("Wave Blast (Q) on cooldown! " + String.format("%.1f", (double) gameState.remainingWaveBlastCooldown / 1000) + "s");
        }
    }

    public void fireLaserBeam() {
         if (gameState == null || gameState.ship == null || gameState.laserBeamArray == null) return;
         long currentTime = System.currentTimeMillis();

         // Recalculate effective cooldown E
         double reductionPerLevelQE = 0.10; 
         double maxReduction = 0.60; 
         double currentReductionQE = Math.min(maxReduction, gameState.cooldownQEUpgradeLevel * reductionPerLevelQE);
         long effectiveCooldownE = (long) (Constants.LASER_BEAM_COOLDOWN_MS * (1.0 - currentReductionQE));

        if (gameState.remainingLaserBeamCooldown <= 0 || gameState.cheatsEnabled) {
            int numBeams = Constants.BOARD_WIDTH / Constants.TILE_SIZE;
            int startX = 0;
            for (int i = 0; i < numBeams; i++) {
                LaserBeam laserBeam = new LaserBeam(
                    startX + i * Constants.TILE_SIZE + (Constants.TILE_SIZE / 2) - (Constants.LASER_BEAM_WIDTH / 2),
                    gameState.ship.getY(),
                    Constants.LASER_BEAM_WIDTH,
                    Constants.LASER_BEAM_HEIGHT,
                    null);
                gameState.laserBeamArray.add(laserBeam);
            }
            gameState.lastLaserBeamUseTime = currentTime;
            if (!gameState.cheatsEnabled) {
                // Use effective cooldown when resetting
                gameState.remainingLaserBeamCooldown = effectiveCooldownE;
            }
        } else {
            System.out.println("Laser Beam (E) on cooldown! " + String.format("%.1f", (double) gameState.remainingLaserBeamCooldown / 1000) + "s");
        }
    }

    public void firePhaseShift() {
        if (gameState == null) return;
        long currentTime = System.currentTimeMillis();

        // Recalculate effective cooldown R
        double reductionPerLevelR = 0.15;  
        double maxReduction = 0.60; 
        double currentReductionR = Math.min(maxReduction, gameState.cooldownRUpgradeLevel * reductionPerLevelR);
        long effectiveCooldownR = (long) (Constants.PHASE_SHIFT_COOLDOWN_MS * (1.0 - currentReductionR));

        if ((gameState.remainingPhaseShiftCooldown <= 0 || gameState.cheatsEnabled) && !gameState.isPhaseShiftActive) {
            gameState.isPhaseShiftActive = true;
            gameState.lastPhaseShiftUseTime = currentTime;
            gameState.phaseShiftEndTime = currentTime + Constants.PHASE_SHIFT_DURATION_MS;
            if (!gameState.cheatsEnabled) {
                 // Use effective cooldown when resetting
                gameState.remainingPhaseShiftCooldown = effectiveCooldownR;
            }
            System.out.println("Phase Shift Activated!");
        } else if (gameState.isPhaseShiftActive) {
            System.out.println("Phase Shift already active!");
        } else {
            System.out.println("Phase Shift (R) on cooldown! " + String.format("%.1f", (double) gameState.remainingPhaseShiftCooldown / 1000) + "s");
        }
    }

    // --- Reset Logic ---
    public void resetGameForStart() {
        if (gameState == null || gameState.ship == null) return;
        
        gameState.score = 0;
        gameState.money = Constants.STARTING_MONEY;
        gameState.enemySlain = 0;
        gameState.gameOver = false;
        gameState.ship.resetHealth();
        gameState.ship.setX(Constants.SHIP_START_X);
        gameState.ship.setY(Constants.SHIP_START_Y);
        if (gameState.currentStage != null) {
            gameState.currentStage.setStageNumber(1);
            gameState.currentStage.setCurrentWave(1);
            gameState.currentStage.setSpecialEnemySpawned(false);
        }
        resetEntityLists();
        resetCooldowns();
        createEnemies(); 
    }
    
    private void resetEntityLists(){
        if (gameState == null) return;
        if (gameState.enemyArray != null) gameState.enemyArray.clear();
        if (gameState.bulletArray != null) gameState.bulletArray.clear();
        if (gameState.enemyBulletArray != null) gameState.enemyBulletArray.clear();
        if (gameState.waveBlastArray != null) gameState.waveBlastArray.clear();
        if (gameState.laserBeamArray != null) gameState.laserBeamArray.clear();
        gameState.isPhaseShiftActive = false;
        gameState.phaseShiftEndTime = 0;
    }
    
    private void resetCooldowns() {
        if (gameState == null) return;
         gameState.lastWaveBlastUseTime = 0;
        gameState.lastLaserBeamUseTime = 0;
        gameState.lastPhaseShiftUseTime = 0;
        gameState.remainingWaveBlastCooldown = 0;
        gameState.remainingLaserBeamCooldown = 0;
        gameState.remainingPhaseShiftCooldown = 0;
    }
} 