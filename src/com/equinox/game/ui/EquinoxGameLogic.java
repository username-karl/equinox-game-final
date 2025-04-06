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
import com.equinox.game.ui.SettingsPanel;
import com.equinox.game.ui.CreditsPanel;
import com.equinox.game.shop.UpgradeManager;
import com.equinox.game.leaderboard.LeaderboardManager;
import com.equinox.game.leaderboard.LeaderboardEntry;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;
import java.util.Random;
import javax.swing.*;
import javax.swing.JOptionPane; // For score submission input
import javax.swing.Box; // For spacing

public class EquinoxGameLogic extends JPanel implements ActionListener, KeyListener {

    // Game States Enum
    public enum GameStateEnum {
        MAIN_MENU,
        PLAYING,
        GAME_OVER,
        SHOP,
        CUTSCENE,
        LEADERBOARD,
        SETTINGS,
        CREDITS
    }
    private GameStateEnum currentGameState;

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
    Image mainMenuBG;

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

    private ShopPanel shopPanel;
    private CutscenePanel cutscenePanel;
    private SettingsPanel settingsPanel;
    private CreditsPanel creditsPanel;
    private UpgradeManager upgradeManager;
    private LeaderboardManager leaderboardManager;

    // Add Buttons for Main Menu
    private JButton startButton;
    private JButton settingsButton;
    private JButton leaderboardButton;
    private JButton creditsButton;
    private JButton exitButton;
    private JPanel menuButtonPanel; // Panel to hold the buttons

    //MAIN EQUINOX GAME CONSTRUCTOR
    //ALL THE LOGIC HERE IS CONTAINED
    public EquinoxGameLogic(AssetLoader assetLoader) { // Accept AssetLoader
        // this.assetLoader = new AssetLoader(); // REMOVED - Use provided loader
        this.assetLoader = assetLoader; 
        renderingSystem = new RenderingSystem(this.assetLoader);
        gameState = new GameState();
        gameState.currentStage = new Stage(1,7);

        // Create entities - Pass gameState to ShipUser constructor
        ship = new ShipUser(Constants.SHIP_START_X, Constants.SHIP_START_Y, Constants.SHIP_WIDTH, Constants.SHIP_HEIGHT,
                          this.assetLoader.getImage(Constants.PLAYER_SHIP_IMG_KEY), gameState);
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

        // Initialize UI Panels
        // Note: ShopPanel needs different arguments
        // Assuming StageManager provides background and AssetLoader is needed
        Image shopBg = null; // Placeholder, StageManager or AssetLoader should provide this
        shopPanel = new ShopPanel(stageManager, shopBg, gameState, assetLoader); 
        cutscenePanel = new CutscenePanel(stageManager, assetLoader); 
        settingsPanel = new SettingsPanel(this, gameState); // Pass gameState
        creditsPanel = new CreditsPanel(this);   // Pass 'this' for back button

        // Initialize Main Menu Buttons
        setupMenuButtons();

        setPreferredSize(new Dimension(Constants.BOARD_WIDTH, Constants.BOARD_HEIGHT));
        setBackground(Color.DARK_GRAY);
        addKeyListener(this);
        setFocusable(true);
        currentGameState = GameStateEnum.MAIN_MENU;
        gameLoop = new Timer(Constants.TIMER_DELAY_MS, this);

        // Initialize LeaderboardManager
        leaderboardManager = new LeaderboardManager();

        // Initial Game State
        currentGameState = GameStateEnum.MAIN_MENU;
        setupMainMenu(); // Call helper to set up panel display
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
        if (currentGameState == GameStateEnum.PLAYING) {
             // Call the new reset method in GameState
             if (gameState != null) gameState.resetForNewGame(); 
             // Call createEnemies from GameUpdateSystem
             if (gameUpdateSystem != null) gameUpdateSystem.createEnemies(); 
             startGameLoop();
        } else if (currentGameState == GameStateEnum.MAIN_MENU) {
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
        // Draw based on state
        switch (currentGameState) {
            case MAIN_MENU:
                // Explicitly draw a background for the main menu
                Image bgImage = null;
                if (assetLoader != null) {
                    bgImage = assetLoader.getImage(Constants.MAIN_MENU_BG_KEY); // NEW: Use the dedicated main menu background key
                }

                if (bgImage != null) {
                    g.drawImage(bgImage, 0, 0, Constants.BOARD_WIDTH, Constants.BOARD_HEIGHT, this);
                } else {
                    // Fallback if image loading fails or assetLoader is null
                    g.setColor(Color.BLACK);
                    g.fillRect(0, 0, getWidth(), getHeight());
                    System.err.println("Warning: Main menu background image not found or AssetLoader not ready. Using fallback black.");
                }
                 // Buttons are painted by Swing automatically
                break;
            case SETTINGS:
            case CREDITS:
            case CUTSCENE:
            case LEADERBOARD: 
                // Rely on Swing to paint the added panel
                break;
            case PLAYING:
            case GAME_OVER:
                if (renderingSystem != null) {
                    renderingSystem.render(g, gameState, currentGameState);
                } else {
                    g.setColor(Color.BLACK);
                    g.fillRect(0, 0, getWidth(), getHeight());
                    g.setColor(Color.RED);
                    g.drawString("Error: RenderingSystem null", 100, 100);
                }
                break;
            case SHOP: 
                 if (renderingSystem != null) {
                    renderingSystem.render(g, gameState, currentGameState);
                } else {
                    g.setColor(Color.BLACK);
                    g.fillRect(0, 0, getWidth(), getHeight());
                    g.setColor(Color.RED);
                    g.drawString("Error: RenderingSystem null", 100, 100);
                }
                // Ensure shopPanel is added/visible in setupShopPanel if needed
                break;
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
        String command = e.getActionCommand();
        System.out.println("Action Performed: " + command + " in state: " + currentGameState); 
        
        // Handle Main Menu button clicks
        if (currentGameState == GameStateEnum.MAIN_MENU) {
             switch (command) {
                case "Start Game":
                    startGame();
                    break;
                case "Settings":
                    currentGameState = GameStateEnum.SETTINGS;
                    setupSettingsPanel();
                    break;
                case "Leaderboard":
                    currentGameState = GameStateEnum.LEADERBOARD;
                    setupLeaderboardPanel();
                    break;
                case "Credits":
                    currentGameState = GameStateEnum.CREDITS;
                    setupCreditsPanel();
                    break;
                case "Exit":
                    exitGame(); 
                    break;
            }
        } 
        // Handle Back buttons from sub-panels
        else if (currentGameState == GameStateEnum.LEADERBOARD ||
                   currentGameState == GameStateEnum.SETTINGS || 
                   currentGameState == GameStateEnum.CREDITS) {
             if ("Back".equals(command)) { 
                currentGameState = GameStateEnum.MAIN_MENU;
                setupMainMenu();
            }
        } 
        // Handle Shop interaction (assuming shop still uses internal listeners primarily)
        else if (currentGameState == GameStateEnum.SHOP) {
             if ("Back".equals(command) || "Continue".equals(command)) { // Example check
                 exitShop(); 
             }
        }
        // Handle game timer event
        else if (e.getSource() == gameLoop && currentGameState == GameStateEnum.PLAYING) {
            updateGame();
            repaint();
        }
    }

    // --- Action Methods Called by InputHandler ---
    // public void fireBullet() { if (gameUpdateSystem != null) gameUpdateSystem.fireBullet(); } // REMOVED
    public void fireWaveBlast() { if (gameUpdateSystem != null) gameUpdateSystem.fireWaveBlast(); }
    public void fireLaserBeam() { if (gameUpdateSystem != null) gameUpdateSystem.fireLaserBeam(); }
    public void firePhaseShift() { if (gameUpdateSystem != null) gameUpdateSystem.firePhaseShift(); }

    // Method to reset the current level/stage state
    public void restartLevel() { 
        if (currentGameState == GameStateEnum.GAME_OVER && gameUpdateSystem != null) {
            System.out.println("Restarting level...");
            gameState.resetForNewGame(); // Use GameState's reset method
            if (gameUpdateSystem != null) gameUpdateSystem.createEnemies(); // Need to recreate enemies after reset
            currentGameState = GameStateEnum.PLAYING;
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

    // Methods for menu navigation (called by InputHandler) - DELETE THESE
    /* DELETE START
    public void menuUp() {
        System.out.println("Menu Up: Before = " + selectedMenuItem);
        selectedMenuItem = (selectedMenuItem - 1 + menuItems.length) % menuItems.length;
        System.out.println("Menu Up: After = " + selectedMenuItem);
        repaint();
    }

    public void menuDown() {
        System.out.println("Menu Down: Before = " + selectedMenuItem);
        selectedMenuItem = (selectedMenuItem + 1) % menuItems.length;
        System.out.println("Menu Down: After = " + selectedMenuItem);
        repaint();
    }

    public void menuSelect() {
        System.out.println("Menu Select: Index = " + selectedMenuItem + ", Item = " + menuItems[selectedMenuItem]);
        handleMenuSelection(menuItems[selectedMenuItem]);
    }
    DELETE END */

    // --- Display Control ---
    public void showLeaderboard() {
         if (mainFrame != null && currentGameState == GameStateEnum.MAIN_MENU) {
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
            // Remove the CURRENTLY displayed panel, whatever it is.
            // This assumes only one of these panels (Leaderboard, Shop, Settings, Credits)
            // or the main game logic panel (`this`) is displayed at a time.
            Component currentComponent = mainFrame.getContentPane().getComponent(0);
            if (currentComponent != null) {
                mainFrame.remove(currentComponent);
            }
            
            mainFrame.add(this); // Add the main logic panel back
            setCurrentState(GameStateEnum.MAIN_MENU); // Set state to MAIN_MENU
            setupMainMenu(); // <--- Add this line to re-add the buttons
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
        if (currentGameState == GameStateEnum.MAIN_MENU || currentGameState == GameStateEnum.PLAYING) {
             repaint(); // Repaint to update cheat indicator or menu item color
        }
    }

    public boolean areCheatsEnabled() {
        return gameState.cheatsEnabled;
    }

    // Called by GameUpdateSystem when win condition is met
    public void handleGameWin() {
        if (currentGameState == GameStateEnum.PLAYING) {
            // Only set the game state here. Prompt/save is handled in signalGameOver
             signalGameOver(); 
        }
    }

    // Method called by CollisionSystem when player health reaches 0 OR game is won
    public void signalGameOver() {
        if (currentGameState == GameStateEnum.PLAYING) {
             stopGameLoop(); // Stop updates
            gameState.gameOver = true; // Mark game as over
            // The gameWon flag is set by GameUpdateSystem if applicable
            currentGameState = GameStateEnum.GAME_OVER;
            System.out.println("State changed to GAME_OVER (Win status: " + gameState.gameWon + ")");

            // REMOVED: Prompt and save logic moved to handleGiveUp
            
            // Ensure repaint happens for Game Over screen
            repaint();
        }
    }

    public void startGame() {
        if (currentGameState == GameStateEnum.MAIN_MENU && gameUpdateSystem != null) {
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
            currentGameState = GameStateEnum.PLAYING;
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
        return currentGameState;
    }

    // Added setter for StageManager to control game state
    public void setCurrentState(GameStateEnum newState) {
        this.currentGameState = newState;
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
        if (currentGameState == GameStateEnum.GAME_OVER) {
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
            LeaderboardManager.addEntryFromGameState(gameState, playerName);

            // After saving, go back to the main menu
            showMainMenu();
        }
    }

    private void setupMainMenu() {
        removeAll(); 
        setLayout(new BorderLayout()); // Ensure parent has a layout
        add(menuButtonPanel, BorderLayout.CENTER); // Add the button panel
        // No need to add key listener here specifically for menu nav
        setFocusable(true);
        requestFocusInWindow(); 
        revalidate();
        repaint();
    }
    
    private void setupSettingsPanel() {
        removeAll();
        if (settingsPanel != null) {
            settingsPanel.updateSettingsState(); 
            setLayout(new BorderLayout()); // Ensure layout
            add(settingsPanel, BorderLayout.CENTER);
        } else { /* Error handling */ }
        revalidate();
        repaint();
        settingsPanel.requestFocusInWindow(); 
    }
    
    private void setupCreditsPanel() {
        removeAll();
        if (creditsPanel != null) {
             setLayout(new BorderLayout()); // Ensure layout
             add(creditsPanel, BorderLayout.CENTER);
        } else { /* Error handling */ }
        revalidate();
        repaint();
         // creditsPanel.requestFocusInWindow(); // Probably not needed
    }
    
    private void setupLeaderboardPanel() {
        removeAll();
        if (leaderboardPanel != null) {
            leaderboardPanel.loadAndDisplayScores(); 
            setLayout(new BorderLayout()); // Ensure layout
            add(leaderboardPanel, BorderLayout.CENTER);
             // leaderboardPanel.requestFocusInWindow(); // Probably not needed
        } else { /* Error handling */ }
        revalidate();
        repaint();
    }

    // KeyListener Implementation
    @Override
    public void keyTyped(KeyEvent e) {
        // Not typically used in action games
    }

    @Override
    public void keyPressed(KeyEvent e) {
        int key = e.getKeyCode();
        System.out.println("Key Pressed: " + KeyEvent.getKeyText(key) + " in state: " + currentGameState); 
        
        if (currentGameState == GameStateEnum.GAME_OVER) {
            if (key == KeyEvent.VK_R) {
                startGame();
            } else if (key == KeyEvent.VK_ESCAPE) {
                 handleScoreSubmission(); 
            }
        } else if (currentGameState == GameStateEnum.PLAYING) {
            if (key == KeyEvent.VK_B) { 
                enterShop();
            } else if (inputHandler != null) { 
                 inputHandler.keyPressed(e);
            } else { /* Error */ }
        } else if (currentGameState == GameStateEnum.SHOP) {
             if (key == KeyEvent.VK_B || key == KeyEvent.VK_ESCAPE) { 
                 exitShop();
             }
        } else if (currentGameState == GameStateEnum.CUTSCENE) {
             if (key == KeyEvent.VK_ENTER || key == KeyEvent.VK_SPACE || key == KeyEvent.VK_ESCAPE) {
                 if (cutscenePanel != null) cutscenePanel.advanceFrame(); 
             }
        } else if (currentGameState == GameStateEnum.LEADERBOARD || 
                   currentGameState == GameStateEnum.SETTINGS || 
                   currentGameState == GameStateEnum.CREDITS) {
            if (key == KeyEvent.VK_ESCAPE) { 
                 currentGameState = GameStateEnum.MAIN_MENU;
                 setupMainMenu();
            }
       }
    }

    @Override
    public void keyReleased(KeyEvent e) {
        // Pass to InputHandler ONLY if in PLAYING state
        if (currentGameState == GameStateEnum.PLAYING && inputHandler != null) {
             inputHandler.keyReleased(e);
        }
    }

    // Helper method from Main Menu Logic (ensure drawMainMenu exists)
    public void handleMenuSelection(String selection) {
        System.out.println("Menu Selection: " + selection); 
        switch (selection) {
            case "Start Game":
                startGame();
                break;
            case "Settings": 
                 currentGameState = GameStateEnum.SETTINGS;
                 setupSettingsPanel();
                break;
            case "Leaderboard":
                currentGameState = GameStateEnum.LEADERBOARD;
                setupLeaderboardPanel();
                break;
            case "Credits": 
                 currentGameState = GameStateEnum.CREDITS;
                 setupCreditsPanel();
                break;
            case "Exit":
                exitGame(); 
                break;
        }
    }

    // setupShopPanel - Ensure this adds the panel
    private void setupShopPanel() {
        removeAll();
        if (shopPanel != null) {
            shopPanel.setupShopUI(); // Refresh shop UI before showing
            add(shopPanel);
        } else {
            System.err.println("ERROR: shopPanel is null during setup!");
            // Optionally switch back to main menu or show error
            currentGameState = GameStateEnum.MAIN_MENU;
            setupMainMenu();
            return;
        }
        revalidate();
        repaint();
        // Shop panel likely handles its own focus/listeners
    }
    
    // Ensure enterShop and exitShop use the setup method correctly
    private void enterShop() {
        if (currentGameState == GameStateEnum.PLAYING) {
            gameLoop.stop(); // Pause game
            currentGameState = GameStateEnum.SHOP;
            removeKeyListener(inputHandler); // Remove game input listener
            setupShopPanel(); // Setup and show the shop panel
        }
    }

    private void exitShop() {
        if (currentGameState == GameStateEnum.SHOP) {
            currentGameState = GameStateEnum.PLAYING;
            removeAll(); // Remove shop panel
            addKeyListener(inputHandler); // Re-add game input listener
            setFocusable(true);
            requestFocusInWindow();
            revalidate();
            repaint();
            gameLoop.start(); // Resume game
        }
    }

    // Game Update Method (Called by Timer)
    private void updateGame() {
        if (gameUpdateSystem != null && currentGameState == GameStateEnum.PLAYING) {
            gameUpdateSystem.update();
        }
        // Could add checks here if other states need updates driven by this timer
    }
    
    // Score Submission Logic
    private void handleScoreSubmission() {
        if (gameState.gameOver) {
            String playerName = JOptionPane.showInputDialog(
                this, 
                "Game Over! Your Score: " + gameState.score + "\nEnter your name for the leaderboard:", 
                "Enter Name", 
                JOptionPane.PLAIN_MESSAGE
            );

            if (playerName != null && !playerName.trim().isEmpty()) {
                // Use the helper method from LeaderboardManager
                LeaderboardManager.addEntryFromGameState(gameState, playerName.trim());
                System.out.println("Score submitted for: " + playerName.trim());
            } else {
                System.out.println("Score submission cancelled or name empty.");
            }
            // Regardless of submission, go to leaderboard screen
            currentGameState = GameStateEnum.LEADERBOARD;
            setupLeaderboardPanel(); 
        } else {
             System.err.println("handleScoreSubmission called when game not over?");
        }
    }

    // Helper to create and layout menu buttons
    private void setupMenuButtons() {
        menuButtonPanel = new JPanel();
        menuButtonPanel.setLayout(new BoxLayout(menuButtonPanel, BoxLayout.Y_AXIS));
        menuButtonPanel.setOpaque(false); // Make transparent

        // Add some space at the top
        menuButtonPanel.add(Box.createVerticalGlue());

        startButton = createMenuButton("Start Game");
        settingsButton = createMenuButton("Settings");
        leaderboardButton = createMenuButton("Leaderboard");
        creditsButton = createMenuButton("Credits");
        exitButton = createMenuButton("Exit");

        menuButtonPanel.add(startButton);
        menuButtonPanel.add(Box.createRigidArea(new Dimension(0, 15))); // Spacing
        menuButtonPanel.add(settingsButton);
        menuButtonPanel.add(Box.createRigidArea(new Dimension(0, 15)));
        menuButtonPanel.add(leaderboardButton);
        menuButtonPanel.add(Box.createRigidArea(new Dimension(0, 15)));
        menuButtonPanel.add(creditsButton);
        menuButtonPanel.add(Box.createRigidArea(new Dimension(0, 15)));
        menuButtonPanel.add(exitButton);

        // Add space at the bottom
        menuButtonPanel.add(Box.createVerticalGlue());
    }

    // Helper to create styled menu buttons
    private JButton createMenuButton(String text) {
        JButton button = new JButton(text);
        button.setFont(new Font("Arial", Font.BOLD, 28));
        button.setForeground(Constants.MENU_ITEM_DEFAULT_COLOR);
        button.setBackground(new Color(0,0,0,0)); // Transparent background
        button.setOpaque(false);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setContentAreaFilled(false);
        button.setAlignmentX(Component.CENTER_ALIGNMENT); // Center align in BoxLayout
        button.setActionCommand(text); // Set action command to button text
        button.addActionListener(this); // Add listener
        // Add mouse hover effect (optional)
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setForeground(Constants.MENU_ITEM_SELECTED_COLOR);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setForeground(Constants.MENU_ITEM_DEFAULT_COLOR);
            }
        });
        return button;
    }
} 