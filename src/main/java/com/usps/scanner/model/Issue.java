// Lives in the "model" subfolder. Models = plain Java classes that represent data.
package com.usps.scanner.model;

// Represents ONE issue found during a vehicle inspection.
// Like a row in a table: { issue, location, severity }.
public class Issue {

    // Private fields = encapsulation (same as your Item.java).
    private String issue;
    private String location;
    private String severity;

    // Empty constructor - Jackson (the JSON library) needs this to build objects from JSON.
    public Issue() {}

    // Convenience constructor - lets us write `new Issue("scratch", "front", "low")`.
    public Issue(String issue, String location, String severity) {
        this.issue = issue;
        this.location = location;
        this.severity = severity;
    }

    // Getters - Jackson uses these to convert this object INTO JSON.
    public String getIssue() { return issue; }
    public String getLocation() { return location; }
    public String getSeverity() { return severity; }

    // Setters - Jackson uses these to populate fields when reading JSON.
    public void setIssue(String issue) { this.issue = issue; }
    public void setLocation(String location) { this.location = location; }
    public void setSeverity(String severity) { this.severity = severity; }
}
