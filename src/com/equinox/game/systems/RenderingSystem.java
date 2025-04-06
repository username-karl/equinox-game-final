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

    // Draw Background based on current stage
    private void drawBackground(Graphics g, GameState gameState) {
        if (gameState == null || gameState.currentStage == null || assetLoader == null) return;
        
        int stageNumber = gameState.currentStage.getStageNumber();
        Image bgImage = assetLoader.getBackgroundImage(stageNumber);
        
        if (bgImage != null) {
            g.drawImage(bgImage, 0, 0, Constants.BOARD_WIDTH, Constants.BOARD_HEIGHT, null);
        } else {
            // Fallback if specific stage background not found
            g.setColor(Color.BLACK); // Or use a default background image
            g.fillRect(0, 0, Constants.BOARD_WIDTH, Constants.BOARD_HEIGHT);
             System.err.println("Warning: Background image not found for stage " + stageNumber + ". Using fallback.");
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

    // DRAW GAME STATS (HUD) - Reworked Layout
    private void drawGameStats(Graphics g, GameState gameState) {
        int padding = Constants.HUD_PADDING;
        // int topY = Constants.HUD_TOP_Y; // No longer using top alignment
        int bottomLineY = Constants.BOARD_HEIGHT - padding; // Reference line for bottom elements
        int elementSpacing = 20; // Vertical spacing between HUD lines
        Font baseFont = new Font("Arial", Font.PLAIN, 16);
        Font boldFont = new Font("Arial", Font.BOLD, 16);
        Font cooldownFont = new Font("Arial", Font.PLAIN, 14);
        // Font stageFont = new Font("Arial", Font.BOLD, 18); // Use smaller font for stage info

        // --- Optional: Semi-Transparent Background Panel ---
        g.setColor(new Color(0, 0, 0, 100)); // Black with alpha
        g.fillRect(0, bottomLineY - 65, Constants.BOARD_WIDTH, 70); // Adjust height as needed

        // --- Bottom Left: HP and Cooldowns ---
        int currentY = bottomLineY;
        int currentX = padding;

        // HP Bar (Moved to Bottom Left)
        if (gameState.ship != null) {
            int healthBarWidth = 150;
            int healthBarHeight = 15; // Slightly smaller bar
            int healthBarX = padding;
            int healthBarY = currentY - elementSpacing - healthBarHeight; // Position above cooldown text
            double healthPercent = (double) gameState.ship.getHealth() / gameState.ship.getMaxHealth();
            int filledWidth = (int) (healthBarWidth * healthPercent);

            // Determine HP Bar color
            Color hpFillColor = Constants.HEALTH_BAR_FILL_COLOR; 
            if (healthPercent <= 0.25) hpFillColor = Color.RED;
            else if (healthPercent <= 0.50) hpFillColor = Color.YELLOW;
            
            // Draw HP Text (Above Bar)
            g.setFont(boldFont);
            g.setColor(Constants.HEALTH_TEXT_COLOR);
            String healthText = String.format("HP (Lvl %d): %d/%d", 
                                             gameState.healthUpgradeLevel, 
                                             gameState.ship.getHealth(), 
                                             gameState.ship.getMaxHealth());
            g.drawString(healthText, healthBarX, healthBarY - 5); // Text above bar

            // Draw HP Bar
            g.setColor(Constants.HEALTH_BAR_BG_COLOR);
            g.fillRect(healthBarX, healthBarY, healthBarWidth, healthBarHeight);
            g.setColor(hpFillColor);
            g.fillRect(healthBarX, healthBarY, filledWidth, healthBarHeight);
            g.setColor(Constants.HEALTH_BAR_BORDER_COLOR);
            g.drawRect(healthBarX, healthBarY, healthBarWidth, healthBarHeight);
            
            // Update Y reference: Y position of the bottom of the HP bar
            currentY = healthBarY + healthBarHeight; // New: Bottom edge of HP bar
        } else {
             currentY = bottomLineY; // Fallback if ship is null
        }
        
        // Cooldown Bars (Below HP)
        int cooldownBarY = currentY + elementSpacing / 2; // Start cooldown bars lower, increase vertical distance
        int cooldownTextY = cooldownBarY + Constants.COOLDOWN_BAR_HEIGHT + 14;
        int barX = padding;
        int barSpacing = Constants.COOLDOWN_BAR_WIDTH + 25; 
        g.setFont(cooldownFont);

        drawCooldownBar(g, "Q", gameState.remainingWaveBlastCooldown, Constants.WAVE_BLAST_COOLDOWN_MS, Constants.COOLDOWN_Q_COLOR, barX, cooldownBarY, cooldownTextY);
        barX += barSpacing;
        drawCooldownBar(g, "E", gameState.remainingLaserBeamCooldown, Constants.LASER_BEAM_COOLDOWN_MS, Constants.COOLDOWN_E_COLOR, barX, cooldownBarY, cooldownTextY);
        barX += barSpacing;
        drawCooldownBar(g, "R", gameState.remainingPhaseShiftCooldown, Constants.PHASE_SHIFT_COOLDOWN_MS, Constants.COOLDOWN_R_COLOR, barX, cooldownBarY, cooldownTextY, gameState.isPhaseShiftActive);

        // --- Bottom Center: Upgrades & World/Wave ---
        currentY = bottomLineY; // Reset Y for center column
        
        // Upgrade Levels
        g.setFont(baseFont);
        g.setColor(Color.LIGHT_GRAY);
        String upgradesText = String.format("DMG: %d | SPD: %d | RATE: %d", 
                                          gameState.damageUpgradeLevel,
                                          gameState.speedUpgradeLevel,
                                          gameState.fireRateUpgradeLevel);
        FontMetrics fmUpgrades = g.getFontMetrics();
        int upgradesWidth = fmUpgrades.stringWidth(upgradesText);
        int upgradesX = (Constants.BOARD_WIDTH - upgradesWidth) / 2;
        g.drawString(upgradesText, upgradesX, currentY);
        
        // World/Wave (Above Upgrades, Smaller Font)
        if (gameState.currentStage != null) {
            // g.setFont(stageFont); // Use smaller font
            g.setColor(Constants.STAGE_INFO_COLOR);
            String stageText = "World: " + gameState.currentStage.getStageNumber() + " | Wave: " + gameState.currentStage.getCurrentWave();
            FontMetrics fmStage = g.getFontMetrics(); // Use metrics from baseFont
            int stageWidth = fmStage.stringWidth(stageText);
            g.drawString(stageText, (Constants.BOARD_WIDTH - stageWidth) / 2, currentY - elementSpacing); // Position above upgrades
        }
        
        // --- Bottom Right: Score, Money, Killed ---
        currentY = bottomLineY;
        g.setFont(baseFont);

        String killedText = "Killed: " + gameState.enemySlain;
        FontMetrics fmKilled = g.getFontMetrics();
        int killedWidth = fmKilled.stringWidth(killedText);
        g.setColor(Constants.KILLED_COLOR);
        g.drawString(killedText, Constants.BOARD_WIDTH - padding - killedWidth, currentY);
        currentY -= elementSpacing; // Move up for next line

        String moneyText = "Money: " + gameState.money;
        FontMetrics fmMoney = g.getFontMetrics();
        int moneyWidth = fmMoney.stringWidth(moneyText);
        g.setColor(Constants.MONEY_COLOR);
        g.drawString(moneyText, Constants.BOARD_WIDTH - padding - moneyWidth, currentY);
        currentY -= elementSpacing; // Move up for next line
        
        String scoreText = "Score: " + gameState.score;
        FontMetrics fmScore = g.getFontMetrics();
        int scoreWidth = fmScore.stringWidth(scoreText);
        g.setColor(Constants.SCORE_COLOR);
        g.drawString(scoreText, Constants.BOARD_WIDTH - padding - scoreWidth, currentY);

        // --- Cheat Indicator (Position relative to other elements) ---
        if (gameState.cheatsEnabled) {
            g.setFont(boldFont);
            g.setColor(Constants.CHEAT_INDICATOR_COLOR);
            String cheatText = "CHEATS ACTIVE";
            FontMetrics fmCheat = g.getFontMetrics();
            int cheatWidth = fmCheat.stringWidth(cheatText);
            // Place it above the World/Wave info in the center
            g.drawString(cheatText, (Constants.BOARD_WIDTH - cheatWidth) / 2, bottomLineY - (2 * elementSpacing) - 5);
            // g.drawString(cheatText, (Constants.BOARD_WIDTH - cheatWidth) / 2, bottomY + 14); // Old position near upgrade levels
        }
    }

    // --- HELPER METHOD FOR DRAWING COOLDOWN BARS ---
    private void drawCooldownBar(Graphics g, String key, long remainingCooldown, long baseCooldown, 
                                 Color cooldownColor, int barX, int barY, int textY) {
        drawCooldownBar(g, key, remainingCooldown, baseCooldown, cooldownColor, barX, barY, textY, false); // Overload for non-active state
    }

    private void drawCooldownBar(Graphics g, String key, long remainingCooldown, long baseCooldown, 
                                 Color cooldownColor, int barX, int barY, int textY, boolean isActive) {
        
        int barWidth = Constants.COOLDOWN_BAR_WIDTH;
        int barHeight = Constants.COOLDOWN_BAR_HEIGHT;
        String text = key + ":";
        int textWidth = g.getFontMetrics().stringWidth(text);
        int textX = barX + (barWidth / 2) - (textWidth / 2); // Center text below bar
        
        double filledPercent = 0.0;
        Color fillColor = Constants.COOLDOWN_READY_COLOR;

        if (isActive) {
            fillColor = Constants.COOLDOWN_R_ACTIVE_COLOR; // Use specific active color (only for R currently)
            filledPercent = 1.0; // Show as full when active
            text = key + ": ACTIVE";
        } else if (remainingCooldown > 0) {
            fillColor = cooldownColor;
            filledPercent = 1.0 - ((double) remainingCooldown / baseCooldown); // Bar fills *up* as cooldown expires
            text = String.format("%s: %.1fs", key, (double) remainingCooldown / 1000);
        } else {
            // Ready state handled by default fillColor and filledPercent = 0 (or 1 if preferred)
             filledPercent = 1.0; // Show as full when ready
             text = key + ": Ready";
        }
        
        int filledWidth = (int) (barWidth * filledPercent);
        
        // Draw Text
        g.setColor(fillColor); // Use the fill color for text for consistency
        textWidth = g.getFontMetrics().stringWidth(text);
        textX = barX + (barWidth / 2) - (textWidth / 2); // Recalculate X based on potentially longer text
        g.drawString(text, textX, textY);
        
        // Draw Background Bar
        g.setColor(Constants.COOLDOWN_BAR_BG_COLOR);
        g.fillRect(barX, barY, barWidth, barHeight);
        
        // Draw Filled Bar
        g.setColor(fillColor);
        g.fillRect(barX, barY, filledWidth, barHeight);
        
        // Draw Border
        g.setColor(Color.GRAY); // Use a neutral border color
        g.drawRect(barX, barY, barWidth, barHeight);
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

        // Change Esc text
        String menuMsg = "Press 'Esc' to Submit Score / Give Up";
        int menuWidth = fm.stringWidth(menuMsg);
        g.drawString(menuMsg, (Constants.BOARD_WIDTH - menuWidth) / 2, Constants.BOARD_HEIGHT / 2 + 50);
    }
} 