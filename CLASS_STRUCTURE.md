# Equinox Game - Java Class Structure

This document outlines the Java classes needed to implement the Equinox game using standard Java libraries (e.g., Swing/AWT).

## Core Package (com.equinox.game)

- **EquinoxGame.java** - Main game class, likely extending `JFrame` or managing the main window.
  - Handles window creation and management.
  - Contains the main game loop.
  - Manages panel/screen switching.
  - Manages global resources (image loading, fonts, etc.).
  - Initializes game systems.

## UI Package (com.equinox.game.ui)

- **GamePanel.java** - The main `JPanel` where the game is rendered and interacts with input.
- **MainMenuPanel.java** - `JPanel` for the main menu with play/quit options.
- **OptionsPanel.java** - `JPanel` for the game settings menu.
- **UpgradePanel.java** - `JPanel` for ship upgrade selection between levels.
- **NarrativePanel.java** - `JPanel` for story cutscenes between locations.
- **GameOverPanel.java** - `JPanel` displayed when player dies.
- **VictoryPanel.java** - `JPanel` displayed when game is completed.
- **HUD.java** - Draws the Heads-up display during gameplay (may be part of `GamePanel`).
- **DialogBox.java** - Text dialog system for narrative.
- **Button.java** - Custom UI button component (or use standard Swing buttons).
- **UpgradeCard.java** - Representation of an upgrade option in the UI.

## Gameplay Package (com.equinox.game.gameplay)

- **Level.java** - Base class for all game levels.
- **LevelManager.java** - Manages level progression.
- **Location.java** - Represents a game location (4 in total).
- **LocationManager.java** - Controls transitions between locations.
- **Wave.java** - Manages enemy wave spawning.

## Entities Package (com.equinox.game.entities)

- **Entity.java** - Base class for all game entities (position, size, velocity, rendering logic).
- **PlayerShip.java** - The player character.
- **Projectile.java** - Base class for all projectiles.
- **PlayerProjectile.java** - Player's weapon projectiles.
- **EnemyProjectile.java** - Enemy weapon projectiles.
- **Explosion.java** - Explosion effect entity.
- **PowerUp.java** - Collectible power-ups.

## Enemies Package (com.equinox.game.entities.enemies)

- **Enemy.java** - Base class for all enemy types.
- **ScoutShip.java** - Fast but fragile enemy.
- **Cruiser.java** - Heavily armored enemy.
- **Drone.java** - Small swarming enemy.
- **Turret.java** - Stationary long-range enemy.
- **Boss.java** - Base class for all bosses.
- **VoidLeviathan.java** - Location 1 boss.
- **GuardianConstruct.java** - Location 2 boss.
- **ParadoxEntity.java** - Location 3 boss.
- **TempleGuardian.java** - Final boss.

## Systems Package (com.equinox.game.systems)

- **CollisionSystem.java** - Manages collision detection between entities (custom implementation).
- **InputHandler.java** - Processes keyboard/mouse input (using Swing listeners).
- **WeaponSystem.java** - Manages weapon firing and cooldowns.
- **UpgradeSystem.java** - Handles ship upgrades.
- **ScoreSystem.java** - Tracks player score.
- **LevelProgressionSystem.java** - Controls level advancement.

## Utils Package (com.equinox.game.utils)

- **ImageLoader.java** - Loads and manages game images (`BufferedImage`).
- **SoundManager.java** - Manages game sounds and music (using `javax.sound.sampled`).
- **Constants.java** - Game constants and configuration.
- **SaveManager.java** - Handles game saving/loading.

## Data Package (com.equinox.game.data)

- **PlayerData.java** - Stores player progress and stats.
- **LevelData.java** - Stores level configurations.
- **EnemyData.java** - Enemy type definitions.
- **UpgradeData.java** - Definition of available upgrades.

## Main Entry Point

- **Main.java** (or within `EquinoxGame.java`) - Contains the `public static void main(String[] args)` method to launch the application.

## Implementation Order

For efficient development, implement these classes in approximately this order:

1. Core game framework (EquinoxGame, GamePanel, MainMenuPanel, input handling, main loop)
2. Basic player ship (`PlayerShip`) with movement and rendering (`Graphics2D`).
3. Simple enemy types (`Enemy`, `ScoutShip`) and projectiles (`Projectile`).
4. Custom collision detection (`CollisionSystem`).
5. Level structure (`Level`, `LevelManager`) and basic progression.
6. Upgrade system (`UpgradeSystem`, `UpgradePanel`).
7. Boss implementations (`Boss`, specific bosses).
8. UI refinement (HUD, menus) and narrative elements.
9. Polish (sound using `javax.sound.sampled`, effects, optimization).

Each phase builds upon the previous, allowing for testing of core gameplay before adding more complex features using standard Java libraries. 