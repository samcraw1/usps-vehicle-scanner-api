package com.usps.scanner.model;

import java.util.List;

// Represents the FULL response from an inspection.
// Matches the JSON shape your React Native app expects: { status, issues, summary }.
public class InspectionResponse {

    private String status;        // "PASS" | "ATTENTION" | "UNSAFE"
    private List<Issue> issues;   // a list of Issue objects (could be empty)
    private String summary;       // one-sentence overall summary

    public InspectionResponse() {}

    public InspectionResponse(String status, List<Issue> issues, String summary) {
        this.status = status;
        this.issues = issues;
        this.summary = summary;
    }

    public String getStatus() { return status; }
    public List<Issue> getIssues() { return issues; }
    public String getSummary() { return summary; }

    public void setStatus(String status) { this.status = status; }
    public void setIssues(List<Issue> issues) { this.issues = issues; }
    public void setSummary(String summary) { this.summary = summary; }
}
