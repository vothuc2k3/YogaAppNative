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

        initializeToolbar();
        initializeDAOs();
        initializeFirestore();
        initializeRecyclerView();
        loadClassData();
    }

    private void initializeToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Classes Management");
        }
        toolbar.setNavigationOnClickListener(v -> finish());
    }

    private void initializeDAOs() {
        classDAO = new ClassDAO(this);
        classSessionDAO = new ClassSessionDAO(this);
    }

    private void initializeFirestore() {
        db = FirebaseFirestore.getInstance();
    }

    private void initializeRecyclerView() {
        recyclerViewClasses = findViewById(R.id.recycler_view_classes);
        recyclerViewClasses.setLayoutManager(new LinearLayoutManager(this));
    }

    private void setupAdapter(List<ClassModel> classList) {
        classAdapter = new ClassAdapter(classList, this, this);
        recyclerViewClasses.setAdapter(classAdapter);
    }

    private void loadClassData() {
        classList = classDAO.getAllClasses();
        if (classList != null && !classList.isEmpty()) {
            setupAdapter(classList);
        } else {
            Toast.makeText(this, "No classes found", Toast.LENGTH_SHORT).show();
        }
    }


    @Override
    public void onItemClick(ClassModel classModel) {
        if (classModel.isDeleted()) {
            Toast.makeText(ClassManagementActivity.this, "This class is deleted...", Toast.LENGTH_SHORT).show();
        } else {
            Intent intent = new Intent(ClassManagementActivity.this, ClassDetailsActivity.class);
            intent.putExtra("id", classModel.getId());
            startActivity(intent);
        }

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
                                        deleteLocalClass(classModel);
                                        deleteFirestoreClass(classModel);
                                    })
                                    .setNegativeButton("No", null)
                                    .show();
                        }else {
                            deleteLocalClass(classModel);
                            deleteFirestoreClass(classModel);
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
                        classAdapter.updateItem(classModel);
                        Toast.makeText(ClassManagementActivity.this, "Class deleted", Toast.LENGTH_SHORT).show();
                    })
                    .setNegativeButton("No", null)
                    .show();
        }
    }

    @Override
    public void onRestoreClick(ClassModel classModel) {
        new AlertDialog.Builder(ClassManagementActivity.this)
                .setTitle("Restore class")
                .setMessage("Are you sure you want to restore this class?")
                .setPositiveButton("Yes", (dialog, which) -> {
                    classModel.setDeleted(false);
                    classDAO.updateClass(classModel);
                    classAdapter.updateItem(classModel);
                })
                .setNegativeButton("No", null)
                .show();
    }

    private void deleteLocalClass(ClassModel classModel) {
        classDAO.hardDelete(classModel.getId());
        classSessionDAO.deleteSessionsByClassId(classModel.getId());
        classList = classDAO.getAllClasses();
        classAdapter.removeItem(classModel);
        Toast.makeText(ClassManagementActivity.this, "Class and its sessions deleted", Toast.LENGTH_SHORT).show();
    }

    private void deleteFirestoreClass(ClassModel classModel) {
        db.collection("classes")
                .document(classModel.getId())
                .delete()
                .addOnSuccessListener(aVoid -> Toast.makeText(ClassManagementActivity.this, "Class and its sessions deleted from Firestore", Toast.LENGTH_SHORT).show())
                .addOnFailureListener(e -> Toast.makeText(ClassManagementActivity.this, "Failed to delete class from Firestore", Toast.LENGTH_SHORT).show());
    }
}
