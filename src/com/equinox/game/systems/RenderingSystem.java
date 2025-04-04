package com.equinox.game.systems;

import com.equinox.game.data.GameState;
import com.equinox.game.entities.Bullet;
import com.equinox.game.entities.EnemyBullet;
import com.equinox.game.entities.LaserBeam;
import com.equinox.game.entities.ShipUser;
import com.equinox.game.entities.WaveBlast;
import com.equinox.game.entities.enemies.Enemy;
import com.equinox.game.entities.enemies.MainBoss;
import com.equinox.game.entities.enemies.Miniboss;
import com.equinox.game.utils.AssetLoader;
import com.equinox.game.utils.Constants;
import com.equinox.game.ui.EquinoxGameLogic; // Import needed for GameStateEnum

import java.awt.*;
import java.util.ArrayList;

public class RenderingSystem {

    private AssetLoader assetLoader;

    public RenderingSystem(AssetLoader assetLoader) {
        this.assetLoader = assetLoader;
    }

    // Updated render method - takes GameState and UI state
    public void render(Graphics g, GameState gameState, EquinoxGameLogic.GameStateEnum currentState) {
        
        drawBackground(g, gameState);

        switch (currentState) {
            case MENU:
                // Menu drawing remains in EquinoxGameLogic for now
                break;
            case PLAYING:
            case GAME_OVER:
                // Pass GameState to drawGame
                drawGame(g, gameState);
                if (currentState == EquinoxGameLogic.GameStateEnum.GAME_OVER) {
                    drawGameOverMessage(g);
                }
                break;
            case CUTSCENE:
                 break; // Handled by CutscenePanel
        }
    }

    private void drawBackground(Graphics g, GameState gameState) {
         Image currentBackground = assetLoader.getBackgroundImage(gameState.currentStage.getStageNumber());
        if (currentBackground != null) {
            g.drawImage(currentBackground, 0, 0, Constants.BOARD_WIDTH, Constants.BOARD_HEIGHT, null);
        } else {
            g.setColor(Color.DARK_GRAY);
            g.fillRect(0, 0, Constants.BOARD_WIDTH, Constants.BOARD_HEIGHT);
        }
    }

    // DRAW GAME METHOD - Simplified, takes GameState
    private void drawGame(Graphics g, GameState gameState) {
        // Get all necessary data from gameState
        drawShip(g, gameState.ship, gameState.isPhaseShiftActive);
        drawEnemies(g, gameState.enemyArray);
        drawPlayerBullets(g, gameState.bulletArray);
        drawTacticalAbilities(g, gameState.waveBlastArray, gameState.laserBeamArray);
        drawEnemyBullets(g, gameState.enemyBulletArray);
        drawGameStats(g, gameState); // Pass GameState to HUD drawing
    }

    // DRAW PLAYER SHIP
    private void drawShip(Graphics g, ShipUser ship, boolean isPhaseShiftActive) {
        if (ship == null) return;
        Graphics2D g2d = (Graphics2D) g.create();
        if (isPhaseShiftActive) {
            g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.5f));
        }
        g2d.drawImage(ship.getImage(), ship.getX(), ship.getY(), ship.getWidth(), ship.getHeight(), null);
        g2d.dispose();
    }

    // DRAW ENEMY ARRAY
    private void drawEnemies(Graphics g, java.util.List<Enemy> enemyArray) {
        if (enemyArray == null) return;
        for (Enemy enemy : enemyArray) {
            if (enemy.isAlive()) {
                g.drawImage(enemy.getImage(), enemy.getX(), enemy.getY(), enemy.getWidth(), enemy.getHeight(), null);
                if (enemy instanceof Miniboss) {
                    drawBossHealthBar(g, (Miniboss) enemy);
                } else if (enemy instanceof MainBoss) {
                    drawBossHealthBar(g, (MainBoss) enemy);
                }
            }
        }
    }

    // DRAW PLAYER BULLETS
    private void drawPlayerBullets(Graphics g, java.util.List<Bullet> bulletArray) {
        if (bulletArray == null) return;
        Image playerLaserImg = assetLoader.getImage(Constants.PLAYER_LASER_IMG_KEY);
        for (Bullet bullet : bulletArray) {
            if (!bullet.isUsed()) {
                if (playerLaserImg != null) {
                     g.drawImage(playerLaserImg, bullet.getX(), bullet.getY(), bullet.getWidth(), bullet.getHeight(), null);
                } else {
                    g.setColor(Color.WHITE);
                    g.fillRect(bullet.getX(), bullet.getY(), bullet.getWidth(), bullet.getHeight());
                }
            }
        }
    }

    // DRAW TACTICAL ABILITIES
    private void drawTacticalAbilities(Graphics g, java.util.List<WaveBlast> waveBlastArray, java.util.List<LaserBeam> laserBeamArray) {
        if (waveBlastArray != null) {
             g.setColor(Color.WHITE);
            for (WaveBlast waveBlast : waveBlastArray) {
                if (!waveBlast.isUsed()) {
                    g.fillRect(waveBlast.getX(), waveBlast.getY(), waveBlast.getWidth(), waveBlast.getHeight());
                }
            }
        }
        if (laserBeamArray != null) {
            g.setColor(Color.CYAN);
            for (LaserBeam laserBeam : laserBeamArray) {
                if (!laserBeam.isUsed()) {
                    g.fillRect(laserBeam.getX(), laserBeam.getY(), laserBeam.getWidth(), laserBeam.getHeight());
                }
            }
        }
    }

    // DRAW ENEMY BULLETS
    private void drawEnemyBullets(Graphics g, java.util.List<EnemyBullet> enemyBulletArray) {
        if (enemyBulletArray == null) return;
        Image enemyLaserImg = assetLoader.getImage(Constants.ENEMY_LASER_IMG_KEY);
        for (EnemyBullet bullet : enemyBulletArray) {
            if (!bullet.isUsed()) {
                if (enemyLaserImg != null) {
                    g.drawImage(enemyLaserImg, bullet.getX(), bullet.getY(), bullet.getWidth(), bullet.getHeight(), null);
                } else {
                     g.setColor(Color.RED);
                     g.fillRect(bullet.getX(), bullet.getY(), bullet.getWidth(), bullet.getHeight());
                }
            }
        }
    }

    // DRAW GAME STATS (HUD) - Simplified, takes GameState
    private void drawGameStats(Graphics g, GameState gameState) {
        int padding = Constants.HUD_PADDING;
        int topY = Constants.HUD_TOP_Y;
        int bottomY = Constants.BOARD_HEIGHT - padding - 20;
        Font baseFont = new Font("Arial", Font.PLAIN, 16);
        Font boldFont = new Font("Arial", Font.BOLD, 16);
        Font cooldownFont = new Font("Arial", Font.PLAIN, 14);
        Font stageFont = new Font("Arial", Font.BOLD, 18);

        // --- Top Left: Score, Money, Killed ---
        g.setFont(baseFont);
        g.setColor(Constants.SCORE_COLOR);
        g.drawString("Score: " + gameState.score, padding, topY);
        g.setColor(Constants.MONEY_COLOR);
        g.drawString("Money: " + gameState.money, padding, topY + 20);
        g.setColor(Constants.KILLED_COLOR);
        g.drawString("Killed: " + gameState.enemySlain, padding, topY + 40);

        // --- Top Center: World / Wave ---
        if (gameState.currentStage != null) {
            g.setFont(stageFont);
            g.setColor(Constants.STAGE_INFO_COLOR);
            String stageText = "World: " + gameState.currentStage.getStageNumber() + " | Wave: " + gameState.currentStage.getCurrentWave();
            FontMetrics fmStage = g.getFontMetrics();
            int stageWidth = fmStage.stringWidth(stageText);
            g.drawString(stageText, (Constants.BOARD_WIDTH - stageWidth) / 2, topY + 10);
        }

        // --- Top Right: Player Health Bar ---
        if (gameState.ship != null) {
            int healthBarWidth = 150;
            int healthBarHeight = 20;
            int healthBarX = Constants.BOARD_WIDTH - healthBarWidth - padding;
            int healthBarY = topY - 5;
            double healthPercent = (double) gameState.ship.getHealth() / gameState.ship.getMaxHealth();
            int filledWidth = (int) (healthBarWidth * healthPercent);

            g.setFont(boldFont);
            g.setColor(Constants.HEALTH_TEXT_COLOR);
            String healthText = "HP: " + gameState.ship.getHealth() + "/" + gameState.ship.getMaxHealth();
            g.drawString(healthText, healthBarX, healthBarY + healthBarHeight + 15);

            g.setColor(Constants.HEALTH_BAR_BG_COLOR);
            g.fillRect(healthBarX, healthBarY, healthBarWidth, healthBarHeight);
            g.setColor(Constants.HEALTH_BAR_FILL_COLOR);
            g.fillRect(healthBarX, healthBarY, filledWidth, healthBarHeight);
            g.setColor(Constants.HEALTH_BAR_BORDER_COLOR);
            g.drawRect(healthBarX, healthBarY, healthBarWidth, healthBarHeight);
        }

        // --- Bottom Left: Ability Cooldowns (Get from GameState) ---
        int cooldownX = padding;
        int cooldownY = bottomY;
        g.setFont(cooldownFont);

        // Wave Blast (Q)
        String qText;
        if (gameState.remainingWaveBlastCooldown > 0) {
            g.setColor(Constants.COOLDOWN_Q_COLOR);
            qText = String.format("Q: %.1fs", (double) gameState.remainingWaveBlastCooldown / 1000);
        } else {
            g.setColor(Constants.COOLDOWN_READY_COLOR);
            qText = "Q: Ready";
        }
        g.drawString(qText, cooldownX, cooldownY);

        // Laser Beam (E)
        String eText;
        int eOffset = g.getFontMetrics().stringWidth(qText) + 15;
        if (gameState.remainingLaserBeamCooldown > 0) {
             g.setColor(Constants.COOLDOWN_E_COLOR);
            eText = String.format("E: %.1fs", (double) gameState.remainingLaserBeamCooldown / 1000);
        } else {
            g.setColor(Constants.COOLDOWN_READY_COLOR);
            eText = "E: Ready";
        }
        g.drawString(eText, cooldownX + eOffset, cooldownY);

        // Phase Shift (R)
        String rText;
        int rOffset = eOffset + g.getFontMetrics().stringWidth(eText) + 15;
        if (gameState.isPhaseShiftActive) {
            g.setColor(Constants.COOLDOWN_R_ACTIVE_COLOR);
            rText = "R: ACTIVE";
        } else if (gameState.remainingPhaseShiftCooldown > 0) {
            g.setColor(Constants.COOLDOWN_R_COLOR);
            rText = String.format("R: %.1fs", (double) gameState.remainingPhaseShiftCooldown / 1000);
        } else {
            g.setColor(Constants.COOLDOWN_READY_COLOR);
            rText = "R: Ready";
        }
        g.drawString(rText, cooldownX + rOffset, cooldownY);

        // --- Cheat Indicator (Get from GameState) ---
        if (gameState.cheatsEnabled) {
            g.setFont(boldFont);
            g.setColor(Constants.CHEAT_INDICATOR_COLOR);
            String cheatText = "CHEATS ACTIVE";
            FontMetrics fmCheat = g.getFontMetrics();
            int cheatWidth = fmCheat.stringWidth(cheatText);
            g.drawString(cheatText, (Constants.BOARD_WIDTH - cheatWidth) / 2, bottomY);
        }
    }

    // DRAW BOSS HEALTH BARS
    private void drawBossHealthBar(Graphics g, Miniboss miniboss) {
         if (miniboss == null || !miniboss.isAlive()) return;
         drawGenericBossHealthBar(g, miniboss.getEnemyBossName(), "Miniboss", miniboss.getHitpoints(), miniboss.getMaxHitpoints());
    }

    private void drawBossHealthBar(Graphics g, MainBoss mainboss) {
        if (mainboss == null || !mainboss.isAlive()) return;
        drawGenericBossHealthBar(g, mainboss.getEnemyBossName(), "BOSS", mainboss.getHitpoints(), mainboss.getMaxHitpoints());
    }

    private void drawGenericBossHealthBar(Graphics g, String name, String title, int currentHp, int maxHp) {
        int healthBarWidth = Constants.BOARD_WIDTH / 2;
        int healthBarHeight = 20;
        int healthBarX = Constants.BOARD_WIDTH / 4;
        int healthBarY = 40;
        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.BOLD, 12));
        g.drawString(title + ": " + name, Constants.BOARD_WIDTH/3, 60);
        double healthPercentage = (maxHp > 0) ? (double) currentHp / maxHp : 0;
        int filledWidth = (int) (healthBarWidth * healthPercentage);

        g.setColor(Color.GRAY);
        g.fillRect(healthBarX, healthBarY, healthBarWidth, healthBarHeight);
        g.setColor(Color.RED);
        g.fillRect(healthBarX, healthBarY, filledWidth, healthBarHeight);
        g.setColor(Color.BLACK);
        g.drawRect(healthBarX, healthBarY, healthBarWidth, healthBarHeight);
    }

    // Draw Game Over Message
    private void drawGameOverMessage(Graphics g) {
        g.setColor(Constants.GAME_OVER_MSG_COLOR);
        g.setFont(new Font("Arial", Font.BOLD, 48));
        FontMetrics fm = g.getFontMetrics();
        String gameOverMsg = "GAME OVER";
        int msgWidth = fm.stringWidth(gameOverMsg);
        g.drawString(gameOverMsg, (Constants.BOARD_WIDTH - msgWidth) / 2, Constants.BOARD_HEIGHT / 2 - 30);

        g.setFont(new Font("Arial", Font.PLAIN, 24));
        fm = g.getFontMetrics();
        String restartMsg = "Press 'R' to Retry";
        int restartWidth = fm.stringWidth(restartMsg);
        g.drawString(restartMsg, (Constants.BOARD_WIDTH - restartWidth) / 2, Constants.BOARD_HEIGHT / 2 + 20);
    }
} 