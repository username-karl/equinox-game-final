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
- **HUD Layout:** Relocated Score/Money/Killed, HP Bar, and World/Wave to bottom of screen to improve visibility.
- **HUD Layout:** Moved Score, Money, Killed, HP Bar, and World/Wave from top to bottom for better visibility.
- **HUD Layout:** Added display for current Damage, Speed, and Fire Rate upgrade levels.
- **HUD Layout:** Adjusted vertical spacing between HP bar and cooldown bars to fix overlap.

## Current Focus
- Define next major task (e.g., Content Expansion, further Polish).

## Next Steps
- Select and plan next development focus (e.g., World 2 enemies/boss, audio/visual polish, Settings/Credits menu).

### Enemy AI
- Modified enemy movement logic: Enemies now move down when hitting *either* the left or right screen boundary (previously only moved down on hitting the right).