# Active Context

## Recent Changes
- **Player Shooting Mechanic Revamp:**
    - Replaced the previous immediate fire-on-press/release logic with a cooldown system.
    - Player now holds SPACE to continuously fire when the cooldown allows.
    - Implemented `fireRateUpgradeLevel` in `GameState` to reduce bullet cooldown.
    - Modified `InputHandler` to set an `isFiring` flag in `GameState`.
    - Updated `GameUpdateSystem` to manage bullet cooldowns and handle firing based on the `isFiring` flag and cooldown status.
    - Removed the direct `fireBullet` call from `EquinoxGameLogic`.
- **Implemented Damage & Multi-Shot Upgrades:**
    - Added constants for base damage/increase and base shots/increase per level.
    - Removed pierce logic from `Bullet.java` and fixed subclass constructors.
    - `GameUpdateSystem.createPlayerBullet()` now fires multiple shots based on `multiShotLevel`.
    - `GameUpdateSystem.handleEnemyHit()` now calculates and applies damage based on `damageUpgradeLevel`.
    - `CollisionSystem.checkPlayerBulletCollisions()` reverted to mark bullets used on first hit.
- **Implemented Damage Upgrade and Tied Multi-Shot Effect:**
    - Added constants for damage increase and damage level threshold for multi-shot.
    - Removed separate multi-shot upgrade path (`multiShotLevel`, UI, etc.).
    - `GameUpdateSystem.handleEnemyHit()` applies damage based on `damageUpgradeLevel`.
    - `GameUpdateSystem.createPlayerBullet()` now checks if `damageUpgradeLevel` meets the threshold (`DAMAGE_LEVEL_FOR_MULTI_SHOT`) and fires 2 **parallel** bullets (using an offset) if true, otherwise 1.
    - `CollisionSystem` marks bullets used on first hit.
- **Balanced Cooldown Reduction:** Reduced maximum cooldown reduction for abilities from 75% to 60%.
- **Fixed Boss Damage:** Resolved issue where shadowed `hitpoints` variable in `SpecialEnemy` prevented inherited `takeDamage` method from correctly reducing boss health.
- **HUD Improvement:** Replaced text cooldown indicators with visual bars in `RenderingSystem.drawGameStats`.
- **HUD Polish:** 
    - Health bar now changes color (Yellow/Red) at low HP.
    - Health text now includes upgrade level.
    - Added display for current Damage, Speed, and Fire Rate upgrade levels.
- **HUD Layout:** Relocated Score/Money/Killed, HP Bar, and World/Wave to bottom of screen for better visibility.
- **HUD Layout:** Fixed overlap issue between HP bar and cooldown bars.
- **Shop System Implementation:**
    - Implemented `Upgrade` class to define upgrade properties and costs.
    - Created `UpgradeManager` to handle upgrade purchase logic and level tracking.
    - Fully functional `ShopPanel` for purchasing upgrades with proper UI feedback.
- **Cutscene Enhancements:**
    - Updated `CutscenePanel` to handle frame advancement and transitions.
    - Added support for asset loading in cutscenes.
    - Improved cutscene timing and flow control.
- **Enemy Behavior Improvements (General):** Enemies move down on hitting *either* border.
- **Enemy Implementation (World 2-4):**
    - Added `EnemyWType1`: Basic shooter, scales HP/Speed, 3-shot spread in World 4.
    - Added `WeaverEnemy`: Moves vertically while weaving horizontally (sine wave), scales HP/Speed.
    - Added `ShieldedEnemy`: Has shield HP that absorbs damage first, scales HP/Shield/Speed (appears World 3+).
    - Removed `LungerEnemy` due to game over issues.
- **Boss Pattern Updates:**
    - `GuardianSpawn` (W2 Miniboss): Fires faster, tighter 3-bullet spread.
    - `ParadoxEntity` (W3 Main Boss): Alternates between single targeted shots and horizontal laser bursts (simulated).
    - `TempleGuardian` (W4 Main Boss): Combines slow, wide 7-bullet spread with occasional fast targeted shots.
- **Difficulty Tuning:**
    - Reduced base HP and HP/Speed scaling factors for `EnemyWType1`, `WeaverEnemy`, and `ShieldedEnemy`.
    - Reduced base Shield HP for `ShieldedEnemy`.
    - Reduced shooting frequency for `ShootingEnemy` (W1).
    - Increased base fire delay for `EnemyWType1`.
    - Increased attack cooldowns for `Miniboss` and `MainBoss` patterns.
    - Randomized initial firing time and fire delay variation for `EnemyWType1` to de-synchronize shots.
    - Randomized initial lunge timer for (removed) `LungerEnemy`.
- **Bug Fix:** Adjusted game over check in `GameUpdateSystem` to prevent enemies going off-screen (like the previous Lunger) from prematurely ending the game.

## Current Focus
- Balancing and polishing existing enemy behaviors and scaling across Worlds 1-4.
- Implementing remaining screens (Settings, Credits).

## Next Steps
- Further refine enemy wave compositions for Worlds 2-4.
- Implement Settings menu functionality (e.g., placeholders for volume controls).
- Implement Credits display panel.
- Add visual/audio feedback for game events (shield hits, enemy deaths, etc.).