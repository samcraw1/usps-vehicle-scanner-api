package com.usps.scanner.leaderboard;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface GameSaveRepository extends JpaRepository 
<GameSave, Long> {

List<GameSave> findByUsername(String username);
List <GameSave> findTop10ByOrderByScoreDesc();
}