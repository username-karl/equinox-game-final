package com.equinox.game.shop;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class UpgradeManager {

    // Store upgrades by ID for easy lookup
    private static final Map<String, Upgrade> upgrades = new HashMap<>();

    static {
        // --- Define Upgrades --- 
        
        // World 1 Crew Line: Engineering (Nova's Core Systems)
        addUpgrade(new Upgrade("HEALTH", "Hull Reinforcement", "Increases maximum ship HP.", 1, 150, 1.6, 5, "Hull Engineering", "+1 Max HP"));
        addUpgrade(new Upgrade("SPEED", "Engine Tuning", "Increases maximum ship speed and acceleration.", 1, 200, 1.5, 5, "Engine Mechanic", "+0.5 Max Speed"));
        
        // World 2 Crew Line: Weapons Bay (New Recruit - Gunner)
        addUpgrade(new Upgrade("DAMAGE", "Enhanced Capacitors", "Increases base bullet damage.", 2, 300, 1.7, 5, "Weapons Bay", "+1 Damage"));
        addUpgrade(new Upgrade("FIRE_RATE", "Autoloader Mechanism", "Increases weapon firing speed.", 2, 400, 1.8, 5, "Weapons Bay", "+10% Fire Rate"));

        // World 3 Crew Line: Tactical Systems (Specialist Officer)
        addUpgrade(new Upgrade("COOLDOWN_QE", "System Optimization", "Reduces cooldown for Wave Blast (Q) and Laser Beam (E).", 3, 500, 1.9, 5, "Tactical Systems", "-10% Cooldown"));
        // Maybe add a second W3 upgrade?

        // World 4 Crew Line: Command Deck (Veteran Commander)
        addUpgrade(new Upgrade("COOLDOWN_R", "Phase Shift Modulator", "Reduces cooldown for Phase Shift (R).", 4, 1000, 2.0, 5, "Command Deck", "-15% Cooldown"));
        addUpgrade(new Upgrade("MONEY_MULT", "Resource Scanners", "Increases money gained from enemies.", 4, 1500, 2.5, 3, "Command Deck", "+15% Money")); // Example: money multiplier

    }

    private static void addUpgrade(Upgrade upgrade) {
        upgrades.put(upgrade.id(), upgrade);
    }

    // Get a specific upgrade by ID
    public static Upgrade getUpgrade(String id) {
        return upgrades.get(id);
    }

    // Get all available upgrades up to a certain world level
    public static List<Upgrade> getAvailableUpgrades(int maxWorldReached) {
        return upgrades.values().stream()
               .filter(u -> u.worldUnlocked() <= maxWorldReached)
               .sorted((u1, u2) -> Integer.compare(u1.worldUnlocked(), u2.worldUnlocked())) // Sort by world, then maybe ID?
               .collect(Collectors.toList());
    }

    // Calculate the cost for the *next* level of an upgrade
    public static int calculateCost(Upgrade upgrade, int currentLevel) {
        if (upgrade == null || currentLevel < 0 || currentLevel >= upgrade.maxLevel()) {
            return -1; // Indicate max level or invalid input
        }
        // Cost = BaseCost * (ScaleFactor ^ currentLevel)
        // currentLevel is 0 for buying level 1, 1 for buying level 2 etc.
        return (int) Math.round(upgrade.baseCost() * Math.pow(upgrade.costScaleFactor(), currentLevel));
    }
} 