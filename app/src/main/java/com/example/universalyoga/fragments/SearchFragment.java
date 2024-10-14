package com.example.universalyoga.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.universalyoga.R;
import com.example.universalyoga.activities.ClassDetailsActivity;
import com.example.universalyoga.adapters.ClassAdapter;
import com.example.universalyoga.models.ClassModel;
import com.example.universalyoga.sqlite.DAO.ClassDAO;

import java.util.List;

public class SearchFragment extends Fragment {

    private SearchView searchView;
    private RecyclerView recyclerView;
    private ClassAdapter classAdapter;
    private TextView tvPrompt;
    private Spinner spinnerFilterDay;
    private ClassDAO classDAO;
    private List<ClassModel> classModels;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_search, container, false);

        searchView = view.findViewById(R.id.search_view_classes);
        recyclerView = view.findViewById(R.id.rv_search_results);
        spinnerFilterDay = view.findViewById(R.id.spinner_filter_day);
        tvPrompt = view.findViewById(R.id.tv_prompt);

        classDAO = new ClassDAO(getContext());
        classModels = classDAO.getAllClasses();

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        setupAdapter(classModels);
        recyclerView.setAdapter(classAdapter);

        performSearch("", "Monday");

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                performSearch(query, spinnerFilterDay.getSelectedItem().toString());
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (newText.isEmpty()) {
                    performSearch("", spinnerFilterDay.getSelectedItem().toString());
                    showPrompt(false); // Show all when text is cleared
                } else {
                    performSearch(newText, spinnerFilterDay.getSelectedItem().toString());
                    showPrompt(false);
                }
                return true;
            }
        });

        spinnerFilterDay.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                performSearch(searchView.getQuery().toString(), parentView.getItemAtPosition(position).toString());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                performSearch(searchView.getQuery().toString(), "");
            }
        });

        return view;
    }

    private void performSearch(String name, String dayOfWeek) {
        List<ClassModel> result;
        if (!dayOfWeek.isEmpty() && !dayOfWeek.equals("All Days")) {
            result = classDAO.searchClassesByNameAndDay(name, dayOfWeek);
        } else {
            result = classDAO.searchClassesByName(name);
        }

        classAdapter.updateData(result);
        if (result.isEmpty()) {
            showPrompt(true);
        } else {
            showPrompt(false);
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

    private void setupAdapter(List<ClassModel> classList) {
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        classAdapter = new ClassAdapter(classList, getContext(), new ClassAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(ClassModel classModel) {
                Intent intent = new Intent(getActivity(), ClassDetailsActivity.class);
                intent.putExtra("id", classModel.getId());
                intent.putExtra("instructorUid", classModel.getInstructorUid());
                intent.putExtra("capacity", classModel.getCapacity());
                intent.putExtra("duration", classModel.getDuration());
                intent.putExtra("sessionCount", classModel.getSessionCount()); // Added sessionCount
                intent.putExtra("type", classModel.getType());
                intent.putExtra("status", classModel.getStatus());
                intent.putExtra("description", classModel.getDescription());
                intent.putExtra("createdAt", classModel.getCreatedAt());
                intent.putExtra("startAt", classModel.getStartAt());
                intent.putExtra("endAt", classModel.getEndAt());
                intent.putExtra("dayOfWeek", classModel.getDayOfWeek());
                if (classModel.getTimeStart() != null) {
                    intent.putExtra("timeStart", classModel.getTimeStart().toString());
                }
                startActivity(intent);
            }

            @Override
            public void onDeleteClick(ClassModel classModel) {
                new androidx.appcompat.app.AlertDialog.Builder(getContext())
                        .setTitle("Delete Class")
                        .setMessage("Are you sure you want to delete this class?")
                        .setPositiveButton("Yes", (dialog, which) -> {
                            classDAO.softDeleteClass(classModel.getId());
                            classModels = classDAO.getAllClasses();
                            classAdapter.updateData(classModels);
                            Toast.makeText(getContext(), "Class deleted", Toast.LENGTH_SHORT).show();
                        })
                        .setNegativeButton("No", null)
                        .show();
            }
        });
        recyclerView.setAdapter(classAdapter);
    }
}
