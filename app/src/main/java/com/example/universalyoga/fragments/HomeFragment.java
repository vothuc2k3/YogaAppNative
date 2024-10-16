package com.example.universalyoga.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.universalyoga.R;
import com.example.universalyoga.activities.AddClassActivity;
import com.example.universalyoga.activities.BaseActivity;
import com.example.universalyoga.activities.BookingManagementActivity;
import com.example.universalyoga.activities.ClassManagementActivity;
import com.example.universalyoga.activities.UserManagementActivity;
import com.example.universalyoga.sqlite.DAO.BookingDAO;
import com.example.universalyoga.sqlite.DAO.ClassDAO;

import java.util.Objects;

public class HomeFragment extends Fragment {

    private TextView totalClassesTextView;
    private TextView totalBookingsTextView;
    private ClassDAO classDAO;
    private BookingDAO bookingDAO;
    private SwipeRefreshLayout swipeRefreshLayout;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_home, container, false);

        classDAO = new ClassDAO(view.getContext());
        bookingDAO = new BookingDAO(view.getContext());

        totalClassesTextView = view.findViewById(R.id.total_classes_value);
        totalBookingsTextView = view.findViewById(R.id.total_bookings_value);

        updateDataFromSQLite();

        swipeRefreshLayout = view.findViewById(R.id.swipeRefreshLayout);
        swipeRefreshLayout.setOnRefreshListener(() -> {
            swipeRefreshLayout.setRefreshing(true);
            updateDataFromSQLite();
            swipeRefreshLayout.setRefreshing(false);
        });

        String role = ((BaseActivity) requireActivity()).getUserRole();
        Log.d("USER ROLE", role);

        view.findViewById(R.id.btn_add_class).setOnClickListener(v -> {
            if (Objects.equals(role, "instructor")) {
                Intent intent = new Intent(getActivity(), AddClassActivity.class);
                startActivity(intent);
            } else {
                Toast.makeText(getContext(), "Unauthorized!", Toast.LENGTH_SHORT).show();
            }
        });

        view.findViewById(R.id.btn_user_management).setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), UserManagementActivity.class);
            startActivity(intent);
        });

        view.findViewById(R.id.btn_class_management).setOnClickListener(v -> {
            startActivity(new Intent(getActivity(), ClassManagementActivity.class));
        });

        view.findViewById(R.id.btn_booking_management).setOnClickListener(v -> startActivity(new Intent(getActivity(), BookingManagementActivity.class)));

        return view;
    }

    private void updateDataFromSQLite() {
        int classesCount = classDAO.getAllUndeletedClasses().size();
        totalClassesTextView.setText(String.valueOf(classesCount));
        int bookingCount = bookingDAO.getAllBookings().size();
        totalBookingsTextView.setText(String.valueOf(bookingCount));
    }
}