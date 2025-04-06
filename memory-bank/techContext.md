# Tech Context

## Technologies Used
- **Language:** Java (Likely JDK 11, 17, or newer)
- **Graphics/UI:** Java Swing and AWT for windowing, rendering (via `JPanel`, `Graphics`), and basic UI elements (`JOptionPane`).
- **Input:** Java AWT `KeyListener`.
- **Data Storage:** Plain text file (`leaderboard.dat`) for local leaderboard persistence.

## Development Setup
- **IDE:** IntelliJ IDEA (indicated by project structure: `.idea/`, `.iml`).
- **Build System:** Likely managed directly within IntelliJ IDEA. No external build scripts (Maven `pom.xml`, Gradle `build.gradle`) are apparent.
- **Dependencies:** Primarily standard Java SE libraries included with the JDK.

## Technical Constraints
- Reliance on Java Swing limits advanced graphical effects and potentially cross-platform UI consistency compared to dedicated game engines.
- Performance may become a factor with a very large number of entities/effects due to software rendering via `Graphics`.
- Lack of built-in physics engine requires manual implementation for complex movement or collision effects.

## Dependencies
- Standard Java Development Kit (JDK) appropriate version.
- No external library dependencies identified currently. 