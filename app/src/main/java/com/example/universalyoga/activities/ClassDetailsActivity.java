package com.example.universalyoga.activities;

import static com.example.universalyoga.utils.Util.convertToTime;

import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.universalyoga.R;
import com.example.universalyoga.adapters.EditClassSessionAdapter;
import com.example.universalyoga.models.ClassCategoryModel;
import com.example.universalyoga.models.ClassModel;
import com.example.universalyoga.models.ClassSessionModel;
import com.example.universalyoga.models.UserModel;
import com.example.universalyoga.sqlite.DAO.CategoryDAO;
import com.example.universalyoga.sqlite.DAO.ClassDAO;
import com.example.universalyoga.sqlite.DAO.ClassSessionDAO;
import com.example.universalyoga.sqlite.DAO.UserDAO;
import com.example.universalyoga.utils.Util;
import com.google.android.material.textfield.TextInputEditText;

import java.sql.Time;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

public class ClassDetailsActivity extends AppCompatActivity {

    private TextInputEditText etCapacity, etDuration, etSessionNumber, etType, etDescription, etStartAt, etEndAt, etDayOfWeek, etTimeStart;
    private Button btnSaveChanges;
    private RecyclerView recyclerViewClassSessions;
    private EditClassSessionAdapter editClassSessionAdapter;
    private ClassSessionDAO classSessionDAO;
    private ClassDAO classDAO;
    private UserDAO userDAO;
    private CategoryDAO categoryDAO;
    private String classId;
    private int dayOfWeek;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_class_details);

        initializeDAOs();
        initializeViews();
        setupToolbar();
        setupListeners();
        loadClassDetails();
    }

    private void initializeDAOs() {
        userDAO = new UserDAO(this);
        classSessionDAO = new ClassSessionDAO(this);
        classDAO = new ClassDAO(this);
    }

    private void initializeViews() {
        recyclerViewClassSessions = findViewById(R.id.recycler_view_sessions);
        recyclerViewClassSessions.setLayoutManager(new LinearLayoutManager(this));
        etCapacity = findViewById(R.id.et_capacity);
        etDuration = findViewById(R.id.et_duration);
        etType = findViewById(R.id.et_type);
        etDescription = findViewById(R.id.et_description);
        etStartAt = findViewById(R.id.et_start_at);
        etEndAt = findViewById(R.id.et_end_at);
        etDayOfWeek = findViewById(R.id.et_day_of_week);
        etTimeStart = findViewById(R.id.et_time_start);
        etSessionNumber = findViewById(R.id.et_number_of_sessions);
        btnSaveChanges = findViewById(R.id.btn_save_changes);

        classId = getIntent().getStringExtra("id");
    }

    private void setupToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Class Details");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        toolbar.setNavigationOnClickListener(v -> finish());
    }

    private void setupListeners() {
        etTimeStart.setOnClickListener(v -> showTimePickerDialog());
        btnSaveChanges.setOnClickListener(v -> saveChanges());
    }

    private void loadClassDetails() {
        ClassModel classModel = classDAO.getClassById(classId);
        categoryDAO = new CategoryDAO(this);
        ClassCategoryModel category = categoryDAO.getCategoryById(classModel.getTypeId());

        etCapacity.setText(String.valueOf(classModel.getCapacity()));
        etDuration.setText(String.valueOf(classModel.getDuration()));
        etType.setText(category.getName());
        etDescription.setText(classModel.getDescription());
        etSessionNumber.setText(String.valueOf(classModel.getSessionCount()));

        if (classModel.getStartAt() > 0) {
            etStartAt.setText(formatDate(new Date(classModel.getStartAt())));
        }

        if (classModel.getEndAt() > 0) {
            etEndAt.setText(formatDate(new Date(classModel.getEndAt())));
        }

        etDayOfWeek.setText(classModel.getDayOfWeek());
        etTimeStart.setText(classModel.getTimeStart().toString());

        dayOfWeek = Util.getDayOfWeekFromString(classModel.getDayOfWeek());

        loadClassSessions(classModel.getId());
    }

    private void loadClassSessions(String classId) {
        List<ClassSessionModel> classSessions = classSessionDAO.getClassSessionsByClassId(classId);
        editClassSessionAdapter = new EditClassSessionAdapter(classSessionDAO, classSessions, this, dayOfWeek, userDAO, updatedSession -> {
            classSessionDAO.updateClassSession(updatedSession);
        });
        recyclerViewClassSessions.setAdapter(editClassSessionAdapter);
    }

    private void saveChanges() {
        try {
            if(!validateInput()){
                return;
            }

            ClassModel classModel = classDAO.getClassById(classId);

            int capacity = Integer.parseInt(etCapacity.getText().toString().trim());
            int duration = Integer.parseInt(etDuration.getText().toString().trim());
            String description = etDescription.getText().toString().trim();

            String timeStartStr = etTimeStart.getText().toString().trim();
            Time timeStart = convertToTime(timeStartStr);

            classModel.setCapacity(capacity);
            classModel.setDuration(duration);
            classModel.setDescription(description);
            classModel.setTimeStart(timeStart);

            classDAO.updateClass(classModel);


            Toast.makeText(this, "Class details saved successfully", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private String formatDate(Date date) {
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());
        return sdf.format(date);
    }

    private void showTimePickerDialog() {
        Calendar calendar = Calendar.getInstance();
        int currentHour = calendar.get(Calendar.HOUR_OF_DAY);
        int currentMinute = calendar.get(Calendar.MINUTE);

        TimePickerDialog timePickerDialog = new TimePickerDialog(this, (view, hourOfDay, minute) -> {
            if (hourOfDay < 6 || hourOfDay > 20) {
                Toast.makeText(this, "Please select a time between 6:00 AM and 8:00 PM", Toast.LENGTH_SHORT).show();
            } else {
                etTimeStart.setText(String.format(Locale.getDefault(), "%02d:%02d", hourOfDay, minute));
            }
        }, Math.max(6, Math.min(20, currentHour)), currentMinute, true);

        timePickerDialog.show();
    }

    private boolean validateInput(){
        if(TextUtils.isEmpty(Objects.requireNonNull(etCapacity.getText()).toString().trim())){
            etCapacity.setError("Capacity is required");
            Toast.makeText(this, "Capacity is required", Toast.LENGTH_SHORT).show();
            return false;
        }
        if(TextUtils.isEmpty(Objects.requireNonNull(etDuration.getText()).toString().trim())){
            etDuration.setError("Duration is required");
            Toast.makeText(this, "Duration is required", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }
}
