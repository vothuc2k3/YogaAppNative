package com.example.universalyoga.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.universalyoga.R;
import com.example.universalyoga.activities.AddClassActivity;
import com.example.universalyoga.activities.BookingManagementActivity;
import com.example.universalyoga.activities.CategoryManagementActivity;
import com.example.universalyoga.activities.ClassManagementActivity;
import com.example.universalyoga.activities.UserManagementActivity;
import com.example.universalyoga.sqlite.DAO.BookingDAO;
import com.example.universalyoga.sqlite.DAO.ClassDAO;
import com.example.universalyoga.sqlite.DAO.ClassSessionDAO;
import com.example.universalyoga.sqlite.DAO.UserDAO;

public class HomeFragment extends Fragment {

    private TextView totalClassesTextView;
    private TextView totalBookingsTextView;
    private TextView totalUsersTextView;
    private TextView totalSessionsTextView;
    private ClassDAO classDAO;
    private BookingDAO bookingDAO;
    private UserDAO userDAO;
    private ClassSessionDAO sessionDAO;
    private SwipeRefreshLayout swipeRefreshLayout;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_home, container, false);

        classDAO = new ClassDAO(view.getContext());
        bookingDAO = new BookingDAO(view.getContext());
        userDAO = new UserDAO(view.getContext());
        sessionDAO = new ClassSessionDAO(view.getContext());

        totalClassesTextView = view.findViewById(R.id.total_classes_value);
        totalBookingsTextView = view.findViewById(R.id.total_bookings_value);
        totalUsersTextView = view.findViewById(R.id.total_users_value);
        totalSessionsTextView = view.findViewById(R.id.total_sessions_value);

        updateDataFromSQLite();

        view.findViewById(R.id.btn_add_class).setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), AddClassActivity.class);
            startActivity(intent);
        });

        view.findViewById(R.id.btn_user_management).setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), UserManagementActivity.class);
            startActivity(intent);
        });

        view.findViewById(R.id.btn_class_management).setOnClickListener(v -> {
            startActivity(new Intent(getActivity(), ClassManagementActivity.class));
        });

        view.findViewById(R.id.btn_class_categories).setOnClickListener(v->{
            startActivity(new Intent(getActivity(), CategoryManagementActivity.class));
        });

        view.findViewById(R.id.btn_booking_management).setOnClickListener(v -> 
        startActivity(new Intent(getActivity(), BookingManagementActivity.class)));

        return view;
    }

    private void updateDataFromSQLite() {
        int classesCount = classDAO.getAllUndeletedClasses().size();
        totalClassesTextView.setText(String.valueOf(classesCount));
        int bookingCount = bookingDAO.getAllBookings().size();
        totalBookingsTextView.setText(String.valueOf(bookingCount));
        int userCount = userDAO.getAllUsers().size();
        totalUsersTextView.setText(String.valueOf(userCount));
        int sessionCount = sessionDAO.getAllClassSessions().size();
        totalSessionsTextView.setText(String.valueOf(sessionCount));
    }
}