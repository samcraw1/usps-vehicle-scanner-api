package com.usps.scanner.leaderboard;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.Instant;
import jakarta.persistence.PrePersist;

@Entity 
@Table(name = "game_saves")
public class GameSave {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column (nullable = false, length = 50)
  private String username;

  @Column (name = "current_level", nullable = false)
  private int currentLevel;

  @Column (nullable = false)
  private int score;

  @Column (name = "time_seconds", nullable = false)
  private int timeSeconds; 

  @Column(name = "saved_at", nullable = false)
  private Instant savedAt;

  public GameSave() {}

  public GameSave (String username, int currentLevel,
    int score,int timeSeconds){
      this.username = username;
      this.currentLevel = currentLevel;
      this.score = score;
      this.timeSeconds = timeSeconds;
      this.savedAt = Instant.now();
    }

    @PrePersist
    private void onPrePersist(){
      if (savedAt == null) savedAt = Instant.now();
    }

  // Getters — Spring uses these when serializing to JSON (REST responses).
  public Long getId() { return id; }
  public String getUsername() { return username; }
  public int getCurrentLevel() { return currentLevel; }
  public int getScore() { return score; }
  public int getTimeSeconds() { return timeSeconds; }
  public Instant getSavedAt() { return savedAt; }

  // Setters — JPA uses these when populating fields from a database row.
  public void setId(Long id) { this.id = id; }
  public void setUsername(String username) { this.username = username; }
  public void setCurrentLevel(int currentLevel) { this.currentLevel = currentLevel; }
  public void setScore(int score) { this.score = score; }
  public void setTimeSeconds(int timeSeconds) { this.timeSeconds = timeSeconds; }
  public void setSavedAt(Instant savedAt) { this.savedAt = savedAt; }
}