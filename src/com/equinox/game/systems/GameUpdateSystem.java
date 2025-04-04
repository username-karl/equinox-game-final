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
        handleStageAndWaveLogic();
        updateCooldowns();
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
                int shootChance = 2; // e.g., 2% chance per frame if cooldown is ready
                
                if (enemy instanceof com.equinox.game.entities.enemies.ShootingEnemy) {
                    if (random.nextInt(100) < shootChance) { 
                        ((com.equinox.game.entities.enemies.ShootingEnemy) enemy).shoot(gameState.enemyBulletArray, assetLoader.getImage(Constants.ENEMY_LASER_IMG_KEY),
                                                      Constants.ENEMY_BULLET_WIDTH, Constants.ENEMY_BULLET_HEIGHT);
                    }
                } else if (enemy instanceof com.equinox.game.entities.enemies.Miniboss) {
                     // Bosses handle their own attack patterns/timing
                     ((com.equinox.game.entities.enemies.Miniboss) enemy).executeAttackPattern(gameState.enemyBulletArray, assetLoader);
                 } else if (enemy instanceof com.equinox.game.entities.enemies.MainBoss) {
                     // Bosses handle their own attack patterns/timing
                     ((com.equinox.game.entities.enemies.MainBoss) enemy).executeAttackPattern(gameState, assetLoader); // Needs GameState for player position
                 }
                
                if (enemy.getY() >= gameState.ship.getY()) {
                    // Call the renamed method
                    gameLogic.signalGameOver(); 
                    return; 
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

        if (moveLeft) {
            shipVelocityX = Math.max(shipVelocityX - Constants.SHIP_ACCELERATION, -Constants.SHIP_MAX_SPEED);
        } else if (moveRight) {
            shipVelocityX = Math.min(shipVelocityX + Constants.SHIP_ACCELERATION, Constants.SHIP_MAX_SPEED);
        } else {
            if (shipVelocityX > 0) {
                shipVelocityX = Math.max(0, shipVelocityX - Constants.SHIP_DECELERATION);
            } else if (shipVelocityX < 0) {
                shipVelocityX = Math.min(0, shipVelocityX + Constants.SHIP_DECELERATION);
            }
        }

        int newShipX = gameState.ship.getX() + shipVelocityX;
        gameState.ship.setX(Math.max(0, Math.min(newShipX, Constants.BOARD_WIDTH - gameState.ship.getWidth())));
    }

    // --- Game Logic & State Updates ---
    private void updateCooldowns() {
        if (gameState == null) return;
        long currentTime = System.currentTimeMillis();
        gameState.remainingWaveBlastCooldown = Math.max(0, Constants.WAVE_BLAST_COOLDOWN_MS - (currentTime - gameState.lastWaveBlastUseTime));
        gameState.remainingLaserBeamCooldown = Math.max(0, Constants.LASER_BEAM_COOLDOWN_MS - (currentTime - gameState.lastLaserBeamUseTime));
        gameState.remainingPhaseShiftCooldown = Math.max(0, Constants.PHASE_SHIFT_COOLDOWN_MS - (currentTime - gameState.lastPhaseShiftUseTime));

        if (gameState.isPhaseShiftActive && currentTime >= gameState.phaseShiftEndTime) {
            gameState.isPhaseShiftActive = false;
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
            int finalStage = 3; // Assuming stage 3 is the last one

            // Check if the final wave of the final stage is completed
            if (currentStageNum == finalStage && currentWave == totalWaves) {
                System.out.println("DEBUG: Final wave of final stage cleared! Game Won!");
                // Save score to leaderboard
                LeaderboardManager.addEntryFromGameState(gameState, "Player"); 
                // Trigger Game Over or Victory Screen?
                gameLogic.signalGameOver(); // Or a signalGameWon() if you add one
                return; // Stop further stage/wave processing
            }

            if (currentWave == totalWaves) {
                 System.out.println("DEBUG: End of Stage " + gameState.currentStage.getStageNumber() + ". Moving to next stage cutscene.");
                // Move to the next stage
                gameState.currentStage.setStageNumber(gameState.currentStage.getStageNumber() + 1);
                gameState.currentStage.setCurrentWave(1);
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
         
        if (enemy instanceof SpecialEnemy) {
            SpecialEnemy specialEnemy = (SpecialEnemy) enemy;
            specialEnemy.setHitpoints(specialEnemy.getHitpoints() - 1);
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
        } else {
            enemy.setAlive(false);
            dropLoot(enemy); 
            gameState.score += Constants.SCORE_PER_ENEMY;
            gameState.enemySlain++;
            gameState.enemyCount--;
        }
    }

    // Method for handling loot drops (Example - Needs implementation based on your design)
    public void dropLoot(Enemy enemy) { 
         // This method needs to be implemented
         // Currently GameState calls gameState.dropLoot() which doesn't exist
         // Example: Add a small chance to drop money
         if (random.nextInt(100) < 10) { // 10% chance
            int lootAmount = 10 + random.nextInt(16); // e.g., 10-25 money
            gameState.addMoney(lootAmount);
            System.out.println("Enemy dropped " + lootAmount + " money!");
        }
    }

    // --- Entity Creation --- 
     public void createEnemies() {
        if (gameState == null || gameState.currentStage == null || gameState.enemyArray == null || assetLoader == null) return;
        
        Random random = new Random();
        int currentWave = gameState.currentStage.getCurrentWave();
        int maxEnemyRows = Constants.ENEMY_INITIAL_ROWS + (currentWave / 2);
        int maxEnemyColumns = Constants.ENEMY_INITIAL_COLUMNS + (currentWave);
        maxEnemyRows = Math.min(maxEnemyRows, Constants.ENEMY_MAX_ROWS);
        maxEnemyColumns = Math.min(maxEnemyColumns, Constants.ENEMY_MAX_COLS);

        gameState.enemyArray.clear(); 

        for (int r = 0; r < maxEnemyRows; r++) {
            for (int c = 0; c < maxEnemyColumns; c++) {
                Image enemyImg = assetLoader.getRandomEnemyImage();
                int enemyType = random.nextInt(6); // TODO: Use constants or enum for types
                Enemy enemy;
                int spawnX = Constants.ENEMY_START_X + c * Constants.ENEMY_WIDTH;
                int spawnY = Constants.ENEMY_START_Y + r * Constants.ENEMY_HEIGHT;
                if (enemyType == 3) { // Shooting Enemy
                    enemy = new ShootingEnemy(
                            spawnX, spawnY, Constants.ENEMY_WIDTH, Constants.ENEMY_HEIGHT,
                            enemyImg, Constants.ENEMY_BASE_VELOCITY_X
                    );
                } else { // Fast Enemy (or other types)
                    enemy = new FastEnemy(
                            spawnX, spawnY, Constants.ENEMY_WIDTH, Constants.ENEMY_HEIGHT,
                            enemyImg, Constants.ENEMY_BASE_VELOCITY_X + 3
                    );
                }
                gameState.enemyArray.add(enemy);
            }
        }
        gameState.enemyCount = gameState.enemyArray.size();
        System.out.println("Created " + gameState.enemyCount + " enemies for wave " + currentWave);
    }

    public void createMiniboss(){
        if (gameState == null || gameState.enemyArray == null || assetLoader == null) return;
        gameState.enemyArray.clear();
        Image minibossImg = assetLoader.getImage(Constants.MINIBOSS_IMG_KEY);
        com.equinox.game.entities.enemies.Miniboss miniboss = new com.equinox.game.entities.enemies.Miniboss(
                Constants.BOARD_WIDTH/2 - Constants.TILE_SIZE * 2, Constants.TILE_SIZE,
                Constants.TILE_SIZE*4, Constants.TILE_SIZE*4, minibossImg, Constants.ENEMY_BASE_VELOCITY_X,
                minibossImg, 50, 100, 2, "Quantum Anomaly" );
        gameState.enemyArray.add(miniboss);
        gameState.enemyCount = 1;
        System.out.println("Created Miniboss: " + miniboss.getEnemyBossName());
    }

    public void createMainboss(){
        if (gameState == null || gameState.enemyArray == null || assetLoader == null) return;
        gameState.enemyArray.clear();
        Image mainbossImg = assetLoader.getImage(Constants.MAINBOSS_IMG_KEY);
        com.equinox.game.entities.enemies.MainBoss mainboss = new com.equinox.game.entities.enemies.MainBoss(
                Constants.BOARD_WIDTH/2 - Constants.TILE_SIZE * 4, Constants.TILE_SIZE,
                Constants.TILE_SIZE*8, Constants.TILE_SIZE*8, mainbossImg, Constants.ENEMY_BASE_VELOCITY_X,
                mainbossImg, 100, 75, 2, "The Collector" );
        gameState.enemyArray.add(mainboss);
        gameState.enemyCount = 1;
        System.out.println("Created Main Boss: " + mainboss.getEnemyBossName());
    }
    
    // --- Player Actions (Called by EquinoxGameLogic via InputHandler) ---
    public void fireBullet() {
        if (gameState == null || gameState.ship == null || gameState.bulletArray == null || assetLoader == null) return;
         com.equinox.game.entities.Bullet bullet = new com.equinox.game.entities.Bullet(
            gameState.ship.getX() + gameState.ship.getWidth() / 2 - Constants.BULLET_WIDTH / 2,
            gameState.ship.getY(),
            Constants.BULLET_WIDTH,
            Constants.BULLET_HEIGHT,
            assetLoader.getImage(Constants.PLAYER_LASER_IMG_KEY),
            0, // velocityX
            Constants.BULLET_VELOCITY_Y // velocityY
            );
        gameState.bulletArray.add(bullet);
    }

    public void fireWaveBlast() {
        if (gameState == null || gameState.ship == null || gameState.waveBlastArray == null) return;
        long currentTime = System.currentTimeMillis();
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
                gameState.remainingWaveBlastCooldown = Constants.WAVE_BLAST_COOLDOWN_MS;
            }
        } else {
            System.out.println("Wave Blast (Q) on cooldown! " + String.format("%.1f", (double) gameState.remainingWaveBlastCooldown / 1000) + "s");
        }
    }

    public void fireLaserBeam() {
         if (gameState == null || gameState.ship == null || gameState.laserBeamArray == null) return;
         long currentTime = System.currentTimeMillis();
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
                gameState.remainingLaserBeamCooldown = Constants.LASER_BEAM_COOLDOWN_MS;
            }
        } else {
            System.out.println("Laser Beam (E) on cooldown! " + String.format("%.1f", (double) gameState.remainingLaserBeamCooldown / 1000) + "s");
        }
    }

    public void firePhaseShift() {
        if (gameState == null) return;
        long currentTime = System.currentTimeMillis();
        if ((gameState.remainingPhaseShiftCooldown <= 0 || gameState.cheatsEnabled) && !gameState.isPhaseShiftActive) {
            gameState.isPhaseShiftActive = true;
            gameState.lastPhaseShiftUseTime = currentTime;
            gameState.phaseShiftEndTime = currentTime + Constants.PHASE_SHIFT_DURATION_MS;
            if (!gameState.cheatsEnabled) {
                gameState.remainingPhaseShiftCooldown = Constants.PHASE_SHIFT_COOLDOWN_MS;
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