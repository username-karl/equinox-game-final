package com.equinox.game.ui;

import com.equinox.game.data.GameState;
import com.equinox.game.data.Stage;
import com.equinox.game.entities.Entity;
import com.equinox.game.entities.ShipUser;
import com.equinox.game.entities.Bullet;
import com.equinox.game.entities.EnemyBullet;
import com.equinox.game.entities.WaveBlast;
import com.equinox.game.entities.LaserBeam;
import com.equinox.game.entities.enemies.Enemy;
import com.equinox.game.entities.enemies.FastEnemy;
import com.equinox.game.entities.enemies.ShootingEnemy;
import com.equinox.game.entities.enemies.SpecialEnemy;
import com.equinox.game.entities.enemies.Miniboss;
import com.equinox.game.entities.enemies.MainBoss;

import com.equinox.game.systems.StageManager;
import com.equinox.game.systems.InputHandler;
import com.equinox.game.systems.CollisionSystem;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;
import java.util.Random;
import javax.swing.*;
import java.util.Map;
import java.util.HashMap;

public class EquinoxGameLogic extends JPanel implements ActionListener {

    // Game States Enum
    public enum GameStateEnum {
        MENU,
        PLAYING,
        CUTSCENE, // Placeholder for potential future use
        GAME_OVER
    }
    private GameStateEnum currentState;

    // Menu State Variables
    private int selectedMenuItem = 0;
    private String[] menuItems = {"Start Game", "Settings", "Credits", "Cheats", "Exit"}; // Added new items

    // Cheats State
    private boolean cheatsEnabled = false;

    // INIT
    private GameState gameState;


    // BOARD
    int tileSize = 32;
    int rows = 24;
    int columns = 24;
    int boardWidth = tileSize * columns;
    int boardHeight = tileSize * rows;

    // Image
    Image shipImg;
    Image enemyImgVar1;
    Image enemyImgVar2;
    Image enemyImgVar3;
    Image world1BG;
    Image laserBlue;
    Image enemyBulletImg;

    Image mainbossImgvar1;
    Image minibossImgvar1;
    ArrayList<Image> enemyImgArray;
    ArrayList<Image> specialEnemyImgArray;
    // Game Settings

    // Backgrounds Map
    Map<Integer, Image> backgroundImages;

    long remainingWaveBlastCooldown;
    long remainingLaserBeamCooldown;
    long remainingPhaseShiftCooldown; // Added for Phase Shift (R)

    // Ship
    int shipWidth = tileSize * 2; // 64px
    int shipHeight = tileSize; // 32px
    int shipX = tileSize * columns / 2 - tileSize;
    int shipY = boardHeight - tileSize * 2;
    int shipVelocityX = 0; // Initial velocity
    int shipAcceleration = 2; // Acceleration rate
    int shipMaxSpeed = 8; // Maximum speed
    int shipDeceleration = 1; // Deceleration rate

    ShipUser ship;

    // Enemies
    ArrayList<Enemy> enemyArray;
    int enemyWidth = tileSize * 2;
    int enemyHeight = tileSize;
    int enemyX = 0;     //X START SPAWN
    int enemyY = 0;     //Y START SPAWN
    int enemyVelocityX = 1;
    int enemyRows = 5;
    int enemyColumns = 5;

    // Bullets
    ArrayList<Bullet> bulletArray;
    int bulletWidth = tileSize / 8; // Bullet size width
    int bulletHeight = tileSize / 2;
    int bulletVelocityY = -20; // Bullet movespeed
    // WaveBlast (Q)
    ArrayList<WaveBlast> waveBlastArray;
    int waveBlastWidth = tileSize / 4; // Renamed from tacticalqWidth
    int waveBlastHeight = tileSize * 10; // Renamed from tacticalqHeight
    int waveBlastVelocityY = -100; // Renamed from tacticalqVelocityY
    long lastWaveBlastUseTime; // Renamed from lastTacticalQUseTime
    long waveBlastCooldown = 2500; // Renamed from tacticalQCooldown
    //LaserBeam (E)
    ArrayList<LaserBeam> laserBeamArray;
    int laserBeamWidth = tileSize; // Renamed from tacticaleWidth
    int laserBeamHeight = tileSize / 8; // Renamed from tacticaleHeight
    int laserBeamVelocityY = -15; // Renamed from tacticaleVelocityY
    long lastLaserBeamUseTime; // Renamed from lastTacticalEUseTime
    long laserBeamCooldown = 1000; // Give it a 1-second cooldown (was 0)
    // Phase Shift (R)
    boolean isPhaseShiftActive = false;
    long phaseShiftEndTime = 0;
    long phaseShiftDuration = 1500; // 1.5 seconds duration
    long lastPhaseShiftUseTime = 0;
    long phaseShiftCooldown = 10000; // 10 seconds cooldown
    //Enemy Bullets
    ArrayList<EnemyBullet> enemyBulletArray;
    int enemyBulletWidth = tileSize / 8;
    int enemyBulletHeight = tileSize / 2;

    // Timer
    Timer gameLoop;

    // Input Handler instance
    private InputHandler inputHandler;
    // Collision System instance
    private CollisionSystem collisionSystem;

    //STAGE DOMAIN
    private StageManager stageManager;

    
    //MAIN EQUINOX GAME CONSTRUCTOR
    //ALL THE LOGIC HERE IS CONTAINED
    public EquinoxGameLogic() { // Made public
        //SetFrame
        setPreferredSize(new Dimension(boardWidth, boardHeight));
        setBackground(Color.DARK_GRAY);

        //GameState init
        gameState = new GameState();
        // Access Stage via gameState instance
        gameState.currentStage = new Stage(1,7);

        // Create and set InputHandler
        inputHandler = new InputHandler(this);
        addKeyListener(inputHandler); // Add handler as listener

        // Create CollisionSystem
        collisionSystem = new CollisionSystem(this);
        
        setFocusable(true);
        currentState = GameStateEnum.MENU; // Start in Menu state

        // Image loading - Use absolute paths from classpath root
        shipImg = new ImageIcon(getClass().getResource("/assets/player_captainnova_portrait.png")).getImage();
        enemyImgVar1 = new ImageIcon(getClass().getResource("/assets/enemy_drone_animated.gif")).getImage();
        enemyImgVar2 = new ImageIcon(getClass().getResource("/assets/enemy_alien_type2.png")).getImage();
        enemyImgVar3 = new ImageIcon(getClass().getResource("/assets/enemy_alien_type3.png")).getImage();


        enemyImgArray = new ArrayList<Image>();
        enemyImgArray.add(enemyImgVar1);
        enemyImgArray.add(enemyImgVar2);
        enemyImgArray.add(enemyImgVar3);


        minibossImgvar1 = new ImageIcon(getClass().getResource("/assets/boss_mini_alien.png")).getImage();
        mainbossImgvar1 = new ImageIcon(getClass().getResource("/assets/boss_main_alien.png")).getImage();
        specialEnemyImgArray = new ArrayList<Image>();
        specialEnemyImgArray.add(minibossImgvar1);
        specialEnemyImgArray.add(mainbossImgvar1);
        

        // //Maps - Load backgrounds for all stages
        // world1BG = new ImageIcon(getClass().getResource("/assets/bg_nebula.png")).getImage(); // Old single background
        loadBackgroundImages(); // Load backgrounds into the map

        // Misc
        laserBlue = new ImageIcon(getClass().getResource("/assets/weapon_laser_blue.png")).getImage();
        enemyBulletImg = new ImageIcon(getClass().getResource("/assets/weapon_laser_red.png")).getImage();
        // Ship - Add maxHealth parameter
        int initialPlayerHealth = 5; // Example health value
        ship = new ShipUser(shipX, shipY, shipWidth, shipHeight, shipImg, initialPlayerHealth);

        // Enemy Array Group
        enemyArray = new ArrayList<Enemy>();
        enemyBulletArray = new ArrayList<EnemyBullet>();

        // Bullets
        bulletArray = new ArrayList<Bullet>();
        // Ship skills
        waveBlastArray = new ArrayList<WaveBlast>();
        laserBeamArray = new ArrayList<LaserBeam>();

        // Game timer
        gameLoop = new Timer(1000 / 60, this);
        createEnemies();
        gameLoop.start();
    }
    
    //Stage Manager
    public void setStageManager(StageManager stageManager) {
        this.stageManager = stageManager;
    }
    //GAMESTATE GETTER FOR CUTSCENES
    public GameState getGameState() {
        return gameState;
    }

    // Getter for Phase Shift state needed by CollisionSystem
    public boolean isPhaseShiftActive() {
        return isPhaseShiftActive;
    }

    // Draw assets
    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        // Draw background based on current stage
        Image currentBackground = backgroundImages.get(gameState.currentStage.getStageNumber());
        if (currentBackground == null) {
             // Fallback if image for stage not found (or handle error)
             currentBackground = backgroundImages.get(1); // Default to stage 1 or a placeholder
             if(currentBackground == null) { // If even fallback fails
                 g.setColor(Color.DARK_GRAY); // Draw plain background
                 g.fillRect(0, 0, boardWidth, boardHeight);
             }
        }

        if (currentBackground != null) {
            g.drawImage(currentBackground, 0, 0, boardWidth, boardHeight, this);
        }
        // Draw based on current state
        switch (currentState) {
            case MENU:
                drawMainMenu(g);
                break;
            case PLAYING:
            case GAME_OVER: // Use GAME_OVER state for drawing game elements + message
                drawGame(g);
                break;
            // Add cases for CUTSCENE etc. if needed
        }
    }

    // DRAW METHOD
    public void drawGame(Graphics g) {
        drawShip(g);
        drawEnemies(g);
        drawPlayerBullets(g);
        drawTacticalAbilities(g);
        drawEnemyBullets(g);
        drawGameStats(g);
    }
    //DRAW PLAYER SHIP FOR DRAW()
    private void drawShip(Graphics g) {
        Graphics2D g2d = (Graphics2D) g.create(); // Create a copy to not affect other drawings
        if (isPhaseShiftActive) {
            // Make ship semi-transparent during phase shift
            g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.5f));
        }
        g2d.drawImage(ship.getImage(), ship.getX(), ship.getY(), ship.getWidth(), ship.getHeight(), null);
        g2d.dispose(); // Dispose of the graphics copy
    }
    //DRAW ENEMY ARRAY FOR DRAW()
    private void drawEnemies(Graphics g) {
        for (Enemy enemy : enemyArray) {
            if (enemy.isAlive()) {
                if (enemy instanceof Miniboss) {
                    g.drawImage(enemy.getImage(), enemy.getX(), enemy.getY(), enemy.getWidth(), enemy.getHeight(), null);
                    drawBossHealthBar(g, (Miniboss) enemy);
                } else if (enemy instanceof MainBoss) {
                    g.drawImage(enemy.getImage(), enemy.getX(), enemy.getY(), enemy.getWidth(), enemy.getHeight(), null);
                    drawBossHealthBar(g, (MainBoss) enemy);
                } else {
                    g.drawImage(enemy.getImage(), enemy.getX(), enemy.getY(), enemy.getWidth(), enemy.getHeight(), null);
                }
            }
        }
    }
    //DRAW PLAYER BULLET FOR DRAW()
    private void drawPlayerBullets(Graphics g) {
        g.setColor(Color.white);
        for (Bullet bullet : bulletArray) {
            if (!bullet.isUsed()) {
                g.drawImage(laserBlue, bullet.getX(), bullet.getY(), bullet.getWidth(), bullet.getHeight(), null);
            }
        }
    }
    //DRAW TACTICALS FOR DRAW()
    private void drawTacticalAbilities(Graphics g) {
        //WaveBlast (Q)
        g.setColor(Color.white);
        for (WaveBlast waveBlast : waveBlastArray) {
            if (!waveBlast.isUsed()) {
                g.fillRect(waveBlast.getX(), waveBlast.getY(), waveBlast.getWidth(), waveBlast.getHeight());
            }
        }
        //LaserBeam (E)
        for (LaserBeam laserBeam : laserBeamArray) {
            if (!laserBeam.isUsed()) {
                g.fillRect(laserBeam.getX(), laserBeam.getY(), laserBeam.getWidth(), laserBeam.getHeight());
            }
        }
    }
    //DRAW ENEMY BULLETS FOR DRAW()
    private void drawEnemyBullets(Graphics g) {
        for (EnemyBullet bullet : enemyBulletArray) {
            if (!bullet.isUsed()) {
                g.drawImage(enemyBulletImg, bullet.getX(), bullet.getY(), bullet.getWidth(), bullet.getHeight(), null);
            }
        }
    }
    //DRAW GAME STATS FOR DRAW()
    private void drawGameStats(Graphics g) {
        int padding = 10;
        int topY = 25;
        int bottomY = boardHeight - padding - 20; // Position cooldowns near bottom
        Font baseFont = new Font("Arial", Font.PLAIN, 16);
        Font boldFont = new Font("Arial", Font.BOLD, 16);
        Font cooldownFont = new Font("Arial", Font.PLAIN, 14);
        Font stageFont = new Font("Arial", Font.BOLD, 18);

        // --- Top Left: Score, Money, Killed ---
        g.setFont(baseFont);
        g.setColor(Color.LIGHT_GRAY);
        g.drawString("Score: " + gameState.score, padding, topY);
        g.setColor(Color.ORANGE);
        g.drawString("Money: " + gameState.money, padding, topY + 20);
        g.setColor(Color.RED);
        g.drawString("Killed: " + gameState.enemySlain, padding, topY + 40);
        
        // --- Top Center: World / Wave ---
        if (gameState.currentStage != null) {
            g.setFont(stageFont);
            g.setColor(Color.WHITE);
            String stageText = "World: " + gameState.currentStage.getStageNumber() + " | Wave: " + gameState.currentStage.getCurrentWave();
            FontMetrics fmStage = g.getFontMetrics();
            int stageWidth = fmStage.stringWidth(stageText);
            g.drawString(stageText, (boardWidth - stageWidth) / 2, topY + 10); // Center horizontally
        }

        // --- Top Right: Player Health Bar (Enhanced) ---
        if (ship != null) {
            int healthBarWidth = 150;
            int healthBarHeight = 20;
            int healthBarX = boardWidth - healthBarWidth - padding;
            int healthBarY = topY - 5; // Align with other top elements
            double healthPercent = (double) ship.getHealth() / ship.getMaxHealth();
            int filledWidth = (int) (healthBarWidth * healthPercent);

            // Health text (e.g., "HP: 5/5")
            g.setFont(boldFont);
            g.setColor(Color.WHITE);
            String healthText = "HP: " + ship.getHealth() + "/" + ship.getMaxHealth();
            FontMetrics fmHealth = g.getFontMetrics();
            // int healthTextWidth = fmHealth.stringWidth(healthText);
            g.drawString(healthText, healthBarX, healthBarY + healthBarHeight + 15); // Draw below bar

            // Health bar background
            g.setColor(Color.DARK_GRAY);
            g.fillRect(healthBarX, healthBarY, healthBarWidth, healthBarHeight);
            // Health bar fill (Green to Red gradient is complex, using simple Green for now)
            g.setColor(Color.GREEN); 
            g.fillRect(healthBarX, healthBarY, filledWidth, healthBarHeight);
            // Health bar border
            g.setColor(Color.WHITE);
            g.drawRect(healthBarX, healthBarY, healthBarWidth, healthBarHeight);
        }
        
        // --- Bottom Left: Ability Cooldowns ---
        int cooldownX = padding;
        int cooldownY = bottomY;
        g.setFont(cooldownFont);

        // Wave Blast (Q)
        g.setColor(Color.YELLOW);
        String qText;
        if (remainingWaveBlastCooldown > 0) {
            qText = String.format("Q: %.1fs", (double) remainingWaveBlastCooldown / 1000);
        } else {
            qText = "Q: Ready";
            g.setColor(Color.GREEN); // Indicate ready
        }
        g.drawString(qText, cooldownX, cooldownY);

        // Laser Beam (E)
        g.setColor(Color.ORANGE); // Different color
        String eText;
        int eOffset = g.getFontMetrics().stringWidth(qText) + 15; // Position after Q text
        if (remainingLaserBeamCooldown > 0) {
            eText = String.format("E: %.1fs", (double) remainingLaserBeamCooldown / 1000);
        } else {
            eText = "E: Ready";
            g.setColor(Color.GREEN); // Indicate ready
        }
        g.drawString(eText, cooldownX + eOffset, cooldownY);

        // Phase Shift (R)
        g.setColor(Color.CYAN);
        String rText;
        int rOffset = eOffset + g.getFontMetrics().stringWidth(eText) + 15; // Position after E text
        if (isPhaseShiftActive) {
            rText = "R: ACTIVE";
            g.setColor(Color.MAGENTA); // Special color for active
        } else if (remainingPhaseShiftCooldown > 0) {
            rText = String.format("R: %.1fs", (double) remainingPhaseShiftCooldown / 1000);
        } else {
            rText = "R: Ready";
            g.setColor(Color.GREEN); // Indicate ready
        }
        g.drawString(rText, cooldownX + rOffset, cooldownY);

        // --- Cheat Indicator ---
        if (cheatsEnabled) {
            g.setFont(boldFont);
            g.setColor(Color.MAGENTA);
            String cheatText = "CHEATS ACTIVE";
            FontMetrics fmCheat = g.getFontMetrics();
            int cheatWidth = fmCheat.stringWidth(cheatText);
            g.drawString(cheatText, (boardWidth - cheatWidth) / 2, bottomY); // Center bottom
        }

        // --- Game Over Message ---
        if (currentState == GameStateEnum.GAME_OVER) {
            g.setColor(Color.RED);
            g.setFont(new Font("Arial", Font.BOLD, 48));
            FontMetrics fm = g.getFontMetrics();
            String gameOverMsg = "GAME OVER";
            int msgWidth = fm.stringWidth(gameOverMsg);
            g.drawString(gameOverMsg, (boardWidth - msgWidth) / 2, boardHeight / 2 - 30);
            
            g.setFont(new Font("Arial", Font.PLAIN, 24));
            fm = g.getFontMetrics();
            String restartMsg = "Press 'R' to Retry";
            int restartWidth = fm.stringWidth(restartMsg);
            g.drawString(restartMsg, (boardWidth - restartWidth) / 2, boardHeight / 2 + 20);
        }
    }
    //DRAW BOSS HEALTH BARS FOR DRAW()
    private void drawBossHealthBar(Graphics g, Miniboss miniboss) {
        if (miniboss.isAlive()) {
            int healthBarWidth = boardWidth / 2; // Half the width of the board
            int healthBarHeight = 20;
            int healthBarX = boardWidth / 4; // Center the health bar
            int healthBarY = 40; // Position it below the top
            //NAME
            g.setColor(Color.WHITE);
            g.setFont(new Font("Arial", Font.BOLD, 12));
            g.drawString("Miniboss: "+miniboss.getEnemyBossName(), boardWidth/3, 60);
            // Calculate the filled portion of the health bar
            double healthPercentage = (double) miniboss.getHitpoints() / miniboss.getMaxHitpoints();
            int filledWidth = (int) (healthBarWidth * healthPercentage);

            // Draw the background of the health bar
            g.setColor(Color.GRAY);
            g.fillRect(healthBarX, healthBarY, healthBarWidth, healthBarHeight);

            // Draw the filled portion of the health bar
            g.setColor(Color.RED);
            g.fillRect(healthBarX, healthBarY, filledWidth, healthBarHeight);

            // Draw the border of the health bar
            g.setColor(Color.BLACK);
            g.drawRect(healthBarX, healthBarY, healthBarWidth, healthBarHeight);
        }
    }
    private void drawBossHealthBar(Graphics g, MainBoss mainboss) {
        if (mainboss.isAlive()) {
            int healthBarWidth = boardWidth / 2; // Half the width of the board
            int healthBarHeight = 20;
            int healthBarX = boardWidth / 4; // Center the health bar
            int healthBarY = 40; // Position it below the top
            //NAME
            g.setColor(Color.WHITE);
            g.setFont(new Font("Arial", Font.BOLD, 12));
            g.drawString("BOSS: "+mainboss.getEnemyBossName(), boardWidth/3, 60);
            // Calculate the filled portion of the health bar
            double healthPercentage = (double) mainboss.getHitpoints() / mainboss.getMaxHitpoints();
            int filledWidth = (int) (healthBarWidth * healthPercentage);

            // Draw the background of the health bar
            g.setColor(Color.GRAY);
            g.fillRect(healthBarX, healthBarY, healthBarWidth, healthBarHeight);

            // Draw the filled portion of the health bar
            g.setColor(Color.RED);
            g.fillRect(healthBarX, healthBarY, filledWidth, healthBarHeight);

            // Draw the border of the health bar
            g.setColor(Color.BLACK);
            g.drawRect(healthBarX, healthBarY, healthBarWidth, healthBarHeight);
        }
    }
    // MAIN MOVE GAME FUNCTION
    public void moveGame() {
        // Only move game elements if in PLAYING state
        if (currentState == GameStateEnum.PLAYING) {
            moveEnemies();
            moveEnemyBullets();
            movePlayerBullets();
            moveTacticalAbilities();
            // Call CollisionSystem methods
            collisionSystem.checkPlayerBulletCollisions(bulletArray, enemyArray);
            collisionSystem.checkTacticalCollisions(waveBlastArray, laserBeamArray, enemyArray);
            collisionSystem.checkEnemyBulletCollisions(enemyBulletArray, ship);
            clearOffScreenBullets();
            handleStageAndWaveLogic();
            updateCooldowns();
            moveShip();
        }
    }
    
    //GAME LOOP HANDLING
    public void startGameLoop(){
        // gameLoopRunning = true; // Now handled by state
        gameLoop.start(); // Ensure timer is running
    }
    public void stopGameLoop(){
        // gameLoopRunning = false; // Now handled by state
        gameLoop.stop(); // Stop the timer
    }
    
    // MOVE ENEMIES for MOVE GAME FUNCTION
    private void moveEnemies() {
        for (Enemy enemy : enemyArray) {
            if (enemy.isAlive()) {
                int enemyWidthToUse = enemy.getWidth();
                if(enemy instanceof Miniboss){
                    enemyWidthToUse = enemy.getWidth();
                }else if(enemy instanceof MainBoss){
                    enemyWidthToUse = enemyWidth;
                }else{
                    enemyWidthToUse = enemyWidth;
                }
                enemy.move(boardWidth, enemyWidthToUse, enemyHeight);
                if (enemy.isMoveDown()) {
                    enemy.moveDown(enemyHeight);
                }
                if (enemy instanceof ShootingEnemy) {
                    ((ShootingEnemy) enemy).shoot(enemyBulletArray, enemyBulletImg, enemyBulletWidth, enemyBulletHeight);
                }
                if (enemy instanceof Miniboss) {
                    // Only call moveY if not moving down
                    if (!enemy.isMoveDown()) {
                        ((Miniboss) enemy).moveY();
                    }
                    ((Miniboss) enemy).shoot(enemyBulletArray, enemyBulletImg, enemyBulletWidth, enemyBulletHeight);
                }
                if (enemy instanceof MainBoss) {
                    // Only call moveY if not moving down
                    if (!enemy.isMoveDown()) {
                        ((MainBoss) enemy).moveY();
                    }
                    ((MainBoss) enemy).shoot(enemyBulletArray, enemyBulletImg, enemyBulletWidth, enemyBulletHeight);
                }
                // Lose condition
                if (enemy.getY() >= ship.getY()) {
                    currentState = GameStateEnum.GAME_OVER; // Change state on loss
                    gameState.gameOver = true; // Keep flag for potential other uses/checks
                }
            }
        }
    }
    //MOVE ENEMY BULLETS for MOVE GAME FUNCTION
    private void moveEnemyBullets() {
        for (EnemyBullet bullet : enemyBulletArray) {
            bullet.move();
        }
    }
    //MOVE PLAYER BULLETS for MOVE GAME FUNCTION
    private void movePlayerBullets() {
        for (Bullet bullet : bulletArray) {
            bullet.setY(bullet.getY() + bulletVelocityY);
        }
    }
    // MOVE SKILLS for MOVE GAME FUNCTION
    private void moveTacticalAbilities() {
        for (WaveBlast waveBlast : waveBlastArray) {
            waveBlast.setY(waveBlast.getY() + waveBlastVelocityY);
        }
        for (LaserBeam laserBeam : laserBeamArray) {
            laserBeam.setY(laserBeam.getY() + laserBeamVelocityY);
        }
    }
    //ENEMY HIT for MOVE GAME FUNCTION - Make public for CollisionSystem
    public void handleEnemyHit(Enemy enemy) {
        // Check specific types correctly
        if (enemy instanceof SpecialEnemy) {
            SpecialEnemy specialEnemy = (SpecialEnemy) enemy;
            specialEnemy.setHitpoints(specialEnemy.getHitpoints() - 1);
            if (specialEnemy.getHitpoints() <= 0) {
                if(enemy instanceof Miniboss){
                    gameState.score += 10000;   //SCORES
                    gameState.money += 2500;    //MONEY
                    gameState.enemySlain++;     //STAT COUNTER
                }else if(enemy instanceof MainBoss){
                    gameState.score += 20000;   //SCORES
                    gameState.money += 5000;    //MONEY
                    gameState.enemySlain++;     //STAT COUNTER
                }
                enemy.setAlive(false);
                gameState.enemyCount--;
            }
        } else {
            enemy.setAlive(false);
            gameState.dropLoot();       //RANDOM MONEY
            gameState.score += 100;
            gameState.enemySlain++;     //STAT COUNTER
            gameState.enemyCount--;
        }
    }
    //OPTIMIZATIONS for MOVE GAME FUNCTION
    private void clearOffScreenBullets() {
        bulletArray.removeIf(bullet -> bullet.isUsed() || bullet.getY() < 0);
        waveBlastArray.removeIf(waveBlast -> waveBlast.isUsed() || waveBlast.getY() < 0);
        laserBeamArray.removeIf(laserBeam -> laserBeam.isUsed() || laserBeam.getY() < 0);
        enemyBulletArray.removeIf(enemyBullet -> enemyBullet.isUsed() || enemyBullet.getY() > boardHeight);
    }
    //STAGE WAVE LOGIC for MOVE GAME FUNCTION
    private void handleStageAndWaveLogic() {
        // Next wave of enemies
        if (gameState.enemyCount == 0) {
            if (gameState.currentStage.getCurrentWave() == gameState.currentStage.getTotalWaves()) {
                // Move to the next stage
                gameState.currentStage.setStageNumber(gameState.currentStage.getStageNumber() + 1);
                gameState.currentStage.setCurrentWave(1);
                gameState.currentStage.setSpecialEnemySpawned(false);
                reset();
                stageManager.startCutscene();
            } else if (gameState.currentStage.getCurrentWave() == gameState.currentStage.getTotalWaves() - 1) {
                // Spawn the mainboss
                gameState.currentStage.setSpecialEnemySpawned(true);
                createMainboss();
                gameState.currentStage.setCurrentWave(gameState.currentStage.getCurrentWave() + 1);
            } else if (gameState.currentStage.getCurrentWave() == gameState.currentStage.getTotalWaves() - 2) {
                // Spawn the miniboss
                gameState.currentStage.setSpecialEnemySpawned(true);
                createMiniboss();
                gameState.currentStage.setCurrentWave(gameState.currentStage.getCurrentWave() + 1);
            } else {
                // Move to the next wave
                gameState.currentStage.setCurrentWave(gameState.currentStage.getCurrentWave() + 1);
                reset();
            }
        }
    }
    // Cooldowns for MOVE GAME FUNCTION
    private void updateCooldowns() {
        long currentTime = System.currentTimeMillis();
        // Wave Blast Cooldown
        remainingWaveBlastCooldown = waveBlastCooldown - (currentTime - lastWaveBlastUseTime);
        if (remainingWaveBlastCooldown < 0) {
            remainingWaveBlastCooldown = 0;
        }
        // Laser Beam Cooldown
        remainingLaserBeamCooldown = laserBeamCooldown - (currentTime - lastLaserBeamUseTime);
        if (remainingLaserBeamCooldown < 0) {
            remainingLaserBeamCooldown = 0;
        }
        // Phase Shift Cooldown & Active Check
        remainingPhaseShiftCooldown = phaseShiftCooldown - (currentTime - lastPhaseShiftUseTime);
        if (remainingPhaseShiftCooldown < 0) {
            remainingPhaseShiftCooldown = 0;
        }
        // Check if phase shift duration has ended
        if (isPhaseShiftActive && currentTime >= phaseShiftEndTime) {
            isPhaseShiftActive = false;
        }
    }
    // MOVE SHIP for MOVE GAME FUNCTION
    private void moveShip() {
        // Get input state from InputHandler
        boolean moveLeft = inputHandler.isMoveLeft(); 
        boolean moveRight = inputHandler.isMoveRight();

        // Ship movement acceleration/deceleration
        if (moveLeft) {
            shipVelocityX = Math.max(shipVelocityX - shipAcceleration, -shipMaxSpeed);
        } else if (moveRight) {
            shipVelocityX = Math.min(shipVelocityX + shipAcceleration, shipMaxSpeed);
        } else {
            // Deceleration
            if (shipVelocityX > 0) {
                shipVelocityX = Math.max(0, shipVelocityX - shipDeceleration);
            } else if (shipVelocityX < 0) {
                shipVelocityX = Math.min(0, shipVelocityX + shipDeceleration);
            }
        }

        // Move the ship
        int newShipX = ship.getX() + shipVelocityX;
        if (newShipX >= 0 && newShipX + ship.getWidth() <= boardWidth) {
            ship.setX(newShipX);
        }
    }
    //RESET for MOVE GAME FUNCTION
    public void reset(){
        enemyArray.clear();
        bulletArray.clear();
        enemyBulletArray.clear();
        waveBlastArray.clear();
        laserBeamArray.clear();
        // Reset Phase Shift state
        isPhaseShiftActive = false; 
        phaseShiftEndTime = 0;
        createEnemies();
    }
    //CREATING ENEMY INSTANCES
    // Create enemies
    public void createEnemies() {
        Random random = new Random();
        int currentWave = gameState.currentStage.getCurrentWave();
        int maxEnemyRows = enemyRows + (currentWave / 2); // Increase rows every 2 waves
        int maxEnemyColumns = enemyColumns + (currentWave); // Increase columns every 3 waves
        maxEnemyRows = Math.min(maxEnemyRows, 15); // Limit to max 15 rows
        maxEnemyColumns = Math.min(maxEnemyColumns, 11); // Limit to max 15 columns

        for (int r = 0; r < maxEnemyRows; r++) {
            for (int c = 0; c < maxEnemyColumns; c++) {
                int randomImgIndex = random.nextInt(enemyImgArray.size());
                int enemyType = random.nextInt(6); // Now 0-5 (6 possibilities)
                Enemy enemy;
                if (enemyType == 3) { // 16.66% Chance to spawn Shooting Enemy
                    enemy = new ShootingEnemy(
                            enemyX + c * enemyWidth,
                            enemyY + r * enemyHeight,
                            enemyWidth,
                            enemyHeight,
                            enemyImgArray.get(randomImgIndex),
                            enemyVelocityX
                    );
                } else { 
                    enemy = new FastEnemy(
                            enemyX + c * enemyWidth,
                            enemyY + r * enemyHeight,
                            enemyWidth,
                            enemyHeight,
                            enemyImgArray.get(randomImgIndex),
                            enemyVelocityX
                    );
                    
                }
                enemyArray.add(enemy);
            }
        }
        gameState.enemyCount = enemyArray.size();
    }
    //SPECIAL ENEMIES
    public void createMiniboss(){
        int minibossWorld1 = 0;
        Miniboss miniboss = new Miniboss(boardWidth/2 - tileSize * 2,
                tileSize,
                tileSize*4,
                tileSize*4,
                specialEnemyImgArray.get(minibossWorld1),
                enemyVelocityX,
                specialEnemyImgArray.get(minibossWorld1),
                50,
                100,
                2,
                "Quantum Anomaly"
                );
        enemyArray.add(miniboss);
        gameState.enemyCount++; // Increment
    }
    public void createMainboss(){
        int mainbossWorld1 = 1;
        MainBoss mainboss = new MainBoss(boardWidth/2 - tileSize * 4,
                tileSize,
                tileSize*8,
                tileSize*8,
                specialEnemyImgArray.get(mainbossWorld1),
                enemyVelocityX,
                specialEnemyImgArray.get(mainbossWorld1),
                100,
                75,
                2,
                "The Collector"
                );
        enemyArray.add(mainboss);
        gameState.enemyCount++; // Increment
    }

    //Interfaced from Action Listener
    @Override
    public void actionPerformed(ActionEvent e) {
        // Run game logic only when PLAYING
        if (currentState == GameStateEnum.PLAYING) { 
             moveGame();
             repaint();
        } else if (currentState == GameStateEnum.GAME_OVER) {
            repaint(); 
        } else if (currentState == GameStateEnum.MENU) {
            // Only need to repaint menu if state changes (handled by input)
            // repaint(); // Repaint constantly if needed, but usually event-driven
        }
    }

    // --- Action Methods Called by InputHandler ---
    public void fireBullet() {
         Bullet bullet = new Bullet(
            ship.getX() + ship.getWidth() / 2 - bulletWidth / 2, // Center bullet 
            ship.getY(), 
            bulletWidth, 
            bulletHeight,
            laserBlue);
        bulletArray.add(bullet);
    }

    public void fireWaveBlast() {
         long currentTime = System.currentTimeMillis();
        // Allow firing if cooldown is ready OR cheats are enabled
        if (remainingWaveBlastCooldown <= 0 || cheatsEnabled) { 
            WaveBlast waveBlast = new WaveBlast(
                ship.getX() + ship.getWidth() / 2 - waveBlastWidth / 2,
                ship.getY() - waveBlastHeight,
                waveBlastWidth,
                waveBlastHeight,
                null);
            waveBlastArray.add(waveBlast);
            lastWaveBlastUseTime = currentTime;
            // Only set cooldown if cheats are NOT enabled
            if (!cheatsEnabled) {
                remainingWaveBlastCooldown = waveBlastCooldown;
            }
        } else {
            System.out.println("Wave Blast (Q) on cooldown! " + String.format("%.1f", (double) remainingWaveBlastCooldown / 1000) + "s");
        }
    }

    public void fireLaserBeam() {
         long currentTime = System.currentTimeMillis();
        // Allow firing if cooldown is ready OR cheats are enabled
        if (remainingLaserBeamCooldown <= 0 || cheatsEnabled) {
            int numBullets = boardWidth / tileSize;
            int startX = 0;
            for (int i = 0; i < numBullets; i++) {
                LaserBeam laserBeam = new LaserBeam(
                    startX + i * tileSize + (tileSize / 2) - (laserBeamWidth / 2),
                    ship.getY(), 
                    laserBeamWidth,
                    laserBeamHeight,
                    null);
                laserBeamArray.add(laserBeam);
            }
            lastLaserBeamUseTime = currentTime;
            // Only set cooldown if cheats are NOT enabled
            if (!cheatsEnabled) {
                remainingLaserBeamCooldown = laserBeamCooldown;
            }
        } 
        else {
            System.out.println("Laser Beam (E) on cooldown! " + String.format("%.1f", (double) remainingLaserBeamCooldown / 1000) + "s");
        }
    }

    public void firePhaseShift() {
        long currentTime = System.currentTimeMillis();
        // Allow firing if cooldown is ready OR cheats are enabled (and not already active)
        if ((remainingPhaseShiftCooldown <= 0 || cheatsEnabled) && !isPhaseShiftActive) {
            isPhaseShiftActive = true;
            lastPhaseShiftUseTime = currentTime;
            phaseShiftEndTime = currentTime + phaseShiftDuration;
            // Only set cooldown if cheats are NOT enabled
            if (!cheatsEnabled) {
                remainingPhaseShiftCooldown = phaseShiftCooldown;
            }
            System.out.println("Phase Shift Activated!"); // Optional debug message
        } else if (isPhaseShiftActive) {
            System.out.println("Phase Shift already active!");
        } else {
            System.out.println("Phase Shift (R) on cooldown! " + String.format("%.1f", (double) remainingPhaseShiftCooldown / 1000) + "s");
        }
    }

    // Method to reset the current level/stage state
    public void restartLevel() {
        if (currentState == GameStateEnum.GAME_OVER) { // Check state
            System.out.println("Restarting level...");
            
            // Reset Game State flags/values
            gameState.gameOver = false;
            gameState.score = 0;
            gameState.enemySlain = 0;
            
            // Reset Player state
            ship.resetHealth();
            ship.setX(shipX);
            ship.setY(shipY);
            shipVelocityX = 0;
            
            // Reset Stage
            gameState.currentStage.setCurrentWave(1);
            gameState.currentStage.setSpecialEnemySpawned(false);
            
            // Clear entities and create initial wave
            reset();
            
            // Reset cooldowns
            lastWaveBlastUseTime = 0;
            lastLaserBeamUseTime = 0;
            lastPhaseShiftUseTime = 0; // Reset Phase Shift cooldown timer
            remainingWaveBlastCooldown = 0;
            remainingLaserBeamCooldown = 0;
            remainingPhaseShiftCooldown = 0; // Reset Phase Shift cooldown display
            
            currentState = GameStateEnum.PLAYING; // Change state back to playing
            
            // Restart the game loop
            startGameLoop();
            
            // Ensure focus for input
            requestFocusInWindow(); 
        }
    }

    // Method to load background images for different stages
    private void loadBackgroundImages() {
        backgroundImages = new HashMap<>();
        try {
            backgroundImages.put(1, new ImageIcon(getClass().getResource("/assets/bg_location1.png")).getImage());
            backgroundImages.put(2, new ImageIcon(getClass().getResource("/assets/bg_location2.png")).getImage());
            backgroundImages.put(3, new ImageIcon(getClass().getResource("/assets/bg_location3.png")).getImage());
        } catch (Exception e) {
            System.err.println("Error loading background images: " + e.getMessage());
            backgroundImages.put(1, new ImageIcon(getClass().getResource("/assets/bg_nebula.png")).getImage());
            backgroundImages.put(2, new ImageIcon(getClass().getResource("/assets/bg_nebula.png")).getImage());
            backgroundImages.put(3, new ImageIcon(getClass().getResource("/assets/bg_nebula.png")).getImage());
        }
    }

    // --- State Management Methods ---
    public GameStateEnum getCurrentState() {
        return currentState;
    }

    public void startGame() {
        // Reset game elements before starting
        resetGameForStart(); 
        if (cheatsEnabled) {
            gameState.money += 100000;
            System.out.println("Cheats Active: +100,000 Money!");
            // Cooldowns are already reset in toggleCheats if enabled
        }
        currentState = GameStateEnum.PLAYING;
        // Call StageManager to potentially start cutscene/first level
        if (stageManager != null) {
            stageManager.startCutscene(); // Or a different method if appropriate for starting game
        }
        System.out.println("State changed to PLAYING");
    }

    public void exitGame() {
        System.out.println("Exiting game.");
        System.exit(0);
    }

    // Method to reset necessary game components when starting a new game from menu
    private void resetGameForStart() {
        gameState.score = 0;
        gameState.money = 150; // Give starting money
        gameState.enemySlain = 0;
        gameState.gameOver = false; 
        // Reset player state
        ship.resetHealth();
        ship.setX(shipX);
        ship.setY(shipY);
        shipVelocityX = 0;
        // Reset stage
        gameState.currentStage.setStageNumber(1); // Start from stage 1
        gameState.currentStage.setCurrentWave(1); // Start from wave 1
        gameState.currentStage.setSpecialEnemySpawned(false);
        // Clear entities and cooldowns
        reset(); // Clears enemies/bullets
        lastWaveBlastUseTime = 0;
        lastLaserBeamUseTime = 0;
        lastPhaseShiftUseTime = 0; 
        remainingWaveBlastCooldown = 0;
        remainingLaserBeamCooldown = 0;
        remainingPhaseShiftCooldown = 0;
        isPhaseShiftActive = false;
        phaseShiftEndTime = 0;
    }

    // Methods for menu navigation (called by InputHandler)
    public void menuUp() {
        if (currentState == GameStateEnum.MENU) {
            selectedMenuItem = (selectedMenuItem - 1 + menuItems.length) % menuItems.length;
            repaint(); // Redraw menu with new selection
        }
    }

    public void menuDown() {
        if (currentState == GameStateEnum.MENU) {
            selectedMenuItem = (selectedMenuItem + 1) % menuItems.length;
            repaint(); // Redraw menu with new selection
        }
    }

    public void menuSelect() {
        if (currentState == GameStateEnum.MENU) {
            switch (selectedMenuItem) {
                case 0: // Start Game
                    startGame();
                    break;
                case 1: // Settings
                    System.out.println("Settings selected (Not implemented yet)");
                    // Future: Change state to SETTINGS
                    break;
                case 2: // Credits
                    System.out.println("Credits selected (Not implemented yet)");
                    // Future: Change state to CREDITS
                    break;
                case 3: // Cheats
                    toggleCheats(); 
                    break;
                case 4: // Exit
                    exitGame();
                    break;
            }
        }
    }

    // --- Cheat Management ---
    public void toggleCheats() {
        cheatsEnabled = !cheatsEnabled;
        System.out.println("Cheats " + (cheatsEnabled ? "Enabled" : "Disabled"));
        if (cheatsEnabled) {
            // Optionally reset cooldowns immediately
            remainingWaveBlastCooldown = 0;
            remainingLaserBeamCooldown = 0;
            remainingPhaseShiftCooldown = 0;
        }
        repaint(); // Redraw menu or HUD if cheats toggled from menu
    }

    public boolean areCheatsEnabled() {
        return cheatsEnabled;
    }

    // --- Draw Main Menu ---
    private void drawMainMenu(Graphics g) {
        // Background (optional, could reuse a background image)
        g.setColor(Color.BLACK);
        g.fillRect(0, 0, boardWidth, boardHeight);

        // Title
        g.setFont(new Font("Arial", Font.BOLD, 72));
        g.setColor(Color.CYAN);
        String title = "EQUINOX";
        FontMetrics fmTitle = g.getFontMetrics();
        int titleWidth = fmTitle.stringWidth(title);
        g.drawString(title, (boardWidth - titleWidth) / 2, boardHeight / 3);

        // Menu Items
        g.setFont(new Font("Arial", Font.PLAIN, 36));
        FontMetrics fmItems = g.getFontMetrics();
        int itemY = boardHeight / 2 + 50;
        for (int i = 0; i < menuItems.length; i++) {
            if (i == selectedMenuItem) {
                g.setColor(Color.YELLOW);
            } else {
                g.setColor(Color.WHITE);
            }
            // Special color for Cheats option based on state
            if (i == 3) { // Index of "Cheats"
                g.setColor(cheatsEnabled ? Color.MAGENTA : (i == selectedMenuItem ? Color.YELLOW : Color.WHITE));
            }
            String itemText = menuItems[i];
            int itemWidth = fmItems.stringWidth(itemText);
            g.drawString(itemText, (boardWidth - itemWidth) / 2, itemY + i * 50);
        }
    }

    // Method called by CollisionSystem when player health reaches 0
    public void playerDied() {
        if (currentState == GameStateEnum.PLAYING) { // Only transition if currently playing
            currentState = GameStateEnum.GAME_OVER;
            gameState.gameOver = true; // Keep flag for compatibility if needed elsewhere
            System.out.println("State changed to GAME_OVER");
        }
    }
} 