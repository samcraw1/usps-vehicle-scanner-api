package com.usps.scanner.leaderboard;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class LeaderboardController {

    private final LeaderboardService service;
    private final InspectionRepository inspectionRepo;
    private final UserRepository userRepo;  

    public LeaderboardController(LeaderboardService service, InspectionRepository inspectionRepo, UserRepository userRepo) {
        this.service = service;
        this.inspectionRepo = inspectionRepo;
        this.userRepo = userRepo; 

    }

    // POST /api/users - create or fetch user by name
    @PostMapping("/users")
    public LeaderboardEntry createUser(@RequestBody Map<String, String> body) {
        String username = body.get("username");
        if (username == null || username.isBlank()) {
            throw new IllegalArgumentException("username is required");
        }
        return service.getOrCreateUser(username.trim());
    }

    // GET /api/users/{username} - lookup user
    @GetMapping("/users/{username}")
    public LeaderboardEntry getUser(@PathVariable String username) {
        return service.getOrCreateUser(username);
    }

    // POST /api/scores - record a new score
    @PostMapping("/scores")
    public ResponseEntity<Score> submitScore(@RequestBody Map<String, Object> body) {
        String username = (String) body.get("username");
        Number timeMs = (Number) body.get("totalTimeMs");
        if (username == null || username.isBlank() || timeMs == null) {
            throw new IllegalArgumentException("username and totalTimeMs are required");
        }
        Score saved = service.submitScore(username.trim(), timeMs.longValue());
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    // GET /api/leaderboard - top 10
    @GetMapping("/leaderboard")
    public List<LeaderboardEntry> getLeaderboard() {
        return service.getLeaderboard();
    }

    // PUT /api/users/{username}/outfit - update a user's outfit colors
    @PutMapping("/users/{username}/outfit")
    public LeaderboardEntry updateOutfit(
            @PathVariable String username,
            @RequestBody Map<String, String> body) {
        String hatColor = body.get("hatColor");
        String shirtColor = body.get("shirtColor");
        if (hatColor == null || shirtColor == null) {
            throw new IllegalArgumentException("hatColor and shirtColor are required");
        }
        return service.updateOutfit(username, hatColor, shirtColor);
    }

    // POST /api/inspections - record a new inspection (for seeding/testing)
    @PostMapping("/inspections")
    public Inspection recordInspection(@RequestBody Map<String, String> body) {
        String username = body.get("username");
        String status = body.get("status");
        String summary = body.get("summary");
        if (username == null || status == null) {
            throw new IllegalArgumentException("username and status are required");
        }
        User user = userRepo.findByUsername(username)
                .orElseGet(() -> userRepo.save(new User(username)));
        return inspectionRepo.save(new Inspection(user, status, summary));
    }

    // GET /api/inspections/{username} - all inspections for that user, newest first
    @GetMapping("/inspections/{username}")
    public List<Inspection> getInspections(@PathVariable String username) {
        return inspectionRepo.findByUser_UsernameOrderByCreatedAtDesc(username);
    }

    // GET /api/supervisor/overview - all carriers + scores + role + inspection summary
    @GetMapping("/supervisor/overview")
    public List<SupervisorEntry> getSupervisorOverview() {
        return service.getSupervisorLeaderboard();
    }
}
