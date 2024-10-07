package com.example.universalyoga.activities;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;

import com.example.universalyoga.sqlite.DAO.UserDAO;
import com.example.universalyoga.models.UserModel;

public class SplashActivity extends AppCompatActivity {

    private UserDAO userDAO;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        userDAO = new UserDAO(this);

        UserModel currentUser = userDAO.getCurrentUser();
        Intent intent;
        if (currentUser != null) {
            intent = new Intent(SplashActivity.this, MainActivity.class);
        } else {
            intent = new Intent(SplashActivity.this, SignInActivity.class);
        }
        startActivity(intent);
        finish();
    }
}
