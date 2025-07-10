package com.journal.smartjournaling;

import com.google.firebase.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class JournalEntry {
    private String entryId;
    private String text;
    private Timestamp timestamp; // Correct Firebase type
    private List<String> tags;

    // Required empty constructor for Firebase
    public JournalEntry() {}

    public JournalEntry(String entryId, String text, List<String> tags, Date date) {
        this.entryId = entryId;
        this.text = text;
        this.timestamp = new Timestamp(date);
        this.tags = tags;
    }

    // Getters
    public String getEntryId() { return entryId; }
    public String getText() { return text; }
    public Timestamp getTimestamp() { return timestamp; }
    public List<String> getTags() { return tags; }

    // Convert Timestamp to milliseconds for sorting
    public long getTimestampMillis() {
        return timestamp != null ? timestamp.toDate().getTime() : 0;
    }

    // Generate formatted date from Timestamp
    public String getFormattedDate() {
        if (timestamp != null) {
            Date date = timestamp.toDate();
            SimpleDateFormat sdf = new SimpleDateFormat("dd MMM yyyy", Locale.getDefault());
            return sdf.format(date);
        }
        return "Unknown Date";
    }
}
