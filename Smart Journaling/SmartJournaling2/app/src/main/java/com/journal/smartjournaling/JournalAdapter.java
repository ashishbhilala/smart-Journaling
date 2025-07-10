package com.journal.smartjournaling;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;

import java.util.*;

public class JournalAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final int TYPE_DATE_HEADER = 0;
    private static final int TYPE_ENTRY = 1;

    private Context context;
    private List<Object> groupedEntries;

    public JournalAdapter(Context context, List<JournalEntry> journalEntries) {
        this.context = context;
        this.groupedEntries = groupEntriesByDate(journalEntries);
    }

    private List<Object> groupEntriesByDate(List<JournalEntry> journalEntries) {
        List<Object> groupedList = new ArrayList<>();
        Collections.sort(journalEntries, (a, b) -> Long.compare(b.getTimestampMillis(), a.getTimestampMillis()));

        String lastDate = "";
        for (JournalEntry entry : journalEntries) {
            String entryDate = entry.getFormattedDate();
            if (!entryDate.equals(lastDate)) {
                groupedList.add(entryDate);
                lastDate = entryDate;
            }
            groupedList.add(entry);
        }
        return groupedList;
    }

    @Override
    public int getItemViewType(int position) {
        return (groupedEntries.get(position) instanceof String) ? TYPE_DATE_HEADER : TYPE_ENTRY;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == TYPE_DATE_HEADER) {
            View view = LayoutInflater.from(context).inflate(R.layout.item_date_header, parent, false);
            return new DateViewHolder(view);
        } else {
            View view = LayoutInflater.from(context).inflate(R.layout.item_journal_entry, parent, false);
            return new EntryViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (getItemViewType(position) == TYPE_DATE_HEADER) {
            String dateHeader = (String) groupedEntries.get(position);
            ((DateViewHolder) holder).bind(dateHeader);
        } else {
            JournalEntry entry = (JournalEntry) groupedEntries.get(position);
            ((EntryViewHolder) holder).bind(entry);

            holder.itemView.setOnClickListener(v -> {
                Intent intent = new Intent(context, JournalEntryDetailActivity.class);
                intent.putExtra("entryId", entry.getEntryId());
                intent.putExtra("userId", FirebaseAuth.getInstance().getCurrentUser().getUid());
                intent.putExtra("title", getTitleFromEntry(entry.getText()));
                intent.putExtra("content", entry.getText());
                intent.putStringArrayListExtra("tags", new ArrayList<>(entry.getTags()));
                intent.putExtra("timestamp", entry.getTimestampMillis());

                context.startActivity(intent);
            });
        }
    }

    private String getTitleFromEntry(String text) {
        String[] words = text.split(" ");
        return words.length > 3 ? words[0] + " " + words[1] + " " + words[2] + "..." : text;
    }

    @Override
    public int getItemCount() {
        return groupedEntries.size();
    }

    static class DateViewHolder extends RecyclerView.ViewHolder {
        TextView dateText;

        DateViewHolder(View itemView) {
            super(itemView);
            dateText = itemView.findViewById(R.id.dateHeader);
        }

        void bind(String date) {
            dateText.setText(date);
        }
    }

    public class EntryViewHolder extends RecyclerView.ViewHolder {
        TextView entryTextView;

        public EntryViewHolder(@NonNull View itemView) {
            super(itemView);
            entryTextView = itemView.findViewById(R.id.entryText); // ✅ use correct ID
        }

        public void bind(JournalEntry entry) {
            entryTextView.setText(getTitleFromEntry(entry.getText())); // ✅ use the right TextView
        }
    }

}
