package com.example.universalyoga.activities;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ExpandableListView;
import android.widget.TextView;
import android.graphics.drawable.Drawable;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;

import com.example.universalyoga.R;
import com.example.universalyoga.adapters.BookingExpandableAdapter;
import com.example.universalyoga.models.BookingModel;
import com.example.universalyoga.models.ClassSessionModel;
import com.example.universalyoga.sqlite.DAO.BookingDAO;
import com.example.universalyoga.sqlite.DAO.BookingSessionDAO;
import com.example.universalyoga.sqlite.DAO.ClassSessionDAO;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BookingManagementActivity extends AppCompatActivity {

    private ExpandableListView expandableListViewBookings;
    private TextView tvEmptyState;
    private ClassSessionDAO classSessionDAO;
    private BookingSessionDAO bookingSessionDAO;
    private List<BookingModel> bookings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_booking_management);

        initializeUI();
        setupToolbar();
        initializeData();
    }

    private void initializeUI() {
        expandableListViewBookings = findViewById(R.id.expandable_list_view_bookings);
        tvEmptyState = findViewById(R.id.tv_empty_state);
    }

    private void setupToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Booking Management");
            toolbar.setTitleTextColor(ContextCompat.getColor(this, R.color.white));
        }

        Drawable drawable = ContextCompat.getDrawable(this, R.drawable.ic_back_arrow);
        if (drawable != null) {
            drawable.setBounds(0, 0, 60, 60);
            toolbar.setNavigationIcon(drawable);
        }
        toolbar.setNavigationOnClickListener(v -> finish());
    }

    private void initializeData() {
        BookingDAO bookingDAO = new BookingDAO(this);
        classSessionDAO = new ClassSessionDAO(this);
        bookingSessionDAO = new BookingSessionDAO(this);

        bookings = bookingDAO.getAllBookings();
        if (bookings.isEmpty()) {
            showEmptyState();
        } else {
            showBookingList();
        }
    }

    private void showEmptyState() {
        tvEmptyState.setVisibility(View.VISIBLE);
        expandableListViewBookings.setVisibility(View.GONE);
    }

    private void showBookingList() {
        tvEmptyState.setVisibility(View.GONE);
        expandableListViewBookings.setVisibility(View.VISIBLE);

        Map<BookingModel, List<ClassSessionModel>> sessionMap = loadSessionDataForBookings();
        setupExpandableListView(sessionMap);
    }

    private Map<BookingModel, List<ClassSessionModel>> loadSessionDataForBookings() {
        Map<BookingModel, List<ClassSessionModel>> sessionMap = new HashMap<>();
        for (BookingModel booking : bookings) {
            List<String> sessionIds = bookingSessionDAO.getSessionIdsByBookingId(booking.getId());
            Log.d("SessionIds", "" + sessionIds);
            List<ClassSessionModel> sessions = new ArrayList<>();
            for (String sessionId : sessionIds) {
                ClassSessionModel session = classSessionDAO.getClassSessionById(sessionId);
                sessions.add(session);
            }
            sessionMap.put(booking, sessions);
        }
        return sessionMap;
    }

    private void setupExpandableListView(Map<BookingModel, List<ClassSessionModel>> sessionMap) {
        BookingExpandableAdapter adapter = new BookingExpandableAdapter(this, bookings, sessionMap);
        expandableListViewBookings.setAdapter(adapter);
        expandableListViewBookings.setOnGroupClickListener((parent, v, groupPosition, id) -> false);
    }
}
