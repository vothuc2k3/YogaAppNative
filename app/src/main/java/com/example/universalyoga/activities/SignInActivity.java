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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sign_in_activity);

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();

        //Initialize UserDAO
        userDAO = new UserDAO(this);

        // Initialize UI components
        emailEditText = findViewById(R.id.email);
        passwordEditText = findViewById(R.id.password);
        loginButton = findViewById(R.id.login_button);
        signUpButton = findViewById(R.id.sign_up_button);
        progressBar = findViewById(R.id.progressBar);

        // Set click listener for login button
        loginButton.setOnClickListener(v -> loginUser());

        signUpButton.setOnClickListener(v -> {
            startActivity(new Intent(SignInActivity.this, SignUpActivity.class));
        });
    }

    private void loginUser() {
        String email = emailEditText.getText().toString().trim();
        String password = passwordEditText.getText().toString().trim();

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
                        FirebaseUser firebaseUser = mAuth.getCurrentUser();
                        if(firebaseUser != null){
                            String uid  = firebaseUser.getUid();
                            FirebaseFirestore db = FirebaseFirestore.getInstance();
                            db.collection("users").document(uid).get().addOnCompleteListener(userTask -> {
                                if(userTask.isSuccessful()){
                                    DocumentSnapshot document = userTask.getResult();
                                    UserModel userModel = document.toObject(UserModel.class);
                                    if(userModel != null){
                                        userDAO.addUser(userModel);
                                        SyncManager.startSync(this);
                                        Toast.makeText(SignInActivity.this, "Login successful", Toast.LENGTH_SHORT).show();
                                        Intent intent = new Intent(SignInActivity.this, MainActivity.class);
                                        startActivity(intent);
                                        finish();
                                    }
                                }
                            });
                        }
                    } else {
                        Toast.makeText(SignInActivity.this, "Authentication failed.", Toast.LENGTH_SHORT).show();
                    }
                    progressBar.setVisibility(View.GONE);
                });
    }

//    private UserModel getUserDataFirestore(String uid) {
//        FirebaseFirestore db = FirebaseFirestore.getInstance();
//        TaskCompletionSource<UserModel> taskCompletionSource = new TaskCompletionSource<>();
//        db.collection("users").document(uid).get()
//                .addOnSuccessListener(documentSnapshot -> {
//                    if (documentSnapshot.exists()) {
//                        String name = documentSnapshot.getString("name");
//                        String email = documentSnapshot.getString("email");
//                        String phoneNumber = documentSnapshot.getString("phoneNumber");
//                        String profileImage = documentSnapshot.getString("profileImage");
//                        String role = documentSnapshot.getString("role");
//
//                        UserModel userModel = new UserModel();
//                        userModel.setUid(uid);
//                        userModel.setName(name);
//                        userModel.setEmail(email);
//                        userModel.setPhoneNumber(phoneNumber);
//                        userModel.setProfileImage(profileImage);
//                        userModel.setRole(role);
//
//                        taskCompletionSource.setResult(userModel);
//                    } else {
//                        taskCompletionSource.setException(new Exception("User data not found"));
//                    }
//                });
//
//        try {
//            return Tasks.await(taskCompletionSource.getTask());
//        } catch (ExecutionException | InterruptedException e) {
//            e.printStackTrace();
//            return null;
//        }
//    }

}

