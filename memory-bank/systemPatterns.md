# System Patterns

## Architecture Overview
- The game follows a basic Entity-Component-System (ECS-like) pattern, although not strictly implemented.
- **Entities:** `ShipUser`, `Enemy`, `Bullet`, `WaveBlast`, `LaserBeam`, etc.
- **Data State:** `GameState` holds the central state (entities, score, money, cooldowns, upgrades, etc.).
- **Systems:** 
    - `EquinoxGameLogic`: Main JPanel, handles game loop timer, state transitions (Menu, Playing, GameOver), and orchestrates system initialization.
    - `InputHandler`: Captures keyboard input and modifies `GameState` flags (movement, firing) or calls methods on `EquinoxGameLogic` (menu actions, ability triggers).
    - `GameUpdateSystem`: Performs core game logic updates each frame (movement, cooldown management, AI/shooting logic, stage progression, entity creation/removal). Enemy AI includes horizontal movement with direction reversal and downward movement triggered upon hitting either the left or right screen boundary.
    - `CollisionSystem`: Detects collisions between different entity types.
    - `RenderingSystem`: Draws the game state (background, entities, HUD). Specifically, the HUD is now rendered at the bottom of the screen with visual bars for cooldowns, a dynamic HP bar (color changes), and key upgrade level indicators.
    - `StageManager`: Handles transitions between stages and cutscenes.
    - `AssetLoader`: Loads and provides access to game assets (images).
    - `LeaderboardManager`: Handles saving and loading leaderboard data.

## Key Technical Decisions
- **State Management:** Centralized `GameState` object passed to systems that need it.
- **Input:** Standard Java KeyListener implemented in `InputHandler`.
- **Rendering:** Custom rendering logic within `RenderingSystem` using Java Graphics API.
- **Game Loop:** Standard Swing Timer in `EquinoxGameLogic`.
- **Dependencies:** Systems are generally initialized in `EquinoxGameLogic` and passed references to each other as needed (sometimes via setters to break circular dependencies, e.g., `GameUpdateSystem` and `CollisionSystem`).
- **Balancing:**
    - Max cooldown reduction for abilities (Q, E, R) capped at 60% to prevent spamming.

## Design Patterns in Use
- **State Pattern:** `GameStateEnum` in `EquinoxGameLogic` controls overall game flow.
- **Observer Pattern (Implicit):** `InputHandler` observes key events and updates `GameState` or notifies `EquinoxGameLogic`.
- **Facade Pattern (Loosely):** `EquinoxGameLogic` acts as a facade for some interactions, calling methods on the various systems.
- **Singleton Pattern (Effectively):** Key systems (`GameUpdateSystem`, `CollisionSystem`, `RenderingSystem`, `GameState`) exist as single instances managed by `EquinoxGameLogic`.

## Component Relationships (Shooting Mechanic Example)
1.  `InputHandler` detects SPACE press/release.
2.  `InputHandler` sets/clears `isFiring` flag in `GameState`.
3.  `GameUpdateSystem.update()` runs.
4.  `GameUpdateSystem.updateCooldowns()` calculates remaining bullet cooldown based on `GameState.lastBulletFireTime` and `GameState.fireRateUpgradeLevel`.
5.  `GameUpdateSystem.handlePlayerShooting()` checks `GameState.isFiring` and `GameState.remainingBulletCooldown`.
6.  If firing is true and cooldown is <= 0, `GameUpdateSystem.createPlayerBullet()` is called.
7.  `createPlayerBullet()` checks if `GameState.damageUpgradeLevel >= Constants.DAMAGE_LEVEL_FOR_MULTI_SHOT`. It sets `numberOfShots` to `MULTI_SHOT_ACTIVE_COUNT` (2) or `BASE_SHOTS_PER_FIRE` (1) accordingly. It loops `numberOfShots` times. Inside the loop, it calculates the appropriate starting `spawnX` (either centered for 1 shot, or offset using `MULTI_SHOT_PARALLEL_OFFSET` for 2 shots) and creates a new `Bullet` entity with velocity (0, `BULLET_VELOCITY_Y`) at that `spawnX`. Bullets are added to `GameState.bulletArray`.
8.  `GameUpdateSystem.update()` updates `GameState.lastBulletFireTime`.
9.  `GameUpdateSystem.movePlayerBullets()` updates bullet positions (all bullets move straight up).
10. `CollisionSystem.checkPlayerBulletCollisions()` iterates through bullets and enemies.
11. If an `Enemy` is alive, the `Bullet` is not `used`, and they collide:
    a. `GameUpdateSystem.handleEnemyHit(enemy)` is called. Inside this method, `currentDamage` is calculated based on `GameState.damageUpgradeLevel`, and `enemy.takeDamage(currentDamage)` is called.
    b. `bullet.setUsed(true)` is called.
    c. The inner loop (`for Enemy`) is broken, as the bullet is now used.
12. `RenderingSystem.render()` draws the bullets from `GameState.bulletArray`. 