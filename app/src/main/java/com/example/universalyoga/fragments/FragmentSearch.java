package com.example.universalyoga.fragments;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.universalyoga.R;
import com.example.universalyoga.adapters.ClassAdapter;
import com.example.universalyoga.models.ClassModel;
import com.example.universalyoga.sqlite.DAO.ClassDAO;
import com.example.universalyoga.sqlite.DAO.UserDAO;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class FragmentSearch extends Fragment {

    private SearchView searchView;
    private RecyclerView recyclerView;
    private ClassAdapter classAdapter;
    private TextView tvSelectDate;
    private TextView tvPrompt;
    private Calendar selectedDate = Calendar.getInstance();
    private ClassDAO classDAO;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_search, container, false);

        searchView = view.findViewById(R.id.search_view_classes);
        recyclerView = view.findViewById(R.id.rv_search_results);
        tvSelectDate = view.findViewById(R.id.tv_select_date);
        tvPrompt = view.findViewById(R.id.tv_prompt);

        classDAO = new ClassDAO(getContext());

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        classAdapter = new ClassAdapter(new ArrayList<>(), getContext());
        recyclerView.setAdapter(classAdapter);

        showPrompt(true);

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                performSearch(query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (newText.isEmpty()) {
                    showPrompt(true);
                } else {
                    performSearch(newText);
                    showPrompt(false);
                }
                return true;
            }
        });

        tvSelectDate.setOnClickListener(v -> {
            DatePickerDialog datePickerDialog = new DatePickerDialog(
                    getContext(),
                    (view1, year, month, dayOfMonth) -> {
                        selectedDate.set(year, month, dayOfMonth);
                        updateDateText();
                    },
                    selectedDate.get(Calendar.YEAR),
                    selectedDate.get(Calendar.MONTH),
                    selectedDate.get(Calendar.DAY_OF_MONTH)
            );
            datePickerDialog.show();
        });

        return view;
    }

    private void updateDateText() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MMM-yyyy", Locale.getDefault());
        tvSelectDate.setText(dateFormat.format(selectedDate.getTime()));
    }

    private void performSearch(String name) {
        List<ClassModel> result = classDAO.searchClassesByName(name);
        classAdapter.updateData(result);
        if (result.isEmpty()) {
            showPrompt(true);
        }
    }

    private void showPrompt(boolean show) {
        if (show) {
            tvPrompt.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
        } else {
            tvPrompt.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
        }
    }
}

