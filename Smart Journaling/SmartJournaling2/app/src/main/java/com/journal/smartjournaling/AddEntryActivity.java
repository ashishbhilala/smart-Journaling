package com.journal.smartjournaling;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.speech.RecognizerIntent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class AddEntryActivity extends AppCompatActivity {
    private static final int REQUEST_CODE_SPEECH_INPUT = 1;

    private FirebaseFirestore db;
    private FirebaseAuth auth;
    private TextInputEditText etJournalEntry;
    private ChipGroup chipGroup;
    private Button btnSave;
    private ImageButton btnVoiceInput;
    private TextView tvSentiment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_entry);

        // Initialize Firebase
        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();

        // Initialize UI elements
        TextView tvCurrentDate = findViewById(R.id.tvCurrentDate);
        etJournalEntry = findViewById(R.id.etJournalEntry);
        btnSave = findViewById(R.id.btnSave);
        btnVoiceInput = findViewById(R.id.btnVoiceInput);
        chipGroup = findViewById(R.id.chipGroup);


        // Set current date dynamically
        String currentDate = new SimpleDateFormat("MMMM dd, yyyy", Locale.getDefault()).format(new Date());
        tvCurrentDate.setText(currentDate);

        // Apply animation
        ImageView ivFeather = findViewById(R.id.ivFeather);
        Animation fadeAnimation = AnimationUtils.loadAnimation(this, R.anim.fade_in_out);
        ivFeather.startAnimation(fadeAnimation);

        // Voice Input Button Click
        btnVoiceInput.setOnClickListener(v -> startVoiceInput());

        // Save Button Click
        btnSave.setOnClickListener(v -> saveJournalEntry());
    }

    private void startVoiceInput() {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Speak your journal entry...");

        try {
            startActivityForResult(intent, REQUEST_CODE_SPEECH_INPUT);
        } catch (Exception e) {
            Toast.makeText(this, "Speech recognition not supported!", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_SPEECH_INPUT && resultCode == RESULT_OK && data != null) {
            ArrayList<String> result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
            if (result != null && !result.isEmpty()) {
                String voiceInput = result.get(0);
                String existingText = etJournalEntry.getText().toString().trim();

                // Append voice text instead of replacing
                etJournalEntry.setText(existingText.isEmpty() ? voiceInput : existingText + " " + voiceInput);
            }
        }
    }
    private void uploadJournalEntry(String entryText, List<String> selectedTags) {
        String userId = auth.getCurrentUser().getUid();

        String entryId = db.collection("users").document(userId)
                .collection("journalEntries").document().getId();

        Date timestamp = new Date();
        JournalEntry entry = new JournalEntry(entryId, entryText, selectedTags, timestamp);

        db.collection("users").document(userId)
                .collection("journalEntries").document(entryId)
                .set(entry)
                .addOnSuccessListener(documentReference -> {
                    Toast.makeText(this, "Entry saved!", Toast.LENGTH_SHORT).show();
                    finish(); // Only finish after save
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Error saving entry: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    private void saveJournalEntry() {
        if (auth.getCurrentUser() == null) {
            Toast.makeText(this, "User not logged in!", Toast.LENGTH_SHORT).show();
            return;
        }

        String entryText = etJournalEntry.getText().toString().trim();
        List<String> selectedTags = getSelectedTags();

        if (entryText.isEmpty()) {
            Toast.makeText(this, "Please enter your journal text!", Toast.LENGTH_SHORT).show();
            return;
        }

        // AI keyword matching
        Map<String, String> responses = AIResponseHelper.loadResponses(this);
        String response = AIResponseHelper.getMatchingResponse(entryText, responses);

        if (response != null) {
            showAIResponse(response, () -> uploadJournalEntry(entryText, selectedTags)); // Save after dialog
        } else {
            uploadJournalEntry(entryText, selectedTags); // No AI needed
        }
    }

    private void showAIResponse(String response, Runnable onDialogClose) {
        if (isFinishing()) return;

        ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("AI is thinking...");
        progressDialog.setCancelable(false);
        progressDialog.show();

        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            if (!isFinishing() && progressDialog.isShowing()) {
                progressDialog.dismiss();
            }

            if (isFinishing()) return;

            View dialogView = LayoutInflater.from(this).inflate(R.layout.ai_response_dialog, null);
            TextView tvResponse = dialogView.findViewById(R.id.tvAiResponse);
            Button btnClose = dialogView.findViewById(R.id.btnCloseDialog);
            tvResponse.setText(response);

            AlertDialog alertDialog = new AlertDialog.Builder(this)
                    .setView(dialogView)
                    .setCancelable(false)
                    .create();

            btnClose.setOnClickListener(v -> {
                alertDialog.dismiss();
                if (onDialogClose != null) {
                    onDialogClose.run(); // Call after user closes dialog
                }
            });

            alertDialog.show();
        }, 2000); // 2s delay for AI feel
    }






    private List<String> getSelectedTags() {
        List<String> selectedTags = new ArrayList<>();
        for (int i = 0; i < chipGroup.getChildCount(); i++) {
            Chip chip = (Chip) chipGroup.getChildAt(i);
            if (chip.isChecked()) {
                selectedTags.add(chip.getText().toString());
            }
        }
        return selectedTags;
    }
}
