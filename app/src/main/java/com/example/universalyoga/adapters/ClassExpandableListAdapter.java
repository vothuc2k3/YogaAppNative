package com.example.universalyoga.adapters;

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
import com.example.universalyoga.models.ClassModel;
import com.example.universalyoga.models.ClassSessionModel;
import com.example.universalyoga.models.UserModel;
import com.example.universalyoga.sqlite.DAO.ClassSessionDAO;
import com.example.universalyoga.sqlite.DAO.UserDAO;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class ClassExpandableListAdapter extends BaseExpandableListAdapter {

    private final Context context;
    private List<ClassModel> classList;
    private Map<ClassModel, List<ClassSessionModel>> sessionMap;
    private final UserDAO userDAO;
    private final ClassSessionDAO classSessionDAO;
    private final OnItemClickListener onItemClickListener;

    public ClassExpandableListAdapter(Context context, List<ClassModel> classList, Map<ClassModel, List<ClassSessionModel>> sessionMap, UserDAO userDAO, OnItemClickListener onItemClickListener) {
        this.context = context;
        this.classList = classList;
        this.sessionMap = sessionMap;
        this.userDAO = userDAO;
        this.classSessionDAO = new ClassSessionDAO(context);
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
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.item_class, null);
        }

        TextView classNameTextView = convertView.findViewById(R.id.class_name);
        TextView capacityTextView = convertView.findViewById(R.id.capacity_value);
        TextView startTimeTextView = convertView.findViewById(R.id.start_time_value);
        TextView durationTextView = convertView.findViewById(R.id.duration_value);
        TextView dayOfWeekTextView = convertView.findViewById(R.id.day_of_week);
        TextView sessionCountTextView = convertView.findViewById(R.id.session_count_value);  // Thêm trường session count
        TextView sessionWarningTextView = convertView.findViewById(R.id.tv_session_warning);
        Button btnAddSession = convertView.findViewById(R.id.btn_add_session);
        Button btnEdit = convertView.findViewById(R.id.btn_edit_class);
        Button btnDelete = convertView.findViewById(R.id.btn_delete_class);

        classNameTextView.setText(classModel.getType());
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

        btnAddSession.setOnClickListener(v -> {
            if (currentSessionCount >= classModel.getSessionCount()) {
                showAddSessionConfirmationDialog(classModel);
            } else {
                Intent intent = new Intent(context, AddSessionActivity.class);
                intent.putExtra("classId", classModel.getId());
                intent.putExtra("instructorUid", classModel.getInstructorUid());
                intent.putExtra("sessionCount", classModel.getSessionCount());
                context.startActivity(intent);
            }
        });

        btnEdit.setOnClickListener(v -> onItemClickListener.onEditClick(classModel));
        btnDelete.setOnClickListener(v -> onItemClickListener.onDeleteClick(classModel));

        return convertView;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        ClassSessionModel sessionModel = (ClassSessionModel) getChild(groupPosition, childPosition);
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.item_class_session, null);
        }

        final UserModel instructor = userDAO.getUserByUid(sessionModel.getInstructorId());

        TextView sessionNumberTextView = convertView.findViewById(R.id.tv_session_number);
        TextView sessionDateTextView = convertView.findViewById(R.id.tv_session_date);
        TextView sessionPriceTextView = convertView.findViewById(R.id.tv_session_price);
        TextView sessionRoomTextView = convertView.findViewById(R.id.tv_session_room);
        TextView sessionNoteTextView = convertView.findViewById(R.id.tv_session_note);
        TextView sessionInstructorTextView = convertView.findViewById(R.id.tv_instructor_name);
        ImageView instructorImageView = convertView.findViewById(R.id.icon_instructor_image);

        sessionNumberTextView.setText(String.valueOf(sessionModel.getSessionNumber()));

        SimpleDateFormat dateFormat = new SimpleDateFormat("dd MMM yyyy", Locale.getDefault());
        String formattedDate = dateFormat.format(new Date(sessionModel.getDate()));
        sessionDateTextView.setText(formattedDate);

        sessionPriceTextView.setText("$" + sessionModel.getPrice());

        sessionRoomTextView.setText(sessionModel.getRoom() != null ? sessionModel.getRoom() : "N/A");
        sessionNoteTextView.setText(sessionModel.getNote() != null ? sessionModel.getNote() : "No notes");

        sessionInstructorTextView.setText(instructor.getName());

        if (instructor.getProfileImage() != null && !instructor.getProfileImage().isEmpty()) {
            Picasso.get()
                    .load(instructor.getProfileImage())
                    .placeholder(R.drawable.ic_default_profile_image)
                    .into(instructorImageView);
        } else {
            instructorImageView.setImageResource(R.drawable.ic_default_profile_image);
        }

        return convertView;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
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
                intent.putExtra("instructorUid", classModel.getInstructorUid());
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

    public interface OnItemClickListener {
        void onEditClick(ClassModel classModel);
        void onDeleteClick(ClassModel classModel);
    }
}
