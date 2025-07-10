package com.journal.smartjournaling;

import java.util.List;

public class JournalGroup {
    private String date;
    private List<JournalEntry> entries;
    private boolean isExpanded; // For expanding/collapsing the list

    public JournalGroup(String date, List<JournalEntry> entries) {
        this.date = date;
        this.entries = entries;
        this.isExpanded = false; // Default collapsed
    }

    public String getDate() { return date; }
    public List<JournalEntry> getEntries() { return entries; }
    public boolean isExpanded() { return isExpanded; }
    public void setExpanded(boolean expanded) { isExpanded = expanded; }
}
