package com.usps.scanner.leaderboard;

// DTO returned by the API - matches the game's User class shape so JSON parsing
// on the client side is dead simple.
public class LeaderboardEntry {
    private String username;
    private long bestTimeMs;
    private Role role;
    private String hatColor;
    private String shirtColor;

    public LeaderboardEntry() {}

    // Legacy 2-arg constructor - uses defaults for new fields
    public LeaderboardEntry(String username, long bestTimeMs) {
        this.username = username;
        this.bestTimeMs = bestTimeMs;
        this.role = Role.CARRIER;
        this.hatColor = "#00427B"; // Default USPS blue
        this.shirtColor = "#23408C"; // Default USPS darker blue
    }

    // Full 5-arg constructor
    public LeaderboardEntry(String username, long bestTimeMs, Role role, String hatColor, String shirtColor) {
        this.username = username;
        this.bestTimeMs = bestTimeMs;
        this.role = role;
        this.hatColor = hatColor;
        this.shirtColor = shirtColor;
    }

    public String getUsername() { return username; }
    public long getBestTimeMs() { return bestTimeMs; }
    public Role getRole() { return role; }
    public String getHatColor() { return hatColor; }
    public String getShirtColor() { return shirtColor; }

    public void setUsername(String username) { this.username = username; }
    public void setBestTimeMs(long bestTimeMs) { this.bestTimeMs = bestTimeMs; }
    public void setRole(Role role) { this.role = role; }
    public void setHatColor(String hatColor) { this.hatColor = hatColor; }
    public void setShirtColor(String shirtColor) { this.shirtColor = shirtColor; }
}
