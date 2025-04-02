# Equinox Game - Java Class Structure

This document outlines the current Java class structure for the Equinox game.

## Core Package (com.equinox.game.core)

- **EquinoxGame.java** - Main game class, likely managing the `JFrame`.
  - Handles window setup.
  - Contains the main game loop.
  - Manages panel switching.

## UI Package (com.equinox.game.ui)

- **EquinoxGameLogic.java** - Main gameplay panel, handles rendering and game logic updates.
- **CutscenePanel.java** - `JPanel` for displaying story cutscenes.
- **ShopPanel.java** - `JPanel` for the shop/upgrade interface.
  - *Note: Corresponding shop logic/classes seem missing or located elsewhere.*

## Entities Package (com.equinox.game.entities)

- **Entity.java** - Base class for all game entities.
- **ShipUser.java** - Represents the player's ship.
- **Bullet.java** - Player's projectile.
- **EnemyBullet.java** - Enemy's projectile.
- **SpecialMove.java** - Base/Interface for special abilities?
- **TacticalE.java** - Specific tactical ability.
- **TacticalQ.java** - Specific tactical ability.

## Enemies Package (com.equinox.game.entities.enemies)

- **Enemy.java** - Base class for enemy types.
- **ShootingEnemy.java** - Enemy that fires projectiles.
- **SpecialEnemy.java** - Enemy with potentially unique behavior.
- **FastEnemy.java** - Enemy focused on speed.
- **MainBoss.java** - Class for a main boss encounter.
- **Miniboss.java** - Class for a mini-boss encounter.

## Systems Package (com.equinox.game.systems)

- **StageManager.java** - Manages game stages or levels.
- **InputHandler.java** - Processes keyboard/mouse input.
- **CollisionSystem.java** - Handles collision detection.
- **CutsceneData.java** - Data related to cutscenes (consider moving to `data` package?).

## Data Package (com.equinox.game.data)

- **GameState.java** - Stores the overall state of the game.
- **Stage.java** - Represents data for a specific stage.

## Shop Package (com.equinox.game.shop)

- *(Currently Empty)* - Shop logic and item definitions likely need to be added here.

## Utils Package (com.equinox.game.utils)

- *(Currently Missing)* - Utility classes (like ImageLoader, SoundManager, Constants) might be needed.

## Main Entry Point

- Likely within `EquinoxGame.java` or a separate `Main.java`.

## Notes on Current Structure

- The structure seems to follow a typical game pattern.
- Some planned packages/classes (e.g., `Utils`, detailed `Gameplay/Level` management, specific UI panels like Main Menu, Game Over) from the original plan might be missing or integrated elsewhere (like `EquinoxGameLogic`).
- Asset loading and management needs clarification (no `ImageLoader` found).
- Sound management is not apparent (`SoundManager` missing).
- The purpose of `SpecialMove`, `TacticalE`, `TacticalQ` could be further documented.
- Location of shop implementation details needs confirmation. 