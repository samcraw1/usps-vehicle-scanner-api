package com.usps.scanner.leaderboard;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class LeaderboardServiceImpl implements LeaderboardService {

    private final UserRepository userRepo;
    private final ScoreRepository scoreRepo;
    private final InspectionRepository inspectionRepo;

    public LeaderboardServiceImpl(UserRepository userRepo, ScoreRepository scoreRepo, InspectionRepository inspectionRepo) {
        this.userRepo = userRepo;
        this.scoreRepo = scoreRepo;
        this.inspectionRepo = inspectionRepo;
    }

    @Override
    @Transactional
    public LeaderboardEntry getOrCreateUser(String username) {
        User user = userRepo.findByUsername(username)
                .orElseGet(() -> userRepo.save(new User(username)));

        long bestTime = scoreRepo.findFirstByUser_UsernameOrderByTotalTimeMsAsc(username)
                .map(Score::getTotalTimeMs)
                .orElse(0L);

        return new LeaderboardEntry(
                user.getUsername(),
                bestTime,
                user.getRole(),
                user.getHatColor(),
                user.getShirtColor()
        );
    }

    @Override
    @Transactional
    public Score submitScore(String username, long totalTimeMs) {
        User user = userRepo.findByUsername(username)
                .orElseGet(() -> userRepo.save(new User(username)));

        // Career progression: unlock SUPERVISOR if time beats 5 minutes
        if (totalTimeMs < 300_000 && user.getRole() == Role.CARRIER) {
            user.setRole(Role.SUPERVISOR);
            userRepo.save(user);
        }

        return scoreRepo.save(new Score(user, totalTimeMs));
    }

    @Override
    public List<LeaderboardEntry> getLeaderboard() {
        List<Score> bests = scoreRepo.findPersonalBestsOrderedByTime();
        return bests.stream()
                .limit(10)
                .map(s -> {
                    User u = s.getUser();
                    return new LeaderboardEntry(
                            u.getUsername(),
                            s.getTotalTimeMs(),
                            u.getRole(),
                            u.getHatColor(),
                            u.getShirtColor()
                    );
                })
                .toList();
    }

    @Override
    @Transactional
    public LeaderboardEntry updateOutfit(String username, String hatColor, String shirtColor) {
        User user = userRepo.findByUsername(username)
                .orElseGet(() -> userRepo.save(new User(username)));

        user.setHatColor(hatColor);
        user.setShirtColor(shirtColor);
        User saved = userRepo.save(user);

        long bestTime = scoreRepo.findFirstByUser_UsernameOrderByTotalTimeMsAsc(username)
                .map(Score::getTotalTimeMs)
                .orElse(0L);

        return new LeaderboardEntry(
                saved.getUsername(),
                bestTime,
                saved.getRole(),
                saved.getHatColor(),
                saved.getShirtColor()
        );
    }

    @Override
    public List<SupervisorEntry> getSupervisorLeaderboard() {
        List<User> allUsers = userRepo.findAll();
        List<SupervisorEntry> entries = new ArrayList<>();

        for (User user : allUsers) {
            // Best time for this user (or 0 if no scores yet)
            long bestTime = scoreRepo.findFirstByUser_UsernameOrderByTotalTimeMsAsc(user.getUsername())
                    .map(Score::getTotalTimeMs)
                    .orElse(0L);

            // Last inspection + total count
            List<Inspection> inspections = inspectionRepo
                    .findByUser_UsernameOrderByCreatedAtDesc(user.getUsername());
            String lastStatus = inspections.isEmpty() ? null : inspections.get(0).getStatus();
            long totalInspections = inspections.size();

            entries.add(new SupervisorEntry(
                    user.getUsername(),
                    bestTime,
                    user.getRole(),
                    lastStatus,
                    totalInspections
            ));
        }

        return entries;
    }
}
