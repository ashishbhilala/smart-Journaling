package com.journal.smartjournaling;


import com.google.firebase.firestore.FirebaseFirestore;
import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;


public class MainActivity extends AppCompatActivity {

    private BottomNavigationView bottomNavigationView;
    private FloatingActionButton fabAddEntry;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        db = FirebaseFirestore.getInstance();
        // Initialize UI elements
        bottomNavigationView = findViewById(R.id.bottom_navigation);
        fabAddEntry = findViewById(R.id.fab_add_entry);

        // Set Default Fragment (Journal Page)
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, new JournalFragment())
                    .commit();
        }

        // Bottom Navigation Click Listener
        bottomNavigationView.setOnItemSelectedListener(item -> {
            Fragment selectedFragment = null;
            int itemId = item.getItemId();
            if (itemId == R.id.nav_journal) {
                selectedFragment = new JournalFragment();
            } else if (itemId == R.id.nav_profile) {
                selectedFragment = new ProfileFragment();
            } else if (itemId == R.id.nav_insights) {
                selectedFragment = new InsightsFragment();
            } else if (itemId == R.id.nav_challenges) {
                selectedFragment = new ChallengesFragment();
            }

            if (selectedFragment != null) {
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, selectedFragment)
                        .commit();
            }
            return true;
        });

        // Floating Action Button Click Listener
        fabAddEntry.setOnClickListener(view -> {
            Intent intent = new Intent(MainActivity.this, AddEntryActivity.class);
            startActivity(intent);
        });
    }
}
