package com.example.universalyoga.activities;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.NumberPicker;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;

import com.example.universalyoga.R;
import com.example.universalyoga.models.ClassModel;
import com.example.universalyoga.models.UserModel;
import com.example.universalyoga.sqlite.DAO.ClassDAO;
import com.example.universalyoga.sqlite.DAO.UserDAO;

import java.sql.Time;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.UUID;

public class AddClassActivity extends AppCompatActivity {

    private Spinner spinnerInstructor, spinnerClassType, spinnerDayOfWeek;
    private EditText inputClassDuration, inputClassPrice, inputClassStartTime, inputClassDescription, inputFirstDay, inputLastDay;
    private NumberPicker numberPickerCapacity;
    private Button btnSubmitClass;
    private int startHour = 0, startMinute = 0;
    private UserDAO userDAO;
    private ClassDAO classDAO;
    private List<UserModel> instructorList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_class_activity);

        userDAO = new UserDAO(this);
        classDAO = new ClassDAO(this);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Add New Class");
        }

        toolbar.setTitleTextColor(ContextCompat.getColor(this, R.color.white));

        Drawable drawable = ContextCompat.getDrawable(this, R.drawable.ic_back_arrow);
        drawable.setBounds(0, 0, 60, 60);
        toolbar.setNavigationIcon(drawable);

        toolbar.setNavigationOnClickListener(v -> finish());

        numberPickerCapacity = findViewById(R.id.number_picker_capacity);
        numberPickerCapacity.setMinValue(10);
        numberPickerCapacity.setMaxValue(20);
        numberPickerCapacity.setWrapSelectorWheel(true);

        inputClassDuration = findViewById(R.id.input_class_duration);
        inputClassPrice = findViewById(R.id.input_class_price);
        inputClassStartTime = findViewById(R.id.input_class_start_time);
        inputClassDescription = findViewById(R.id.input_class_description);
        inputFirstDay = findViewById(R.id.input_start_at);
        inputLastDay = findViewById(R.id.input_end_at);
        btnSubmitClass = findViewById(R.id.btn_submit_class);
        spinnerInstructor = findViewById(R.id.spinner_instructor);
        spinnerDayOfWeek = findViewById(R.id.spinner_day_of_week);
        spinnerClassType = findViewById(R.id.spinner_class_type);

        loadInstructorsIntoSpinner();

        inputClassStartTime.setOnClickListener(v -> showTimePickerDialog());

        inputFirstDay.setOnClickListener(v -> showDatePickerDialog(inputFirstDay));

        inputLastDay.setOnClickListener(v -> showDatePickerDialog(inputLastDay));

        btnSubmitClass.setOnClickListener(v -> createClass());
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
            editText.setText(selectedDate);
        }, year, month, day);

        datePickerDialog.getDatePicker().setMinDate(calendar.getTimeInMillis());

        datePickerDialog.show();
    }

    private void loadInstructorsIntoSpinner() {
        instructorList = userDAO.getAllInstructors();
        List<String> instructorNames = new ArrayList<>();

        for (UserModel instructor : instructorList) {
            instructorNames.add(instructor.getName());  // Hiển thị tên instructor
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, instructorNames);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerInstructor.setAdapter(adapter);
    }

    private void createClass() {
        String classType = spinnerClassType.getSelectedItem().toString();
        int classCapacity = numberPickerCapacity.getValue();
        String classDurationStr = inputClassDuration.getText().toString();
        String classPriceStr = inputClassPrice.getText().toString();
        String classDescription = inputClassDescription.getText().toString();
        String dayOfWeek = spinnerDayOfWeek.getSelectedItem().toString();

        if (TextUtils.isEmpty(classDurationStr)) {
            inputClassDuration.setError("Duration is required");
            return;
        }

        if (TextUtils.isEmpty(classPriceStr)) {
            inputClassPrice.setError("Price is required");
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

        if (TextUtils.isEmpty(inputLastDay.getText())) {
            inputLastDay.setError("End date is required");
            return;
        }

        if (inputFirstDay.getText().toString().compareTo(inputLastDay.getText().toString()) > 0) {
            inputLastDay.setError("End date must be greater than start date");
            return;
        }

        String timeStartStr = inputClassStartTime.getText().toString();

        String[] timeParts = timeStartStr.split(":");
        int hour = Integer.parseInt(timeParts[0]);
        int minute = Integer.parseInt(timeParts[1]);

        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, hour);
        calendar.set(Calendar.MINUTE, minute);

        Time timeStart = new Time(calendar.getTimeInMillis());
        Date startDate = calendar.getTime(); 

        int selectedInstructorPosition = spinnerInstructor.getSelectedItemPosition();
        String instructorUid = instructorList.get(selectedInstructorPosition).getUid();

        ClassModel newClass = new ClassModel();
        newClass.setId(UUID.randomUUID().toString());
        newClass.setInstructorUid(instructorUid);
        newClass.setDayOfWeek(dayOfWeek);
        newClass.setTimeStart(timeStart);
        newClass.setStartAt(startDate.getTime()); 
        newClass.setType(classType);
        newClass.setStatus("open");
        newClass.setCapacity(classCapacity);
        newClass.setDuration(Integer.parseInt(classDurationStr));
        newClass.setPrice(Integer.parseInt(classPriceStr));
        newClass.setDescription(classDescription);

        calendar.add(Calendar.MINUTE, Integer.parseInt(classDurationStr));
        Date endDate = calendar.getTime();
        newClass.setEndAt(endDate.getTime());

        classDAO.addClass(newClass);

        Toast.makeText(this, "New Class Added!", Toast.LENGTH_SHORT).show();

        finish();
    }
}
