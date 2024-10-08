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
import com.example.universalyoga.worker.SyncManager;
import com.google.android.gms.tasks.TaskCompletionSource;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import com.example.universalyoga.R;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.concurrent.ExecutionException;

public class SignInActivity extends AppCompatActivity {

    private EditText emailEditText, passwordEditText;
    private Button loginButton, signUpButton;
    private ProgressBar progressBar;
    private FirebaseAuth mAuth;
    private UserDAO userDAO;
    private static String USERS_COLLECTION = "users";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sign_in_activity);

        mAuth = FirebaseAuth.getInstance();

        userDAO = new UserDAO(this);

        emailEditText = findViewById(R.id.email);
        passwordEditText = findViewById(R.id.password);
        loginButton = findViewById(R.id.login_button);
        signUpButton = findViewById(R.id.sign_up_button);
        progressBar = findViewById(R.id.progressBar);

        loginButton.setOnClickListener(v -> loginUser());

        signUpButton.setOnClickListener(v -> {
            startActivity(new Intent(SignInActivity.this, SignUpActivity.class));
        });
    }

    private void loginUser() {
        String email = emailEditText.getText().toString().trim().toLowerCase();
        String password = passwordEditText.getText().toString().trim();

        // Validate email and password input
        if (TextUtils.isEmpty(email)) {
            emailEditText.setError("Email is required.");
            return;
        }
        if (TextUtils.isEmpty(password)) {
            passwordEditText.setError("Password is required.");
            return;
        }
        if (password.length() < 6) {
            passwordEditText.setError("Password must be >= 6 characters.");
            return;
        }

        progressBar.setVisibility(View.VISIBLE);

        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        // Successfully signed in, now retrieve the current user
                        FirebaseUser firebaseUser = mAuth.getCurrentUser();
                        if (firebaseUser != null) {
                            String uid = firebaseUser.getUid();
                            FirebaseFirestore db = FirebaseFirestore.getInstance();

                            // Retrieve user data from Firestore
                            db.collection(USERS_COLLECTION).document(uid).get()
                                    .addOnCompleteListener(userTask -> {
                                        progressBar.setVisibility(View.GONE);

                                        if (userTask.isSuccessful()) {
                                            DocumentSnapshot document = userTask.getResult();
                                            if (document != null && document.exists()) {
                                                UserModel userModel = document.toObject(UserModel.class);
                                                if (userModel != null) {
                                                    boolean isUserStored = userDAO.getUserByUid(userModel.getUid()) != null;
                                                    if (!isUserStored) {
                                                        userDAO.addUser(userModel);
                                                    }
                                                    Toast.makeText(SignInActivity.this, "Login successful", Toast.LENGTH_SHORT).show();
                                                    Intent intent = new Intent(SignInActivity.this, MainActivity.class);
                                                    startActivity(intent);
                                                    finish();
                                                } else {
                                                    Toast.makeText(SignInActivity.this, "User data conversion failed.", Toast.LENGTH_SHORT).show();
                                                }
                                            } else {
                                                Toast.makeText(SignInActivity.this, "User data not found in Firestore.", Toast.LENGTH_SHORT).show();
                                            }
                                        } else {
                                            Toast.makeText(SignInActivity.this, "Failed to retrieve user data.", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                        }
                    } else {
                        // If sign in fails, display a message to the user.
                        progressBar.setVisibility(View.GONE);
                        String errorMessage = task.getException() != null ? task.getException().getMessage() : "Authentication failed.";
                        Toast.makeText(SignInActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
                    }
                });
    }
}

