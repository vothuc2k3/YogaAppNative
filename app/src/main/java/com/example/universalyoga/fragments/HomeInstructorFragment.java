package com.example.universalyoga.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.universalyoga.R;
import com.example.universalyoga.adapters.ClassExpandableListAdapter;
import com.example.universalyoga.models.ClassModel;
import com.example.universalyoga.models.ClassSessionModel;
import com.example.universalyoga.sqlite.DAO.ClassDAO;
import com.example.universalyoga.sqlite.DAO.ClassSessionDAO;
import com.example.universalyoga.sqlite.DAO.UserDAO;
import com.google.firebase.auth.FirebaseAuth;


import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HomeInstructorFragment extends Fragment {

    private String uid;
    private TextView noClassesTextView;
    private ExpandableListView expandableListView;
    private ClassExpandableListAdapter classExpandableListAdapter;
    private List<ClassModel> classList;
    private Map<ClassModel, List<ClassSessionModel>> sessionMap;
    private ClassDAO classDAO;
    private ClassSessionDAO classSessionDAO;
    private UserDAO userDAO;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home_instructor, container, false);

        uid = FirebaseAuth.getInstance().getCurrentUser().getUid();

        noClassesTextView = view.findViewById(R.id.tv_no_classes);
        expandableListView = view.findViewById(R.id.expandableListView);

        classSessionDAO = new ClassSessionDAO(getContext());
        classDAO = new ClassDAO(getContext());
        userDAO = new UserDAO(getContext());

        loadData();

        return view;
    }

    private void loadData() {
        classList = classDAO.getClassesWithSessionsByInstructor(uid);

        sessionMap = new HashMap<>();

        if (classList.isEmpty()) {
            noClassesTextView.setVisibility(View.VISIBLE);
            expandableListView.setVisibility(View.GONE);
        } else {
            noClassesTextView.setVisibility(View.GONE);
            expandableListView.setVisibility(View.VISIBLE);

            for (ClassModel classModel : classList) {
                List<ClassSessionModel> sessions = classSessionDAO.getClassSessionsByClassId(classModel.getId());
                sessionMap.put(classModel, sessions);
            }

            classExpandableListAdapter = new ClassExpandableListAdapter(
                    getContext(),
                    classList,
                    sessionMap,
                    userDAO,
                    userDAO.getUserByUid(uid).getRole(),
                    new ClassExpandableListAdapter.OnItemClickListener() {
                        @Override
                        public void onEditClick(ClassModel classModel) {}
                        @Override
                        public void onDeleteClick(ClassModel classModel) {}
                    });
            expandableListView.setAdapter(classExpandableListAdapter);
        }
    }
}
