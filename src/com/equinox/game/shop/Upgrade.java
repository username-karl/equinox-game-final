package com.equinox.game.shop;

// Represents a single upgrade type
public record Upgrade(
    String id,            // Unique ID (e.g., "HEALTH", "SPEED")
    String name,          // Display Name (e.g., "Hull Reinforcement")
    String description,   // Description text
    int worldUnlocked,    // World number (1, 2, 3, 4) when it appears in shop
    int baseCost,         // Cost for Level 1
    double costScaleFactor, // Multiplier for cost increase per level (e.g., 1.5)
    int maxLevel,         // Maximum level for this upgrade
    String crewLine,        // Flavour text indicating which "crew" provides this
    String effectPerLevelText // Text describing the effect per level (e.g., "+1 HP")
) {
    // No additional methods needed for a simple record
} 