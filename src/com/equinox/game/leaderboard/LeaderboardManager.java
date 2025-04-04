package com.equinox.game.leaderboard;

import java.io.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class LeaderboardManager {

    private static final String LEADERBOARD_FILE = "leaderboard.dat";
    public static final int MAX_ENTRIES = 10;

    // Load entries from the file
    @SuppressWarnings("unchecked") // Suppress warning for the cast from Object
    public static List<LeaderboardEntry> loadEntries() {
        List<LeaderboardEntry> entries = new ArrayList<>();
        File file = new File(LEADERBOARD_FILE);
        if (file.exists()) {
            try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
                Object data = ois.readObject();
                if (data instanceof List) { // Basic type check
                   entries = (List<LeaderboardEntry>) data; // Unchecked cast
                }
            } catch (IOException | ClassNotFoundException e) {
                System.err.println("Error loading leaderboard: " + e.getMessage());
                // Optionally delete corrupted file? Or return empty list
            }
        } 
        // Ensure list is mutable and sort just in case file wasn't sorted
        entries = new ArrayList<>(entries);
        Collections.sort(entries); 
        return entries;
    }

    // Save entries to the file
    public static void saveEntries(List<LeaderboardEntry> entries) {
        // Ensure we only save the top entries and the list is sorted
        Collections.sort(entries);
        List<LeaderboardEntry> entriesToSave = new ArrayList<>();
        for (int i = 0; i < Math.min(entries.size(), MAX_ENTRIES); i++) {
             entriesToSave.add(entries.get(i));
        }
        
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(LEADERBOARD_FILE))) {
            oos.writeObject(entriesToSave);
        } catch (IOException e) {
            System.err.println("Error saving leaderboard: " + e.getMessage());
        }
    }

    // Add a new entry, sort, trim, and save
    public static void addEntry(int score, long timeMillis, int maxMoney, String playerName) {
        List<LeaderboardEntry> entries = loadEntries();
        LeaderboardEntry newEntry = new LeaderboardEntry(score, timeMillis, maxMoney, playerName);
        entries.add(newEntry);
        // Sorting and trimming is handled by saveEntries
        saveEntries(entries);
        System.out.println("Leaderboard entry added for " + playerName);
    }

     // Method to add entry based on GameState
     public static void addEntryFromGameState(com.equinox.game.data.GameState gameState, String playerName) {
        if (gameState == null || gameState.startTimeMillis == 0) {
            System.err.println("Cannot add leaderboard entry: Invalid GameState or game not started.");
            return;
        }
        long endTimeMillis = System.currentTimeMillis();
        long timeTakenMillis = endTimeMillis - gameState.startTimeMillis;
        
        addEntry(gameState.score, timeTakenMillis, gameState.maxMoneyAchieved, playerName);
    }

} 