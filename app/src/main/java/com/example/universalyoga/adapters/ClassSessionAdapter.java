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

import java.util.List;

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

        holder.tvSessionNote.setText(session.getNote());
        holder.tvSessionPrice.setText("$" + session.getPrice()); // Hiển thị giá tiền
        holder.tvSessionRoom.setText(session.getRoom()); // Hiển thị phòng học
    }

    @Override
    public int getItemCount() {
        return classSessionList.size();
    }

    public static class ClassSessionViewHolder extends RecyclerView.ViewHolder {
        TextView tvSessionNote, tvSessionPrice, tvSessionRoom;

        public ClassSessionViewHolder(@NonNull View itemView) {
            super(itemView);
            tvSessionNote = itemView.findViewById(R.id.tv_session_note);
            tvSessionPrice = itemView.findViewById(R.id.tv_session_price); // TextView cho giá tiền
            tvSessionRoom = itemView.findViewById(R.id.tv_session_room);   // TextView cho phòng học
        }
    }
}
