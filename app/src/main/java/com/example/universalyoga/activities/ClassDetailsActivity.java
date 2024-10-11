package com.example.universalyoga.activities;

import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.universalyoga.R;
import com.example.universalyoga.models.UserModel;
import com.example.universalyoga.sqlite.DAO.UserDAO;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class ClassDetailsActivity extends AppCompatActivity {

    private TextView tvClassId, tvInstructorName, tvCapacity, tvDuration, tvPrice,
            tvType, tvStatus, tvDescription, tvStartAt, tvEndAt,
            tvDayOfWeek, tvTimeStart;
    private UserDAO userDAO;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_class_details);

        userDAO = new UserDAO(this);

        tvClassId = findViewById(R.id.tv_class_id);
        tvInstructorName = findViewById(R.id.tv_instructor_name);
        tvCapacity = findViewById(R.id.tv_capacity);
        tvDuration = findViewById(R.id.tv_duration);
        tvPrice = findViewById(R.id.tv_price);
        tvType = findViewById(R.id.tv_type);
        tvStatus = findViewById(R.id.tv_status);
        tvDescription = findViewById(R.id.tv_description);
        tvStartAt = findViewById(R.id.tv_start_at);
        tvEndAt = findViewById(R.id.tv_end_at);
        tvDayOfWeek = findViewById(R.id.tv_day_of_week);
        tvTimeStart = findViewById(R.id.tv_time_start);

        String id = getIntent().getStringExtra("id");
        String instructorUid = getIntent().getStringExtra("instructorUid");

        UserModel instructor = userDAO.getUserByUid(instructorUid);

        int capacity = getIntent().getIntExtra("capacity", 0);
        int duration = getIntent().getIntExtra("duration", 0);
        int price = getIntent().getIntExtra("price", 0);
        String type = getIntent().getStringExtra("type");
        String status = getIntent().getStringExtra("status");
        String description = getIntent().getStringExtra("description");
        long startAtLong = getIntent().getLongExtra("startAt", 0);
        long endAtLong = getIntent().getLongExtra("endAt", 0);
        String dayOfWeek = getIntent().getStringExtra("dayOfWeek");
        String timeStart = getIntent().getStringExtra("timeStart");

        Log.d("ClassDetailsActivity", "Start at: " + startAtLong);
        Log.d("ClassDetailsActivity", "End at: " + endAtLong);

        tvClassId.setText(id);
        tvInstructorName.setText(instructor.getName());
        tvCapacity.setText(String.valueOf(capacity));
        tvDuration.setText(String.valueOf(duration) + " minutes");
        tvPrice.setText("$" + String.valueOf(price));
        tvType.setText(type);
        tvStatus.setText(status);
        tvDescription.setText(description);

        if (startAtLong > 0) {
            tvStartAt.setText(formatDate(new Date(startAtLong)));
        }
        if (endAtLong > 0) {
            tvEndAt.setText(formatDate(new Date(endAtLong)));
        }

        tvDayOfWeek.setText(dayOfWeek != null ? dayOfWeek : "N/A");
        tvTimeStart.setText(timeStart != null ? timeStart : "N/A");

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true); // Hiển thị nút back trên toolbar
        }

        toolbar.setNavigationOnClickListener(v -> {
            finish();
        });
    }

    private String formatDate(Date date) {
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());
        return sdf.format(date);
    }
}
