package com.example.universalyoga.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.universalyoga.R;
import com.example.universalyoga.activities.AddSessionActivity;
import com.example.universalyoga.models.ClassModel;
import com.example.universalyoga.sqlite.DAO.ClassSessionDAO;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ClassAdapter extends RecyclerView.Adapter<ClassAdapter.ClassViewHolder> {

    private final List<ClassModel> classList;
    private final Context context;
    private final OnItemClickListener onItemClickListener;
    private final ClassSessionDAO classSessionDAO;

    public ClassAdapter(List<ClassModel> classList, Context context, OnItemClickListener onItemClickListener) {
        this.classList = classList;
        this.context = context;
        this.onItemClickListener = onItemClickListener;
        this.classSessionDAO = new ClassSessionDAO(context);
    }

    @NonNull
    @Override
    public ClassViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_class, parent, false);
        return new ClassViewHolder(view, onItemClickListener);
    }

    @Override
    public void onBindViewHolder(@NonNull ClassViewHolder holder, int position) {

        ClassModel classModel = classList.get(position);

        holder.classNameTextView.setText(classModel.getType());
        holder.capacityTextView.setText("Capacity: " + classModel.getCapacity());
        holder.sessionCountTextView.setText("Sessions: " + classModel.getSessionCount());
        holder.dayOfWeekTextView.setText(classModel.getDayOfWeek());

        SimpleDateFormat sdf = new SimpleDateFormat("dd MMM yyyy", Locale.getDefault());

        if (classModel.getStartAt() > 0) {
            holder.startTimeTextView.setText("Start: " + sdf.format(new Date(classModel.getStartAt())));
        } else {
            holder.startTimeTextView.setText("Start: N/A");
        }

        holder.durationTextView.setText("Duration: " + classModel.getDuration() + " minutes");

        int currentSessionCount = classSessionDAO.getClassSessionsByClassId(classModel.getId()).size();

        if (classModel.isDeleted()) {
            holder.itemView.setBackgroundColor(ContextCompat.getColor(context, R.color.deleted_class));  // Set màu cho lớp đã xóa (VD: màu xám)
        } else if (currentSessionCount < classModel.getSessionCount()) {
            holder.itemView.setBackgroundColor(ContextCompat.getColor(context, R.color.incomplete_class));  // Incomplete session color
            holder.sessionWarningTextView.setVisibility(View.VISIBLE);
        } else {
            holder.itemView.setBackgroundColor(ContextCompat.getColor(context, R.color.complete_class));    // Complete session color
            holder.sessionWarningTextView.setVisibility(View.GONE);
        }
        holder.btnAddSession.setOnClickListener(v -> {
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

        holder.btnEdit.setOnClickListener(v -> {
            if (onItemClickListener != null) {
                onItemClickListener.onItemClick(classModel);
            }
        });

        holder.btnDelete.setOnClickListener(v -> {
            if (onItemClickListener != null) {
                onItemClickListener.onDeleteClick(classModel);
            }
        });


        holder.itemView.setOnClickListener(v -> {
            if (onItemClickListener != null) {
                onItemClickListener.onItemClick(classModel);
            }
        });
    }

    // Method to show confirmation dialog
    private void showAddSessionConfirmationDialog(ClassModel classModel) {
        new AlertDialog.Builder(context)
                .setTitle("Add More Sessions")
                .setMessage("All sessions are filled. Do you want to add more sessions?")
                .setPositiveButton("Yes", (dialog, which) -> {
                    // Show input dialog to ask how many sessions to add
                    showSessionCountInputDialog(classModel);
                })
                .setNegativeButton("No", null)
                .show();
    }

    // Method to show input dialog for session count
    private void showSessionCountInputDialog(ClassModel classModel) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Enter Number of Sessions to Add");

        final EditText input = new EditText(context);
        input.setHint("Number of sessions");
        input.setInputType(android.text.InputType.TYPE_CLASS_NUMBER);

        LinearLayout layout = new LinearLayout(context);
        layout.setPadding(50, 0, 50, 0);
        layout.addView(input);

        builder.setView(layout);

        builder.setPositiveButton("Add", (dialog, which) -> {
            String inputText = input.getText().toString().trim();
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

    @Override
    public int getItemCount() {
        return classList.size();
    }

    public void updateData(List<ClassModel> newClassList) {
        this.classList.clear();
        this.classList.addAll(newClassList);
        notifyDataSetChanged();
    }


    public static class ClassViewHolder extends RecyclerView.ViewHolder {
        TextView classNameTextView, capacityTextView, sessionCountTextView, startTimeTextView, durationTextView, sessionWarningTextView, dayOfWeekTextView;
        Button btnDelete, btnAddSession, btnEdit;

        public ClassViewHolder(@NonNull View itemView, OnItemClickListener onItemClickListener) {
            super(itemView);
            classNameTextView = itemView.findViewById(R.id.class_name);
            capacityTextView = itemView.findViewById(R.id.capacity_value);
            startTimeTextView = itemView.findViewById(R.id.start_time_value);
            durationTextView = itemView.findViewById(R.id.duration_value);
            sessionCountTextView = itemView.findViewById(R.id.session_count_value);
            sessionWarningTextView = itemView.findViewById(R.id.tv_session_warning);
            dayOfWeekTextView = itemView.findViewById(R.id.day_of_week);
            btnDelete = itemView.findViewById(R.id.btn_delete_class);
            btnAddSession = itemView.findViewById(R.id.btn_add_session);
            btnEdit = itemView.findViewById(R.id.btn_edit_class);
        }
    }

    public interface OnItemClickListener {
        void onItemClick(ClassModel classModel);
        void onDeleteClick(ClassModel classModel);
    }
}
