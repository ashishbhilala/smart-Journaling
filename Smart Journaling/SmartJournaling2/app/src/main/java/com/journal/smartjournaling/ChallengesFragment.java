package com.journal.smartjournaling;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import com.google.android.material.snackbar.Snackbar;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class ChallengesFragment extends Fragment {

    private TextView tvPrompt, tvProgressCount, tvNextPrompt;
    private ProgressBar progressBar;
    private Button btnStartEntry;
    private CardView cardNextChallenge;

    private String[] prompts = {
            "What made you smile today?",
            "Describe a recent challenge and how you overcame it.",
            "What are three things you're grateful for?",
            "What is something new you learned recently?",
            "How do you handle stress?",
            "Describe your ideal day.",
            "What motivates you to grow as a person?"
    };

    private int totalDays = 30;

    @Nullable
    @Override

    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_challenges, container, false);

        tvPrompt = view.findViewById(R.id.tvPrompt);
        tvProgressCount = view.findViewById(R.id.tvProgressCount);
        progressBar = view.findViewById(R.id.progressBar);
        btnStartEntry = view.findViewById(R.id.btnStartEntry);
        cardNextChallenge = view.findViewById(R.id.cardNextChallenge);
        tvNextPrompt = view.findViewById(R.id.tvNextPrompt);

        int dayIndex = getTodayIndex();
        tvPrompt.setText(prompts[dayIndex % prompts.length]);

        int completedDays = loadCompletedDays();
        progressBar.setMax(totalDays);

        ObjectAnimator animation = ObjectAnimator.ofInt(progressBar, "progress", 0, completedDays);
        animation.setDuration(1000); // 1 second animation
        animation.start();

        progressBar.setProgress(completedDays);
        tvProgressCount.setText(completedDays + " of " + totalDays + " days completed");

        SharedPreferences prefs = getContext().getSharedPreferences("challenge_prefs", Context.MODE_PRIVATE);
        String lastCompleted = prefs.getString("last_completed_date", "");
        String today = new SimpleDateFormat("yyyyMMdd", Locale.getDefault()).format(new Date());

        // Disable writing again if already completed today
        if (lastCompleted.equals(today)) {
            btnStartEntry.setEnabled(false);
            btnStartEntry.setText("Challenge Completed");
        }

        btnStartEntry.setOnClickListener(v -> {
            SharedPreferences.Editor editor = prefs.edit();
            editor.putString("last_completed_date", today);
            editor.apply();

            Intent intent = new Intent(getActivity(), AddEntryActivity.class);
            intent.putExtra("promptText", prompts[dayIndex % prompts.length]);
            startActivity(intent);
        });

        // Handle Next Challenge teaser card
        if (!lastCompleted.equals(today)) {
            cardNextChallenge.setAlpha(1f);
            cardNextChallenge.setClickable(true);
            tvNextPrompt.setText("New Challenge Unlocked! Tap to begin.");
            cardNextChallenge.setOnClickListener(v -> {
                // Implement your action here for the next challenge
                Snackbar.make(v, "Come back tomorrow to unlock this challenge!", Snackbar.LENGTH_SHORT).show();
            });
        } else {
            cardNextChallenge.setAlpha(0.5f);
            cardNextChallenge.setClickable(false); // Optionally make it non-clickable
            tvNextPrompt.setText("Unlocks Tomorrow!");
        }

        return view;
    }


    private int getTodayIndex() {
        Calendar calendar = Calendar.getInstance();
        return calendar.get(Calendar.DAY_OF_YEAR);
    }

    private int loadCompletedDays() {
        // TODO: Replace with Firestore count
        return 15; // Simulated
    }
}
