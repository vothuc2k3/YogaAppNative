package com.example.universalyoga.activities;

import android.app.TimePickerDialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.NumberPicker;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.universalyoga.R;
import com.example.universalyoga.models.ClassModel;
import com.google.firebase.Timestamp;

import java.util.Calendar;

public class AddClassActivity extends AppCompatActivity {

    private Spinner spinnerClassType;
    private EditText inputClassDuration, inputClassPrice, inputClassStartTime, inputClassDescription;
    private NumberPicker numberPickerCapacity;
    private Button btnSubmitClass;
    private int startHour = 0, startMinute = 0;  // Default start time values

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_class_activity);

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

        inputClassStartTime.setOnClickListener(v -> showTimePickerDialog());

        btnSubmitClass.setOnClickListener(v -> createClass());
    }

    private void showTimePickerDialog() {
        // Khởi tạo TimePickerDialog để chọn giờ
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

    private void createClass() {
        // Lấy giá trị từ Spinner Class Type
        String classType = spinnerClassType.getSelectedItem().toString();
        int classCapacity = numberPickerCapacity.getValue();  // Lấy giá trị từ NumberPicker
        String classDuration = inputClassDuration.getText().toString();
        String classPrice = inputClassPrice.getText().toString();
        String classDescription = inputClassDescription.getText().toString();

        // Kiểm tra tính hợp lệ của dữ liệu đầu vào
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

        ClassModel newClass = new ClassModel();
        newClass.setType(classType);
        newClass.setCapacity(classCapacity);
        newClass.setDuration(Integer.parseInt(classDuration));
        newClass.setPrice(Double.parseDouble(classPrice));
        newClass.setDescription(classDescription);
        newClass.setStartAt(Timestamp.now());

        Toast.makeText(this, "Class Created Successfully", Toast.LENGTH_SHORT).show();

        // Reset form
        resetForm();
    }

    private void resetForm() {
        spinnerClassType.setSelection(0);
        numberPickerCapacity.setValue(10);
        inputClassDuration.setText("");
        inputClassPrice.setText("");
        inputClassStartTime.setText("");
        inputClassDescription.setText("");
    }
}
