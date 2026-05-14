package com.usps.scanner.leaderboard;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ScoreRepository extends JpaRepository<Score, Long> {

    // Best (lowest) time for a single user - used for getOrCreate user response
    Optional<Score> findFirstByUser_UsernameOrderByTotalTimeMsAsc(String username);

    // Top-10 leaderboard query. Returns one entry per user (their personal best),
    // sorted by time ascending. Uses JPQL with a subquery to find each user's best.
    @Query("SELECT s FROM Score s WHERE s.totalTimeMs = " +
           "(SELECT MIN(s2.totalTimeMs) FROM Score s2 WHERE s2.user = s.user) " +
           "ORDER BY s.totalTimeMs ASC")
    List<Score> findPersonalBestsOrderedByTime();
}
