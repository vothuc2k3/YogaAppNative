package com.example.universalyoga.activities;

import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.GravityCompat;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.bumptech.glide.Glide;
import com.example.universalyoga.R;
import com.example.universalyoga.models.UserModel;
import com.example.universalyoga.sqlite.DAO.UserDAO;
import com.google.android.material.navigation.NavigationView;

public class MainActivity extends AppCompatActivity {

    private DrawerLayout drawerLayout;
    private NavigationView endDrawer;
    private Button openDrawerButton;
    private ImageView profileImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize the views
        drawerLayout = findViewById(R.id.drawer_layout);
        endDrawer = findViewById(R.id.end_drawer);
        openDrawerButton = findViewById(R.id.open_drawer_button);
        profileImage = findViewById(R.id.profile_image);

        // Fetch the current user
        UserDAO userDAO = new UserDAO(this);
        UserModel currentUser = userDAO.getCurrentUser();

        if (currentUser != null) {
            String profileImageUrl = currentUser.getProfileImage();

            if (profileImageUrl != null && !profileImageUrl.isEmpty()) {
                Glide.with(this)
                        .load("https://firebasestorage.googleapis.com/v0/b/yoga-application-63a57.appspot.com/o/defaultAvatar.png?alt=media&token=8199f3df-0c74-418d-b65d-c5c2e306a1fa")
                        .placeholder(R.drawable.ic_default_profile_image) // Set placeholder if loading fails
                        .into(profileImage);
            } else {
                Glide.with(this)
                        .load(R.drawable.ic_default_profile_image)
                        .into(profileImage);
            }
        }

        // Edge to Edge display
        EdgeToEdge.enable(this);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Set up the button to open the drawer
        openDrawerButton.setOnClickListener(v -> drawerLayout.openDrawer(GravityCompat.END));
    }
}
