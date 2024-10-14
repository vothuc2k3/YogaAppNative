package com.example.universalyoga.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.universalyoga.R;
import com.example.universalyoga.models.ClassSessionModel;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ClassSessionAdapter extends RecyclerView.Adapter<ClassSessionAdapter.ClassSessionViewHolder> {

    private List<ClassSessionModel> classSessionList;
    private Context context;

    public ClassSessionAdapter(List<ClassSessionModel> classSessionList, Context context) {
        this.classSessionList = classSessionList;
        this.context = context;
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

        holder.tvSessionNumber.setText(String.valueOf(session.getSessionNumber()));
        holder.tvSessionNote.setText(session.getNote());
        holder.tvSessionPrice.setText("$" + session.getPrice());
        holder.tvSessionRoom.setText(session.getRoom());

        SimpleDateFormat dateFormat = new SimpleDateFormat("dd MMM yyyy", Locale.getDefault());
        String sessionDate = dateFormat.format(new Date(session.getDate()));
        holder.tvSessionDate.setText(sessionDate);
    }

    @Override
    public int getItemCount() {
        return classSessionList.size();
    }

    public static class ClassSessionViewHolder extends RecyclerView.ViewHolder {
        TextView tvSessionNote, tvSessionPrice, tvSessionRoom, tvSessionNumber, tvSessionDate;

        public ClassSessionViewHolder(@NonNull View itemView) {
            super(itemView);
            tvSessionNumber = itemView.findViewById(R.id.tv_session_number);
            tvSessionNote = itemView.findViewById(R.id.tv_session_note);
            tvSessionPrice = itemView.findViewById(R.id.tv_session_price); // TextView cho giá tiền
            tvSessionRoom = itemView.findViewById(R.id.tv_session_room);   // TextView cho phòng học
            tvSessionDate = itemView.findViewById(R.id.tv_session_date);
        }
    }
}
