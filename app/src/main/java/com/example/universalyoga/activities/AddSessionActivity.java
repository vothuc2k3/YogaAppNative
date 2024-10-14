package com.example.universalyoga.activities;

import android.os.Bundle;
import android.text.TextUtils;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.universalyoga.R;
import com.example.universalyoga.models.ClassModel;
import com.example.universalyoga.models.ClassSessionModel;
import com.example.universalyoga.sqlite.DAO.ClassDAO;
import com.example.universalyoga.sqlite.DAO.ClassSessionDAO;

import java.util.Calendar;
import java.util.List;
import java.util.UUID;

public class AddSessionActivity extends AppCompatActivity {

    private EditText inputPrice, inputNotes;
    private AutoCompleteTextView inputRoom;
    private Button btnAddSession;
    private ClassSessionDAO classSessionDAO;
    private ClassDAO classDAO;
    private String classId;
    private String instructorUid;
    private int sessionCount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_session);

        classSessionDAO = new ClassSessionDAO(this);
        classDAO = new ClassDAO(this);

        inputPrice = findViewById(R.id.input_price);
        inputRoom = findViewById(R.id.input_room);
        inputNotes = findViewById(R.id.input_notes);
        btnAddSession = findViewById(R.id.btn_add_session);

        ArrayAdapter<CharSequence> roomAdapter = ArrayAdapter.createFromResource(
                this, R.array.rooms_array, android.R.layout.simple_dropdown_item_1line);
        inputRoom.setAdapter(roomAdapter);

        inputRoom.setOnClickListener(v -> inputRoom.showDropDown());

        classId = getIntent().getStringExtra("classId");
        instructorUid = getIntent().getStringExtra("instructorUid");
        sessionCount = getIntent().getIntExtra("sessionCount", 1);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Add Session");
        }
        toolbar.setNavigationOnClickListener(v -> finish());

        btnAddSession.setOnClickListener(v -> addSession());

        inputRoom.setOnClickListener(v -> inputRoom.showDropDown());
        inputRoom.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus) {
                inputRoom.showDropDown();
            }
        });
    }

    private void addSession() {
        String priceStr = inputPrice.getText().toString().trim();
        String room = inputRoom.getText().toString().trim();
        String notes = inputNotes.getText().toString().trim();

        if (TextUtils.isEmpty(priceStr)) {
            inputPrice.setError("Price is required");
            return;
        }

        int price = Integer.parseInt(priceStr);

        List<ClassSessionModel> existingSessions = classSessionDAO.getClassSessionsByClassId(classId);
        long date;

        if (existingSessions.isEmpty()) {
            Calendar calendar = Calendar.getInstance();
            date = calendar.getTimeInMillis();
        } else {
            date = existingSessions.get(existingSessions.size() - 1).getDate() + (7 * 24 * 60 * 60 * 1000);
        }

        int maxSessionNumber = 0;
        for (ClassSessionModel session : existingSessions) {
            if (session.getSessionNumber() > maxSessionNumber) {
                maxSessionNumber = session.getSessionNumber();
            }
        }

        ClassModel classModel = classDAO.getClassById(classId);
        if (classModel != null) {
            int currentSessionCount = existingSessions.size();
            int remainingSessionsToAdd = classModel.getSessionCount() - currentSessionCount;

            if (sessionCount > remainingSessionsToAdd) {
                sessionCount = remainingSessionsToAdd; // Chỉ thêm số session còn thiếu
            }

            long lastSessionDate = date;

            for (int i = 0; i < sessionCount; i++) {
                ClassSessionModel newSession = new ClassSessionModel();
                newSession.setId(UUID.randomUUID().toString());
                newSession.setSessionNumber(maxSessionNumber + i + 1);
                newSession.setClassId(classId);
                newSession.setPrice(price);
                newSession.setInstructorId(instructorUid);
                newSession.setDate(date);
                newSession.setRoom(room);
                newSession.setNote(notes);

                long result = classSessionDAO.addClassSession(newSession);
                if (result == -1) {
                    Toast.makeText(this, "Failed to add session", Toast.LENGTH_SHORT).show();
                    return;
                }
                lastSessionDate = date;
                date += 7 * 24 * 60 * 60 * 1000;
            }

            classModel.setEndAt(lastSessionDate);

            classDAO.updateClass(classModel);  // Cập nhật class model trong cơ sở dữ liệu
        }

        Toast.makeText(this, "Sessions added successfully!", Toast.LENGTH_SHORT).show();
        finish();
    }
}
