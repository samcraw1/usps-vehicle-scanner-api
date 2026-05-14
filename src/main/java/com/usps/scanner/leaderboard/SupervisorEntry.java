package com.usps.scanner.leaderboard;

public class SupervisorEntry {

    private String username;
    private long bestTimeMs;
    private Role role;
    private String lastInspectionStatus;
    private long totalInspections;

    public SupervisorEntry(String username, long bestTimeMs, Role role, String lastInspectionStatus, long totalInspections) {
        this.username = username;
        this.bestTimeMs = bestTimeMs;
        this.role = role;
        this.lastInspectionStatus = lastInspectionStatus;
        this.totalInspections = totalInspections;
        
    }

    public long getBestTimeMs() {
        return bestTimeMs;
    }

    public String getUsername() {
        return username;
    }

    public Role getRole() {
        return role;
    }

    public String getLastInspectionStatus() {
        return lastInspectionStatus;
    }

    public long getTotalInspections() {
        return totalInspections;
    }

    public void setBestTimeMs(long bestTimeMs) {
        this.bestTimeMs = bestTimeMs;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public void setLastInspectionStatus(String lastInspectionStatus) {
        this.lastInspectionStatus = lastInspectionStatus;
    }

    public void setTotalInspections(long totalInspections) {
        this.totalInspections = totalInspections;
    }



}
