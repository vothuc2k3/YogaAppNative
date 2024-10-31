package com.example.universalyoga.activities;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;

import com.example.universalyoga.R;
import com.example.universalyoga.models.UserModel;
import com.example.universalyoga.sqlite.DAO.UserDAO;
import com.example.universalyoga.utils.Util;
import com.squareup.picasso.Picasso;

public class UserDetailActivity extends AppCompatActivity {

    private ImageView profileImageView;
    private EditText nameEditText, phoneNumberEditText;
    private EditText emailEditText, roleEditText;
    private Button btnUpdate;
    private UserDAO userDAO;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_details);

        profileImageView = findViewById(R.id.user_profile_image);
        nameEditText = findViewById(R.id.user_name);
        emailEditText = findViewById(R.id.user_email);
        phoneNumberEditText = findViewById(R.id.user_phone_number);
        roleEditText = findViewById(R.id.user_role);
        btnUpdate = findViewById(R.id.btn_update_user);

        UserModel userModel = (UserModel) getIntent().getSerializableExtra("USER_MODEL");

        userDAO = new UserDAO(this);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle(userModel.getName() + "'s Profile");
        }
        toolbar.setTitleTextColor(ContextCompat.getColor(this, R.color.white));
        toolbar.setNavigationOnClickListener(v -> finish());

        if (userModel != null) {
            nameEditText.setText(userModel.getName());
            emailEditText.setText(userModel.getEmail());
            phoneNumberEditText.setText(userModel.getPhoneNumber());
            roleEditText.setText(Util.capitalizeFirstLetter(userModel.getRole()));

            emailEditText.setEnabled(false);
            roleEditText.setEnabled(false);

            if (userModel.getProfileImage() != null && !userModel.getProfileImage().isEmpty()) {
                Picasso.get()
                        .load(userModel.getProfileImage())
                        .placeholder(R.drawable.ic_default_profile_image)
                        .into(profileImageView);
            } else {
                profileImageView.setImageResource(R.drawable.ic_default_profile_image);
            }
        }

        btnUpdate.setOnClickListener(v -> {
            String newName = nameEditText.getText().toString().trim();
            String newPhoneNumber = phoneNumberEditText.getText().toString().trim();

            if (TextUtils.isEmpty(newName)) {
                nameEditText.setError("Name is required");
                return;
            }

            if (TextUtils.isEmpty(newPhoneNumber)) {
                phoneNumberEditText.setError("Phone number is required");
                return;
            }

            if (!newPhoneNumber.matches("^0\\d{9}$")) {
                phoneNumberEditText.setError("Phone number must be in the format 0xxxxxxxxx");
                return;
            }

            userModel.setName(newName);
            userModel.setPhoneNumber(newPhoneNumber);
            userDAO.updateUser(userModel);
            Toast.makeText(UserDetailActivity.this, "User updated successfully!", Toast.LENGTH_SHORT).show();
            Intent resultIntent = new Intent();
            resultIntent.putExtra("UPDATED_USER", userModel);
            setResult(RESULT_OK, resultIntent);
            finish();

        });
    }
}
