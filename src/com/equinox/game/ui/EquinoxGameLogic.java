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
import com.equinox.game.systems.RenderingSystem;
import com.equinox.game.systems.GameUpdateSystem;
import com.equinox.game.utils.Constants;
import com.equinox.game.utils.AssetLoader;
import com.equinox.game.leaderboard.LeaderboardPanel;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Random;
import javax.swing.*;

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
    private String[] menuItems = {"Start Game", "Settings", "Leaderboard", "Credits", "Cheats", "Exit"};

    // Cheats State
    private boolean cheatsEnabled = false;

    // INIT
    public GameState gameState;
    private AssetLoader assetLoader;

    // BOARD
    int tileSize = Constants.TILE_SIZE;
    int rows = Constants.ROWS;
    int columns = Constants.COLUMNS;
    int boardWidth = Constants.BOARD_WIDTH;
    int boardHeight = Constants.BOARD_HEIGHT;

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
    // Map<Integer, Image> backgroundImages;

    // Cooldown Tracking Variables (State) - REMOVED (now in GameState)
    /*
    public long remainingWaveBlastCooldown;
    public long remainingLaserBeamCooldown;
    public long remainingPhaseShiftCooldown;
    public long lastWaveBlastUseTime;
    public long lastLaserBeamUseTime;
    public long lastPhaseShiftUseTime;
    public boolean isPhaseShiftActive = false;
    public long phaseShiftEndTime = 0;
    */

    // Ship - REMOVED redundant dimension/velocity vars
    /*
    int shipWidth = Constants.SHIP_WIDTH;
    int shipHeight = Constants.SHIP_HEIGHT;
    int shipX = Constants.SHIP_START_X;
    int shipY = Constants.SHIP_START_Y;
    int shipVelocityX = 0;
    int shipAcceleration = Constants.SHIP_ACCELERATION;
    int shipMaxSpeed = Constants.SHIP_MAX_SPEED;
    int shipDeceleration = Constants.SHIP_DECELERATION;
    */
    ShipUser ship; // Keep the ship object itself

    // Enemies - REMOVED redundant dimension/velocity/count vars
    /*
    ArrayList<Enemy> enemyArray;
    int enemyWidth = Constants.ENEMY_WIDTH;
    int enemyHeight = Constants.ENEMY_HEIGHT;
    int enemyX = Constants.ENEMY_START_X;
    int enemyY = Constants.ENEMY_START_Y;
    int enemyVelocityX = Constants.ENEMY_BASE_VELOCITY_X;
    int enemyRows = Constants.ENEMY_INITIAL_ROWS;
    int enemyColumns = Constants.ENEMY_INITIAL_COLUMNS;
    */
    // Keep entity list references needed for GameState initialization
    ArrayList<Enemy> enemyArray;
    ArrayList<Bullet> bulletArray;
    ArrayList<WaveBlast> waveBlastArray;
    ArrayList<LaserBeam> laserBeamArray;
    ArrayList<EnemyBullet> enemyBulletArray;

    // Bullets - REMOVED redundant dimension/velocity vars
    /*
    int bulletWidth = Constants.BULLET_WIDTH;
    int bulletHeight = Constants.BULLET_HEIGHT;
    int bulletVelocityY = Constants.BULLET_VELOCITY_Y;
    */
    // WaveBlast (Q) - REMOVED redundant dimension/velocity/cooldown vars
    /*
    int waveBlastWidth = Constants.WAVE_BLAST_WIDTH;
    int waveBlastHeight = Constants.WAVE_BLAST_HEIGHT;
    int waveBlastVelocityY = Constants.WAVE_BLAST_VELOCITY_Y;
    long waveBlastCooldown = Constants.WAVE_BLAST_COOLDOWN_MS;
    */
    //LaserBeam (E) - REMOVED redundant dimension/velocity/cooldown vars
    /*
    int laserBeamWidth = Constants.LASER_BEAM_WIDTH;
    int laserBeamHeight = Constants.LASER_BEAM_HEIGHT;
    int laserBeamVelocityY = Constants.LASER_BEAM_VELOCITY_Y;
    long laserBeamCooldown = Constants.LASER_BEAM_COOLDOWN_MS;
    */
    // Phase Shift (R) - REMOVED redundant duration/cooldown vars
    /*
    long phaseShiftDuration = Constants.PHASE_SHIFT_DURATION_MS;
    long phaseShiftCooldown = Constants.PHASE_SHIFT_COOLDOWN_MS;
    */
    //Enemy Bullets - REMOVED redundant dimension vars
    /*
    int enemyBulletWidth = Constants.ENEMY_BULLET_WIDTH;
    int enemyBulletHeight = Constants.ENEMY_BULLET_HEIGHT;
    */

    // Timer
    Timer gameLoop;

    // Input Handler instance
    private InputHandler inputHandler;
    // Collision System instance
    private CollisionSystem collisionSystem;
    // Rendering System instance
    private RenderingSystem renderingSystem;
    private GameUpdateSystem gameUpdateSystem;

    //STAGE DOMAIN
    private StageManager stageManager;
    private LeaderboardPanel leaderboardPanel;
    private JFrame mainFrame;

    
    //MAIN EQUINOX GAME CONSTRUCTOR
    //ALL THE LOGIC HERE IS CONTAINED
    public EquinoxGameLogic(AssetLoader assetLoader) { // Accept AssetLoader
        // this.assetLoader = new AssetLoader(); // REMOVED - Use provided loader
        this.assetLoader = assetLoader; 
        renderingSystem = new RenderingSystem(this.assetLoader);
        gameState = new GameState();
        gameState.currentStage = new Stage(1,7);

        // Create entities
        ship = new ShipUser(Constants.SHIP_START_X, Constants.SHIP_START_Y, Constants.SHIP_WIDTH, Constants.SHIP_HEIGHT,
                          this.assetLoader.getImage(Constants.PLAYER_SHIP_IMG_KEY), Constants.SHIP_INITIAL_HEALTH);
        enemyArray = new ArrayList<>();
        bulletArray = new ArrayList<>();
        waveBlastArray = new ArrayList<>();
        laserBeamArray = new ArrayList<>();
        enemyBulletArray = new ArrayList<>();
        
        // Populate GameState with initial entities/state
        gameState.ship = this.ship;
        gameState.enemyArray = this.enemyArray;
        gameState.bulletArray = this.bulletArray;
        gameState.waveBlastArray = this.waveBlastArray;
        gameState.laserBeamArray = this.laserBeamArray;
        gameState.enemyBulletArray = this.enemyBulletArray;
        // Initialize other GameState fields if needed (score, money etc. default to 0/constants)
        gameState.money = Constants.STARTING_MONEY;

        this.inputHandler = new InputHandler(this); // InputHandler still needs this for UI actions (menu, restart)
        
        // Instantiate systems AFTER gameState is populated
        // Break circular dependency: Create GUS -> Create CS -> Set CS in GUS
        this.gameUpdateSystem = new GameUpdateSystem(this.gameState, 
                                                  inputHandler, null, // Pass null for StageManager initially
                                                  assetLoader, this);
        this.collisionSystem = new CollisionSystem(this.gameState, this.gameUpdateSystem, this);
        this.gameUpdateSystem.setCollisionSystem(this.collisionSystem); // Set the dependency

        this.leaderboardPanel = new LeaderboardPanel(this);

        setPreferredSize(new Dimension(Constants.BOARD_WIDTH, Constants.BOARD_HEIGHT));
        setBackground(Color.DARK_GRAY);
        addKeyListener(inputHandler);
        setFocusable(true);
        currentState = GameStateEnum.MENU;
        gameLoop = new Timer(Constants.TIMER_DELAY_MS, this);
    }
    
    // Initialize StageManager after GameUpdateSystem is created
    // And pass necessary AssetLoader reference
    public void initializeStageManager(JFrame frame) {
         this.mainFrame = frame;
         this.stageManager = new StageManager(this, frame, this.assetLoader);
         // Now give GameUpdateSystem the reference to StageManager if it needs it
         if (this.gameUpdateSystem != null) {
             this.gameUpdateSystem.setStageManager(this.stageManager); // Use setter
         }
         // Call setStageManager if it performs additional setup
         // setStageManager(this.stageManager);
    }

    //Stage Manager
    public void setStageManager(StageManager stageManager) {
        this.stageManager = stageManager;
        // Possibly trigger initial enemy creation or menu display here
        if (currentState == GameStateEnum.PLAYING) {
             // Call the new reset method in GameState
             if (gameState != null) gameState.resetForNewGame(); 
             // Call createEnemies from GameUpdateSystem
             if (gameUpdateSystem != null) gameUpdateSystem.createEnemies(); 
             startGameLoop();
        } else if (currentState == GameStateEnum.MENU) {
            repaint(); // Ensure menu is drawn initially
        }
    }
    //GAMESTATE GETTER FOR CUTSCENES
    public GameState getGameState() {
        return gameState;
    }

    // Getter for Phase Shift state - REMOVED (Handled by GameState)
    /*
    public boolean isPhaseShiftActive() {
        return isPhaseShiftActive;
    }
    */

    // Draw assets - DELEGATED TO RenderingSystem
    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        // Delegate rendering to the RenderingSystem - CORRECTED CALL
        renderingSystem.render(g, gameState, currentState);
        
        // Keep Main Menu drawing here for now as it's tied to this panel's state
        if (currentState == GameStateEnum.MENU) {
            drawMainMenu(g);
        }
    }

    // DRAW METHOD - REMOVED (Moved to RenderingSystem)
    // public void drawGame(Graphics g) { ... }
    //DRAW PLAYER SHIP FOR DRAW() - REMOVED
    // private void drawShip(Graphics g) { ... }
    //DRAW ENEMY ARRAY FOR DRAW() - REMOVED
    // private void drawEnemies(Graphics g) { ... }
    //DRAW PLAYER BULLET FOR DRAW() - REMOVED
    // private void drawPlayerBullets(Graphics g) { ... }
    //DRAW TACTICALS FOR DRAW() - REMOVED
    // private void drawTacticalAbilities(Graphics g) { ... }
    //DRAW ENEMY BULLETS FOR DRAW() - REMOVED
    // private void drawEnemyBullets(Graphics g) { ... }
    //DRAW GAME STATS FOR DRAW() - REMOVED
    // private void drawGameStats(Graphics g) { ... }
    //DRAW BOSS HEALTH BARS FOR DRAW() - REMOVED
    // private void drawBossHealthBar(Graphics g, Miniboss miniboss) { ... }
    // private void drawBossHealthBar(Graphics g, MainBoss mainboss) { ... }
    // private void drawGameOverMessage(Graphics g) { ... } // Also moved to RenderingSystem

    // MAIN MOVE GAME FUNCTION - REMOVED (Handled by GameUpdateSystem)
    /*
    public void moveGame() {
        if (currentState == GameStateEnum.PLAYING) {
            moveEnemies();
            moveEnemyBullets();
            movePlayerBullets();
            moveTacticalAbilities();
            collisionSystem.checkPlayerBulletCollisions(bulletArray, enemyArray);
            collisionSystem.checkTacticalCollisions(waveBlastArray, laserBeamArray, enemyArray);
            collisionSystem.checkEnemyBulletCollisions(enemyBulletArray, ship);
            clearOffScreenBullets();
            handleStageAndWaveLogic();
            updateCooldowns();
            moveShip();
        }
    }
    */
    
    //GAME LOOP HANDLING
    public void startGameLoop(){
        gameLoop.start();
    }
    public void stopGameLoop(){
        gameLoop.stop();
    }
    
    // MOVE ENEMIES for MOVE GAME FUNCTION - REMOVED
    /*
    private void moveEnemies() { ... }
    */
    //MOVE ENEMY BULLETS for MOVE GAME FUNCTION - REMOVED
    /*
    private void moveEnemyBullets() { ... }
    */
    //MOVE PLAYER BULLETS for MOVE GAME FUNCTION - REMOVED
    /*
    private void movePlayerBullets() { ... }
    */
    // MOVE SKILLS for MOVE GAME FUNCTION - REMOVED
    /*
    private void moveTacticalAbilities() { ... }
    */
    //ENEMY HIT for MOVE GAME FUNCTION - REMOVED (Handled by GameUpdateSystem/CollisionSystem)
    /*
    public void handleEnemyHit(Enemy enemy) { ... }
    */
    //OPTIMIZATIONS for MOVE GAME FUNCTION - REMOVED
    /*
    private void clearOffScreenBullets() { ... }
    */
    //STAGE WAVE LOGIC for MOVE GAME FUNCTION - REMOVED
    /*
    private void handleStageAndWaveLogic() { ... }
    */
    // Cooldowns for MOVE GAME FUNCTION - REMOVED
    /*
    private void updateCooldowns() { ... }
    */
    // MOVE SHIP for MOVE GAME FUNCTION - REMOVED
    /*
    private void moveShip() { ... }
    */
    //RESET for MOVE GAME FUNCTION - REMOVED (Use GameState.resetForNewGame)
    /*
    public void reset(){
        enemyArray.clear();
        bulletArray.clear();
        enemyBulletArray.clear();
        waveBlastArray.clear();
        laserBeamArray.clear();
        isPhaseShiftActive = false;
        phaseShiftEndTime = 0;
    }
    */
    //CREATING ENEMY INSTANCES - REMOVED (Handled by GameUpdateSystem)
    /*
    public void createEnemies() { ... }
    */
    //SPECIAL ENEMIES - REMOVED (Handled by GameUpdateSystem)
    /*
    public void createMiniboss(){ ... }
    */
    /*
    public void createMainboss(){ ... }
    */

    //Interfaced from Action Listener
    @Override
    public void actionPerformed(ActionEvent e) {
        if (currentState == GameStateEnum.PLAYING) {
             // moveGame(); // REMOVED - Replaced by GameUpdateSystem
             gameUpdateSystem.update();
             repaint();
        } else if (currentState == GameStateEnum.GAME_OVER) {
            repaint(); // Keep rendering game over screen
        } else if (currentState == GameStateEnum.MENU) {
            // No constant repaint needed for menu
        }
    }

    // --- Action Methods Called by InputHandler ---
    public void fireBullet() { if (gameUpdateSystem != null) gameUpdateSystem.fireBullet(); }
    public void fireWaveBlast() { if (gameUpdateSystem != null) gameUpdateSystem.fireWaveBlast(); }
    public void fireLaserBeam() { if (gameUpdateSystem != null) gameUpdateSystem.fireLaserBeam(); }
    public void firePhaseShift() { if (gameUpdateSystem != null) gameUpdateSystem.firePhaseShift(); }

    // Method to reset the current level/stage state
    public void restartLevel() { 
        if (currentState == GameStateEnum.GAME_OVER && gameUpdateSystem != null) {
            System.out.println("Restarting level...");
            gameState.resetForNewGame(); // Use GameState's reset method
            if (gameUpdateSystem != null) gameUpdateSystem.createEnemies(); // Need to recreate enemies after reset
            currentState = GameStateEnum.PLAYING;
            startGameLoop();
            requestFocusInWindow();
        }
     }

    // Method to reset necessary game components - REMOVED (Use GameState.resetForNewGame)
    /*
    private void resetGameForStart() {
        gameState.score = 0;
        gameState.money = Constants.STARTING_MONEY;
        gameState.enemySlain = 0;
        gameState.gameOver = false;
        ship.resetHealth();
        ship.setX(Constants.SHIP_START_X);
        ship.setY(Constants.SHIP_START_Y);
        shipVelocityX = 0;
        gameState.currentStage.setStageNumber(1);
        gameState.currentStage.setCurrentWave(1);
        gameState.currentStage.setSpecialEnemySpawned(false);
        reset();
        lastWaveBlastUseTime = 0;
        lastLaserBeamUseTime = 0;
        lastPhaseShiftUseTime = 0;
        remainingWaveBlastCooldown = 0;
        remainingLaserBeamCooldown = 0;
        remainingPhaseShiftCooldown = 0;
    }
    */

    // Methods for menu navigation (called by InputHandler)
    public void menuUp() {
        if (currentState == GameStateEnum.MENU) {
            selectedMenuItem = (selectedMenuItem - 1 + menuItems.length) % menuItems.length;
            repaint();
        }
    }

    public void menuDown() {
        if (currentState == GameStateEnum.MENU) {
            selectedMenuItem = (selectedMenuItem + 1) % menuItems.length;
            repaint();
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
                    break;
                case 2: // Leaderboard
                    showLeaderboard();
                    break;
                case 3: // Credits
                    System.out.println("Credits selected (Not implemented yet)");
                    break;
                case 4: // Cheats
                    toggleCheats();
                    break;
                case 5: // Exit
                    exitGame();
                    break;
            }
        }
    }

    // --- Display Control ---
    public void showLeaderboard() {
         if (mainFrame != null && currentState == GameStateEnum.MENU) {
            mainFrame.remove(this);
            leaderboardPanel.loadAndDisplayScores(); // Load scores before showing
            mainFrame.add(leaderboardPanel);
            mainFrame.pack();
            mainFrame.revalidate();
            mainFrame.repaint();
            leaderboardPanel.requestFocusInWindow();
            // Do NOT change game state here, panel handles its own logic/back button
        }
    }

    public void showMainMenu() {
        if (mainFrame != null) { // Check frame exists
            // Assume current panel is LeaderboardPanel (or ShopPanel, CutscenePanel etc.)
            // A more robust way would track the current non-game panel
            mainFrame.remove(leaderboardPanel); // Remove leaderboard
            // Potentially remove other panels if logic gets complex
            
            mainFrame.add(this); // Add the main logic panel back
            setCurrentState(GameStateEnum.MENU); // Set state to MENU
            mainFrame.pack();
            mainFrame.revalidate();
            mainFrame.repaint();
            requestFocusInWindow();
        }
    }

    // --- Cheat Management ---
    public void toggleCheats() {
        gameState.cheatsEnabled = !gameState.cheatsEnabled;
        System.out.println("Cheats " + (gameState.cheatsEnabled ? "Enabled" : "Disabled"));
        // REMOVED direct cooldown reset - handled by GameState/GameUpdateSystem potentially
        /*
        if (gameState.cheatsEnabled) {
            remainingWaveBlastCooldown = 0;
            remainingLaserBeamCooldown = 0;
            remainingPhaseShiftCooldown = 0;
        }
        */
        if (currentState == GameStateEnum.MENU || currentState == GameStateEnum.PLAYING) {
             repaint(); // Repaint to update cheat indicator or menu item color
        }
    }

    public boolean areCheatsEnabled() {
        return gameState.cheatsEnabled;
    }

    // --- Draw Main Menu ---
    private void drawMainMenu(Graphics g) {
        g.setColor(Color.BLACK);
        g.fillRect(0, 0, boardWidth, boardHeight);

        g.setFont(new Font("Arial", Font.BOLD, 72));
        g.setColor(Constants.MENU_TITLE_COLOR);
        String title = "EQUINOX";
        FontMetrics fmTitle = g.getFontMetrics();
        int titleWidth = fmTitle.stringWidth(title);
        g.drawString(title, (boardWidth - titleWidth) / 2, boardHeight / 3);

        g.setFont(new Font("Arial", Font.PLAIN, 36));
        FontMetrics fmItems = g.getFontMetrics();
        int itemY = boardHeight / 2 + 50;
        for (int i = 0; i < menuItems.length; i++) {
            Color itemColor = (i == selectedMenuItem) ? Constants.MENU_ITEM_SELECTED_COLOR : Constants.MENU_ITEM_DEFAULT_COLOR;
            if (i == 3) {
                itemColor = gameState.cheatsEnabled ? Constants.MENU_ITEM_CHEAT_ACTIVE_COLOR : itemColor;
            }
            g.setColor(itemColor);
            String itemText = menuItems[i];
            int itemWidth = fmItems.stringWidth(itemText);
            g.drawString(itemText, (boardWidth - itemWidth) / 2, itemY + i * 50);
        }
    }

    // Called by GameUpdateSystem when win condition is met
    public void handleGameWin() {
        if (currentState == GameStateEnum.PLAYING) {
            // Only set the game state here. Prompt/save is handled in signalGameOver
             signalGameOver(); 
        }
    }

    // Method called by CollisionSystem when player health reaches 0 OR game is won
    public void signalGameOver() {
        if (currentState == GameStateEnum.PLAYING) {
             stopGameLoop(); // Stop updates
            gameState.gameOver = true; // Mark game as over
            // The gameWon flag is set by GameUpdateSystem if applicable
            currentState = GameStateEnum.GAME_OVER;
            System.out.println("State changed to GAME_OVER (Win status: " + gameState.gameWon + ")");

            // REMOVED: Prompt and save logic moved to handleGiveUp
            
            // Ensure repaint happens for Game Over screen
            repaint();
        }
    }

    public void startGame() {
        if (currentState == GameStateEnum.MENU && gameUpdateSystem != null) {
            gameState.resetForNewGame(); // Use GameState's reset method
            gameState.startTimeMillis = System.currentTimeMillis(); // RECORD START TIME
            if (gameState.cheatsEnabled) {
                // Use the new addMoney helper to also update maxMoneyAchieved
                gameState.addMoney(Constants.CHEAT_MONEY_BONUS);
                System.out.println("Cheats Active: +" + Constants.CHEAT_MONEY_BONUS + " Money!");
            } else {
                 // Ensure maxMoney is correctly initialized even if cheats aren't on
                 gameState.maxMoneyAchieved = gameState.money;
            }
            currentState = GameStateEnum.PLAYING;
            System.out.println("State changed to PLAYING");
            if (stageManager != null) {
                stageManager.startCutscene(); 
            } else {
                gameUpdateSystem.createEnemies(); // Delegate enemy creation
            }
            startGameLoop();
             requestFocusInWindow();
        }
    }

    public void exitGame() { System.exit(0); }

    // Added getter for current state needed by InputHandler
    public GameStateEnum getCurrentState() {
        return currentState;
    }

    // Added setter for StageManager to control game state
    public void setCurrentState(GameStateEnum newState) {
        this.currentState = newState;
        System.out.println("GameState changed to: " + newState); // Debug log
        if (newState != GameStateEnum.PLAYING) {
            stopGameLoop(); // Stop game updates if not playing
        } else if (newState == GameStateEnum.PLAYING && !gameLoop.isRunning()){
             startGameLoop(); // Start loop if transitioning to playing and it's stopped
        } 
        // Repaint might be needed depending on how panels are switched
        repaint(); 
    }

    // Called when Esc is pressed on Game Over screen
    public void handleGiveUp() {
        if (currentState == GameStateEnum.GAME_OVER) {
            System.out.println("Give Up / Submit Score selected...");
            
            // Determine prompt message based on win/loss
            String promptTitle = gameState.gameWon ? "Victory!" : "Game Over";
            String promptMessage = gameState.gameWon ? 
                "You Won! Enter your name for the leaderboard:" :
                "Game Over. Enter your name to save score:";

            // Prompt for player name
            String playerName = JOptionPane.showInputDialog(
                mainFrame, 
                promptMessage, 
                promptTitle, 
                JOptionPane.PLAIN_MESSAGE
            );

            // Handle empty or cancelled input
            if (playerName == null || playerName.trim().isEmpty()) {
                playerName = "Player"; // Default name
            }
            playerName = playerName.trim(); 
            if (playerName.length() > 15) { 
                playerName = playerName.substring(0, 15); 
            }

            // Save score with the entered name
            com.equinox.game.leaderboard.LeaderboardManager.addEntryFromGameState(gameState, playerName);

            // After saving, go back to the main menu
            showMainMenu();
        }
    }
} 