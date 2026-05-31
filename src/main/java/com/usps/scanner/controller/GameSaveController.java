package com.usps.scanner.controller;

import com.usps.scanner.leaderboard.GameSave;
import com.usps.scanner.leaderboard.GameSaveRepository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;
import org.springframework.web.bind.annotation.RequestParam;


@RestController
@RequestMapping("/api/game-saves")

public class GameSaveController {

    private final GameSaveRepository gameSaveRepo;

    public GameSaveController (GameSaveRepository gameSaveRepo) {
        this.gameSaveRepo = gameSaveRepo;
        
    }

@PostMapping 
public GameSave save (@RequestBody GameSave save) {return gameSaveRepo.save(save);}

@GetMapping("/user/{username}")
public List<GameSave> byUser (@PathVariable String username){return gameSaveRepo.findByUsername(username);}

@GetMapping("/leaderboard")
public List<GameSave> leaderboard() {return gameSaveRepo.findTop10ByOrderByScoreDesc();
    
}

}