package com.example.universalyoga.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.universalyoga.R;
import com.example.universalyoga.adapters.ClassAdapter;
import com.example.universalyoga.models.ClassModel;
import com.example.universalyoga.models.ClassSessionModel;
import com.example.universalyoga.sqlite.DAO.ClassDAO;
import com.example.universalyoga.sqlite.DAO.ClassSessionDAO;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

public class ClassManagementActivity extends AppCompatActivity implements ClassAdapter.OnItemClickListener {

    private RecyclerView recyclerViewClasses;
    private ClassAdapter classAdapter;
    private ClassDAO classDAO;
    private ClassSessionDAO classSessionDAO;
    private List<ClassModel> classList;
    private FirebaseFirestore db;

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
        classSessionDAO = new ClassSessionDAO(this);

        db = FirebaseFirestore.getInstance();

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
        if (classModel.isDeleted()) {
            new AlertDialog.Builder(ClassManagementActivity.this)
                    .setTitle("Hard delete")
                    .setMessage("Are you sure you want to (HARD) delete this class?")
                    .setPositiveButton("Yes", (dialog, which) -> {
                        List<ClassSessionModel> sessions = classSessionDAO.getClassSessionsByClassId(classModel.getId());
                        if (!sessions.isEmpty()) {
                            new AlertDialog.Builder(ClassManagementActivity.this)
                                    .setTitle("Class has sessions")
                                    .setMessage("This class has sessions. Do you want to hard delete the class and all its sessions?")
                                    .setPositiveButton("Yes", (dialog2, which2) -> {
                                        classDAO.hardDelete(classModel.getId());
                                        classSessionDAO.deleteSessionsByClassId(classModel.getId());
                                        db.collection("classes")
                                                .document(classModel.getId())
                                                .delete()
                                                .addOnSuccessListener(aVoid -> Toast.makeText(ClassManagementActivity.this, "Class and its sessions deleted from Firestore", Toast.LENGTH_SHORT).show())
                                                .addOnFailureListener(e -> Toast.makeText(ClassManagementActivity.this, "Failed to delete class from Firestore", Toast.LENGTH_SHORT).show());
                                        classList = classDAO.getAllClasses();
                                        classAdapter.updateData(classList);
                                        Toast.makeText(ClassManagementActivity.this, "Class and its sessions deleted", Toast.LENGTH_SHORT).show();
                                    })
                                    .setNegativeButton("No", null)
                                    .show();
                        } else {
                            classDAO.hardDelete(classModel.getId());

                            db.collection("classes")
                                    .document(classModel.getId())
                                    .delete()
                                    .addOnSuccessListener(aVoid -> Toast.makeText(ClassManagementActivity.this, "Class deleted from Firestore", Toast.LENGTH_SHORT).show())
                                    .addOnFailureListener(e -> Toast.makeText(ClassManagementActivity.this, "Failed to delete class from Firestore", Toast.LENGTH_SHORT).show());
                            classList = classDAO.getAllClasses();
                            classAdapter.updateData(classList);
                            Toast.makeText(ClassManagementActivity.this, "Class deleted", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .setNegativeButton("No", null)
                    .show();
        } else {
            new AlertDialog.Builder(ClassManagementActivity.this)
                    .setTitle("Delete Class")
                    .setMessage("Are you sure you want to (soft) delete this class?")
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
}
