package com.journal.smartjournaling;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.journal.smartjournaling.R;


public class ProfileFragment extends Fragment {

    private TextView tvWelcome;
    private Button btnLogout;
    private FirebaseAuth auth;

    public ProfileFragment() {
        // Required empty public constructor
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        // Initialize Firebase Auth
        auth = FirebaseAuth.getInstance();

        // Find Views
        tvWelcome = view.findViewById(R.id.tvWelcome);
        btnLogout = view.findViewById(R.id.btnLogout);

        // Get logged-in user and set welcome message
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            user.reload().addOnCompleteListener(task -> {
                String fullName = user.getDisplayName();
                if (fullName != null && !fullName.isEmpty()) {
                    tvWelcome.setText("Welcome, " + fullName);
                } else {
                    tvWelcome.setText("Welcome, User");
                }
            });
        }


        // Logout button click listener
        btnLogout.setOnClickListener(v -> {
            auth.signOut();
            Intent intent = new Intent(getActivity(), LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        });

        return view;
    }
}
