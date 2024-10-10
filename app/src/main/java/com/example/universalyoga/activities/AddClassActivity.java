package com.example.universalyoga.activities;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
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
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Calendar;
import java.util.UUID;

public class AddClassActivity extends AppCompatActivity {

    private Spinner spinnerClassType;
    private EditText inputClassDuration, inputClassPrice, inputClassStartTime, inputClassDescription;
    private NumberPicker numberPickerCapacity;
    private Button btnSubmitClass;
    private int startHour = 0, startMinute = 0;
    private UserDAO userDAO;
    private ClassDAO classDAO;

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
            getSupportActionBar().setTitle("User Management");
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

        spinnerClassType = findViewById(R.id.spinner_class_type);
        inputClassDuration = findViewById(R.id.input_class_duration);
        inputClassPrice = findViewById(R.id.input_class_price);
        inputClassStartTime = findViewById(R.id.input_class_start_time);
        inputClassDescription = findViewById(R.id.input_class_description);
        btnSubmitClass = findViewById(R.id.btn_submit_class);

        // Thiết lập sự kiện click cho EditText chọn ngày giờ
        inputClassStartTime.setOnClickListener(v -> showDatePickerDialog());

        btnSubmitClass.setOnClickListener(v -> createClass());
    }

    private void showDatePickerDialog() {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        // Ngày hiện tại
        long today = calendar.getTimeInMillis();

        // Ngày tối đa là 7 ngày sau
        calendar.add(Calendar.DAY_OF_MONTH, 7);
        long maxDate = calendar.getTimeInMillis();

        DatePickerDialog datePickerDialog = new DatePickerDialog(this, (view, selectedYear, selectedMonth, selectedDay) -> {
            // Lưu trữ ngày đã chọn và hiển thị trên EditText
            String selectedDate = String.format("%02d/%02d/%d", selectedDay, selectedMonth + 1, selectedYear);
            inputClassStartTime.setText(selectedDate);
            // Gọi phương thức để hiển thị TimePicker
            showTimePickerDialog();
        }, year, month, day);

        // Thiết lập ngày tối thiểu và tối đa
        datePickerDialog.getDatePicker().setMinDate(today);
        datePickerDialog.getDatePicker().setMaxDate(maxDate);

        datePickerDialog.show();
    }

    private void showTimePickerDialog() {
        Calendar calendar = Calendar.getInstance();
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);

        TimePickerDialog timePickerDialog = new TimePickerDialog(this, (view, hourOfDay, minuteOfHour) -> {
            startHour = hourOfDay;
            startMinute = minuteOfHour;
            // Hiển thị thời gian đã chọn
            inputClassStartTime.append(String.format(" %02d:%02d", startHour, startMinute));
        }, hour, minute, true);

        timePickerDialog.show();
    }

    private void createClass() {
        String classType = spinnerClassType.getSelectedItem().toString();
        int classCapacity = numberPickerCapacity.getValue();  // Lấy giá trị từ NumberPicker
        String classDuration = inputClassDuration.getText().toString();
        String classPrice = inputClassPrice.getText().toString();
        String classDescription = inputClassDescription.getText().toString();

        if (TextUtils.isEmpty(classDuration)) {
            inputClassDuration.setError("Duration is required");
            return;
        }

        if (TextUtils.isEmpty(classPrice)) {
            inputClassPrice.setError("Price is required");
            return;
        }

        if (TextUtils.isEmpty(inputClassStartTime.getText())) {
            inputClassStartTime.setError("Start time is required");
            return;
        }

        FirebaseUser fbUser = FirebaseAuth.getInstance().getCurrentUser();
        UserModel currentUser = userDAO.getUserByUid(fbUser.getUid());

        ClassModel newClass = new ClassModel();

        newClass.setId(UUID.randomUUID().toString());
        newClass.setCreatorUid(currentUser.getUid());
        newClass.setInstructorUid(currentUser.getUid());
        newClass.setType(classType);
        newClass.setStatus("open");
        newClass.setCapacity(classCapacity);
        newClass.setDuration(Integer.parseInt(classDuration));
        newClass.setPrice(Integer.parseInt(classPrice));
        newClass.setDescription(classDescription);

        String[] dateTime = inputClassStartTime.getText().toString().split(" ");
        String[] dateParts = dateTime[0].split("/");
        int selectedDay = Integer.parseInt(dateParts[0]);
        int selectedMonth = Integer.parseInt(dateParts[1]) - 1; // Tháng bắt đầu từ 0
        int selectedYear = Integer.parseInt(dateParts[2]);

        Calendar startCalendar = Calendar.getInstance();
        startCalendar.set(selectedYear, selectedMonth, selectedDay, startHour, startMinute);
        Timestamp startTimestamp = new Timestamp(startCalendar.getTime());
        newClass.setStartAt(startTimestamp);

        int durationMinutes = Integer.parseInt(classDuration);
        startCalendar.add(Calendar.MINUTE, durationMinutes);
        Timestamp endTimestamp = new Timestamp(startCalendar.getTime());
        newClass.setEndAt(endTimestamp);

        Log.d("Class: ", newClass.toString());

        classDAO.addClass(newClass);

        Toast.makeText(this, "New Class Added!", Toast.LENGTH_SHORT).show();
        finish();
    }
}
