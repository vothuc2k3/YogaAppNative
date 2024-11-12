package com.example.universalyoga.activities;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.universalyoga.R;
import com.example.universalyoga.adapters.InstructorAdapter;
import com.example.universalyoga.models.ClassSessionModel;
import com.example.universalyoga.models.UserModel;
import com.example.universalyoga.sqlite.DAO.ClassSessionDAO;
import com.example.universalyoga.sqlite.DAO.UserDAO;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class SessionDetailsActivity extends AppCompatActivity {

    private UserModel instructor;
    private ClassSessionModel classSessionModel;
    private ClassSessionDAO classSessionDAO;
    private UserDAO userDAO;

    private ImageView ivInstructorAvatar;
    private TextView tvInstructorName;
    private EditText etSessionDate, etSessionPrice, etSessionRoom, etSessionNote;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_session_details);

        initializeToolbar();
        initializeDAOs();
        initializeViews();
        setupListeners();
        loadSessionData();
    }

    private void initializeToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Session Details");
        }

        toolbar.setNavigationOnClickListener(v -> finish());
    }

    private void initializeDAOs() {
        classSessionDAO = new ClassSessionDAO(this);
        userDAO = new UserDAO(this);
    }

    private void initializeViews() {
        ivInstructorAvatar = findViewById(R.id.iv_instructor_avatar);
        tvInstructorName = findViewById(R.id.tv_instructor_name);
        etSessionDate = findViewById(R.id.et_session_date);
        etSessionPrice = findViewById(R.id.et_session_price);
        etSessionRoom = findViewById(R.id.et_session_room);
        etSessionNote = findViewById(R.id.et_session_note);
    }

    private void setupListeners() {
        ivInstructorAvatar.setOnClickListener(v -> showInstructorSelectionDialog());
        Button btnSaveSession = findViewById(R.id.btn_save_session);
        btnSaveSession.setOnClickListener(v -> saveSessionData());
    }

    private void loadSessionData() {
        final String sessionId = getIntent().getStringExtra("classSessionId");
        classSessionModel = classSessionDAO.getClassSessionById(sessionId);
        instructor = userDAO.getUserByUid(classSessionModel.getInstructorId());

        if (classSessionModel != null) {
            updateUI();
        }
    }

    private void updateUI() {
        tvInstructorName.setText(instructor.getName());
        Picasso.get()
                .load(instructor.getProfileImage())
                .placeholder(R.drawable.ic_default_profile_image)
                .into(ivInstructorAvatar);

        List<ClassSessionModel> allSessions = classSessionDAO.getClassSessionsByClassId(classSessionModel.getClassId());
        allSessions.sort(Comparator.comparingLong(ClassSessionModel::getDate));

        etSessionDate.setText(new SimpleDateFormat("dd MMM yyyy").format(classSessionModel.getDate()));
        etSessionPrice.setText(String.valueOf(classSessionModel.getPrice()));
        etSessionRoom.setText(classSessionModel.getRoom());
        etSessionNote.setText(classSessionModel.getNote());
    }

    private void showInstructorSelectionDialog() {
        List<UserModel> instructors = userDAO.getAllInstructors();

        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_instructor_selection, null);
        RecyclerView recyclerView = dialogView.findViewById(R.id.recycler_view_instructors);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Select Instructor").setView(dialogView).setNegativeButton("Cancel", null);

        AlertDialog dialog = builder.create();

        InstructorAdapter adapter = new InstructorAdapter(this, instructors, selectedInstructor -> {
            instructor = selectedInstructor;
            classSessionModel.setInstructorId(selectedInstructor.getUid());
            tvInstructorName.setText(selectedInstructor.getName());
            Picasso.get().load(selectedInstructor.getProfileImage()).placeholder(R.drawable.ic_default_profile_image).into(ivInstructorAvatar);
            dialog.dismiss();
        });

        recyclerView.setAdapter(adapter);
        dialog.show();
    }

    private void saveSessionData() {
        classSessionModel.setPrice(Integer.parseInt(etSessionPrice.getText().toString()));
        classSessionModel.setRoom(etSessionRoom.getText().toString());
        classSessionModel.setNote(etSessionNote.getText().toString());

        long result = classSessionDAO.updateClassSession(classSessionModel);
        if (result != -1) {
            Toast.makeText(this, "Session updated successfully", Toast.LENGTH_SHORT).show();
            finish();
        } else {
            Toast.makeText(this, "Failed to update session", Toast.LENGTH_SHORT).show();
        }
    }
}
