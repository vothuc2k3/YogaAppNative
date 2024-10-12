package com.example.universalyoga.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.universalyoga.R;
import com.example.universalyoga.adapters.ClassSessionAdapter;
import com.example.universalyoga.models.ClassSessionModel;
import com.example.universalyoga.models.UserModel;
import com.example.universalyoga.sqlite.DAO.ClassSessionDAO;
import com.example.universalyoga.sqlite.DAO.UserDAO;

import android.view.MenuItem;
import android.widget.Toast;


import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;


public class ClassDetailsActivity extends AppCompatActivity {

    private TextView tvInstructorName, tvCapacity, tvDuration, tvPrice,
            tvType, tvStatus, tvDescription, tvStartAt, tvEndAt,
            tvDayOfWeek, tvTimeStart;
    private RecyclerView recyclerViewClassSessions;
    private ClassSessionAdapter classSessionAdapter;
    private ClassSessionDAO classSessionDAO;
    private UserDAO userDAO;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_class_details);

        userDAO = new UserDAO(this);
        classSessionDAO = new ClassSessionDAO(this);

        recyclerViewClassSessions = findViewById(R.id.recycler_view_sessions);
        recyclerViewClassSessions.setLayoutManager(new LinearLayoutManager(this));

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
            getSupportActionBar().setTitle("Class Details");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        toolbar.setNavigationOnClickListener(v -> {
            finish();
        });

        loadClassSessions(id);
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
        }
        return super.onOptionsItemSelected(item);
    }

    private void navigateToAddSession() {
        Intent intent = new Intent(this, AddSessionActivity.class);
        intent.putExtra("classId", getIntent().getStringExtra("id"));
        intent.putExtra("instructorUid", getIntent().getStringExtra("instructorUid"));
        intent.putExtra("sessionCount", getIntent().getIntExtra("sessionCount", 0));
        startActivity(intent);
    }


    private void loadClassSessions(String classId) {
        List<ClassSessionModel> classSessions = classSessionDAO.getClassSessionsByClassId(classId);

        classSessionAdapter = new ClassSessionAdapter(classSessions, this);
        recyclerViewClassSessions.setAdapter(classSessionAdapter);
    }

    private String formatDate(Date date) {
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());
        return sdf.format(date);
    }
}