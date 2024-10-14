package com.example.universalyoga.activities;

import static com.example.universalyoga.utils.Util.convertToTime;

import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
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
import com.example.universalyoga.models.ClassModel;
import com.example.universalyoga.models.ClassSessionModel;
import com.example.universalyoga.models.UserModel;
import com.example.universalyoga.sqlite.DAO.ClassDAO;
import com.example.universalyoga.sqlite.DAO.ClassSessionDAO;
import com.example.universalyoga.sqlite.DAO.UserDAO;
import com.google.android.material.textfield.TextInputEditText;

import java.sql.Time;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ClassDetailsActivity extends AppCompatActivity {

    private Spinner spinnerInstructor;
    private TextInputEditText etCapacity, etDuration, etSessionNumber, etType, etDescription, etStartAt, etEndAt, etDayOfWeek, etTimeStart;
    private Button btnSaveChanges;
    private RecyclerView recyclerViewClassSessions;
    private EditClassSessionAdapter editClassSessionAdapter;
    private List<UserModel> instructorList;
    private ArrayAdapter<String> instructorAdapter;
    private ClassSessionDAO classSessionDAO;
    private ClassDAO classDAO;
    private UserDAO userDAO;

    private String classId;
    private String selectedInstructorUid;
    private int sessionCount;
    private long classStartDate;
    private int dayOfWeek; // Thứ trong tuần của lớp học

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_class_details);

        // Initialize DAOs
        userDAO = new UserDAO(this);
        classSessionDAO = new ClassSessionDAO(this);
        classDAO = new ClassDAO(this);

        // Initialize RecyclerView for class sessions
        recyclerViewClassSessions = findViewById(R.id.recycler_view_sessions);
        recyclerViewClassSessions.setLayoutManager(new LinearLayoutManager(this));

        // Initialize inputs
        spinnerInstructor = findViewById(R.id.spinner_instructor);
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

        // Get data from Intent
        classId = getIntent().getStringExtra("id");
        selectedInstructorUid = getIntent().getStringExtra("instructorUid");
        sessionCount = getIntent().getIntExtra("sessionCount", 0);

        // Load instructors for the spinner
        loadInstructors();

        // Load class details
        loadClassDetails();

        // Toolbar setup
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Class Details");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        toolbar.setNavigationOnClickListener(v -> finish());

        // TimePicker for timeStart
        etTimeStart.setOnClickListener(v -> showTimePickerDialog());

        btnSaveChanges.setOnClickListener(v -> saveChanges());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.class_details_toolbar_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_add_session) {
            navigateToAddSession();
            return true;
        } else if (id == R.id.btn_save_changes) {
            saveChanges();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void loadInstructors() {
        instructorList = userDAO.getAllInstructors();
        List<String> instructorNames = new ArrayList<>();

        int selectedPosition = 0;
        for (int i = 0; i < instructorList.size(); i++) {
            UserModel instructor = instructorList.get(i);
            instructorNames.add(instructor.getName());

            if (instructor.getUid().equals(selectedInstructorUid)) {
                selectedPosition = i;
            }
        }

        instructorAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, instructorNames);
        instructorAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerInstructor.setAdapter(instructorAdapter);
        spinnerInstructor.setSelection(selectedPosition);

        spinnerInstructor.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedInstructorUid = instructorList.get(position).getUid();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }

    private void loadClassDetails() {
        ClassModel classModel = classDAO.getClassById(classId);

        etCapacity.setText(String.valueOf(classModel.getCapacity()));
        etDuration.setText(String.valueOf(classModel.getDuration()));
        etType.setText(classModel.getType());
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

        // Lưu ngày bắt đầu của lớp học và thứ trong tuần
        classStartDate = classModel.getStartAt();
        dayOfWeek = getDayOfWeekFromString(classModel.getDayOfWeek());

        // Load sessions
        loadClassSessions(classModel.getId());
    }

    private int getDayOfWeekFromString(String dayOfWeekString) {
        // Hàm chuyển đổi tên thứ (như "Monday") thành giá trị Calendar tương ứng
        switch (dayOfWeekString.toLowerCase()) {
            case "monday":
                return Calendar.MONDAY;
            case "tuesday":
                return Calendar.TUESDAY;
            case "wednesday":
                return Calendar.WEDNESDAY;
            case "thursday":
                return Calendar.THURSDAY;
            case "friday":
                return Calendar.FRIDAY;
            case "saturday":
                return Calendar.SATURDAY;
            case "sunday":
                return Calendar.SUNDAY;
            default:
                return -1;
        }
    }

    private void loadClassSessions(String classId) {
        List<ClassSessionModel> classSessions = classSessionDAO.getClassSessionsByClassId(classId);

        editClassSessionAdapter = new EditClassSessionAdapter(classSessions, this, classStartDate, dayOfWeek, updatedSession -> {
            classSessionDAO.updateClassSession(updatedSession);
        });
        recyclerViewClassSessions.setAdapter(editClassSessionAdapter);
    }

    private void navigateToAddSession() {
        Intent intent = new Intent(this, AddSessionActivity.class);
        intent.putExtra("classId", classId);
        intent.putExtra("instructorUid", selectedInstructorUid);
        intent.putExtra("sessionCount", sessionCount);
        startActivity(intent);
    }

    private void saveChanges() {
        try {
            ClassModel classModel = classDAO.getClassById(classId);

            int capacity = Integer.parseInt(etCapacity.getText().toString().trim());
            int duration = Integer.parseInt(etDuration.getText().toString().trim());
            String description = etDescription.getText().toString().trim();

            String timeStartStr = etTimeStart.getText().toString().trim();
            Time timeStart = convertToTime(timeStartStr);

            classModel.setInstructorUid(selectedInstructorUid);
            classModel.setCapacity(capacity);
            classModel.setDuration(duration);
            classModel.setDescription(description);
            classModel.setTimeStart(timeStart);

            classDAO.updateClass(classModel);

            Toast.makeText(this, "Class details saved successfully", Toast.LENGTH_SHORT).show();
            finish();
        } catch (Exception e) {
            Toast.makeText(this, "Failed to save class details", Toast.LENGTH_SHORT).show();
            finish();
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
}
