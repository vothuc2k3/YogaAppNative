package com.example.universalyoga.activities;

import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.graphics.drawable.Drawable;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.universalyoga.R;
import com.example.universalyoga.adapters.BookingAdapter;
import com.example.universalyoga.models.BookingModel;
import com.example.universalyoga.models.ClassModel;
import com.example.universalyoga.models.ClassSessionModel;
import com.example.universalyoga.sqlite.DAO.BookingDAO;
import com.example.universalyoga.sqlite.DAO.BookingSessionDAO;
import com.example.universalyoga.sqlite.DAO.ClassDAO;
import com.example.universalyoga.sqlite.DAO.ClassSessionDAO;
import com.example.universalyoga.sqlite.DAO.UserDAO;

import java.util.List;

public class BookingManagementActivity extends AppCompatActivity {

    private RecyclerView recyclerViewBookings;
    private ProgressBar progressBar;
    private TextView tvEmptyState;
    private BookingAdapter bookingAdapter;

    private BookingDAO bookingDAO;
    private ClassSessionDAO classSessionDAO;
    private ClassDAO classDAO;
    private BookingSessionDAO bookingSessionDAO;
    private UserDAO userDAO;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_booking_management);

        recyclerViewBookings = findViewById(R.id.recycler_view_bookings);
        progressBar = findViewById(R.id.progress_bar);
        tvEmptyState = findViewById(R.id.tv_empty_state);

        bookingDAO = new BookingDAO(this);
        classSessionDAO = new ClassSessionDAO(this);
        classDAO = new ClassDAO(this);
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
            progressBar.setVisibility(View.GONE);
            tvEmptyState.setVisibility(View.VISIBLE);
        } else {
            progressBar.setVisibility(View.GONE);
            tvEmptyState.setVisibility(View.GONE);

            bookingAdapter = new BookingAdapter(bookings, classSessionDAO, classDAO, bookingSessionDAO, userDAO);
            recyclerViewBookings.setLayoutManager(new LinearLayoutManager(this));
            recyclerViewBookings.setAdapter(bookingAdapter);
        }
    }
}
