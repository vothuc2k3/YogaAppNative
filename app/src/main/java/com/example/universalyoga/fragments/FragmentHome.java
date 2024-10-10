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
import com.example.universalyoga.activities.UserManagementActivity;
import com.example.universalyoga.sqlite.DAO.ClassDAO;

public class FragmentHome extends Fragment {

    private TextView totalClassesTextView;
    private TextView totalBookingsTextView;
    private ClassDAO classDAO;
    private int totalClasses = 0;
    private SwipeRefreshLayout swipeRefreshLayout;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_home, container, false);

        classDAO = new ClassDAO(view.getContext());

        totalClassesTextView = view.findViewById(R.id.total_classes_value);
        totalBookingsTextView = view.findViewById(R.id.total_bookings_value);

        updateDataFromSQLite();

        swipeRefreshLayout = view.findViewById(R.id.swipeRefreshLayout);
        swipeRefreshLayout.setOnRefreshListener(() -> {
            swipeRefreshLayout.setRefreshing(true);
            updateDataFromSQLite();
            swipeRefreshLayout.setRefreshing(false);
        });

        view.findViewById(R.id.btn_add_class).setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), AddClassActivity.class);
            startActivity(intent);
        });

        view.findViewById(R.id.btn_search_class).setOnClickListener(v -> {
        });

        view.findViewById(R.id.btn_user_management).setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), UserManagementActivity.class);
            startActivity(intent);
        });

        return view;
    }

    private void updateDataFromSQLite() {
        int classesCount = classDAO.getAllClasses().size();
        totalClassesTextView.setText(String.valueOf(classesCount));
    }
}