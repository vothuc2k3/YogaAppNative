package com.example.universalyoga.adapters;

import static com.example.universalyoga.R.drawable.ic_default_profile_image;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;

import com.example.universalyoga.R;
import com.example.universalyoga.activities.AddSessionActivity;
import com.example.universalyoga.models.ClassCategoryModel;
import com.example.universalyoga.models.ClassModel;
import com.example.universalyoga.models.ClassSessionModel;
import com.example.universalyoga.models.UserModel;
import com.example.universalyoga.sqlite.DAO.ClassSessionDAO;
import com.example.universalyoga.sqlite.DAO.UserDAO;
import com.example.universalyoga.sqlite.DAO.CategoryDAO;
import com.google.firebase.auth.FirebaseAuth;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

public class ClassExpandableListAdapter extends BaseExpandableListAdapter {

    private final String role;
    private final Context context;
    private List<ClassModel> classList;
    private Map<ClassModel, List<ClassSessionModel>> sessionMap;
    private final UserDAO userDAO;
    private final ClassSessionDAO classSessionDAO;
    private final CategoryDAO categoryDAO;
    private final OnItemClickListener onItemClickListener;

    public ClassExpandableListAdapter(Context context, List<ClassModel> classList, Map<ClassModel, List<ClassSessionModel>> sessionMap, UserDAO userDAO, String role, OnItemClickListener onItemClickListener) {
        this.context = context;
        this.classList = classList;
        this.sessionMap = sessionMap;
        this.userDAO = userDAO;
        this.classSessionDAO = new ClassSessionDAO(context);
        this.categoryDAO = new CategoryDAO(context);
        this.role = role;
        this.onItemClickListener = onItemClickListener;
    }

    @Override
    public int getGroupCount() {
        return classList.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return sessionMap.get(classList.get(groupPosition)).size();
    }

    @Override
    public Object getGroup(int groupPosition) {
        return classList.get(groupPosition);
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return sessionMap.get(classList.get(groupPosition)).get(childPosition);
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        ClassModel classModel = (ClassModel) getGroup(groupPosition);
        final ClassCategoryModel category = categoryDAO.getCategoryById(classModel.getTypeId());
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflateViewBasedOnRole(inflater);
        }
        bindGroupView(convertView, classModel, category);
        return convertView;
    }

    private View inflateViewBasedOnRole(LayoutInflater inflater) {
        if (role.equals("admin")) {
            return inflater.inflate(R.layout.item_class, null);
        } else {
            return inflater.inflate(R.layout.item_instructor_class, null);
        }
    }

    private void bindGroupView(View convertView, ClassModel classModel, ClassCategoryModel category) {
        TextView classNameTextView = convertView.findViewById(R.id.class_name);
        TextView capacityTextView = convertView.findViewById(R.id.capacity_value);
        TextView startTimeTextView = convertView.findViewById(R.id.start_time_value);
        TextView durationTextView = convertView.findViewById(R.id.duration_value);
        TextView dayOfWeekTextView = convertView.findViewById(R.id.day_of_week);
        TextView sessionCountTextView = convertView.findViewById(R.id.session_count_value);
        TextView sessionWarningTextView = convertView.findViewById(R.id.tv_session_warning);

        classNameTextView.setText(category.getName());
        capacityTextView.setText("Capacity: " + classModel.getCapacity());
        durationTextView.setText("Duration: " + classModel.getDuration() + " minutes");
        dayOfWeekTextView.setText("Day: " + classModel.getDayOfWeek());
        sessionCountTextView.setText("Sessions: " + classModel.getSessionCount());

        SimpleDateFormat sdf = new SimpleDateFormat("dd MMM yyyy", Locale.getDefault());
        if (classModel.getStartAt() > 0) {
            String startDate = sdf.format(new Date(classModel.getStartAt()));
            startTimeTextView.setText("Start: " + startDate);
        } else {
            startTimeTextView.setText("Start: N/A");
        }

        int currentSessionCount = classSessionDAO.getClassSessionsByClassId(classModel.getId()).size();

        if (currentSessionCount < classModel.getSessionCount()) {
            sessionWarningTextView.setVisibility(View.VISIBLE);
            convertView.setBackgroundColor(context.getResources().getColor(R.color.incomplete_class));
        } else {
            sessionWarningTextView.setVisibility(View.GONE);
            convertView.setBackgroundColor(context.getResources().getColor(R.color.complete_class));
        }

        if (role.equals("admin")) {
            setupAdminButtons(convertView, classModel, currentSessionCount);
        }
    }

    private void setupAdminButtons(View convertView, ClassModel classModel, int currentSessionCount) {
        Button btnAddSession = convertView.findViewById(R.id.btn_add_session);
        Button btnEdit = convertView.findViewById(R.id.btn_edit_class);
        Button btnDelete = convertView.findViewById(R.id.btn_delete_class);

        btnAddSession.setOnClickListener(v -> {
            if (currentSessionCount >= classModel.getSessionCount()) {
                showAddSessionConfirmationDialog(classModel);
            } else {
                Intent intent = new Intent(context, AddSessionActivity.class);
                intent.putExtra("classId", classModel.getId());
                intent.putExtra("sessionCount", classModel.getSessionCount());
                context.startActivity(intent);
            }
        });

        btnEdit.setOnClickListener(v -> onItemClickListener.onEditClick(classModel));
        btnDelete.setOnClickListener(v -> onItemClickListener.onDeleteClick(classModel));
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        ClassSessionModel sessionModel = (ClassSessionModel) getChild(groupPosition, childPosition);
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.item_class_session, null);
        }

        setInstructorDetails(sessionModel, convertView);
        setSessionDetails(sessionModel, convertView);
        highlightCurrentInstructor(sessionModel, convertView);

        return convertView;
    }

    private void setInstructorDetails(ClassSessionModel sessionModel, View convertView) {
        UserModel instructor = userDAO.getUserByUid(sessionModel.getInstructorId());

        TextView sessionInstructorTextView = convertView.findViewById(R.id.tv_instructor_name);
        ImageView instructorImageView = convertView.findViewById(R.id.icon_instructor_image);

        sessionInstructorTextView.setText(instructor.getName());
        if (instructor.getProfileImage() != null && !instructor.getProfileImage().isEmpty()) {
            Picasso.get()
                    .load(instructor.getProfileImage())
                    .placeholder(ic_default_profile_image)
                    .into(instructorImageView);
        } else {
            instructorImageView.setImageResource(ic_default_profile_image);
        }
    }

    private void setSessionDetails(ClassSessionModel sessionModel, View convertView) {
        TextView sessionNumberTextView = convertView.findViewById(R.id.tv_session_number);
        TextView sessionDateTextView = convertView.findViewById(R.id.tv_session_date);
        TextView sessionPriceTextView = convertView.findViewById(R.id.tv_session_price);
        TextView sessionRoomTextView = convertView.findViewById(R.id.tv_session_room);
        TextView sessionNoteTextView = convertView.findViewById(R.id.tv_session_note);
        TextView sessionStartTimeTextView = convertView.findViewById(R.id.tv_start_time);
        TextView sessionEndTimeTextView = convertView.findViewById(R.id.tv_end_time);

        int sessionIndex = calculateSessionIndex(sessionModel);
        sessionNumberTextView.setText(" " + sessionIndex);

        SimpleDateFormat dateFormat = new SimpleDateFormat("dd MMM yyyy", Locale.getDefault());
        sessionDateTextView.setText(" " + dateFormat.format(new Date(sessionModel.getDate())));

        sessionPriceTextView.setText(" $ " + sessionModel.getPrice());
        sessionRoomTextView.setText(sessionModel.getRoom() != null ? " " + sessionModel.getRoom() : "N/A");
        sessionNoteTextView.setText(sessionModel.getNote() != null ? " " + sessionModel.getNote() : "No notes");

        SimpleDateFormat timeFormat = new SimpleDateFormat("hh:mm a", Locale.getDefault());
        sessionStartTimeTextView.setText(" " + timeFormat.format(new Date(sessionModel.getStartTime())));
        sessionEndTimeTextView.setText(" " + timeFormat.format(new Date(sessionModel.getEndTime())));
    }

    private int calculateSessionIndex(ClassSessionModel sessionModel) {
        List<ClassSessionModel> allSessions = classSessionDAO.getClassSessionsByClassId(sessionModel.getClassId());
        Collections.sort(allSessions, Comparator.comparingLong(ClassSessionModel::getDate));

        int sessionIndex = 1;
        for (ClassSessionModel session : allSessions) {
            if (session.getId().equals(sessionModel.getId())) {
                break;
            }
            sessionIndex++;
        }
        return sessionIndex;
    }

    private void highlightCurrentInstructor(ClassSessionModel sessionModel, View convertView) {
        String currentInstructorUid = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();
        if (sessionModel.getInstructorId().equals(currentInstructorUid)) {
            convertView.setBackgroundColor(context.getResources().getColor(R.color.colorPrimary));
        } else {
            convertView.setBackgroundColor(context.getResources().getColor(android.R.color.transparent));
        }
    }


    private void showAddSessionConfirmationDialog(ClassModel classModel) {
        new AlertDialog.Builder(context)
                .setTitle("Add More Sessions")
                .setMessage("All sessions are filled. Do you want to add more sessions?")
                .setPositiveButton("Yes", (dialog, which) -> {
                    showSessionCountInputDialog(classModel);
                })
                .setNegativeButton("No", null)
                .show();
    }

    private void showSessionCountInputDialog(ClassModel classModel) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Enter Number of Sessions to Add");

        final EditText input = new EditText(context);
        input.setHint("Number of sessions");
        input.setInputType(android.text.InputType.TYPE_CLASS_NUMBER);

        final LinearLayout layout = new LinearLayout(context);
        layout.setPadding(50, 0, 50, 0);
        layout.addView(input);

        builder.setView(layout);

        builder.setPositiveButton("Add", (dialog, which) -> {
            final String inputText = input.getText().toString().trim();
            if (!inputText.isEmpty()) {
                int numberOfSessions = Integer.parseInt(inputText);
                Intent intent = new Intent(context, AddSessionActivity.class);
                intent.putExtra("classId", classModel.getId());
                intent.putExtra("sessionCount", numberOfSessions);
                context.startActivity(intent);
            } else {
                Toast.makeText(context, "Please enter a valid number", Toast.LENGTH_SHORT).show();
            }
        });
        builder.setNegativeButton("Cancel", null);
        builder.show();
    }

    public void updateData(List<ClassModel> newClassList, Map<ClassModel, List<ClassSessionModel>> newSessionMap) {
        this.classList = newClassList;
        this.sessionMap = newSessionMap;
        notifyDataSetChanged();
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }

    public interface OnItemClickListener {
        void onEditClick(ClassModel classModel);

        void onDeleteClick(ClassModel classModel);
    }
}
