package com.example.universalyoga.activities;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;

import com.example.universalyoga.R;
import com.example.universalyoga.models.ClassModel;
import com.example.universalyoga.sqlite.DAO.ClassDAO;
import com.example.universalyoga.sqlite.DAO.CategoryDAO;
import com.example.universalyoga.models.ClassCategoryModel;

import java.sql.Time;
import java.util.Calendar;
import java.util.List;
import java.util.UUID;

public class AddClassActivity extends AppCompatActivity {

    private EditText inputDayOfWeek, inputClassDuration, inputClassStartTime, inputClassDescription, inputFirstDay, inputClassType, inputNumberOfSessions, inputClassCapacity;
    private int startHour = 0, startMinute = 0;
    private ClassDAO classDAO;
    private CategoryDAO categoryDAO;
    private List<ClassCategoryModel> categoryList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_class);

        classDAO = new ClassDAO(this);
        categoryDAO = new CategoryDAO(this);
        categoryList = categoryDAO.getAllCategories();

        // Setup Toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Add New Class");
        }

        toolbar.setTitleTextColor(ContextCompat.getColor(this, R.color.white));

        Drawable drawable = ContextCompat.getDrawable(this, R.drawable.ic_back_arrow);
        if (drawable != null) {
            drawable.setBounds(0, 0, 60, 60);
            toolbar.setNavigationIcon(drawable);
        }

        toolbar.setNavigationOnClickListener(v -> finish());

        // Initialize input fields
        inputClassDuration = findViewById(R.id.input_class_duration);
        inputClassStartTime = findViewById(R.id.input_class_start_time);
        inputClassDescription = findViewById(R.id.input_class_description);
        inputFirstDay = findViewById(R.id.input_start_at);
        inputDayOfWeek = findViewById(R.id.input_day_of_week);
        inputClassType = findViewById(R.id.input_class_type);
        inputNumberOfSessions = findViewById(R.id.input_number_of_sessions);
        inputClassCapacity = findViewById(R.id.input_class_capacity);
        Button btnSubmitClass = findViewById(R.id.btn_submit_class);

        inputClassType.setOnClickListener(v -> showClassTypeDialog());
        inputDayOfWeek.setOnClickListener(v -> showDayOfWeekDialog());
        inputClassStartTime.setOnClickListener(v -> showTimePickerDialog());
        inputFirstDay.setOnClickListener(v -> showDatePickerDialog(inputFirstDay));
        btnSubmitClass.setOnClickListener(v -> createClass());
    }

    private void showDayOfWeekDialog() {
        new androidx.appcompat.app.AlertDialog.Builder(this)
                .setTitle("Select Day of the Week")
                .setItems(R.array.days_of_week_array, (dialog, which) -> {
                    String[] daysOfWeek = getResources().getStringArray(R.array.days_of_week_array);
                    inputDayOfWeek.setText(daysOfWeek[which]);
                })
                .show();
    }

    private void showClassTypeDialog() {
        new androidx.appcompat.app.AlertDialog.Builder(this)
                .setTitle("Select Class Type")
                .setItems(categoryList.stream().map(ClassCategoryModel::getName).toArray(String[]::new), (dialog, which) -> {
                    inputClassType.setText(categoryList.get(which).getName());
                    inputClassType.setTag(categoryList.get(which).getId()); // Lưu ID vào tag
                })
                .show();
    }

    private void showTimePickerDialog() {
        Calendar calendar = Calendar.getInstance();
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);

        TimePickerDialog timePickerDialog = new TimePickerDialog(this, (view, hourOfDay, minuteOfHour) -> {
            startHour = hourOfDay;
            startMinute = minuteOfHour;
            inputClassStartTime.setText(String.format("%02d:%02d", startHour, startMinute));
        }, hour, minute, true);

        timePickerDialog.show();
    }

    private void showDatePickerDialog(EditText editText) {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(this, (view, selectedYear, selectedMonth, selectedDay) -> {
            String selectedDate = String.format("%02d/%02d/%d", selectedDay, selectedMonth + 1, selectedYear);

            Calendar selectedCalendar = Calendar.getInstance();
            selectedCalendar.set(Calendar.YEAR, selectedYear);
            selectedCalendar.set(Calendar.MONTH, selectedMonth);
            selectedCalendar.set(Calendar.DAY_OF_MONTH, selectedDay);

            String dayOfWeek = inputDayOfWeek.getText().toString();
            int selectedDayOfWeek = getDayOfWeekInt(dayOfWeek);

            if (selectedCalendar.get(Calendar.DAY_OF_WEEK) != selectedDayOfWeek) {
                editText.setError("Selected date must be a " + dayOfWeek);
                editText.setText("");
            } else {
                editText.setText(selectedDate);
                editText.setError(null);
            }
        }, year, month, day);

        datePickerDialog.getDatePicker().setMinDate(calendar.getTimeInMillis());
        datePickerDialog.show();
    }

    private void createClass() {
        String classTypeId = (String) inputClassType.getTag();
        String classCapacityStr = inputClassCapacity.getText().toString();
        String classDurationStr = inputClassDuration.getText().toString();
        String classDescription = inputClassDescription.getText().toString();
        String dayOfWeek = inputDayOfWeek.getText().toString();
        String numberOfSessionsStr = inputNumberOfSessions.getText().toString();

        if (TextUtils.isEmpty(classTypeId)) {
            inputClassType.setError("Class type is required");
            return;
        }

        if (TextUtils.isEmpty(classDurationStr)) {
            inputClassDuration.setError("Duration is required");
            return;
        }

        if (TextUtils.isEmpty(inputClassStartTime.getText())) {
            inputClassStartTime.setError("Start time is required");
            return;
        }

        if (TextUtils.isEmpty(inputFirstDay.getText())) {
            inputFirstDay.setError("Start date is required");
            return;
        }

        if (TextUtils.isEmpty(classCapacityStr)) {
            inputClassCapacity.setError("Capacity is required");
            return;
        }

        if (TextUtils.isEmpty(numberOfSessionsStr)) {
            inputNumberOfSessions.setError("Number of sessions is required");
            return;
        }

        String timeStartStr = inputClassStartTime.getText().toString();
        String[] timeParts = timeStartStr.split(":");
        int hour = Integer.parseInt(timeParts[0]);
        int minute = Integer.parseInt(timeParts[1]);

        String[] startDateParts = inputFirstDay.getText().toString().split("/");
        int startDay = Integer.parseInt(startDateParts[0]);
        int startMonth = Integer.parseInt(startDateParts[1]) - 1;
        int startYear = Integer.parseInt(startDateParts[2]);

        Calendar startCalendar = Calendar.getInstance();
        startCalendar.set(Calendar.YEAR, startYear);
        startCalendar.set(Calendar.MONTH, startMonth);
        startCalendar.set(Calendar.DAY_OF_MONTH, startDay);
        startCalendar.set(Calendar.HOUR_OF_DAY, hour);
        startCalendar.set(Calendar.MINUTE, minute);
        startCalendar.set(Calendar.SECOND, 0);

        int selectedDayOfWeek = getDayOfWeekInt(dayOfWeek);
        if (startCalendar.get(Calendar.DAY_OF_WEEK) != selectedDayOfWeek) {
            inputFirstDay.setError("Start date must be a " + dayOfWeek);
            return;
        }

        int classCapacity = Integer.parseInt(classCapacityStr);
        int numberOfSessions = Integer.parseInt(numberOfSessionsStr);
        Calendar endCalendar = (Calendar) startCalendar.clone();
        endCalendar.add(Calendar.DAY_OF_YEAR, (numberOfSessions - 1) * 7);
        endCalendar.set(Calendar.HOUR_OF_DAY, 23);
        endCalendar.set(Calendar.MINUTE, 59);
        endCalendar.set(Calendar.SECOND, 59);

        long startAt = startCalendar.getTimeInMillis();
        long endAt = endCalendar.getTimeInMillis();
        Time timeStart = new Time(startAt);

        ClassModel newClass = new ClassModel();
        newClass.setId(UUID.randomUUID().toString());
        newClass.setDayOfWeek(dayOfWeek);
        newClass.setTimeStart(timeStart);
        newClass.setStartAt(startAt);
        newClass.setSessionCount(numberOfSessions);
        newClass.setEndAt(endAt);
        newClass.setTypeId(classTypeId);
        newClass.setStatus("pending");
        newClass.setCapacity(classCapacity);
        newClass.setDuration(Integer.parseInt(classDurationStr));
        newClass.setDescription(classDescription);

        classDAO.addClass(newClass);

        Toast.makeText(this, "New Class Added!", Toast.LENGTH_SHORT).show();
        finish();
    }

    private int getDayOfWeekInt(String dayOfWeek) {
        switch (dayOfWeek) {
            case "Sunday":
                return Calendar.SUNDAY;
            case "Monday":
                return Calendar.MONDAY;
            case "Tuesday":
                return Calendar.TUESDAY;
            case "Wednesday":
                return Calendar.WEDNESDAY;
            case "Thursday":
                return Calendar.THURSDAY;
            case "Friday":
                return Calendar.FRIDAY;
            case "Saturday":
                return Calendar.SATURDAY;
            default:
                return -1;
        }
    }
}
