package com.equinox.game.leaderboard;

import java.io.Serializable;

// Using a record for simplicity and immutability
// Implements Serializable to allow saving/loading
public record LeaderboardEntry(
    int score,
    long timeMillis, // Time taken in milliseconds
    int maxMoney,
    String playerName // Added for display, maybe default to "Player" or ask?
) implements Serializable, Comparable<LeaderboardEntry> {

    // Custom comparator to sort by score descending
    @Override
    public int compareTo(LeaderboardEntry other) {
        // Higher score comes first
        return Integer.compare(other.score, this.score);
    }

    // Helper method to format time for display
    public String getFormattedTime() {
        long totalSeconds = timeMillis / 1000;
        long minutes = totalSeconds / 60;
        long seconds = totalSeconds % 60;
        long millis = timeMillis % 1000;
        return String.format("%02d:%02d.%03d", minutes, seconds, millis);
    }
} 