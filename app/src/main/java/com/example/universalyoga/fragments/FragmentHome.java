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

import com.example.universalyoga.R;
import com.example.universalyoga.activities.AddClassActivity;
import com.example.universalyoga.firestore.ClassFirestore;

public class FragmentHome extends Fragment {

    private TextView totalClassesTextView;
    private TextView totalBookingsTextView;
    private ClassFirestore classFirestore;
    private int totalClasses = 0;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_home, container, false);

        totalClassesTextView = view.findViewById(R.id.total_classes_value);
        totalBookingsTextView = view.findViewById(R.id.total_bookings_value);

        updateDataFromDatabase();

        view.findViewById(R.id.btn_add_class).setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), AddClassActivity.class);
            startActivity(intent);
        });

        view.findViewById(R.id.btn_search_class).setOnClickListener(v -> {
        });

        return view;
    }

    private void updateDataFromDatabase() {
        classFirestore = new ClassFirestore();

        classFirestore.getClassesCount(new ClassFirestore.ClassesCountCallback() {
            @Override
            public void onSuccess(int totalClasses) {
                totalClassesTextView.setText(String.valueOf(totalClasses));
                totalBookingsTextView.setText(String.valueOf(50));
            }

            @Override
            public void onFailure(Exception e) {
                totalClassesTextView.setText("Error");
            }
        });
    }
}
