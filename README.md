# EQUINOX

## Description

EQUINOX is a fast-paced, top-down space shooter game built in Java using Swing. Players pilot the spacecraft captained by Nova, battling through waves of enemies across different locations in the galaxy.

## Features

*   **Classic Arcade Gameplay:** Top-down view, keyboard controls for movement, mouse/spacebar for shooting.
*   **Dynamic HUD:** Displays score, money, kills, health, stage/wave info, and ability cooldowns.
*   **Player Abilities:**
    *   **Wave Blast (Q):** A powerful energy wave.
    *   **Laser Beam (E):** A continuous beam attack.
    *   **Phase Shift (R):** Temporary invulnerability.
*   **Main Menu:** Start Game, Settings (placeholder), Credits (placeholder), Cheats, Exit.
*   **Cheat System:** Toggle invincibility, infinite money, and no ability cooldowns via the main menu.
*   **Basic Game Loop:** Progress through waves, face increasingly difficult enemies.
*   **Enemy Variety:** Includes basic enemies, shooting enemies, minibosses, and main bosses.
*   **Cutscene System:** Basic framework for displaying narrative sequences (currently used for game start).

## How to Run

1.  **Prerequisites:** Make sure you have Java Development Kit (JDK) installed (e.g., JDK 11 or later).
2.  **Compile:** Navigate to the `src` directory in your terminal and compile the Java files:
    ```bash
    javac com/equinox/game/core/EquinoxGame.java -d ../out
    ```
    (This assumes an `out` directory exists at the project root for compiled classes. Adjust as needed.)
3.  **Run:** Navigate to the output directory (`out` in this example) and run the main class:
    ```bash
    java com.equinox.game.core.EquinoxGame
    ```
    *(Ensure that the `assets` folder is accessible from where you run the command, often by having it in the project root or classpath.)*

## Controls

*   **Movement:** A/D or Left/Right Arrow Keys
*   **Shoot:** Spacebar
*   **Wave Blast:** Q
*   **Laser Beam:** E
*   **Phase Shift:** R
*   **Menu Navigation:** Up/Down Arrow Keys or W/S
*   **Menu Select:** Enter
*   **Restart (Game Over):** R

## Development Status

This project is currently under development. Future plans include implementing the upgrade system, shop system, more levels/locations, and refining existing features.
