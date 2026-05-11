package com.usps.scanner.controller;

import com.usps.scanner.model.HistoryEntry;
import com.usps.scanner.model.InspectionResponse;
import com.usps.scanner.service.InspectionHistoryService;
import com.usps.scanner.service.OpenAIService;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class InspectionController {

    // Both services injected via the constructor.
    // Spring sees the constructor and passes both in automatically.
    private final OpenAIService openAIService;
    private final InspectionHistoryService historyService;

    public InspectionController(OpenAIService openAIService, InspectionHistoryService historyService) {
        this.openAIService = openAIService;
        this.historyService = historyService;
    }

    // POST /api/inspect - analyze an uploaded image AND record it in history.
    @PostMapping("/inspect")
    public InspectionResponse inspect(@RequestParam("image") MultipartFile image) {
        InspectionResponse response = openAIService.analyze(image);

        // Record this inspection in history (newest first).
        historyService.record(image.getOriginalFilename(), image.getSize(), response);

        return response;
    }

    // GET /api/history - list of recent inspections (newest first, max 20).
    @GetMapping("/history")
    public List<HistoryEntry> getHistory() {
        return historyService.getAll();
    }

    // DELETE /api/history - wipe history.
    // Returns a tiny status object so the caller knows it worked.
    @DeleteMapping("/history")
    public Map<String, Object> clearHistory() {
        int wiped = historyService.size();
        historyService.clear();
        return Map.of("cleared", true, "removed", wiped);
    }

    // GET /api/health - simple "is the server alive" check.
    // Useful for monitoring tools and for visiting in a browser to confirm the server is up.
    @GetMapping("/health")
    public Map<String, Object> health() {
        return Map.of(
                "status", "ok",
                "service", "USPS Vehicle Scanner API",
                "historyCount", historyService.size()
        );
    }
}
