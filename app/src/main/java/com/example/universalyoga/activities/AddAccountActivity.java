package com.example.universalyoga.activities;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;

import com.example.universalyoga.R;
import com.example.universalyoga.models.UserModel;
import com.example.universalyoga.sqlite.DAO.UserDAO;
import com.example.universalyoga.utils.Util;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import org.json.JSONObject;

import java.util.Objects;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class AddAccountActivity extends AppCompatActivity {

    private TextInputEditText etEmail, etUsername, etPhoneNumber, etPassword, etConfirmPassword;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_account);
        progressBar = findViewById(R.id.progressBar);
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

        if (username.length() < 6) {
            etUsername.setError("Username must be at least 6!");
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

    private void createAccount(String email, String password, String name, String phoneNumber) {
        progressBar.setVisibility(View.VISIBLE);
        try {
            boolean hasConnection = Util.checkNetworkConnection(this);
            if (!hasConnection) {
                Toast.makeText(this, "No internet connection, the request will be saved and sent later", Toast.LENGTH_SHORT).show();
            }
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("email", email);
            jsonObject.put("password", password);
            jsonObject.put("name", name);
            jsonObject.put("phoneNumber", phoneNumber);
            jsonObject.put("role", "instructor");

            String jsonString = jsonObject.toString();
            OkHttpClient client = new OkHttpClient();
            RequestBody body = RequestBody.create(MediaType.parse("application/json"), jsonString);

            Request request = new Request.Builder()
                    .url("https://yogaappfirebasebackend-production.up.railway.app/createUser")
                    .post(body)
                    .build();

            new Thread(() -> {
                try {
                    Response response = client.newCall(request).execute();
                    if (response.isSuccessful()) {
                        runOnUiThread(() -> {
                            Toast.makeText(AddAccountActivity.this, "Account created successfully!", Toast.LENGTH_SHORT).show();
                            finish();
                        });
                    } else {
                        String responseBody = response.body().string();
                        JSONObject jsonObjectError = new JSONObject(responseBody);
                        String errorMessage = jsonObjectError.optString("message", "Failed to create account");
                        runOnUiThread(() -> Toast.makeText(AddAccountActivity.this, errorMessage, Toast.LENGTH_SHORT).show());
                    }
                } catch (Exception e) {
                    runOnUiThread(() -> Toast.makeText(AddAccountActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show());
                }
            }).start();
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Failed to create JSON data", Toast.LENGTH_SHORT).show();
        } finally {
            progressBar.setVisibility(View.INVISIBLE);
        }
    }
}
