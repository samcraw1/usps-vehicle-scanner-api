package com.usps.scanner.leaderboard;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.Instant;

@Entity
@Table(name = "inspections")
public class Inspection {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    @JsonIgnore
    private User user;

    @Column(nullable = false, length = 16)
    private String status;

    @Column(length = 500)
    private String summary;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    public Inspection() {}

    public Inspection(User user, String status, String summary) {
        this.user = user;
        this.status = status;
        this.summary = summary;
        this.createdAt = Instant.now();
    }

    public Long getId() { return id; }
    public User getUser() { return user; }
    public String getStatus() { return status; }
    public String getSummary() { return summary; }
    public Instant getCreatedAt() { return createdAt; }

    public void setId(Long id) { this.id = id; }
    public void setUser(User user) { this.user = user; }
    public void setStatus(String status) { this.status = status; }
    public void setSummary(String summary) { this.summary = summary; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }
}
