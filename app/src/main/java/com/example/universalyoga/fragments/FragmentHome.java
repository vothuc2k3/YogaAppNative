package com.example.universalyoga.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.example.universalyoga.R;

public class FragmentHome extends Fragment {

    private TextView totalClassesTextView;
    private TextView totalBookingsTextView;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        totalClassesTextView = view.findViewById(R.id.total_classes_value);
        totalBookingsTextView = view.findViewById(R.id.total_bookings_value);

        updateDataFromDatabase();

        view.findViewById(R.id.btn_add_class).setOnClickListener(v -> {
            // Chuyển đến màn hình thêm lớp học mới
            // Ví dụ: getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new AddClassFragment()).commit();
        });

        view.findViewById(R.id.btn_search_class).setOnClickListener(v -> {
            // Chuyển đến màn hình tìm kiếm lớp học
            // Ví dụ: getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new SearchClassFragment()).commit();
        });

        return view;
    }

    private void updateDataFromDatabase() {
        // Giả sử dữ liệu từ cơ sở dữ liệu được lấy như thế này
        int totalClasses = 10; // Ví dụ: lấy tổng số lớp học từ cơ sở dữ liệu
        int totalBookings = 50; // Ví dụ: lấy tổng số lượt đặt lớp học từ cơ sở dữ liệu

        // Cập nhật giao diện với dữ liệu thực
        totalClassesTextView.setText(String.valueOf(totalClasses));
        totalBookingsTextView.setText(String.valueOf(totalBookings));
    }
}
