package com.usps.scanner.leaderboard;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import com.fasterxml.jackson.annotation.JsonIgnore;

import java.time.Instant;

// One row in the "scores" table - links to a User via foreign key.
@Entity
@Table(name = "scores", indexes = { @jakarta.persistence.Index(name = "idx_scores_time", columnList = "total_time_ms") })
public class Score {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Foreign key to User. JsonIgnore so we don't serialize the full User in responses
    // (we send back just username via a DTO).
    @ManyToOne(optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    @JsonIgnore
    private User user;

    @Column(name = "total_time_ms", nullable = false)
    private long totalTimeMs;

    @Column(name = "completed_at", nullable = false)
    private Instant completedAt;

    public Score() {}

    public Score(User user, long totalTimeMs) {
        this.user = user;
        this.totalTimeMs = totalTimeMs;
        this.completedAt = Instant.now();
    }

    public Long getId() { return id; }
    public User getUser() { return user; }
    public long getTotalTimeMs() { return totalTimeMs; }
    public Instant getCompletedAt() { return completedAt; }

    public void setId(Long id) { this.id = id; }
    public void setUser(User user) { this.user = user; }
    public void setTotalTimeMs(long totalTimeMs) { this.totalTimeMs = totalTimeMs; }
    public void setCompletedAt(Instant completedAt) { this.completedAt = completedAt; }
}
