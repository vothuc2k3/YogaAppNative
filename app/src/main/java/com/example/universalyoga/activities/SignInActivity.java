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

import com.example.universalyoga.models.UserModel;
import com.example.universalyoga.sqlite.DAO.UserDAO;
import com.example.universalyoga.utils.Util;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import com.example.universalyoga.R;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class SignInActivity extends AppCompatActivity {
    private EditText emailEditText, passwordEditText;
    private ProgressBar progressBar;
    private FirebaseAuth mAuth;
    private UserDAO userDAO;
    private static final String USERS_COLLECTION = "users";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);
        initializeUI();
        setUpListeners();
    }

    private void initializeUI() {
        mAuth = FirebaseAuth.getInstance();
        userDAO = new UserDAO(this);
        emailEditText = findViewById(R.id.email);
        passwordEditText = findViewById(R.id.password);
        progressBar = findViewById(R.id.progressBar);
    }

    private void setUpListeners() {
        Button loginButton = findViewById(R.id.login_button);
        Button signUpButton = findViewById(R.id.sign_up_button);

        loginButton.setOnClickListener(v -> attemptUserLogin());

        signUpButton.setOnClickListener(v -> {
            Intent intent = new Intent(SignInActivity.this, SignUpActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        });
    }

    private void attemptUserLogin() {
        String email = emailEditText.getText().toString().trim().toLowerCase();
        String password = passwordEditText.getText().toString().trim();

        if (!validateInput(email, password)) {
            return;
        }

        if (!Util.checkNetworkConnection(this)) {
            Toast.makeText(SignInActivity.this, "No internet connection, try again later...", Toast.LENGTH_SHORT)
                    .show();
            return;
        }

        progressBar.setVisibility(View.VISIBLE);
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection(USERS_COLLECTION).whereEqualTo("email", email)
                .get()
                .addOnCompleteListener(roleCheckTask -> {
                    if (roleCheckTask.isSuccessful() && !roleCheckTask.getResult().isEmpty()) {
                        DocumentSnapshot document = roleCheckTask.getResult().getDocuments().get(0);
                        String role = document.getString("role");
                        if ("admin".equals(role) || "instructor".equals(role)) {
                            signInWithEmail(email, password, db);
                        } else {
                            progressBar.setVisibility(View.GONE);
                            Toast.makeText(SignInActivity.this, "Access denied: Unauthorized role.", Toast.LENGTH_SHORT)
                                    .show();
                        }
                    } else {
                        progressBar.setVisibility(View.GONE);
                        Toast.makeText(SignInActivity.this, "User not found or role check failed.", Toast.LENGTH_SHORT)
                                .show();
                    }
                });
    }

    private void signInWithEmail(String email, String password, FirebaseFirestore db) {
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    progressBar.setVisibility(View.GONE);
                    if (task.isSuccessful()) {
                        FirebaseUser firebaseUser = mAuth.getCurrentUser();
                        if (firebaseUser != null) {
                            String uid = firebaseUser.getUid();
                            retrieveUserData(uid, db);
                        }
                    } else {
                        String errorMessage = task.getException() != null
                                ? task.getException().getMessage()
                                : "Authentication failed.";
                        Toast.makeText(SignInActivity.this, errorMessage, Toast.LENGTH_SHORT)
                                .show();
                    }
                });
    }

    private void retrieveUserData(String uid, FirebaseFirestore db) {
        db.collection(USERS_COLLECTION).document(uid).get()
                .addOnCompleteListener(userTask -> {
                    if (userTask.isSuccessful() && userTask.getResult().exists()) {
                        UserModel userModel = userTask.getResult().toObject(UserModel.class);
                        if (userModel != null && ("admin".equals(userModel.getRole()) || "instructor".equals(userModel.getRole()))) {
                            boolean isUserStored = userDAO.getUserByUid(userModel.getUid()) != null;
                            if (!isUserStored) {
                                userDAO.addUser(userModel);
                            }
                            Toast.makeText(SignInActivity.this, "Login successful", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(SignInActivity.this, MainActivity.class);
                            startActivity(intent);
                            finish();
                        } else {
                            Toast.makeText(SignInActivity.this, "Access denied", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(SignInActivity.this, "User data not found in Firestore.", Toast.LENGTH_SHORT).show();
                    }
                });
    }


    private boolean validateInput(String email, String password) {
        if (TextUtils.isEmpty(email)) {
            emailEditText.setError("Email is required.");
            return false;
        }
        if (TextUtils.isEmpty(password)) {
            passwordEditText.setError("Password is required.");
            return false;
        }
        if (password.length() < 6) {
            passwordEditText.setError("Password must be >= 6 characters.");
            return false;
        }
        return true;
    }
}
