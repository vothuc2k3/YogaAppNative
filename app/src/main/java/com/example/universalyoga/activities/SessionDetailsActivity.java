package com.example.universalyoga.activities;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import com.example.universalyoga.R;
import com.example.universalyoga.models.ClassSessionModel;
import com.example.universalyoga.models.UserModel;
import com.example.universalyoga.sqlite.DAO.ClassSessionDAO;
import com.example.universalyoga.sqlite.DAO.UserDAO;
import com.squareup.picasso.Picasso;

public class SessionDetailsActivity extends AppCompatActivity {

    private UserModel instructor;
    private ClassSessionModel classSessionModel;
    private ClassSessionDAO classSessionDAO;
    private UserDAO userDAO;

    private ImageView ivInstructorAvatar;
    private TextView tvInstructorName;
    private EditText etSessionNumber, etSessionDate, etSessionPrice, etSessionRoom, etSessionNote;
    private Button btnSaveSession;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_session_details);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Session Details");
        }

        toolbar.setNavigationOnClickListener(v -> finish());

        final String sessionId = getIntent().getStringExtra("classSessionId");

        classSessionDAO = new ClassSessionDAO(this);
        userDAO = new UserDAO(this);

        classSessionModel = classSessionDAO.getClassSessionById(sessionId);
        instructor = userDAO.getUserByUid(classSessionModel.getInstructorId());

        ivInstructorAvatar = findViewById(R.id.iv_instructor_avatar);
        tvInstructorName = findViewById(R.id.tv_instructor_name);
        etSessionNumber = findViewById(R.id.et_session_number);
        etSessionDate = findViewById(R.id.et_session_date);
        etSessionPrice = findViewById(R.id.et_session_price);
        etSessionRoom = findViewById(R.id.et_session_room);
        etSessionNote = findViewById(R.id.et_session_note);
        btnSaveSession = findViewById(R.id.btn_save_session);

        if (classSessionModel != null) {
            tvInstructorName.setText(instructor.getName());

            Picasso.get()
                    .load(instructor.getProfileImage())
                    .placeholder(R.drawable.ic_default_profile_image)
                    .into(ivInstructorAvatar);

            etSessionNumber.setText(String.valueOf(classSessionModel.getSessionNumber()));

            etSessionDate.setText(new java.text.SimpleDateFormat("dd MMM yyyy")
                    .format(new java.util.Date(classSessionModel.getDate())));

            etSessionPrice.setText(String.valueOf(classSessionModel.getPrice()));
            etSessionRoom.setText(classSessionModel.getRoom());
            etSessionNote.setText(classSessionModel.getNote());
        }

        btnSaveSession.setOnClickListener(v -> {
        });
    }
}
