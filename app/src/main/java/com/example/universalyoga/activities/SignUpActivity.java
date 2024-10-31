package com.example.universalyoga.activities;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.universalyoga.R;
import com.example.universalyoga.models.UserModel;
import com.example.universalyoga.sqlite.DAO.UserDAO;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

public class SignUpActivity extends AppCompatActivity {

    private EditText emailEditText, passwordEditText, confirmPasswordEditText, nameEditText, phoneNumberEditText;
    private Button signUpButton, loginButton;
    private ProgressBar progressBar;
    private FirebaseAuth mAuth;
    private UserDAO userDAO;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        initializeUI();
        setUpListeners();
    }

    private void initializeUI() {
        mAuth = FirebaseAuth.getInstance();
        userDAO = new UserDAO(this);
        nameEditText = findViewById(R.id.username);
        emailEditText = findViewById(R.id.email);
        phoneNumberEditText = findViewById(R.id.phone_number);
        passwordEditText = findViewById(R.id.password);
        confirmPasswordEditText = findViewById(R.id.confirm_password);
        signUpButton = findViewById(R.id.sign_up_button);
        loginButton = findViewById(R.id.login_button);
        progressBar = findViewById(R.id.progressBar);
    }

    private void setUpListeners() {
        signUpButton.setOnClickListener(v -> registerUser());
        loginButton.setOnClickListener(v -> {
            startActivity(new Intent(SignUpActivity.this, SignInActivity.class));
            finish();
        });
    }

    private void registerUser() {
        String name = nameEditText.getText().toString().trim();
        String email = emailEditText.getText().toString().trim();
        String phoneNumber = phoneNumberEditText.getText().toString().trim();
        String password = passwordEditText.getText().toString().trim();
        String confirmPassword = confirmPasswordEditText.getText().toString().trim();

        if (!validateInput(name, email, phoneNumber, password, confirmPassword)) {
            return;
        }

        progressBar.setVisibility(View.VISIBLE);

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser firebaseUser = mAuth.getCurrentUser();
                        Toast.makeText(SignUpActivity.this, "Registration successful", Toast.LENGTH_SHORT).show();

                        if (firebaseUser != null) {
                            String uid = firebaseUser.getUid();
                            UserModel userModel = new UserModel(uid, name, email, phoneNumber);
                            FirebaseFirestore db = FirebaseFirestore.getInstance();

                            db.collection("users").document(uid)
                                    .set(userModel.toMap())
                                    .addOnSuccessListener(aVoid -> {
                                        userDAO.addUser(userModel);
                                        Toast.makeText(SignUpActivity.this, "User data synced to Firestore and SQLite", Toast.LENGTH_SHORT).show();

                                        Intent intent = new Intent(SignUpActivity.this, MainActivity.class);
                                        startActivity(intent);
                                        finish();
                                    })
                                    .addOnFailureListener(e -> {
                                        Toast.makeText(SignUpActivity.this, "Failed to sync user data to Firestore: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                    });
                        }
                    } else {
                        Toast.makeText(SignUpActivity.this, "Registration failed: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }

                    progressBar.setVisibility(View.GONE);
                });
    }

    private boolean validateInput(String name, String email, String phoneNumber, String password, String confirmPassword) {
        if (TextUtils.isEmpty(name)) {
            nameEditText.setError("Name is required.");
            return false;
        }

        if (TextUtils.isEmpty(email)) {
            emailEditText.setError("Email is required.");
            return false;
        }

        if (TextUtils.isEmpty(phoneNumber)) {
            phoneNumberEditText.setError("Phone number is required.");
            return false;
        }

        if (TextUtils.isEmpty(password)) {
            passwordEditText.setError("Password is required.");
            return false;
        }

        if (TextUtils.isEmpty(confirmPassword)) {
            confirmPasswordEditText.setError("Please confirm your password.");
            return false;
        }

        if (!password.equals(confirmPassword)) {
            confirmPasswordEditText.setError("Passwords do not match.");
            return false;
        }

        if (password.length() < 6) {
            passwordEditText.setError("Password must be >= 6 characters.");
            return false;
        }

        return true;
    }
}
