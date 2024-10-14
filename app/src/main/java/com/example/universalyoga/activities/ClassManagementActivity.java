package com.example.universalyoga.activities;

import android.content.Intent;
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
        classList = classDAO.getAllClasses();
        if (classList != null && !classList.isEmpty()) {
            // Setup adapter after data is loaded
            setupAdapter(classList);
        } else {
            Toast.makeText(this, "No classes found", Toast.LENGTH_SHORT).show();
        }
    }

    private void setupAdapter(List<ClassModel> classList) {
        classAdapter = new ClassAdapter(classList, this, this);
        recyclerViewClasses.setAdapter(classAdapter);
    }

    @Override
    public void onItemClick(ClassModel classModel) {
        Intent intent = new Intent(ClassManagementActivity.this, ClassDetailsActivity.class);
        intent.putExtra("id", classModel.getId());
        intent.putExtra("instructorUid", classModel.getInstructorUid());
        intent.putExtra("capacity", classModel.getCapacity());
        intent.putExtra("duration", classModel.getDuration());
        intent.putExtra("sessionCount", classModel.getSessionCount());
        intent.putExtra("type", classModel.getType());
        intent.putExtra("status", classModel.getStatus());
        intent.putExtra("description", classModel.getDescription());
        intent.putExtra("createdAt", classModel.getCreatedAt());
        intent.putExtra("startAt", classModel.getStartAt());
        intent.putExtra("endAt", classModel.getEndAt());
        intent.putExtra("dayOfWeek", classModel.getDayOfWeek());
        if (classModel.getTimeStart() != null) {
            intent.putExtra("timeStart", classModel.getTimeStart().toString());
        }
        startActivity(intent);
    }

    @Override
    public void onDeleteClick(ClassModel classModel) {
        new androidx.appcompat.app.AlertDialog.Builder(ClassManagementActivity.this)
                .setTitle("Delete Class")
                .setMessage("Are you sure you want to delete this class?")
                .setPositiveButton("Yes", (dialog, which) -> {
                    classDAO.softDeleteClass(classModel.getId());
                    classList = classDAO.getAllClasses();
                    classAdapter.updateData(classList);
                    Toast.makeText(ClassManagementActivity.this, "Class deleted", Toast.LENGTH_SHORT).show();
                })
                .setNegativeButton("No", null)
                .show();
    }
}
