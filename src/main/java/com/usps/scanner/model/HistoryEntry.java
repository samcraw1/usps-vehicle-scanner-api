package com.usps.scanner.model;

import java.time.Instant;

// One row in the inspection history log.
// Wraps an InspectionResponse with metadata: when it happened, file name, file size.
public class HistoryEntry {

    private String timestamp;           // ISO-8601 string like "2026-05-10T22:30:15Z"
    private String filename;            // original uploaded file name
    private long imageSize;             // bytes
    private InspectionResponse result;  // the actual analysis result

    public HistoryEntry() {}

    public HistoryEntry(String filename, long imageSize, InspectionResponse result) {
        this.timestamp = Instant.now().toString();   // capture the moment NOW
        this.filename = filename;
        this.imageSize = imageSize;
        this.result = result;
    }

    public String getTimestamp() { return timestamp; }
    public String getFilename() { return filename; }
    public long getImageSize() { return imageSize; }
    public InspectionResponse getResult() { return result; }

    public void setTimestamp(String timestamp) { this.timestamp = timestamp; }
    public void setFilename(String filename) { this.filename = filename; }
    public void setImageSize(long imageSize) { this.imageSize = imageSize; }
    public void setResult(InspectionResponse result) { this.result = result; }
}
