# Equinox Game Development Steps

This document outlines the step-by-step process for building the Equinox game as described in the design document, using standard Java libraries (Swing/AWT).

## Phase 1: Project Setup and Core Framework

1.  [x] **Setup Project Structure**
    -   [x] Create basic package structure (e.g., `com.equinox.game.core`, `com.equinox.game.ui`, `com.equinox.game.entities`, etc.).
    -   [x] Set up a standard Java project (using an IDE or build tools like Maven/Gradle, configured for standard Java SE).

2.  [x] **Implement Basic Game Window and Loop**
    -   [x] Create `EquinoxGame.java` extending `JFrame` or managing a `JFrame` instance.
    -   [x] Set up the main game window.
    -   [x] Implement the core game loop (timing, updates, rendering).
    -   [x] Create `GamePanel.java` extending `JPanel` for custom rendering (`paintComponent` method using `Graphics2D`).

3.  [x] **Create Panel Management System**
    -   [x] Implement logic in `EquinoxGame` to switch between different `JPanel`s (e.g., Main Menu, Game, Options).
    -   [x] Create `MainMenuPanel.java` with basic buttons (using Swing components or custom drawing).

4.  [x] **Setup Player Ship**
    -   [x] Create `PlayerShip.java` extending `Entity`.
    -   [x] Implement basic rendering using `Graphics2D` (e.g., drawing a shape or placeholder image).
    -   [x] Add keyboard input handling using Swing's KeyListeners or Key Bindings in `GamePanel` (InputHandler extracted).
    -   [x] Implement basic movement logic (updating position based on input).

## Phase 2: Core Gameplay Mechanics

5.  [x] **Implement Combat System**
    -   [x] Create `Projectile.java` and specific subclasses (`PlayerProjectile`).
    -   [x] Implement shooting mechanics (creating projectiles on key press).
    -   [x] Implement basic collision detection (e.g., rectangle intersection checks) in a `CollisionSystem` (CollisionSystem extracted).

6.  [ ] **Create Enemy Framework**
    -   [ ] Design base `Enemy.java` class extending `Entity`.
    -   [ ] Implement simple enemy types (e.g., basic fighters).
    -   [x] Add basic AI movement patterns (e.g., moving downwards).

7.  [x] **Design Level Structure**
    -   [x] Create `Level.java` to manage game state for a level.
    -   [x] Implement wave-based enemy spawning within `GamePanel` or a dedicated `Wave` class.
    -   [x] Implement basic background rendering (e.g., a scrolling starfield drawn with `Graphics2D`).

8.  [x] **Refine Core Mechanics**
    -   [x] Improve collision handling in `CollisionSystem` (entity-specific logic).
    -   [x] Implement damage system (health/hull points, retry mechanism).
    -   [ ] Add simple visual effects for hits/destruction (e.g., drawing explosion sprites).

## Phase 3: Game Progression

9.  [ ] **Design Upgrade System**
    -   [ ] Create `UpgradePanel.java` (extending `JPanel`).
    -   [ ] Implement the 4 upgrade categories and their effects on player stats.
    -   [ ] Add logic to show the `UpgradePanel` after completing levels.

10. **Implement Boss Fights**
    -   [x] Create specialized `Boss.java` class extending `Enemy`.
    -   [ ] Design unique attack patterns and behaviors for each boss.
    -   [x] Add boss health display (likely in the `HUD`).

11. [ ] **Create Location Transitions**
    -   [x] Add `NarrativePanel.java` (extending `JPanel`) for story display (basic implementation exists).
    -   [ ] Implement background changes for different locations.
    -   [ ] Handle transitions between levels/locations managed by `LevelManager` and `EquinoxGame`.

## Phase 4: Polish and Refinement

12. [ ] **Add Sound and Music**
    -   [ ] Create `SoundManager.java` using `javax.sound.sampled` for loading and playing WAV files.
    -   [ ] Integrate sound effects for weapons, explosions, etc.
    -   [ ] Add background music playback.

13. **Improve Visual Effects**
    -   [ ] Implement simple particle effects manually if desired (drawing small shapes/images).
    -   [ ] Add screen shake (by slightly offsetting the rendering coordinates) or flash effects.
    -   [ ] Refine background parallax scrolling.

14. [ ] **Enhance User Interface**
    -   [ ] Create `HUD.java` to draw UI elements (health, shield, score) onto `GamePanel`.
    -   [ ] Add a game pause mechanism and menu panel.
    -   [ ] Implement `OptionsPanel.java` for settings.

15. [x] **Integrate Narrative Elements**
    -   [ ] Implement opening sequence/panel.
    -   [x] Show narrative panels between locations (basic panel exists).
    -   [x] Implement `GameOverPanel` and `VictoryPanel` (GameOver/Retry exists).
    -   [ ] add more cutscenes to the game.

## Phase 5: Final Polishing

16. **Performance Optimization**
    -   [ ] Implement object pooling for frequently created objects like projectiles and effects.
    -   [ ] Use `BufferedImage` efficiently; consider pre-loading images.
    -   [ ] Optimize rendering logic and collision checks.

17. **Add Game Saving/Loading**
    -   [ ] Create `SaveManager.java` to handle saving/loading game state (player progress, upgrades) to files (e.g., using serialization or simple text files).
    -   [ ] Add settings persistence.

18. **Testing and Balancing**
    -   [ ] Test gameplay thoroughly.
    -   [ ] Balance difficulty progression (enemy stats, wave timing, upgrade costs).
    -   [ ] Fix bugs and optimize performance based on testing.

19. [x] **Final Release Preparation**
    -   [ ] Package game as an executable JAR.
    -   [ ] Create installation/running instructions.
    -   [x] Finalize documentation (Asset requirements updated).

## Implementation Details for Key Components (Pure Java)

### Player Ship Class Structure
The `PlayerShip` class should include:
- Core stats (hull, shield, weapon, engine).
- Position, velocity, and bounding box for collisions.
- Reference to its sprite (`BufferedImage`).
- Logic for handling input updates.
- `update(double deltaTime)` method.
- `render(Graphics2D g)` method.
- [x] Damage handling system.

### Level Management
- `LevelManager` to control progression.
- `Wave` system/logic for enemy spawning based on time or triggers.
- Boss trigger conditions.
- Level completion criteria.

### Upgrade System
- Four upgrade categories as per design doc.
- Upgrade effects implementation modifying player stats.
- UI (`UpgradePanel`) for upgrade selection.

### Enemy Types
- Specific classes extending `Enemy`.
- AI logic within their `update` methods.
- Bosses: Unique behaviors and attack patterns coded in their respective classes. 