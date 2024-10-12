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
import com.example.universalyoga.models.ClassSessionModel;
import com.example.universalyoga.sqlite.DAO.ClassSessionDAO;

import java.util.UUID;

public class AddSessionActivity extends AppCompatActivity {

    private EditText inputPrice, inputNotes;
    private AutoCompleteTextView inputRoom;
    private Button btnAddSession;
    private ClassSessionDAO classSessionDAO;
    private String classId;
    private String instructorUid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_session);

        classSessionDAO = new ClassSessionDAO(this);

        inputPrice = findViewById(R.id.input_price);
        inputRoom = findViewById(R.id.input_room);
        inputNotes = findViewById(R.id.input_notes);
        btnAddSession = findViewById(R.id.btn_add_session);

        ArrayAdapter<CharSequence> roomAdapter = ArrayAdapter.createFromResource(
                this, R.array.rooms_array, android.R.layout.simple_dropdown_item_1line);

        inputRoom.setAdapter(roomAdapter);

        inputRoom.setOnClickListener(v -> inputRoom.showDropDown());

        classId = getIntent().getStringExtra("CLASS_ID");
        instructorUid = getIntent().getStringExtra("instructorUid");

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Add Session");
        }
        toolbar.setNavigationOnClickListener(v -> finish());

        btnAddSession.setOnClickListener(v -> addSession());
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

        ClassSessionModel newSession = new ClassSessionModel();
        newSession.setId(UUID.randomUUID().toString());
        newSession.setClassId(classId);
        newSession.setPrice(price);
        newSession.setRoom(room);
        newSession.setNote(notes);

        long result = classSessionDAO.addClassSession(newSession);
        if (result != -1) {
            Toast.makeText(this, "Session added successfully!", Toast.LENGTH_SHORT).show();
            finish();
        } else {
            Toast.makeText(this, "Failed to add session", Toast.LENGTH_SHORT).show();
        }
    }
}
