package com.journal.smartjournaling;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.*;
import java.util.*;

public class JournalFragment extends Fragment {
    private RecyclerView recyclerView;
    private JournalAdapter journalAdapter;
    private List<JournalEntry> journalEntries = new ArrayList<>();
    private FirebaseFirestore db;

    private FirebaseAuth auth;
    private String userId;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_journal, container, false);
        recyclerView = view.findViewById(R.id.recyclerViewJournal);
        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();

        // Get currently logged-in user
        FirebaseUser user = auth.getCurrentUser();
        if (user != null) {
            userId = user.getUid();  // Dynamically set userId
            loadJournalEntries();
        } else {
            Toast.makeText(getContext(), "User not logged in!", Toast.LENGTH_SHORT).show();
        }

        return view;
    }


    private void loadJournalEntries() {
        db.collection("users").document(userId).collection("journalEntries")
                .orderBy("timestamp", Query.Direction.DESCENDING)
                .addSnapshotListener((snapshots, e) -> {
                    if (e != null) return;
                    if (snapshots != null) {
                        journalEntries.clear();
                        for (DocumentSnapshot doc : snapshots) {
                            JournalEntry entry = doc.toObject(JournalEntry.class);
                            if (entry != null) journalEntries.add(entry);
                        }
                        journalAdapter = new JournalAdapter(getContext(), journalEntries);
                        recyclerView.setAdapter(journalAdapter);
                    }
                });
    }

}
