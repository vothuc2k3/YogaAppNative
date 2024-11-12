package com.example.universalyoga.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;

import com.example.universalyoga.R;
import com.example.universalyoga.fragments.HomeFragment;
import com.example.universalyoga.fragments.HomeInstructorFragment;
import com.example.universalyoga.fragments.ProfileFragment;
import com.example.universalyoga.fragments.SearchFragment;
import com.example.universalyoga.models.UserModel;
import com.example.universalyoga.sqlite.AppDatabaseHelper;
import com.example.universalyoga.sqlite.DAO.*;
import com.example.universalyoga.worker.SyncManager;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.squareup.picasso.Picasso;

public class MainActivity extends AppCompatActivity {

    private DrawerLayout drawerLayout;
    private UserModel currentUser;
    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initUser();
        SyncManager.startSyncing(this);
        setupFragments(savedInstanceState);
        setupNavigation();
        setupToolbar();
        setupDrawer();
    }

    private void initUser() {
        UserDAO userDAO = new UserDAO(this);
        FirebaseUser fbUser = FirebaseAuth.getInstance().getCurrentUser();
        if (fbUser != null) currentUser = userDAO.getUserByUid(fbUser.getUid());
    }

    private void setupFragments(Bundle savedInstanceState) {
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container,
                            currentUser.getRole().equals("admin") ? new HomeFragment() : new HomeInstructorFragment())
                    .commit();
        }
    }

    private void setupNavigation() {
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.nav_home) {
                loadFragment(currentUser.getRole().equals("admin") ? new HomeFragment() : new HomeInstructorFragment(), "Home");
            } else if (itemId == R.id.nav_search) {
                loadFragment(new SearchFragment(), "Search");
            } else if (itemId == R.id.nav_profile) {
                AppDatabaseHelper db = new AppDatabaseHelper(this);
                db.getWritableDatabase();
                ProfileFragment profileFragment = new ProfileFragment();
                Bundle bundle = new Bundle();
                bundle.putSerializable("user", currentUser);
                profileFragment.setArguments(bundle);
                loadFragment(profileFragment, "Profile");
            }
            return true;
        });
    }

    private void loadFragment(Fragment fragment, String title) {
        getSupportFragmentManager().beginTransaction()
                .setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out)
                .replace(R.id.fragment_container, fragment)
                .commit();
        toolbar.setTitle(title);
    }

    private void setupToolbar() {
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setBackgroundColor(getResources().getColor(R.color.navBarColor));

        NavigationView navigationView = findViewById(R.id.end_drawer);
        View headerView = navigationView.getHeaderView(0);
        setupDrawerHeader(headerView);

        drawerLayout = findViewById(R.id.drawer_layout);
    }

    private void setupDrawer() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.open_drawer, R.string.close_drawer);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        toolbar.setNavigationOnClickListener(v -> {
            if (drawerLayout.isDrawerOpen(GravityCompat.END)) {
                drawerLayout.closeDrawer(GravityCompat.END);
            } else {
                drawerLayout.openDrawer(GravityCompat.END);
            }
        });

        NavigationView navigationView = findViewById(R.id.end_drawer);
        navigationView.setNavigationItemSelectedListener(item -> {
            if (item.getItemId() == R.id.nav_logout) {
                handleLogout();
            } else if (item.getItemId() == R.id.nav_reset_database) {
                showDialogResetDatabase();
            }
            drawerLayout.closeDrawer(GravityCompat.END);
            return true;
        });
    }

    private void setupDrawerHeader(View headerView) {
        ImageView profileImage = headerView.findViewById(R.id.profile_image);
        TextView nameTextView = headerView.findViewById(R.id.profile_name);
        TextView emailTextView = headerView.findViewById(R.id.profile_email);

        nameTextView.setText(currentUser.getName());
        emailTextView.setText(currentUser.getEmail());
        Picasso.get()
                .load(currentUser.getProfileImage())
                .placeholder(R.drawable.ic_default_profile_image)
                .into(profileImage);
    }

    private void showDialogResetDatabase() {
        if (!"admin".equals(currentUser.getRole())) {
            Toast.makeText(this, "You are not authorized to reset the database", Toast.LENGTH_SHORT).show();
            return;
        }
        new AlertDialog.Builder(this)
                .setMessage("Are you sure you want to reset the database?")
                .setPositiveButton("Yes", (dialog, which) -> confirmResetDatabase())
                .setNegativeButton("No", null)
                .create().show();
    }

    private void confirmResetDatabase() {
        clearLocalDatabase();
        clearFirestoreDatabase();
        Toast.makeText(this, "Database reset successfully", Toast.LENGTH_SHORT).show();
    }

    private void clearLocalDatabase() {
        new ClassSessionDAO(this).resetTable();
        new ClassDAO(this).resetTable();
        new BookingSessionDAO(this).resetTable();
        new BookingDAO(this).resetTable();
        new CategoryDAO(this).resetTable();
    }

    private void clearFirestoreDatabase() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        String[] collections = {"class_sessions", "classes", "bookings", "categories", "carts"};
        for (String collection : collections) {
            db.collection(collection).get().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        db.collection(collection).document(document.getId()).delete();
                    }
                }
            });
        }
    }

    private void handleLogout() {
        FirebaseAuth.getInstance().signOut();
        Toast.makeText(this, "Logged out successfully", Toast.LENGTH_SHORT).show();
        startActivity(new Intent(MainActivity.this, SignInActivity.class)
                .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK));
        finish();
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.END)) {
            drawerLayout.closeDrawer(GravityCompat.END);
        } else {
            super.onBackPressed();
        }
    }
}
