package com.journal.smartjournaling;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

public class JournalEntryDetailActivity extends AppCompatActivity {

    private TextView tvTitle, tvDate, tvTime, tvEntryContent;
    private LinearLayout tagsContainer;
    private ImageView btnClose, ivAddPhoto;
    private Button btnDelete;
    private FirebaseFirestore db;
    private String entryId, userId;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_journal_entry_detail);

        // Initialize UI elements
        tvTitle = findViewById(R.id.tvTitle);
        tvDate = findViewById(R.id.tvDate);
        tvTime = findViewById(R.id.tvTime);
        tvEntryContent = findViewById(R.id.tvEntryContent);
        tagsContainer = findViewById(R.id.tagsContainer);
        btnClose = findViewById(R.id.btnClose);
        ivAddPhoto = findViewById(R.id.ivAddPhoto);
        btnDelete = findViewById(R.id.btnDelete);
        db = FirebaseFirestore.getInstance();

        // Get Data from Intent
        Intent intent = getIntent();
        if (intent != null) {
            tvTitle.setText(intent.getStringExtra("title"));
            tvEntryContent.setText(intent.getStringExtra("content"));
            entryId = intent.getStringExtra("entryId");
            userId = intent.getStringExtra("userId");

            // Set Date and Time
            long timestamp = intent.getLongExtra("timestamp", 0);
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd MMM yyyy", Locale.getDefault());
            SimpleDateFormat timeFormat = new SimpleDateFormat("hh:mm a", Locale.getDefault());
            tvDate.setText(dateFormat.format(timestamp));
            tvTime.setText(timeFormat.format(timestamp));

            // Set Tags
            List<String> tags = intent.getStringArrayListExtra("tags");
            if (tags != null) {
                for (String tag : tags) {
                    TextView tagView = new TextView(this);
                    tagView.setText(tag);
                    tagView.setPadding(16, 8, 16, 8);
                    tagView.setBackground(getDrawable(R.drawable.tag_background));
                    tagsContainer.addView(tagView);
                }
            }
        }

        // Close Button
        btnClose.setOnClickListener(v -> finish());

        // Delete Entry
        btnDelete.setOnClickListener(v -> {
            db.collection("users").document(userId).collection("journalEntries")
                    .document(entryId)
                    .delete()
                    .addOnSuccessListener(aVoid -> {
                        Toast.makeText(JournalEntryDetailActivity.this, "Entry deleted", Toast.LENGTH_SHORT).show();
                        finish();
                    })
                    .addOnFailureListener(e ->
                            Toast.makeText(JournalEntryDetailActivity.this, "Failed to delete", Toast.LENGTH_SHORT).show());
        });
    }
}
