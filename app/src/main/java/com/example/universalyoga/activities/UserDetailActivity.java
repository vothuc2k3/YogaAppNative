package com.example.universalyoga.activities;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;

import com.example.universalyoga.R;
import com.example.universalyoga.models.UserModel;
import com.example.universalyoga.utils.Util;
import com.squareup.picasso.Picasso;

public class UserDetailActivity extends AppCompatActivity {

    private ImageView profileImageView;
    private TextView nameTextView, emailTextView, phoneNumberTextView, roleTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_detail);

        profileImageView = findViewById(R.id.user_profile_image);
        nameTextView = findViewById(R.id.user_name);
        emailTextView = findViewById(R.id.user_email);
        phoneNumberTextView = findViewById(R.id.user_phone_number);
        roleTextView = findViewById(R.id.user_role);

        UserModel userModel = (UserModel) getIntent().getSerializableExtra("USER_MODEL");

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle(userModel.getName() + "'s Profile");
        }

        toolbar.setTitleTextColor(ContextCompat.getColor(this, R.color.white));

        toolbar.setNavigationOnClickListener(v -> {
            finish();
        });

        if (userModel != null) {
            nameTextView.setText("Name: " + userModel.getName());
            emailTextView.setText("Email: " + userModel.getEmail());
            phoneNumberTextView.setText("Phone number: " + userModel.getPhoneNumber());
            roleTextView.setText("Role: " + Util.capitalizeFirstLetter(userModel.getRole()));

            if (userModel.getProfileImage() != null && !userModel.getProfileImage().isEmpty()) {
                Picasso.get()
                        .load(userModel.getProfileImage())
                        .placeholder(R.drawable.ic_default_profile_image) // Use a placeholder image
                        .into(profileImageView);
            } else {
                profileImageView.setImageResource(R.drawable.ic_default_profile_image);
            }
        }
    }
}
