package com.usps.scanner.service;

import com.usps.scanner.model.HistoryEntry;
import com.usps.scanner.model.InspectionResponse;

import java.util.List;

// Interface for recording and retrieving inspection history.
// (Implementation lives in InspectionHistoryServiceImpl.)
public interface InspectionHistoryService {
    void record(String filename, long imageSize, InspectionResponse result);
    List<HistoryEntry> getAll();
    void clear();
    int size();
}
