package com.example.universalyoga.activities;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.universalyoga.R;
import com.example.universalyoga.adapters.InstructorAdapter;
import com.example.universalyoga.models.ClassModel;
import com.example.universalyoga.models.ClassSessionModel;
import com.example.universalyoga.models.UserModel;
import com.example.universalyoga.sqlite.DAO.ClassDAO;
import com.example.universalyoga.sqlite.DAO.ClassSessionDAO;
import com.example.universalyoga.sqlite.DAO.UserDAO;

import java.sql.Time;
import java.util.Calendar;
import java.util.List;
import java.util.UUID;

public class AddSessionActivity extends AppCompatActivity {

    private EditText inputPrice, inputNotes, inputInstructor;
    private AutoCompleteTextView inputRoom;
    private Button btnAddSession;
    private ClassSessionDAO classSessionDAO;
    private ClassDAO classDAO;
    private UserDAO userDAO;
    private String classId, instructorUid = null;
    private int sessionCount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_session);

        classSessionDAO = new ClassSessionDAO(this);
        classDAO = new ClassDAO(this);
        userDAO = new UserDAO(this);

        inputPrice = findViewById(R.id.input_price);
        inputRoom = findViewById(R.id.input_room);
        inputNotes = findViewById(R.id.input_notes);
        inputInstructor = findViewById(R.id.input_instructor);
        btnAddSession = findViewById(R.id.btn_add_session);

        ArrayAdapter<CharSequence> roomAdapter = ArrayAdapter.createFromResource(
                this, R.array.rooms_array, android.R.layout.simple_dropdown_item_1line);
        inputRoom.setAdapter(roomAdapter);

        inputRoom.setOnClickListener(v -> inputRoom.showDropDown());

        classId = getIntent().getStringExtra("classId");
        sessionCount = getIntent().getIntExtra("sessionCount", 1);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Add Session");
        }
        toolbar.setNavigationOnClickListener(v -> finish());

        btnAddSession.setOnClickListener(v -> addSession());

        inputInstructor.setOnClickListener(v -> showInstructorDialog());
    }

    private void showInstructorDialog() {
        List<UserModel> instructors = userDAO.getAllInstructors();

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = this.getLayoutInflater();

        TextView title = new TextView(this);
        title.setText("Choose Instructor");
        title.setPadding(20, 30, 20, 10);
        title.setTextSize(20f);
        title.setTextColor(getResources().getColor(android.R.color.black));

        View dialogView = inflater.inflate(R.layout.dialog_instructor_selection, null);
        builder.setView(dialogView);
        builder.setCustomTitle(title);

        final AlertDialog alertDialog = builder.create();

        RecyclerView recyclerView = dialogView.findViewById(R.id.recycler_view_instructors);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        InstructorAdapter adapter = new InstructorAdapter(this, instructors, instructor -> {
            inputInstructor.setText(instructor.getName());
            instructorUid = instructor.getUid();
            alertDialog.dismiss();
        });
        recyclerView.setAdapter(adapter);

        alertDialog.show();
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

        List<UserModel> allInstructors = userDAO.getAllInstructors();
        ClassModel classModel = classDAO.getClassById(classId);

        if (classModel != null) {
            long startAtDate = classModel.getStartAt();
            Time timeStart = classModel.getTimeStart();
            int durationInMinutes = classModel.getDuration();
            long durationInMillis = durationInMinutes * 60 * 1000;

            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(startAtDate);
            calendar.set(Calendar.HOUR_OF_DAY, timeStart.getHours());
            calendar.set(Calendar.MINUTE, timeStart.getMinutes());

            long startTimeInMillis = calendar.getTimeInMillis();
            long endTimeInMillis = startTimeInMillis + durationInMillis;

            UserModel selectedInstructor = null;

            for (UserModel instructor : allInstructors) {
                List<ClassSessionModel> instructorSessions = classSessionDAO.getSessionsByInstructorId(instructor.getUid());
                boolean isConflict = false;

                for (ClassSessionModel session : instructorSessions) {
                    long sessionStartTime = session.getStartTime();
                    long sessionEndTime = session.getEndTime();

                    if ((startTimeInMillis >= sessionStartTime && startTimeInMillis < sessionEndTime) ||
                            (endTimeInMillis > sessionStartTime && endTimeInMillis <= sessionEndTime)) {
                        isConflict = true;
                        break;
                    }
                }

                if (!isConflict) {
                    selectedInstructor = instructor;
                    break;
                }
            }

            if (selectedInstructor == null) {
                Toast.makeText(this, "All instructors have schedule conflicts during this time.", Toast.LENGTH_LONG).show();
                return;
            }

            if (!selectedInstructor.getUid().equals(instructorUid)) {
                Toast.makeText(this, "Instructor " + selectedInstructor.getName() + " was automatically selected due to schedule conflict.", Toast.LENGTH_LONG).show();
            }

            int currentSessionCount = classSessionDAO.getClassSessionsByClassId(classId).size();
            int remainingSessionsToAdd = classModel.getSessionCount() - currentSessionCount;

            if (sessionCount > remainingSessionsToAdd) {
                sessionCount = remainingSessionsToAdd;
            }

            long lastSessionDate = startAtDate;

            for (int i = 0; i < sessionCount; i++) {
                ClassSessionModel newSession = new ClassSessionModel();
                newSession.setId(UUID.randomUUID().toString());
                newSession.setClassId(classId);
                newSession.setPrice(price);
                newSession.setInstructorId(selectedInstructor.getUid());
                newSession.setStartTime(startTimeInMillis);
                newSession.setEndTime(endTimeInMillis);
                newSession.setDate(calendar.getTimeInMillis());
                newSession.setRoom(room);
                newSession.setNote(notes);

                long result = classSessionDAO.addClassSession(newSession);
                if (result == -1) {
                    Toast.makeText(this, "Failed to add session", Toast.LENGTH_SHORT).show();
                    return;
                }

                calendar.add(Calendar.DAY_OF_YEAR, 7);
                startTimeInMillis = calendar.getTimeInMillis();
                endTimeInMillis = startTimeInMillis + durationInMillis;
                lastSessionDate = calendar.getTimeInMillis();
            }

            classModel.setEndAt(lastSessionDate);
            classDAO.updateClass(classModel); // Cập nhật thông tin class
        }

        Toast.makeText(this, "Sessions added successfully!", Toast.LENGTH_SHORT).show();
        finish();
    }

}
