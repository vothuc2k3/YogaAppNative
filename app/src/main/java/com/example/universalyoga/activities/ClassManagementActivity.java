package com.example.universalyoga.activities;

import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.universalyoga.R;
import com.example.universalyoga.adapters.ClassAdapter;
import com.example.universalyoga.models.ClassModel;
import com.example.universalyoga.sqlite.DAO.ClassDAO;

import java.util.List;

public class ClassManagementActivity extends AppCompatActivity implements ClassAdapter.OnItemClickListener {

    private RecyclerView recyclerViewClasses;
    private ClassAdapter classAdapter;
    private ClassDAO classDAO;
    private List<ClassModel> classList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_class_management);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Classes Management");
        }

        toolbar.setNavigationOnClickListener(v -> finish());

        classDAO = new ClassDAO(this);

        recyclerViewClasses = findViewById(R.id.recycler_view_classes);
        recyclerViewClasses.setLayoutManager(new LinearLayoutManager(this));

        loadClassData();
    }

    private void loadClassData() {
        classList = classDAO.getAllClasses(); // Load all classes from SQLite
        if (classList != null && !classList.isEmpty()) {
            classAdapter = new ClassAdapter(classList, this, this);
            recyclerViewClasses.setAdapter(classAdapter);
        } else {
            Toast.makeText(this, "No classes found", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onItemClick(ClassModel classModel) {
        // Handle class item click (e.g., navigate to class details, edit class, etc.)
        Toast.makeText(this, "Clicked on: " + classModel.getType(), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onDeleteClick(ClassModel classModel) {
        // Handle delete class
        classDAO.softDeleteClass(classModel.getId()); // Delete class from SQLite
        classList.remove(classModel); // Remove from the list
        classAdapter.notifyDataSetChanged(); // Update RecyclerView
        Toast.makeText(this, "Class deleted", Toast.LENGTH_SHORT).show();
    }
}
