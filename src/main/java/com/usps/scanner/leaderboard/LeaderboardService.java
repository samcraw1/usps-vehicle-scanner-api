package com.usps.scanner.leaderboard;

import java.util.List;

// Contract for the leaderboard service - lets us mock easily in tests
// and swap implementations later (Dependency Inversion Principle).
public interface LeaderboardService {

    /** Look up a user by name; if not found, create one. Always returns a LeaderboardEntry
     *  with the user's best time (0 for new users). */
    LeaderboardEntry getOrCreateUser(String username);

    /** Record a new score for an existing or newly-created user. Returns the saved Score. */
    Score submitScore(String username, long totalTimeMs);

    /** Top 10 leaderboard entries, sorted by lowest time. One entry per user (personal best). */
    List<LeaderboardEntry> getLeaderboard();

    LeaderboardEntry updateOutfit(String username, String hatColor, String shirtColor);

    List<SupervisorEntry> getSupervisorLeaderboard();
}
