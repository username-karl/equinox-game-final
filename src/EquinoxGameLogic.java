import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;
import java.util.Random;
import javax.swing.*;

public class EquinoxGameLogic extends JPanel implements ActionListener, KeyListener {

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
    Image minibossImgvar1;
    ArrayList<Image> enemyImgArray;
    ArrayList<Image> specialEnemyImgArray;
    // Game Settings
    int difficulty = 2;
    long remainingCooldown;
    long remainingCooldownE;

    // Ship
    int shipWidth = tileSize * 2; // 64px
    int shipHeight = tileSize; // 32px
    int shipX = tileSize * columns / 2 - tileSize;
    int shipY = boardHeight - tileSize * 2;
    int shipVelocityX = 0; // Initial velocity is 0
    int shipAcceleration = 2; // Acceleration rate
    int shipMaxSpeed = 8; // Maximum speed
    int shipDeceleration = 1; // Deceleration rate

    ShipUser ship;

    // Enemies
    ArrayList<Enemy> enemyArray;
    int enemyWidth = tileSize * 2;
    int enemyHeight = tileSize;
    int enemyX = tileSize;
    int enemyY = tileSize;
    int enemyVelocityX = 1;
    int enemyRows = 3;
    int enemyColumns = 7;

    // Bullets
    ArrayList<Bullet> bulletArray;
    int bulletWidth = tileSize / 8; // Bullet size width
    int bulletHeight = tileSize / 2;
    int bulletVelocityY = -10; // Bullet movespeed
    // TacticalQ
    ArrayList<TacticalQ> tacticalArray;
    int tacticalqWidth = tileSize / 2; // Bullet size width
    int tacticalqHeight = tileSize * 5;
    int tacticalqVelocityY = -50; // Bullet movespeed
    long lastTacticalQUseTime; // Last tactical use time
    long tacticalQCooldown = 3000; // Tactical cooldown in ms
    //TacticalE
    ArrayList<TacticalE> tacticalEArray;
    int tacticaleWidth = tileSize; // Smaller projectile
    int tacticaleHeight = tileSize / 4;
    int tacticaleVelocityY = -10;
    long lastTacticalEUseTime;
    long tacticalECooldown = 9000;
    //Enemy Bullets
    ArrayList<EnemyBullet> enemyBulletArray;
    int enemyBulletWidth = tileSize / 8;
    int enemyBulletHeight = tileSize / 2;

    // Timer
    Timer gameLoop;



    // Movement flags
    boolean moveLeft = false;
    boolean moveRight = false;

    EquinoxGameLogic() {
        //SetFrame
        setPreferredSize(new Dimension(boardWidth, boardHeight));
        setBackground(Color.DARK_GRAY);

        setFocusable(true);
        addKeyListener(this);
        //GameState init
        gameState = new GameState();
        gameState.currentStage = new Stage(1,4);




        // Image loading
        // Creatures
        shipImg = new ImageIcon(getClass().getResource("./img/protagtest.png")).getImage();
        enemyImgVar1 = new ImageIcon(getClass().getResource("./img/enemyvar1.gif")).getImage();
        enemyImgVar2 = new ImageIcon(getClass().getResource("./img/monstertestvar2.png")).getImage();
        enemyImgVar3 = new ImageIcon(getClass().getResource("./img/monstertestvar3.png")).getImage();


        enemyImgArray = new ArrayList<Image>();
        enemyImgArray.add(enemyImgVar1);
        enemyImgArray.add(enemyImgVar2);
        enemyImgArray.add(enemyImgVar3);


        minibossImgvar1 = new ImageIcon(getClass().getResource("./img/minibossvar1.png")).getImage();
        specialEnemyImgArray = new ArrayList<Image>();
        specialEnemyImgArray.add(minibossImgvar1);


        // //Maps
        world1BG = new ImageIcon(getClass().getResource("./img/world1BG.png")).getImage();
        // Misc
        laserBlue = new ImageIcon(getClass().getResource("./img/laserBlue.png")).getImage();
        enemyBulletImg = new ImageIcon(getClass().getResource("./img/laserRed.png")).getImage();
        // Ship
        ship = new ShipUser(shipX, shipY, shipWidth, shipHeight, shipImg);

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

    // Draw assets
    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        if (world1BG != null) {
            g.drawImage(world1BG, 0, 0, boardWidth, boardHeight, this);
        }
        draw(g);
    }

    // DRAW
    public void draw(Graphics g) {

        // Draw Ship
        g.drawImage(ship.img, ship.getX(), ship.getY(), ship.getWidth(), ship.getHeight(), null);

        // Draw Enemies
        for (int i = 0; i < enemyArray.size(); i++) {
            Enemy enemy = enemyArray.get(i);
            if (enemy.isAlive()) {
                if(enemy instanceof Miniboss){
                    g.drawImage(enemy.img, enemy.getX(), enemy.getY(), enemy.getWidth(), enemy.getHeight(), null);
                }else{
                    g.drawImage(enemy.img, enemy.getX(), enemy.getY(), enemy.getWidth(), enemy.getHeight(), null);
                }
            }
        }


        // Draw Bullets
        g.setColor(Color.white);
        for (int i = 0; i < bulletArray.size(); i++) {
            Block bullet = bulletArray.get(i);
            if (!bullet.isUsed()) {
                g.drawImage(laserBlue, bullet.getX(), bullet.getY(), bullet.getWidth(), bullet.getHeight(), null);
                // g.drawRect(bullet.getX(),bullet.getY(),bullet.getWidth(),bullet.getHeight());
                // g.fillRect(bullet.getX(),bullet.getY(),bullet.getWidth(),bullet.getHeight());
            }
        }
        // Draw TacticalQ Projectile
        for (int i = 0; i < tacticalArray.size(); i++) {
            Block bullet = tacticalArray.get(i);
            if (!bullet.isUsed()) {
                // g.drawImage(laserBlue,bullet.getX(),bullet.getY(),bullet.getWidth(),bullet.getHeight(),null);
                // g.drawRect(bullet.getX(),bullet.getY(),bullet.getWidth(),bullet.getHeight());
                g.fillRect(bullet.getX(), bullet.getY(), bullet.getWidth(), bullet.getHeight());
            }
        }
        // Draw TacticalE Projectile
        for (int i = 0; i < tacticalEArray.size(); i++) {
            Block bullet = tacticalEArray.get(i);
            if (!bullet.isUsed()) {
                // g.drawImage(laserBlue,bullet.getX(),bullet.getY(),bullet.getWidth(),bullet.getHeight(),null);
                // g.drawRect(bullet.getX(),bullet.getY(),bullet.getWidth(),bullet.getHeight());
                g.fillRect(bullet.getX(), bullet.getY(), bullet.getWidth(), bullet.getHeight());
            }
        }
        //Draw Enemy Bullets
        for (int i = 0; i < enemyBulletArray.size(); i++) {
            EnemyBullet bullet = enemyBulletArray.get(i);
            g.drawImage(enemyBulletImg, bullet.getX(), bullet.getY(), bullet.getWidth(), bullet.getHeight(), null);
        }




        //DRAW STATS
        // Draw Score
        g.setColor(Color.LIGHT_GRAY);
        g.setFont(new Font("Arial", Font.PLAIN, 24));
        if (gameState.gameOver) {
            g.drawString("Game Over: " + String.valueOf(gameState.score), 10, 35);
        } else {
            g.drawString(String.valueOf(gameState.score), 10, 35);
        }

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
        //Draw Stage and Wave String
        g.drawString("World: " + gameState.currentStage.getStageNumber() + " Wave: " + gameState.currentStage.getCurrentWave(), 10, 60);

    }

    // MOVE
    public void moveGame() {
        // ENEMIES
        for (int i = 0; i < enemyArray.size(); i++) {
            Enemy enemy = enemyArray.get(i);
            if (enemy.isAlive()) {
                enemy.move(boardWidth, enemyWidth, enemyHeight);
                if(enemy.isMoveDown()){
                    enemy.moveDown(enemyHeight);
                }
                if(enemy instanceof ShootingEnemy){
                    ((ShootingEnemy) enemy).shoot(enemyBulletArray, enemyBulletImg, enemyBulletWidth, enemyBulletHeight);
                }
                if(enemy instanceof Miniboss){
                    ((Miniboss) enemy).moveY();
                    ((Miniboss) enemy).shoot(enemyBulletArray, enemyBulletImg, enemyBulletWidth, enemyBulletHeight);
                }
                // Lose condition
                if (enemy.getY() >= ship.getY()) {
                    gameState.gameOver = true;
                }
            }
        }
        //Enemy Bullets
        for (int i = 0; i < enemyBulletArray.size(); i++) {
            EnemyBullet bullet = enemyBulletArray.get(i);
            bullet.move();
            // Bullet collision check
            if(detectCollision(bullet, ship)){
                gameState.gameOver = true;
            }
        }

        // Bullets Move
        for (int i = 0; i < bulletArray.size(); i++) {
            Bullet bullet = bulletArray.get(i);
            bullet.setY(bullet.getY() + bulletVelocityY);

            // Bullet collision check
            for (int j = 0; j < enemyArray.size(); j++) {
                Enemy enemy = enemyArray.get(j);
                if (!bullet.isUsed() && enemy.isAlive() && detectCollision(bullet, enemy)) {
                    bullet.setUsed(true);

                    if(enemy instanceof Miniboss){
                        ((Miniboss) enemy).setHitpoints(((Miniboss) enemy).getHitpoints() - 1);
                        if(((Miniboss) enemy).getHitpoints() <= 0){
                            gameState.score += 10000;
                            enemy.setAlive(false);
                            gameState.enemyCount--;
                        }
                    }else{
                        enemy.setAlive(false);
                        gameState.enemyCount--;
                    }
                    gameState.score += 100;
                }
            }
        }

        // TacticalQ Move
        for (int i = 0; i < tacticalArray.size(); i++) {
            Bullet bullet = tacticalArray.get(i);
            bullet.setY(bullet.getY() + tacticalqVelocityY);

            // TacticalQ collision check
            for (int j = 0; j < enemyArray.size(); j++) {
                Enemy enemy = enemyArray.get(j);
                if (!bullet.isUsed() && enemy.isAlive() && detectCollision(bullet, enemy)) {
//                    enemy.setAlive(false);
//                    gameState.enemyCount--;
//                    gameState.score += 100;
                    if(enemy instanceof Miniboss){
                        ((Miniboss) enemy).setHitpoints(((Miniboss) enemy).getHitpoints() - 1);
                        if(((Miniboss) enemy).getHitpoints() <= 0){
                            gameState.score += 10000;
                            enemy.setAlive(false);
                            gameState.enemyCount--;
                        }
                    }else{
                        enemy.setAlive(false);
                        gameState.enemyCount--;
                    }
                    gameState.score += 100;
                }
            }
        }
        // TacticalE Move
        for (int i = 0; i < tacticalEArray.size(); i++) {
            TacticalE tacticale = tacticalEArray.get(i);
            tacticale.setY(tacticale.getY() + tacticaleVelocityY);

            // TacticalE collision check
            for (int j = 0; j < enemyArray.size(); j++) {
                Enemy enemy = enemyArray.get(j);
                // Check if the enemy is in the first row and if it collides with TacticalE
                if (!tacticale.isUsed() && enemy.isAlive() && detectCollision(tacticale, enemy)) {
                    tacticale.setUsed(true);
//                    enemy.setAlive(false);
//                    gameState.enemyCount--;
//                    gameState.score += 100;
                    if(enemy instanceof Miniboss){
                        ((Miniboss) enemy).setHitpoints(((Miniboss) enemy).getHitpoints() - 1);
                        if(((Miniboss) enemy).getHitpoints() <= 0){
                            gameState.score += 10000;
                            enemy.setAlive(false);
                            gameState.enemyCount--;
                        }
                    }else{
                        enemy.setAlive(false);
                        gameState.enemyCount--;
                    }
                    gameState.score += 100;
                }
            }
        }

        // Optimizations (Optimized bullets off-screen clear)
        while (bulletArray.size() > 0 && (bulletArray.get(0).isUsed() || bulletArray.get(0).getY() < 0)) {
            bulletArray.remove(0);// Removes the first element of the array
        }
        while (tacticalArray.size() > 0 && (tacticalArray.get(0).isUsed() || tacticalArray.get(0).getY() < 0)) {
            tacticalArray.remove(0);// Removes the first element of the array
        }
        while (tacticalEArray.size() > 0 && (tacticalEArray.get(0).isUsed() || tacticalEArray.get(0).getY() < 0)) {
            tacticalEArray.remove(0);// Removes the first element of the array
        }
        while (enemyBulletArray.size() > 0 && (enemyBulletArray.get(0).isUsed() || enemyBulletArray.get(0).getY() > boardHeight)) {
            enemyBulletArray.remove(0);// Removes the first element of the array
        }

        // Next wave of enemies
        if (gameState.enemyCount == 0) {
            if (gameState.currentStage.isMinibossSpawned()) {
                // Move to the next stage
                gameState.currentStage.setStageNumber(gameState.currentStage.getStageNumber() + 1);
                gameState.currentStage.setCurrentWave(1);
                gameState.currentStage.setMinibossSpawned(false);
                reset();
            } else if (gameState.currentStage.getCurrentWave() < gameState.currentStage.getTotalWaves()) {
                // Move to the next wave
                gameState.currentStage.setCurrentWave(gameState.currentStage.getCurrentWave() + 1);
                reset();
            } else {
                // Spawn the miniboss
                gameState.currentStage.setMinibossSpawned(true);
                createMiniboss();
            }
        }

        long currentTime = System.currentTimeMillis();
        remainingCooldown = tacticalQCooldown - (currentTime - lastTacticalQUseTime);
        if (remainingCooldown < 0) {
            remainingCooldown = 0;
        }
        remainingCooldownE = tacticalECooldown - (currentTime - lastTacticalEUseTime);
        if (remainingCooldownE < 0) {
            remainingCooldownE = 0;
        }

        // Ship movement
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

    // Create enemies
    public void createEnemies() {
        Random random = new Random();
        for (int r = 0; r < enemyRows; r++) {
            for (int c = 0; c < enemyColumns; c++) {
                int randomImgIndex = random.nextInt(enemyImgArray.size());
                int enemyType = random.nextInt(6); // Now 0-5 (6 possibilities)
                Enemy enemy;
                switch (enemyType) {
                    case 1:
                        enemy = new FastEnemy(
                                enemyX + c * enemyWidth,
                                enemyY + r * enemyHeight,
                                enemyWidth,
                                enemyHeight,
                                enemyImgArray.get(randomImgIndex),
                                enemyVelocityX
                        );
                        break;
                    case 2:
                        enemy = new FastEnemy(
                                enemyX + c * enemyWidth,
                                enemyY + r * enemyHeight,
                                enemyWidth,
                                enemyHeight,
                                enemyImgArray.get(randomImgIndex),
                                enemyVelocityX
                        );
                        break;
                    case 3:
                        enemy = new ShootingEnemy(
                                enemyX + c * enemyWidth,
                                enemyY + r * enemyHeight,
                                enemyWidth,
                                enemyHeight,
                                enemyImgArray.get(randomImgIndex),
                                enemyVelocityX
                        );
                        break;
                    case 4:
                        enemy = new FastEnemy(
                                enemyX + c * enemyWidth,
                                enemyY + r * enemyHeight,
                                enemyWidth,
                                enemyHeight,
                                enemyImgArray.get(randomImgIndex),
                                enemyVelocityX
                        );
                        break;
                    case 5:
                        enemy = new FastEnemy(
                                enemyX + c * enemyWidth,
                                enemyY + r * enemyHeight,
                                enemyWidth,
                                enemyHeight,
                                enemyImgArray.get(randomImgIndex),
                                enemyVelocityX
                        );
                        break;
                    case 6:
                        enemy = new FastEnemy(
                                enemyX + c * enemyWidth,
                                enemyY + r * enemyHeight,
                                enemyWidth,
                                enemyHeight,
                                enemyImgArray.get(randomImgIndex),
                                enemyVelocityX
                        );
                        break;
                    default:
                        enemy = new Enemy(
                                enemyX + c * enemyWidth,
                                enemyY + r * enemyHeight,
                                enemyWidth,
                                enemyHeight,
                                enemyImgArray.get(randomImgIndex),
                                enemyVelocityX
                        );
                        break;
                }
                enemyArray.add(enemy);
            }
        }
        gameState.enemyCount = enemyArray.size();
    }
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
                2);
        enemyArray.add(miniboss);
        gameState.enemyCount = enemyArray.size();
    }
    public void reset(){
        enemyArray.clear();
        bulletArray.clear();
        enemyBulletArray.clear();
        createEnemies();
    }

    public boolean detectCollision(Block a, Block b) {
        return a.getX() < b.getX() + b.getWidth() && // entity a's top left corner doesn't reach b's top right corner
                a.getX() + a.getWidth() > b.getX() && // entity a's top right corner passes b's top left corner
                a.getY() < b.getY() + b.getHeight() && // entity a's top left corner doesn't reach b's bottom left corner
                a.getY() + a.getHeight() > b.getY(); // entity a's bottom left corner passes b's top let corner
    }

    //
    @Override
    public void actionPerformed(ActionEvent e) {

        moveGame();
        repaint();
        if (gameState.gameOver) {
            gameLoop.stop();
        }
    }

    @Override
    public void keyTyped(KeyEvent e) {
    }

    @Override
    public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_A) {
            moveLeft = true;
        } else if (e.getKeyCode() == KeyEvent.VK_D) {
            moveRight = true;
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_A) {
            moveLeft = false;
        } else if (e.getKeyCode() == KeyEvent.VK_D) {
            moveRight = false;
        } else if (e.getKeyCode() == KeyEvent.VK_SPACE) {
            Bullet bullet = new Bullet(ship.getX() + ship.getWidth() * 15 / 32, ship.getY(), bulletWidth, bulletHeight,
                    laserBlue);
            bulletArray.add(bullet);
        } else if (e.getKeyCode() == KeyEvent.VK_Q) {
            // TacticalQ
            long currentTime = System.currentTimeMillis();

            if (currentTime - lastTacticalQUseTime >= tacticalQCooldown) {
                // Cooldown is over, allow the ability
                TacticalQ tacticalq = new TacticalQ(ship.getX() + ship.getWidth() * 15 / 32, ship.getY(),
                        tacticalqWidth, tacticalqHeight, null);
                tacticalArray.add(tacticalq);
                lastTacticalQUseTime = currentTime; // Update the last use time
            } else {
                // Ability is on cooldown
                System.out.println("Tactical Q on cooldown!");
            }
        } else if (e.getKeyCode() == KeyEvent.VK_E) {
            // TacticalE
            long currentTime = System.currentTimeMillis();

            if (currentTime - lastTacticalEUseTime >= tacticalECooldown) {
                // Cooldown is over, allow the ability
                int numBullets = boardWidth / tileSize; // Number of bullets based on board width
                int startX = 0; // Start at the left edge of the screen

                for (int i = 0; i < numBullets; i++) {
                    TacticalE tacticale = new TacticalE(startX + i * tileSize, ship.getY(),
                            tacticaleWidth, tacticaleHeight, null);
                    tacticalEArray.add(tacticale);
                }
                lastTacticalEUseTime = currentTime; // Update the last use time
            } else {
                // Ability is on cooldown
                System.out.println("Tactical E on cooldown!");
            }
        }

    }
}