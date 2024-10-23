package com.example.universalyoga.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.example.universalyoga.R;
import com.example.universalyoga.fragments.HomeFragment;
import com.example.universalyoga.fragments.HomeInstructorFragment;
import com.example.universalyoga.fragments.ProfileFragment;
import com.example.universalyoga.models.UserModel;
import com.example.universalyoga.sqlite.AppDatabaseHelper;
import com.example.universalyoga.sqlite.DAO.UserDAO;
import com.example.universalyoga.worker.SyncManager;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;

import com.example.universalyoga.fragments.SearchFragment;
import com.google.firebase.auth.FirebaseUser;
import com.squareup.picasso.Picasso;

public class MainActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private ActionBarDrawerToggle toggle;
    private UserDAO userDAO;
    private UserModel currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        userDAO = new UserDAO(this);
        FirebaseUser fbUser = FirebaseAuth.getInstance().getCurrentUser();
        currentUser = userDAO.getUserByUid(fbUser.getUid());

        if (fbUser != null) {
            SyncManager.startSyncing(this);
        }

        if (savedInstanceState == null && currentUser.getRole().equals("admin")) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, new HomeFragment())
                    .commit();
        } else if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, new HomeInstructorFragment())
                    .commit();
        }

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);

        toolbar = findViewById(R.id.toolbar);

        bottomNavigationView.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.nav_home) {
                if (currentUser.getRole().equals("admin")) {
                    getSupportFragmentManager().beginTransaction()
                            .setCustomAnimations(android.R.anim.slide_in_left, android.R.anim.slide_out_right)
                            .replace(R.id.fragment_container, new HomeFragment())
                            .commit();
                    toolbar.setTitle("Home");
                } else {
                    getSupportFragmentManager().beginTransaction()
                            .setCustomAnimations(android.R.anim.slide_in_left, android.R.anim.slide_out_right)
                            .replace(R.id.fragment_container, new HomeInstructorFragment())
                            .commit();
                    toolbar.setTitle("Home");
                }
            } else if (itemId == R.id.nav_search) {
                getSupportFragmentManager().beginTransaction()
                        .setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out)
                        .replace(R.id.fragment_container, new SearchFragment())
                        .commit();
                toolbar.setTitle("Search");
            } else if (itemId == R.id.nav_profile) {
                AppDatabaseHelper db = new AppDatabaseHelper(this);
                db.getWritableDatabase();
                Bundle bundle = new Bundle();
                bundle.putSerializable("user", currentUser);
                ProfileFragment profileFragment = new ProfileFragment();
                profileFragment.setArguments(bundle);
                getSupportFragmentManager().beginTransaction()
                        .setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out)
                        .replace(R.id.fragment_container, profileFragment)
                        .commit();
                toolbar.setTitle("Profile");
            }
            return true;
        });


        navigationView = findViewById(R.id.end_drawer);
        View headerView = navigationView.getHeaderView(0);
        ImageView profileImage = headerView.findViewById(R.id.profile_image);
        TextView nameTextView = headerView.findViewById(R.id.profile_name);
        TextView emailTextView = headerView.findViewById(R.id.profile_email);

        nameTextView.setText(currentUser.getName());
        emailTextView.setText(currentUser.getEmail());
        Picasso.get()
                .load(currentUser.getProfileImage())
                .placeholder(R.drawable.ic_default_profile_image)
                .into(profileImage);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        drawerLayout = findViewById(R.id.drawer_layout);

        navigationView.setNavigationItemSelectedListener(item -> {
            if (item.getItemId() == R.id.nav_logout) {
                handleLogout();
            }
            drawerLayout.closeDrawer(GravityCompat.END);
            return true;
        });

        toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.open_drawer, R.string.close_drawer) {
            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
            }

            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
            }

            @Override
            public void onDrawerSlide(View drawerView, float slideOffset) {
                super.onDrawerSlide(drawerView, slideOffset);
            }
        };

        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        toolbar.setNavigationOnClickListener(v -> {
            if (drawerLayout.isDrawerOpen(GravityCompat.END)) {
                drawerLayout.closeDrawer(GravityCompat.END);
            } else {
                drawerLayout.openDrawer(GravityCompat.END);
            }
        });
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.END)) {
            drawerLayout.closeDrawer(GravityCompat.END);
        } else {
            super.onBackPressed();
        }
    }

    private void handleLogout() {
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        mAuth.signOut();
        Toast.makeText(this, "Logged out successfully", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(MainActivity.this, SignInActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}
