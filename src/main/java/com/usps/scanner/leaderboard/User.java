package com.usps.scanner.leaderboard;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.Instant;

// JPA Entity - one row in the "users" table.
@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false, length = 50)
    private String username;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role = Role.CARRIER;
    
    @Column(name = "hat_color", length = 7)
    private String hatColor = "#00427B"; // Default USPS blue
    
    @Column(name = "shirt_color", length = 7)
    private String shirtColor = "#23408C"; // Default USPS darker blue

    public User() {}

    public User(String username) {
        this.username = username;
        this.createdAt = Instant.now();
    }

    public Long getId() { return id; }
    public String getUsername() { return username; }
    public Instant getCreatedAt() { return createdAt; }

    public void setId(Long id) { this.id = id; }
    public void setUsername(String username) { this.username = username; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }

    public Role getRole() { return role; }
    public String getHatColor() { return hatColor; }
    public String getShirtColor() { return shirtColor; }

    public void setRole(Role role) { this.role = role; }
    public void setHatColor(String hatColor) { this.hatColor = hatColor; }
    public void setShirtColor(String shirtColor) { this.shirtColor = shirtColor; }
}   
