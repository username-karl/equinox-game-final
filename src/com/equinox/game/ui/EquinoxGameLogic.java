package com.equinox.game.ui;

import com.equinox.game.data.GameState;
import com.equinox.game.data.Stage;
import com.equinox.game.entities.Entity;
import com.equinox.game.entities.ShipUser;
import com.equinox.game.entities.Bullet;
import com.equinox.game.entities.EnemyBullet;
import com.equinox.game.entities.TacticalQ;
import com.equinox.game.entities.TacticalE;
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

public class EquinoxGameLogic extends JPanel implements ActionListener {

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


    long remainingCooldown;
    long remainingCooldownE;

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
    // TacticalQ
    ArrayList<TacticalQ> tacticalArray;
    int tacticalqWidth = tileSize / 4; // Bullet size width
    int tacticalqHeight = tileSize * 10;
    int tacticalqVelocityY = -100; // Bullet movespeed
    long lastTacticalQUseTime; // Last tactical use time
    long tacticalQCooldown = 2500; // Tactical Q Cooldown in ms
    //TacticalE
    ArrayList<TacticalE> tacticalEArray;
    int tacticaleWidth = tileSize; // Smaller projectile
    int tacticaleHeight = tileSize / 8;
    int tacticaleVelocityY = -15;
    long lastTacticalEUseTime;
    long tacticalECooldown = 0; //Tactical E Cooldown in ms
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
    private boolean gameLoopRunning = false;

    
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

        // Image loading - Use absolute paths from classpath root
        shipImg = new ImageIcon(getClass().getResource("/assets/protagtest.png")).getImage();
        enemyImgVar1 = new ImageIcon(getClass().getResource("/assets/enemyvar1.gif")).getImage();
        enemyImgVar2 = new ImageIcon(getClass().getResource("/assets/monstertestvar2.png")).getImage();
        enemyImgVar3 = new ImageIcon(getClass().getResource("/assets/monstertestvar3.png")).getImage();


        enemyImgArray = new ArrayList<Image>();
        enemyImgArray.add(enemyImgVar1);
        enemyImgArray.add(enemyImgVar2);
        enemyImgArray.add(enemyImgVar3);


        minibossImgvar1 = new ImageIcon(getClass().getResource("/assets/minibossvar1.png")).getImage();
        mainbossImgvar1 = new ImageIcon(getClass().getResource("/assets/mainbossvar1.png")).getImage();
        specialEnemyImgArray = new ArrayList<Image>();
        specialEnemyImgArray.add(minibossImgvar1);
        specialEnemyImgArray.add(mainbossImgvar1);
        

        // //Maps
        world1BG = new ImageIcon(getClass().getResource("/assets/world1BG.png")).getImage();
        // Misc
        laserBlue = new ImageIcon(getClass().getResource("/assets/laserBlue.png")).getImage();
        enemyBulletImg = new ImageIcon(getClass().getResource("/assets/laserRed.png")).getImage();
        // Ship - Add maxHealth parameter
        int initialPlayerHealth = 5; // Example health value
        ship = new ShipUser(shipX, shipY, shipWidth, shipHeight, shipImg, initialPlayerHealth);

        // Enemy Array Group
        enemyArray = new ArrayList<Enemy>();
        enemyBulletArray = new ArrayList<EnemyBullet>();

        // Bullets
        bulletArray = new ArrayList<Bullet>();
        // Ship skills
        tacticalArray = new ArrayList<TacticalQ>();
        tacticalEArray = new ArrayList<TacticalE>();

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

    // Draw assets
    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        if (world1BG != null) {
            g.drawImage(world1BG, 0, 0, boardWidth, boardHeight, this);
        }
        draw(g);
    }

    // DRAW METHOD
    public void draw(Graphics g) {
        drawShip(g);
        drawEnemies(g);
        drawPlayerBullets(g);
        drawTacticalAbilities(g);
        drawEnemyBullets(g);
        drawGameStats(g);
    }
    //DRAW PLAYER SHIP FOR DRAW()
    private void drawShip(Graphics g) {
        g.drawImage(ship.getImage(), ship.getX(), ship.getY(), ship.getWidth(), ship.getHeight(), null);
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
        //TACTICAL Q
        g.setColor(Color.white);
        for (TacticalQ tacticalq : tacticalArray) {
            if (!tacticalq.isUsed()) {
                g.fillRect(tacticalq.getX(), tacticalq.getY(), tacticalq.getWidth(), tacticalq.getHeight());
            }
        }
        //TACTICAL E
        for (TacticalE tacticale : tacticalEArray) {
            if (!tacticale.isUsed()) {
                g.fillRect(tacticale.getX(), tacticale.getY(), tacticale.getWidth(), tacticale.getHeight());
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
        // Draw Score
        g.setColor(Color.LIGHT_GRAY);
        g.setFont(new Font("Arial", Font.PLAIN, 16));
        if (gameState.gameOver) {
            g.drawString("Game Over: " + String.valueOf(gameState.score), 10, 35);
        } else {
            g.drawString("Score: " + String.valueOf(gameState.score), 10, 35);
        }
        // Draw Money
        g.setColor(Color.ORANGE);
        g.setFont(new Font("Arial", Font.PLAIN, 16));
        g.drawString("Money: " + gameState.money, 10, 60);
        // Draw Killed
        g.setColor(Color.RED);
        g.setFont(new Font("Arial", Font.PLAIN, 16));
        g.drawString("Killed: " + gameState.enemySlain, 672, 60);
        // Draw TacticalQ cooldown
        g.setColor(Color.YELLOW);
        g.setFont(new Font("Arial", Font.PLAIN, 18));
        if (remainingCooldown > 0) {
            g.drawString("Tactical Q: " + String.format("%.1f", (double) remainingCooldown / 1000) + "s", 10,
                    tileSize * 24);
        } else {
            g.drawString("Tactical Q: Ready", 10, tileSize * 24);
        }
        // Draw TacticalE cooldown
        if (remainingCooldownE > 0) {
            g.drawString("Tactical E: " + String.format("%.1f", (double) remainingCooldownE / 1000) + "s", 10 + tileSize * 5,
                    tileSize * 24);
        } else {
            g.drawString("Tactical E: Ready", 10 + tileSize * 5, tileSize * 24);
        }
        // Draw Stage and Wave String
        g.drawString("World: " + gameState.currentStage.getStageNumber() + " Wave: " + gameState.currentStage.getCurrentWave(), 10, 85);
    
        // Draw Player Health
        if (ship != null) {
            g.setColor(Color.GREEN);
            g.setFont(new Font("Arial", Font.BOLD, 18));
            g.drawString("Health: " + ship.getHealth() + " / " + ship.getMaxHealth(), boardWidth - 150, 35); // Position top right
            
            // Optional: Draw a simple health bar
            int barX = boardWidth - 155;
            int barY = 45;
            int barWidth = 140;
            int barHeight = 15;
            double healthPercent = (double)ship.getHealth() / ship.getMaxHealth();
            int filledWidth = (int)(barWidth * healthPercent);
            
            g.setColor(Color.DARK_GRAY);
            g.fillRect(barX, barY, barWidth, barHeight);
            g.setColor(Color.RED);
            g.fillRect(barX, barY, filledWidth, barHeight);
            g.setColor(Color.WHITE);
            g.drawRect(barX, barY, barWidth, barHeight);
        }

        // Display Game Over message and Restart prompt
        if (gameState.gameOver) {
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
        if (gameLoopRunning) {
            moveEnemies();
            moveEnemyBullets();
            movePlayerBullets();
            moveTacticalAbilities();
            // Call CollisionSystem methods
            collisionSystem.checkPlayerBulletCollisions(bulletArray, enemyArray);
            collisionSystem.checkTacticalCollisions(tacticalArray, tacticalEArray, enemyArray);
            collisionSystem.checkEnemyBulletCollisions(enemyBulletArray, ship);
            clearOffScreenBullets();
            handleStageAndWaveLogic();
            updateCooldowns();
            moveShip();
        }
    }
    
    //GAME LOOP HANDLING
    public void startGameLoop(){
        gameLoopRunning = true;
        gameLoop.start();
    }
    public void stopGameLoop(){
        gameLoopRunning = false;
        gameLoop.stop();
    }
    
    // MOVE ENEMIES for MOVE GAME FUNCTION
    private void moveEnemies() {
        for (Enemy enemy : enemyArray) {
            if (enemy.isAlive()) {
                int enemyWidthToUse = enemy.getWidth();
                if(enemy instanceof Miniboss){
                    enemyWidthToUse = enemy.getWidth();
                }else if(enemy instanceof MainBoss){
                    enemyWidthToUse = enemy.getWidth();
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
                    gameState.gameOver = true;
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
        for (TacticalQ tacticalq : tacticalArray) {
            tacticalq.setY(tacticalq.getY() + tacticalqVelocityY);
        }
        for (TacticalE tacticale : tacticalEArray) {
            tacticale.setY(tacticale.getY() + tacticaleVelocityY);
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
        tacticalArray.removeIf(tacticalq -> tacticalq.isUsed() || tacticalq.getY() < 0);
        tacticalEArray.removeIf(tacticale -> tacticale.isUsed() || tacticale.getY() < 0);
        enemyBulletArray.removeIf(enemyBullet -> enemyBullet.isUsed() || enemyBullet.getY() > boardHeight);
    }
    //STAGE WAVE LOGIC for MOVE GAME FUNCTION
    private void handleStageAndWaveLogic() {
        // Next wave of enemies
        if (gameState.enemyCount == 0) {
            stopGameLoop();
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
                startGameLoop();
            } else if (gameState.currentStage.getCurrentWave() == gameState.currentStage.getTotalWaves() - 2) {
                // Spawn the miniboss
                gameState.currentStage.setSpecialEnemySpawned(true);
                createMiniboss();
                gameState.currentStage.setCurrentWave(gameState.currentStage.getCurrentWave() + 1);
                startGameLoop();
            } else {
                // Move to the next wave
                gameState.currentStage.setCurrentWave(gameState.currentStage.getCurrentWave() + 1);
                reset();
                startGameLoop(); // Add this line to restart the game loop
            }
        }
    }
    // Cooldowns for MOVE GAME FUNCTION
    private void updateCooldowns() {
        long currentTime = System.currentTimeMillis();
        remainingCooldown = tacticalQCooldown - (currentTime - lastTacticalQUseTime);
        if (remainingCooldown < 0) {
            remainingCooldown = 0;
        }
        remainingCooldownE = tacticalECooldown - (currentTime - lastTacticalEUseTime);
        if (remainingCooldownE < 0) {
            remainingCooldownE = 0;
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
        tacticalArray.clear();
        tacticalEArray.clear();
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
                100,
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
                500,
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
        // Only run game logic if loop is active AND game is not over
        if (gameLoopRunning && !gameState.gameOver) { 
             moveGame();
             repaint();
        } else if (gameState.gameOver) {
            // If game is over, stop the loop but keep repainting to show message
            stopGameLoop(); 
            repaint(); 
        }
        // Removed the direct stopGameLoop() call based on gameOver flag from here
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

    public void fireTacticalQ() {
         long currentTime = System.currentTimeMillis();
        if (remainingCooldown <= 0) { // Check remaining cooldown directly
            TacticalQ tacticalq = new TacticalQ(
                ship.getX() + ship.getWidth() / 2 - tacticalqWidth / 2, // Center ability 
                ship.getY() - tacticalqHeight, // Start above ship?
                tacticalqWidth, 
                tacticalqHeight, 
                null); // Needs an image or draw differently
            tacticalArray.add(tacticalq);
            lastTacticalQUseTime = currentTime; // Update the last use time
            remainingCooldown = tacticalQCooldown; // Reset cooldown display immediately
        } else {
            System.out.println("Tactical Q on cooldown! " + String.format("%.1f", (double) remainingCooldown / 1000) + "s");
        }
    }

    public void fireTacticalE() {
         long currentTime = System.currentTimeMillis();
        if (remainingCooldownE <= 0) { // Check remaining cooldown directly
            int numBullets = boardWidth / tileSize; // Number of bullets based on board width
            int startX = 0; // Start at the left edge of the screen
            for (int i = 0; i < numBullets; i++) {
                TacticalE tacticale = new TacticalE(
                    startX + i * tileSize + (tileSize / 2) - (tacticaleWidth / 2), // Center in each tile
                    ship.getY(), 
                    tacticaleWidth, 
                    tacticaleHeight, 
                    null); // Needs an image or draw differently
                tacticalEArray.add(tacticale);
            }
            lastTacticalEUseTime = currentTime; // Update the last use time
            remainingCooldownE = tacticalECooldown; // Reset cooldown display immediately
        } 
        else {
            System.out.println("Tactical E on cooldown! " + String.format("%.1f", (double) remainingCooldownE / 1000) + "s");
        }
    }

    // Method to reset the current level/stage state
    public void restartLevel() {
        if (gameState.gameOver) { // Only restart if game is actually over
            System.out.println("Restarting level...");
            
            // Reset Game State flags/values
            gameState.gameOver = false;
            gameState.score = 0; // Or reset score per level?
            // gameState.money = ???; // Reset money? Keep money?
            gameState.enemySlain = 0;
            
            // Reset Player state
            ship.resetHealth();
            ship.setX(shipX); // Reset position
            ship.setY(shipY);
            shipVelocityX = 0; // Reset velocity
            
            // Reset Stage (stay on the same stage/wave where player died?)
            // OR reset to the beginning of the current stage?
            // Let's reset to the beginning of the current stage:
            gameState.currentStage.setCurrentWave(1);
            gameState.currentStage.setSpecialEnemySpawned(false);
            
            // Clear entities and create initial wave
            reset(); // Calls createEnemies inside
            
            // Reset cooldowns
            lastTacticalQUseTime = 0;
            lastTacticalEUseTime = 0;
            remainingCooldown = 0;
            remainingCooldownE = 0;
            
            // Restart the game loop
            startGameLoop();
            
            // Ensure focus for input
            requestFocusInWindow(); 
        }
    }
} 