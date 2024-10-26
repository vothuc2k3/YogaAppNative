package com.example.universalyoga.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.universalyoga.R;
import com.example.universalyoga.models.ClassSessionModel;
import com.example.universalyoga.models.UserModel;
import com.example.universalyoga.sqlite.DAO.ClassSessionDAO;
import com.example.universalyoga.sqlite.DAO.UserDAO;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ClassSessionAdapter extends RecyclerView.Adapter<ClassSessionAdapter.ClassSessionViewHolder> {

    private final List<ClassSessionModel> classSessionList;
    private final ClassSessionDAO classSessionDAO;
    private final UserDAO userDAO;

    public ClassSessionAdapter(List<ClassSessionModel> classSessionList, Context context) {
        this.classSessionList = classSessionList;
        this.classSessionDAO = new ClassSessionDAO(context);
        this.userDAO = new UserDAO(context);
    }

    @NonNull
    @Override
    public ClassSessionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_class_session, parent, false);
        return new ClassSessionViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ClassSessionViewHolder holder, int position) {
        ClassSessionModel session = classSessionList.get(position);

        List<ClassSessionModel> allSessions = classSessionDAO.getClassSessionsByClassId(session.getClassId());
        Collections.sort(allSessions, Comparator.comparingLong(ClassSessionModel::getDate));

        int sessionIndex = 0;
        for (int i = 0; i < allSessions.size(); i++) {
            if (allSessions.get(i).getId().equals(session.getId())) {
                sessionIndex = i + 1;
                break;
            }
        }

        holder.tvSessionNumber.setText("Session " + sessionIndex);

        holder.tvSessionNote.setText(session.getNote());
        holder.tvSessionPrice.setText("$" + session.getPrice());
        holder.tvSessionRoom.setText(session.getRoom());

        UserModel instructor = userDAO.getUserByUid(session.getInstructorId());
        holder.tv_instructor_name.setText(instructor.getName());
        Picasso.get()
                .load(instructor.getProfileImage())
                .placeholder(R.drawable.ic_default_profile_image)
                .into(holder.ivInstructor);

        SimpleDateFormat dateFormat = new SimpleDateFormat("dd MMM yyyy", Locale.getDefault());
        String sessionDate = dateFormat.format(new Date(session.getDate()));
        holder.tvSessionDate.setText(sessionDate);

        SimpleDateFormat timeFormat = new SimpleDateFormat("hh:mm a", Locale.getDefault());
        String startTime = timeFormat.format(new Date(session.getStartTime()));
        String endTime = timeFormat.format(new Date(session.getEndTime()));

        holder.tvSessionStartTime.setText(startTime);
        holder.tvSessionEndTime.setText(endTime);
    }

    @Override
    public int getItemCount() {
        return classSessionList.size();
    }

    public static class ClassSessionViewHolder extends RecyclerView.ViewHolder {
        TextView tvSessionNote, tvSessionPrice, tvSessionRoom, tvSessionNumber,
                tvSessionDate, tvSessionStartTime, tvSessionEndTime, tv_instructor_name;
        ImageView ivInstructor;

        public ClassSessionViewHolder(@NonNull View itemView) {
            super(itemView);
            tvSessionNumber = itemView.findViewById(R.id.tv_session_number);
            tvSessionNote = itemView.findViewById(R.id.tv_session_note);
            tvSessionPrice = itemView.findViewById(R.id.tv_session_price);
            tvSessionRoom = itemView.findViewById(R.id.tv_session_room);
            tvSessionDate = itemView.findViewById(R.id.tv_session_date);
            tvSessionStartTime = itemView.findViewById(R.id.tv_start_time);
            tvSessionEndTime = itemView.findViewById(R.id.tv_end_time);
            tv_instructor_name = itemView.findViewById(R.id.tv_instructor_name);
            ivInstructor = itemView.findViewById(R.id.icon_instructor_image);
        }
    }
}
