# Object-Oriented Programming (OOP) Principles in Equinox Game

This document outlines how key OOP principles are applied within the Equinox game project.

## 1. Encapsulation

Encapsulation is the bundling of data (attributes) and the methods that operate on that data into a single unit (a class). It also involves controlling access to that data using access modifiers.

**Examples in Equinox:**

*   **Bundling Data and Methods:** Classes like `Entity`, `ShipUser`, `Enemy`, `GameState`, `Bullet`, etc., group their relevant data (e.g., `x`, `y`, `width`, `height`, `hitpoints`, `score`, `money`) with the methods that manage or affect that data (e.g., `move()`, `shoot()`, `getHitpoints()`, `setAlive()`, `addMoney()`).
*   **Access Control:**
    *   `private` members (like `currentState` in `EquinoxGameLogic`) restrict direct access from outside the class, ensuring state changes happen through controlled methods.
    *   `protected` members (like `currentCooldown` in `SpecialEnemy`) allow access within the class and its subclasses, useful for inheritance.
    *   `public` methods (like `move()`, `shoot()`, `setCurrentState()`) define the intended interface for interacting with objects.

## 2. Inheritance

Inheritance allows a class (subclass or derived class) to inherit properties and methods from another class (superclass or base class). This promotes code reuse and establishes an "is-a" relationship.

**Examples in Equinox:**

*   **Entity Hierarchy:**
    *   Base `Entity` class likely defines common attributes like position (`x`, `y`) and dimensions (`width`, `height`).
    *   `Enemy`, `ShipUser`, `Bullet`, `EnemyBullet` inherit from `Entity`.
    *   `Enemy` serves as a base for more specific enemy types.
    *   `ShootingEnemy`, `FastEnemy`, `SpecialEnemy` inherit from `Enemy`.
    *   `MainBoss` and `Miniboss` inherit from `SpecialEnemy`, adding boss-specific behaviors.
*   **Code Reuse:** Common functionalities like position management (`getX()`, `setX()`) are defined in `Entity` and reused by all subclasses. Basic enemy movement logic is defined in `Enemy` and inherited by specific enemy types.

## 3. Polymorphism

Polymorphism ("many forms") allows objects of different classes to be treated as objects of a common superclass, while still invoking the specific methods of the subclass at runtime. This is often achieved through method overriding and interface implementation.

**Examples in Equinox:**

*   **Method Overriding:**
    *   `MainBoss` and `Miniboss` override the `shoot()` method potentially inherited from `SpecialEnemy` or `Enemy` to provide their unique firing patterns.
    *   `EquinoxGameLogic` overrides `paintComponent()` from `JPanel` to implement custom game rendering and `actionPerformed()` from `ActionListener` to handle game loop events.
    *   `LeaderboardEntry` overrides `compareTo()` from `Comparable` to define custom sorting logic.
*   **Interface Implementation:**
    *   `EquinoxGameLogic` implements `ActionListener`.
    *   `InputHandler` implements `KeyListener`.
    *   `LeaderboardEntry` implements `Serializable` (allowing it to be saved/loaded) and `Comparable` (allowing sorting).
*   **Subtype Polymorphism (Runtime Polymorphism):**
    *   The loops in `GameUpdateSystem` and `RenderingSystem` iterate over lists of `Enemy` objects.
    *   Inside these loops, `instanceof` checks are used to determine the specific subtype (`Miniboss`, `MainBoss`).
    *   Based on the subtype, specific methods like `moveY()`, `executeAttackPattern()`, or `drawBossHealthBar()` are called on the object, even though the loop variable might be typed as the generic `Enemy`.

## 4. Abstraction

Abstraction involves hiding complex implementation details and exposing only the essential features of an object or system. It focuses on the "what" rather than the "how".

**Examples in Equinox:**

*   **Interfaces:** `ActionListener` and `KeyListener` define contracts (methods like `actionPerformed` or `keyPressed`) without specifying how they should be implemented. This allows different classes (`EquinoxGameLogic`, `InputHandler`) to fulfill these contracts in their own way.
*   **Base Classes:** `Entity` provides an abstract concept of a game object with position and appearance, hiding the specific details of ships, enemies, or bullets when only these basic properties are needed. `Enemy` provides an abstraction for common enemy behaviors.
*   **Method Abstraction:** Methods like `LeaderboardManager.addEntryFromGameState()` abstract away the details of file loading, list manipulation, sorting, and file saving, providing a simple way to add a score. 