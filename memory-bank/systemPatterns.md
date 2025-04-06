# System Patterns

## Architecture Overview
- The game follows a basic Entity-Component-System (ECS-like) pattern, although not strictly implemented.
- **Entities:** 
    - Player: `ShipUser`
    - Bullets: `Bullet`, `EnemyBullet`
    - Abilities: `WaveBlast`, `LaserBeam`
    - Basic Enemies (W1): `Enemy`, `FastEnemy`, `ShootingEnemy`
    - Advanced Enemies (W2+): `EnemyWType1` (shooter), `WeaverEnemy` (sine wave movement), `ShieldedEnemy` (absorbs damage)
    - Bosses: `Miniboss`, `MainBoss`, `GuardianSpawn`, `ParadoxEntity`, `TempleGuardian`
- **Data State:** `GameState` holds the central state (entities, score, money, cooldowns, upgrades, etc.).
- **Systems:** 
    - `EquinoxGameLogic`: Main JPanel, handles game loop timer, state transitions (Menu, Playing, GameOver), and orchestrates system initialization.
    - `InputHandler`: Captures keyboard input and modifies `GameState` flags (movement, firing) or calls methods on `EquinoxGameLogic` (menu actions, ability triggers).
    - `GameUpdateSystem`: Performs core game logic updates each frame (movement, cooldown management, AI/shooting logic, stage progression, entity creation/removal). Also handles spawning logic for different enemy types based on world/wave, including probability distributions and stat scaling.
    - `CollisionSystem`: Detects collisions between different entity types.
    - `RenderingSystem`: Draws the game state (background, entities, HUD). Specifically, the HUD is now rendered at the bottom of the screen with visual bars for cooldowns, a dynamic HP bar (color changes), and key upgrade level indicators.
    - `StageManager`: Handles transitions between stages and cutscenes.
    - `AssetLoader`: Loads and provides access to game assets (images).
    - `LeaderboardManager`: Handles saving and loading leaderboard data.
    - `UpgradeManager`: Manages upgrade definitions, purchase logic, and level tracking.
    - `ShopPanel`: Provides UI for purchasing upgrades with proper feedback.
    - `CutscenePanel`: Handles cutscene rendering, frame advancement, and transitions.

## Key Technical Decisions
- **State Management:** Centralized `GameState` object passed to systems that need it.
- **Input:** Standard Java KeyListener implemented in `InputHandler`.
- **Rendering:** Custom rendering logic within `RenderingSystem` using Java Graphics API.
- **Game Loop:** Standard Swing Timer in `EquinoxGameLogic`.
- **Dependencies:** Systems are generally initialized in `EquinoxGameLogic` and passed references to each other as needed (sometimes via setters to break circular dependencies, e.g., `GameUpdateSystem` and `CollisionSystem`).
- **Balancing:**
    - Max cooldown reduction for abilities (Q, E, R) capped at 60% to prevent spamming.
    - Enemy HP/Speed scaling adjusted based on playtesting.
    - Enemy firing rates/patterns adjusted (reduced frequency, de-synchronized where possible).
- **Shop System:**
    - Upgrades defined with `Upgrade` class (ID, name, cost per level, max level).
    - `UpgradeManager` handles purchase logic and level tracking.
    - `ShopPanel` provides UI for browsing and purchasing upgrades.
    - Cost progression formula implements exponential scaling.
- **Cutscene System:**
    - `CutscenePanel` manages frame timing and transitions.
    - Cutscenes can be triggered between stages.
    - Assets loaded via `AssetLoader`.

## Design Patterns in Use
- **State Pattern:** `GameStateEnum` in `EquinoxGameLogic` controls overall game flow.
- **Observer Pattern (Implicit):** `InputHandler` observes key events and updates `GameState` or notifies `EquinoxGameLogic`.
- **Facade Pattern (Loosely):** `EquinoxGameLogic` acts as a facade for some interactions, calling methods on the various systems.
- **Singleton Pattern (Effectively):** Key systems (`GameUpdateSystem`, `CollisionSystem`, `RenderingSystem`, `GameState`) exist as single instances managed by `EquinoxGameLogic`.
- **Strategy Pattern (Partial):** Different enemy types implement variations of movement and attack behaviors.
- **Factory Method Pattern (Loose):** `GameUpdateSystem` contains methods for creating different entity types.

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

## Shop System Flow
1. Player enters shop by pressing 'B' during gameplay.
2. `EquinoxGameLogic` changes state to `SHOP` and activates `ShopPanel`.
3. `ShopPanel` displays available upgrades and current levels.
4. When player selects an upgrade:
   a. `ShopPanel` calls `UpgradeManager.attemptPurchase()` with upgrade ID.
   b. `UpgradeManager` checks if player has enough money and if upgrade is below max level.
   c. If purchase is valid, money is deducted and upgrade level is increased in `GameState`.
   d. `ShopPanel` updates UI to reflect changes.
5. Player can return to game by pressing 'B' again. 

## Enemy Spawning Flow (`createEnemies` in GameUpdateSystem)
1. Determine current `stageNum` and `currentWave` from `GameState`.
2. Calculate max rows/columns for the wave.
3. Clear existing `enemyArray`.
4. Loop through spawn grid (rows/columns).
5. Inside loop, based on `stageNum`:
    - **World 1:** Spawn `FastEnemy` or `ShootingEnemy` randomly.
    - **Worlds 2-4:** 
        - Use `random.nextInt()` to determine enemy type based on probability distribution for that world (e.g., W3: 30% Shielded, 30% Weaver, 30% WType1, 10% Placeholder).
        - Calculate scaled stats (HP, Speed, ShieldHP) using base values from `Constants` and scaling factors raised to the power of `worldExponent` (`stageNum - 1`).
        - Retrieve appropriate asset key from `Constants`.
        - Instantiate the chosen enemy subclass (`WeaverEnemy`, `EnemyWType1`, `ShieldedEnemy`, or base `Enemy` for placeholders) with scaled stats and dependencies.
    - **Fallback:** If asset loading fails or type calculation is invalid, spawn a basic `Enemy` using `createFallbackEnemy` helper.
6. Add created `Enemy` instance to `gameState.enemyArray`.
7. After loop, update `gameState.enemyCount`. 