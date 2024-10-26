package com.example.universalyoga.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.SearchView;

import com.example.universalyoga.R;
import com.example.universalyoga.adapters.UserAdapter;
import com.example.universalyoga.models.UserModel;
import com.example.universalyoga.sqlite.DAO.UserDAO;

import java.util.List;

public class UserManagementActivity extends AppCompatActivity {

    private UserAdapter userAdapter;
    private UserDAO userDAO;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_management);

        SearchView searchView = findViewById(R.id.search_view_users);
        RecyclerView recyclerView = findViewById(R.id.rv_user_list);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("User Management");
        }

        toolbar.setTitleTextColor(ContextCompat.getColor(this, R.color.white));

        toolbar.setNavigationOnClickListener(v -> finish());

        searchView.setIconified(false);
        searchView.requestFocus();

        userDAO = new UserDAO(this);

        List<UserModel> userList = userDAO.getAllUsers();


        userAdapter = new UserAdapter(userList, userModel -> {
            Intent intent = new Intent(UserManagementActivity.this, UserDetailActivity.class);
            intent.putExtra("USER_MODEL", userModel);
            startActivity(intent);
        });
        recyclerView.setAdapter(userAdapter);

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                performSearch(query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                performSearch(newText);
                return true;
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.user_management_toolbar_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_add_account) {
            Intent intent = new Intent(UserManagementActivity.this, AddAccountActivity.class);
            startActivity(intent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void performSearch(String query) {
        List<UserModel> filteredUsers = userDAO.searchUsersByName(query);
        userAdapter.updateData(filteredUsers);
    }
}
