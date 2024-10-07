package com.example.universalyoga.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import com.example.universalyoga.R;
import com.example.universalyoga.fragments.FragmentHome;
import com.example.universalyoga.models.UserModel;
import com.example.universalyoga.sqlite.DAO.UserDAO;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;


public class MainActivity extends AppCompatActivity {

    private DrawerLayout drawerLayout;
    private NavigationView endDrawer;
    private ActionBarDrawerToggle toggle;
    private UserDAO userDAO;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, new FragmentHome())
                    .commit();
        }

        userDAO = new UserDAO(this);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        drawerLayout = findViewById(R.id.drawer_layout);
        endDrawer = findViewById(R.id.end_drawer);

        endDrawer.setNavigationItemSelectedListener(item ->{
            if(item.getItemId() == R.id.nav_logout){
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
                drawerLayout.closeDrawer(GravityCompat.END); // Close end drawer if open
            } else {
                drawerLayout.openDrawer(GravityCompat.END); // Open end drawer
            }
        });
    }

    @Override
    public void onBackPressed() {
        // Close drawer if it's open when the back button is pressed
        if (drawerLayout.isDrawerOpen(GravityCompat.END)) {
            drawerLayout.closeDrawer(GravityCompat.END); // Close end drawer if open
        } else {
            super.onBackPressed(); // Otherwise, perform default back behavior
        }
    }

    private void handleLogout(){
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        mAuth.signOut();
        UserModel currentUser = userDAO.getCurrentUser();
        if(currentUser != null){
            userDAO.deleteUser(currentUser.getUid());
        }
        Toast.makeText(this, "Logged out successfully", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(MainActivity.this, SignInActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}
