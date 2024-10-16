package com.example.universalyoga.activities;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

public abstract class BaseActivity extends AppCompatActivity {

    private String role;
    private FirebaseFirestore db; 

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        db = FirebaseFirestore.getInstance();

        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        if (firebaseUser != null) {
            getUserRoleFromFirestore(firebaseUser.getUid());
        } else {
            redirectToSignIn();
        }
    }

    private void getUserRoleFromFirestore(String uid) {
        DocumentReference userRef = db.collection("users").document(uid);

        userRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful() && task.getResult().exists()) {
                role = task.getResult().getString("role");

                checkUserRole();
            } else {
                redirectToSignIn();
            }
        });
    }

    public String getUserRole() {
        return role;
    }
    protected abstract void onRoleReceived(String role);

    private void checkUserRole() {
        if (role != null) {
            onRoleReceived(role);
        } else {
            handleMissingRole();
        }
    }

    private void redirectToSignIn() {
        Intent intent = new Intent(BaseActivity.this, SignInActivity.class);
        startActivity(intent);
        finish();
    }

    protected void handleMissingRole() {
    }
}
