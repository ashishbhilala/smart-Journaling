package com.journal.smartjournaling;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity {

    private EditText emailInput, passwordInput;
    private Button loginButton;
    private TextView signupLink;
    private ProgressBar progressBar;
    private FirebaseAuth mAuth;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();

        // Initialize UI elements
        emailInput = findViewById(R.id.emailInput);
        passwordInput = findViewById(R.id.passwordInput);
        loginButton = findViewById(R.id.loginButton);
        signupLink = findViewById(R.id.signupLink);
        progressBar = findViewById(R.id.progressBar);

        // Apply Fade-in Animation
        Animation fadeIn = AnimationUtils.loadAnimation(this, R.anim.fade_in);
        emailInput.startAnimation(fadeIn);
        passwordInput.startAnimation(fadeIn);
        loginButton.startAnimation(fadeIn);
        signupLink.startAnimation(fadeIn);

        // Login Button Click Event
        loginButton.setOnClickListener(v -> loginUser());

        // Navigate to Signup Page
        signupLink.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, SignupActivity.class);
            startActivity(intent);
        });

        // Check if User is Already Logged In
        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            // Redirect to Main Activity if Already Logged In
            startActivity(new Intent(LoginActivity.this, MainActivity.class));
            finish();
        }
    }

    private void loginUser() {
        String email = emailInput.getText().toString().trim();
        String password = passwordInput.getText().toString().trim();

        if (TextUtils.isEmpty(email)) {
            emailInput.setError("Enter email!");
            return;
        }
        if (TextUtils.isEmpty(password)) {
            passwordInput.setError("Enter password!");
            return;
        }

        // Show progress bar
        progressBar.setVisibility(View.VISIBLE);

        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    progressBar.setVisibility(View.GONE);
                    if (task.isSuccessful()) {
                        // Login Successful
                        Toast.makeText(LoginActivity.this, "Login successful!", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(LoginActivity.this, MainActivity.class));
                        finish();
                    } else {
                        // Login Failed
                        Toast.makeText(LoginActivity.this, "Login failed! Check your credentials.", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
