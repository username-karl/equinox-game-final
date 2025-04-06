# Progress

## What Works and Current Status
- **Gameplay:** Core loop functional (movement, shooting, collision, upgrades, basic world/wave progression).
- **HUD:** Functional with bottom layout, visual cooldowns, HP bar feedback, upgrade level display.
- **Entities:** Player, BasicEnemy (`FastEnemy`, `ShootingEnemy`), `EnemyWType1`, `WeaverEnemy`, `ShieldedEnemy`, Bullets, Abilities.
- **Bosses:** Miniboss (W1), MainBoss (W1), `GuardianSpawn` (W2 Miniboss), `ParadoxEntity` (W3 Main Boss), `TempleGuardian` (W4 Main Boss) exist with basic/updated attack patterns.
- **Upgrades:** Functional via Shop.
- **Enemy AI:** Basic movement, downward movement on border hit, specific patterns for Weaver, WType1 (shooting), Shielded (shield logic).
- **Shop System:** Fully implemented.
- **Cutscenes:** Basic framework functional.
- **Difficulty Scaling:** Implemented for newer enemies (HP/Speed), tuned down based on feedback.
- **Shooting Frequency:** Tuned down for basic enemies and bosses.

## What Works (Detailed)
- Basic game structure (JPanel, game loop).
- Player ship movement (left/right with acceleration/deceleration based on upgrades).
- Placeholder enemy movement and spawning (basic patterns, stage progression). 
- Player abilities (Wave Blast, Laser Beam, Phase Shift) with cooldowns managed in `GameUpdateSystem` and tied to upgrades in `GameState`.
- **Player shooting:**
  - Basic shooting (SPACE key) uses a cooldown system managed in `GameUpdateSystem`.
  - Holding SPACE allows continuous firing (respecting cooldown).
  - Fire rate upgrade (`fireRateUpgradeLevel`) correctly reduces the bullet cooldown.
- Basic collision detection (player bullets vs enemies, enemy bullets vs player, tactical abilities vs enemies).
- Player bullet damage now scales with `damageUpgradeLevel`.
- Player fires 2 **parallel** shots simultaneously when Damage upgrade reaches level 4+.
- Game state management (`GameStateEnum` in `EquinoxGameLogic`).
- HUD rendering (score, money, health) via `RenderingSystem`.
- HUD displays visual bars for ability cooldowns.
- HUD Health bar changes color at low health and displays HP upgrade level.
- HUD displays current levels for DMG, SPD, RATE upgrades.
- HUD layout redesigned, moved elements from top to bottom.
- Basic Asset Loading (`AssetLoader`).
- Main Menu navigation and basic functionality (Start, Leaderboard, Cheats, Exit).
- Game Over detection and handling (including win condition and prompting for leaderboard name).
- Leaderboard viewing (`LeaderboardPanel`).
- Cheat system (toggle, money bonus).
- Shop system functionality (`ShopPanel`, `Upgrade`, `UpgradeManager`).
- Cutscene system with frame advancement and transitions (`CutscenePanel`).
- Enhanced boss firing patterns for more varied gameplay.
- **Enemy Implementation:**
    - World 1: `FastEnemy`, `ShootingEnemy`.
    - World 2: `WeaverEnemy`, `EnemyWType1` (single shot) + placeholders.
    - World 3: `ShieldedEnemy`, `WeaverEnemy`, `EnemyWType1` (single shot) + placeholders.
    - World 4: `WeaverEnemy`, `EnemyWType1` (3-shot spread) + placeholders.
- **Boss Implementation:**
    - World 1: `Miniboss` (original spread), `MainBoss` (original patterns).
    - World 2: `GuardianSpawn` miniboss (tighter spread). (Main boss uses default `MainBoss` logic).
    - World 3: `ParadoxEntity` main boss (targeted/laser pattern).
    - World 4: `TempleGuardian` main boss (wide spread/targeted combo).
- Game Over condition adjusted to prevent off-screen enemies triggering it.

## What's Left to Build
- Implement Settings menu functionality (Volume controls, etc.).
- Implement Credits display screen/panel.
- Refine enemy spawning patterns/wave compositions for Worlds 2-4.
- Replace placeholder enemies in Worlds 2-4 with final intended types (e.g., W2 Type 3, W2 Type 5, etc.).
- Define and implement specific attack pattern for W2 Main Boss (`GuardianConstruct`).
- Add visual/audio feedback (shield hits/breaks, unique enemy deaths, ability sounds, low health warnings, etc.).
- Polish cutscenes (add specific content/dialogue).
- General balancing, bug fixing, visual polish.

## Current Status
- Core gameplay loop functional with multiple distinct enemy types and boss patterns across 4 worlds.
- Shop system, HUD, basic cutscenes are implemented.
- Difficulty scaling and enemy firing rates have been adjusted based on playtesting feedback.
- **Next Task:** Implementing Settings/Credits screens OR further refining enemy/wave design for Worlds 2-4.

## Known Issues
- Placeholder enemies still used in Worlds 2-4.
- World 2 Main Boss (`GuardianConstruct`) needs a unique attack pattern.
- Lack of audio/visual feedback for many events. 