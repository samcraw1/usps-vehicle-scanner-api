package com.usps.scanner.service;

import com.usps.scanner.model.HistoryEntry;
import com.usps.scanner.model.InspectionResponse;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Deque;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedDeque;

// @Service - Spring auto-creates one shared instance and injects it where needed.
// "In-memory" = data lives in RAM only. Restart the server, history is gone.
// (Real apps use a DB - but for a portfolio piece this is fine and shows you understand state management.)
@Service
public class InspectionHistoryServiceImpl implements InspectionHistoryService {

    // Cap so history can't grow forever. Drops the oldest when full.
    private static final int MAX_ENTRIES = 20;

    // ConcurrentLinkedDeque = thread-safe double-ended list.
    // Spring handles requests in parallel - if two users hit /api/inspect at the same time,
    // a regular ArrayList could corrupt. ConcurrentLinkedDeque handles it safely.
    private final Deque<HistoryEntry> entries = new ConcurrentLinkedDeque<>();

    // Add a new inspection to the front (newest first).
    @Override
    public void record(String filename, long imageSize, InspectionResponse result) {
        entries.addFirst(new HistoryEntry(filename, imageSize, result));

        // Trim from the back (oldest) if over the cap.
        while (entries.size() > MAX_ENTRIES) {
            entries.pollLast();
        }
    }

    // Return a snapshot of the current history (newest first).
    // Wrapping in a new ArrayList prevents callers from modifying our internal state.
    @Override
    public List<HistoryEntry> getAll() {
        return new ArrayList<>(entries);
    }

    // Wipe history.
    @Override
    public void clear() {
        entries.clear();
    }

    // Convenience: how many entries currently stored.
    @Override
    public int size() {
        return entries.size();
    }
}
