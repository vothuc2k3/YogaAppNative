package com.example.universalyoga.activities;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;

import com.example.universalyoga.R;
import com.example.universalyoga.models.UserModel;
import com.example.universalyoga.sqlite.DAO.UserDAO;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Objects;

public class AddAccountActivity extends AppCompatActivity {

    private TextInputEditText etEmail, etUsername, etPhoneNumber, etPassword, etConfirmPassword;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_account);

        setupToolbar();
        initializeFields();
        setupConfirmButton();
    }

    private void setupToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Add Account");
        }
        toolbar.setNavigationOnClickListener(v -> finish());
        toolbar.setTitleTextColor(ContextCompat.getColor(this, R.color.white));
    }

    private void initializeFields() {
        mAuth = FirebaseAuth.getInstance();
        etEmail = findViewById(R.id.et_email);
        etUsername = findViewById(R.id.et_username);
        etPhoneNumber = findViewById(R.id.et_phone_number);
        etPassword = findViewById(R.id.et_password);
        etConfirmPassword = findViewById(R.id.et_confirm_password);
    }

    private void setupConfirmButton() {
        Button btnConfirm = findViewById(R.id.btn_confirm);
        btnConfirm.setOnClickListener(v -> validateAndCreateAccount());
    }

    private void validateAndCreateAccount() {
        String email = Objects.requireNonNull(etEmail.getText()).toString().trim();
        String username = Objects.requireNonNull(etUsername.getText()).toString().trim();
        String phoneNumber = Objects.requireNonNull(etPhoneNumber.getText()).toString().trim();
        String password = Objects.requireNonNull(etPassword.getText()).toString().trim();
        String confirmPassword = Objects.requireNonNull(etConfirmPassword.getText()).toString().trim();

        if (TextUtils.isEmpty(email)) {
            etEmail.setError("Email is required");
            etEmail.requestFocus();
            return;
        }

        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            etEmail.setError("Invalid email format");
            etEmail.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(username)) {
            etUsername.setError("Username is required");
            etUsername.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(phoneNumber)) {
            etPhoneNumber.setError("Phone number is required");
            etPhoneNumber.requestFocus();
            return;
        }

        if (!phoneNumber.matches("^0\\d{9}$")) {
            etPhoneNumber.setError("Phone number must be in the format 0xxxxxxxxx");
            etPhoneNumber.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(password)) {
            etPassword.setError("Password is required");
            etPassword.requestFocus();
            return;
        }

        if (password.length() < 6) {
            etPassword.setError("Password must be at least 6 characters");
            etPassword.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(confirmPassword)) {
            etConfirmPassword.setError("Confirm password is required");
            etConfirmPassword.requestFocus();
            return;
        }

        if (!password.equals(confirmPassword)) {
            etConfirmPassword.setError("Passwords do not match");
            etConfirmPassword.requestFocus();
            return;
        }

        createAccount(email, password, username, phoneNumber);
    }

    private void createAccount(String email, String password, String username, String phoneNumber) {
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser user = mAuth.getCurrentUser();

                        if (user != null) {
                            String uid = user.getUid();
                            UserModel newUser = new UserModel(uid, username, email, phoneNumber);

                            FirebaseFirestore db = FirebaseFirestore.getInstance();
                            db.collection("users").document(uid)
                                    .set(newUser.toMap())
                                    .addOnSuccessListener(aVoid -> {
                                        UserDAO userDAO = new UserDAO(AddAccountActivity.this);
                                        userDAO.addUser(newUser);
                                        Intent resultIntent = new Intent();
                                        resultIntent.putExtra("NEW_USER", newUser);
                                        setResult(RESULT_OK, resultIntent);
                                        Toast.makeText(AddAccountActivity.this, "Account created successfully!", Toast.LENGTH_SHORT).show();
                                        finish();
                                    })
                                    .addOnFailureListener(e -> Toast.makeText(AddAccountActivity.this, "Failed to create user data in Firestore: " + e.getMessage(), Toast.LENGTH_SHORT).show());
                        }
                    } else {
                        if (task.getException() instanceof FirebaseAuthUserCollisionException) {
                            Toast.makeText(AddAccountActivity.this, "This email is already in use.", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(AddAccountActivity.this, "Account creation failed: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
}
