package com.example.universalyoga.activities;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.universalyoga.R;
import com.example.universalyoga.adapters.CategoryAdapter;
import com.example.universalyoga.models.ClassCategoryModel;
import com.example.universalyoga.sqlite.DAO.CategoryDAO;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

public class CategoryManagementActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private CategoryDAO categoryDAO;
    private List<ClassCategoryModel> categoryList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category_management);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        toolbar.setNavigationOnClickListener(v->finish());
        toolbar.setTitleTextColor(getResources().getColor(R.color.white));
        toolbar.setTitle("Category Management");

        categoryDAO = new CategoryDAO(this);
        categoryList = categoryDAO.getAllCategories();

        FloatingActionButton floatingActionButton = findViewById(R.id.fab_add_category);
        recyclerView = findViewById(R.id.recycler_view_categories);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(new CategoryAdapter(categoryList, this));

        floatingActionButton.setOnClickListener(v->showDialogAddCategory());
    }

    private void showDialogAddCategory() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Add Category");

        View dialogView = getLayoutInflater().inflate(R.layout.dialog_add_category, null);
        builder.setView(dialogView);

        EditText nameInput = dialogView.findViewById(R.id.edit_text_category_name);
        EditText descriptionInput = dialogView.findViewById(R.id.edit_text_category_description);

        builder.setPositiveButton("Add", (dialog, which) -> {
            String name = nameInput.getText().toString();
            String description = descriptionInput.getText().toString();
            ClassCategoryModel category = new ClassCategoryModel(UUID.randomUUID().toString(), name, description);
            categoryDAO.addCategory(category);
            categoryList.add(category);
            Objects.requireNonNull(recyclerView.getAdapter()).notifyItemInserted(categoryList.size() - 1);
        });

        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());

        builder.show();
    }
}
