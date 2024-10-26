package com.example.universalyoga.activities;

import android.os.Bundle;
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
import com.example.universalyoga.sqlite.DAO.UserDAO;

import java.util.HashMap;
import java.util.List;

public class BookingManagementActivity extends AppCompatActivity {

    private ExpandableListView expandableListViewBookings;
    private TextView tvEmptyState;
    private BookingDAO bookingDAO;
    private ClassSessionDAO classSessionDAO;
    private BookingSessionDAO bookingSessionDAO;
    private UserDAO userDAO;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_booking_management);

        expandableListViewBookings = findViewById(R.id.expandable_list_view_bookings);
        tvEmptyState = findViewById(R.id.tv_empty_state);

        bookingDAO = new BookingDAO(this);
        classSessionDAO = new ClassSessionDAO(this);
        bookingSessionDAO = new BookingSessionDAO(this);
        userDAO = new UserDAO(this);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Booking Management");
        }

        toolbar.setTitleTextColor(ContextCompat.getColor(this, R.color.white));
        Drawable drawable = ContextCompat.getDrawable(this, R.drawable.ic_back_arrow);
        if (drawable != null) {
            drawable.setBounds(0, 0, 60, 60);
            toolbar.setNavigationIcon(drawable);
        }

        toolbar.setNavigationOnClickListener(v -> finish());

        loadBookings();
    }

    private void loadBookings() {
        List<BookingModel> bookings = bookingDAO.getAllBookings();
        if (bookings.isEmpty()) {
            tvEmptyState.setVisibility(View.VISIBLE);
            expandableListViewBookings.setVisibility(View.GONE);
        } else {
            tvEmptyState.setVisibility(View.GONE);
            expandableListViewBookings.setVisibility(View.VISIBLE);

            HashMap<BookingModel, List<ClassSessionModel>> sessionMap = new HashMap<>();
            for (BookingModel booking : bookings) {
                List<String> sessionIds = bookingSessionDAO.getSessionIdsByBookingId(booking.getId());
                for (String sessionId : sessionIds) {
                    ClassSessionModel session = classSessionDAO.getClassSessionById(sessionId);
                    sessionMap.computeIfAbsent(booking, k -> new java.util.ArrayList<>()).add(session);
                }
            }

            BookingExpandableAdapter bookingExpandableAdapter
                    = new BookingExpandableAdapter(this, bookings, sessionMap);

            expandableListViewBookings.setAdapter(bookingExpandableAdapter);

            expandableListViewBookings.setOnGroupClickListener((parent, v, groupPosition, id) -> false);
        }
    }
}
