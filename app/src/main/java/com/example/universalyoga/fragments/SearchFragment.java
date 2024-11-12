package com.example.universalyoga.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ExpandableListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;

import com.example.universalyoga.R;
import com.example.universalyoga.activities.ClassDetailsActivity;
import com.example.universalyoga.activities.SessionDetailsActivity;
import com.example.universalyoga.adapters.ClassExpandableListAdapter;
import com.example.universalyoga.models.ClassModel;
import com.example.universalyoga.models.ClassSessionModel;
import com.example.universalyoga.models.UserModel;
import com.example.universalyoga.sqlite.DAO.ClassDAO;
import com.example.universalyoga.sqlite.DAO.ClassSessionDAO;
import com.example.universalyoga.sqlite.DAO.UserDAO;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class SearchFragment extends Fragment {

    private SearchView searchView;
    private ExpandableListView expandableListView;
    private TextView tvPrompt;
    private Spinner spinnerFilterDay;
    private ClassDAO classDAO;
    private ClassSessionDAO classSessionDAO;
    private UserDAO userDAO;
    private ClassExpandableListAdapter expandableListAdapter;
    private List<ClassModel> classModels;
    private Map<ClassModel, List<ClassSessionModel>> sessionMap;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_search, container, false);

        searchView = view.findViewById(R.id.search_view_classes);
        expandableListView = view.findViewById(R.id.expandable_list_view);
        spinnerFilterDay = view.findViewById(R.id.spinner_filter_day);
        tvPrompt = view.findViewById(R.id.tv_prompt);

        classDAO = new ClassDAO(getContext());
        classSessionDAO = new ClassSessionDAO(getContext());
        userDAO = new UserDAO(getContext());
        classModels = classDAO.getAllUndeletedClasses();
        sessionMap = loadSessionDataForClasses(classModels);

        setupExpandableListAdapter();

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
                    showPrompt(false);
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

    private Map<ClassModel, List<ClassSessionModel>> loadSessionDataForClasses(List<ClassModel> classModels) {
        Map<ClassModel, List<ClassSessionModel>> sessionMap = new HashMap<>();
        for (ClassModel classModel : classModels) {
            List<ClassSessionModel> sessions = classSessionDAO.getClassSessionsByClassId(classModel.getId());
            sessionMap.put(classModel, sessions);
        }
        return sessionMap;
    }

    private void performSearch(String query, String dayOfWeek) {
        List<ClassSessionModel> sessionResults = classSessionDAO.getSessionsByInstructorName(query);

        List<ClassModel> classResults = classDAO.searchClassesByDay(dayOfWeek);

        List<ClassModel> filteredClasses = new ArrayList<>();
        Map<ClassModel, List<ClassSessionModel>> filteredSessionMap = new HashMap<>();

        for (ClassModel classModel : classResults) {
            List<ClassSessionModel> relevantSessions = new ArrayList<>();

            for (ClassSessionModel session : sessionResults) {
                if (session.getClassId().equals(classModel.getId())) {
                    relevantSessions.add(session);
                }
            }

            if (!relevantSessions.isEmpty()) {
                filteredClasses.add(classModel);
                filteredSessionMap.put(classModel, relevantSessions);
            }
        }

        expandableListAdapter.updateData(filteredClasses, filteredSessionMap);

        showPrompt(filteredClasses.isEmpty());
    }

    private void showPrompt(boolean show) {
        if (show) {
            tvPrompt.setVisibility(View.VISIBLE);
            expandableListView.setVisibility(View.GONE);
        } else {
            tvPrompt.setVisibility(View.GONE);
            expandableListView.setVisibility(View.VISIBLE);
        }
    }

    private void setupExpandableListAdapter() {
        expandableListAdapter = new ClassExpandableListAdapter(getContext(),
                classModels,
                sessionMap,
                userDAO,
                userDAO.getUserByUid(FirebaseAuth.getInstance().getUid()).getRole(),
                new ClassExpandableListAdapter.OnItemClickListener() {
                    @Override
                    public void onEditClick(ClassModel classModel) {
                        Intent intent = new Intent(getActivity(), ClassDetailsActivity.class);
                        intent.putExtra("id", classModel.getId());
                        if (classModel.getTimeStart() != null) {
                            intent.putExtra("timeStart", classModel.getTimeStart().toString());
                        }
                        startActivity(intent);
                    }

                    @Override
                    public void onDeleteClick(ClassModel classModel) {
                        new androidx.appcompat.app.AlertDialog.Builder(requireContext())
                                .setTitle("Delete Class")
                                .setMessage("Are you sure you want to delete this class?")
                                .setPositiveButton("Yes", (dialog, which) -> {
                                    classDAO.softDeleteClass(classModel.getId());
                                    classModels = classDAO.getAllUndeletedClasses();
                                    expandableListAdapter.updateData(classModels, loadSessionDataForClasses(classModels));
                                    Toast.makeText(getContext(), "Class deleted", Toast.LENGTH_SHORT).show();
                                })
                                .setNegativeButton("No", null)
                                .show();
                    }
                });

        expandableListView.setAdapter(expandableListAdapter);

        expandableListView.setOnGroupClickListener((parent, v, groupPosition, id) -> false);

        expandableListView.setOnChildClickListener((parent, v, groupPosition, childPosition, id) -> {
            UserModel currentUser = userDAO.getUserByUid(FirebaseAuth.getInstance().getUid());
            if (currentUser.getRole().equals("admin")) {
                ClassSessionModel sessionModel = (ClassSessionModel) expandableListAdapter.getChild(groupPosition, childPosition);
                Intent intent = new Intent(getContext(), SessionDetailsActivity.class);
                intent.putExtra("classSessionId", sessionModel.getId());
                startActivity(intent);
                return false;
            }
            return true;
        });
    }
}
