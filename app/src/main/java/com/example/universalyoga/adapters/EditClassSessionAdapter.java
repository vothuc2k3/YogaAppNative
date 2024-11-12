package com.example.universalyoga.adapters;

import android.app.DatePickerDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.universalyoga.R;
import com.example.universalyoga.models.ClassSessionModel;
import com.example.universalyoga.models.UserModel;
import com.example.universalyoga.sqlite.DAO.ClassDAO;
import com.example.universalyoga.sqlite.DAO.ClassSessionDAO;
import com.example.universalyoga.sqlite.DAO.UserDAO;
import com.google.android.material.textfield.TextInputEditText;
import com.squareup.picasso.Picasso;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

public class EditClassSessionAdapter extends RecyclerView.Adapter<EditClassSessionAdapter.EditClassSessionViewHolder> {

    private final ClassSessionDAO classSessionDAO;
    private final UserDAO userDAO;
    private final List<ClassSessionModel> sessionList;
    private final Context context;
    private final OnSaveSessionListener onSaveSessionListener;
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
    private final int dayOfWeek;

    public EditClassSessionAdapter(ClassSessionDAO classSessionDAO, List<ClassSessionModel> sessionList, Context context, int dayOfWeek, UserDAO userDAO, OnSaveSessionListener onSaveSessionListener) {
        this.classSessionDAO = classSessionDAO;
        this.userDAO = userDAO;
        this.sessionList = sessionList;
        this.context = context;
        this.dayOfWeek = dayOfWeek;
        this.onSaveSessionListener = onSaveSessionListener;
    }

    @NonNull
    @Override
    public EditClassSessionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_edit_class_session, parent, false);
        return new EditClassSessionViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull EditClassSessionViewHolder holder, int position) {
        ClassSessionModel sessionModel = sessionList.get(position);

        setupInstructor(holder, sessionModel);
        setupSessionDetails(holder, sessionModel);
        setupDatePicker(holder, sessionModel);
        setupSaveButton(holder, sessionModel);
        setupDeleteSession(holder, position, sessionModel);
    }

    private void setupInstructor(EditClassSessionViewHolder holder, ClassSessionModel sessionModel) {
        UserModel instructor = userDAO.getUserByUid(sessionModel.getInstructorId());
        if (instructor != null) {
            holder.tvInstructorName.setText(instructor.getName());
            Picasso.get().load(instructor.getProfileImage()).placeholder(R.drawable.ic_default_profile_image).into(holder.ivInstructorAvatar);
        } else {
            holder.tvInstructorName.setText("Unknown Instructor");
            holder.ivInstructorAvatar.setImageResource(R.drawable.ic_default_profile_image);
        }

        View.OnClickListener changeInstructorListener = v -> showInstructorSelectionDialog(sessionModel, holder);
        holder.tvInstructorName.setOnClickListener(changeInstructorListener);
        holder.ivInstructorAvatar.setOnClickListener(changeInstructorListener);
    }

    private void setupSessionDetails(EditClassSessionViewHolder holder, ClassSessionModel sessionModel) {
        holder.etSessionDate.setText(dateFormat.format(new Date(sessionModel.getDate())));
        holder.etSessionPrice.setText(String.valueOf(sessionModel.getPrice()));
        holder.etSessionRoom.setText(sessionModel.getRoom());
        holder.etSessionNote.setText(sessionModel.getNote());
    }

    private void setupDatePicker(EditClassSessionViewHolder holder, ClassSessionModel sessionModel) {
        holder.etSessionDate.setOnClickListener(v -> {
            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(sessionModel.getDate());

            DatePickerDialog datePickerDialog = new DatePickerDialog(context, (view, year, month, dayOfMonth) -> {
                Calendar selectedDate = Calendar.getInstance();
                selectedDate.set(year, month, dayOfMonth);

                String formattedDate = dateFormat.format(selectedDate.getTime());
                holder.etSessionDate.setText(formattedDate);

                sessionModel.setDate(selectedDate.getTimeInMillis());
            }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));

            datePickerDialog.show();
        });
    }

    private void setupSaveButton(EditClassSessionViewHolder holder, ClassSessionModel sessionModel) {
        holder.btnSaveSession.setOnClickListener(v -> {
            try {
                if(holder.etSessionRoom.getText().toString().trim().isEmpty()){
                    holder.etSessionRoom.setError("Room is required");
                    Toast.makeText(context, "Room is required", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (validateSessionDate(holder)) {
                    updateSessionModel(holder, sessionModel);
                    if (onSaveSessionListener != null) {
                        onSaveSessionListener.onSaveSession(sessionModel);
                        updateClassDates(sessionModel);
                    }
                    Toast.makeText(context, "Session updated!", Toast.LENGTH_SHORT).show();
                }
            } catch (ParseException | NumberFormatException e) {
                Toast.makeText(context, "Invalid input!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setupDeleteSession(EditClassSessionViewHolder holder, int position, ClassSessionModel sessionModel) {
        holder.itemView.setOnLongClickListener(v -> {
            new AlertDialog.Builder(context)
                    .setTitle("Delete Session")
                    .setMessage("Are you sure you want to delete this session?")
                    .setPositiveButton("Yes", (dialog, which) -> deleteSession(position, sessionModel))
                    .setNegativeButton("No", null)
                    .show();
            return true;
        });
    }

    private boolean validateSessionDate(EditClassSessionViewHolder holder) throws ParseException {
        String sessionDate = Objects.requireNonNull(holder.etSessionDate.getText()).toString().trim();
        Date date = dateFormat.parse(sessionDate);

        if (date == null) throw new ParseException("Invalid date", 0);
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);

        if (calendar.get(Calendar.DAY_OF_WEEK) != dayOfWeek) {
            Toast.makeText(context, "Session must be on the same day of the week as the class.", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    private void updateSessionModel(EditClassSessionViewHolder holder, ClassSessionModel sessionModel) throws ParseException {
        sessionModel.setDate(dateFormat.parse(holder.etSessionDate.getText().toString().trim()).getTime());
        sessionModel.setPrice(Integer.parseInt(holder.etSessionPrice.getText().toString().trim()));
        sessionModel.setRoom(holder.etSessionRoom.getText().toString().trim());
        sessionModel.setNote(holder.etSessionNote.getText().toString().trim());
    }

    private void deleteSession(int position, ClassSessionModel sessionModel) {
        sessionList.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, sessionList.size());
        classSessionDAO.softDeleteClassSession(sessionModel.getId());
        Toast.makeText(context, "Session deleted", Toast.LENGTH_SHORT).show();
        updateClassDates(sessionModel);
    }

    private void updateClassDates(ClassSessionModel sessionModel) {
        ClassDAO classDAO = new ClassDAO(context);
        classDAO.updateClassStartAndEndDate(sessionModel.getClassId());
    }


    private void showInstructorSelectionDialog(ClassSessionModel sessionModel, EditClassSessionViewHolder holder) {
        List<UserModel> instructors = userDAO.getAllInstructors();

        View dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_instructor_selection, null);
        RecyclerView recyclerView = dialogView.findViewById(R.id.recycler_view_instructors);
        recyclerView.setLayoutManager(new LinearLayoutManager(context));

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Select Instructor").setView(dialogView).setNegativeButton("Cancel", null);

        AlertDialog dialog = builder.create();

        InstructorAdapter adapter = new InstructorAdapter(context, instructors, selectedInstructor -> {
            sessionModel.setInstructorId(selectedInstructor.getUid());
            holder.tvInstructorName.setText(selectedInstructor.getName());
            Picasso.get().load(selectedInstructor.getProfileImage()).placeholder(R.drawable.ic_default_profile_image).into(holder.ivInstructorAvatar);

            dialog.dismiss();
        });

        recyclerView.setAdapter(adapter);
        dialog.show();
    }

    @Override
    public int getItemCount() {
        return sessionList.size();
    }

    public static class EditClassSessionViewHolder extends RecyclerView.ViewHolder {
        TextInputEditText etSessionDate, etSessionPrice, etSessionRoom, etSessionNote;
        Button btnSaveSession;
        TextView tvInstructorName;
        ImageView ivInstructorAvatar;

        public EditClassSessionViewHolder(@NonNull View itemView) {
            super(itemView);

            etSessionDate = itemView.findViewById(R.id.et_session_date);
            etSessionPrice = itemView.findViewById(R.id.et_session_price);
            etSessionRoom = itemView.findViewById(R.id.et_session_room);
            etSessionNote = itemView.findViewById(R.id.et_session_note);
            btnSaveSession = itemView.findViewById(R.id.btn_save_session);
            tvInstructorName = itemView.findViewById(R.id.tv_instructor_name);
            ivInstructorAvatar = itemView.findViewById(R.id.iv_instructor_avatar);
        }
    }

    public interface OnSaveSessionListener {
        void onSaveSession(ClassSessionModel updatedSession);
    }
}
