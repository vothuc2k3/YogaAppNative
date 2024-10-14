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
import com.example.universalyoga.models.UserModel;
import com.squareup.picasso.Picasso;

import java.util.List;

public class InstructorAdapter extends RecyclerView.Adapter<InstructorAdapter.InstructorViewHolder> {

    private List<UserModel> instructors;
    private Context context;
    private OnInstructorClickListener listener;

    public InstructorAdapter(Context context, List<UserModel> instructors, OnInstructorClickListener listener) {
        this.context = context;
        this.instructors = instructors;
        this.listener = listener;
    }

    @NonNull
    @Override
    public InstructorViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_instructor, parent, false);
        return new InstructorViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull InstructorViewHolder holder, int position) {
        UserModel instructor = instructors.get(position);
        holder.tvInstructorName.setText(instructor.getName());

        Picasso.get()
                .load(instructor.getProfileImage())
                .placeholder(R.drawable.ic_default_profile_image)
                .into(holder.ivInstructorAvatar);

        holder.itemView.setOnClickListener(v -> listener.onInstructorClick(instructor));
    }

    @Override
    public int getItemCount() {
        return instructors.size();
    }

    public static class InstructorViewHolder extends RecyclerView.ViewHolder {
        ImageView ivInstructorAvatar;
        TextView tvInstructorName;

        public InstructorViewHolder(@NonNull View itemView) {
            super(itemView);
            ivInstructorAvatar = itemView.findViewById(R.id.iv_instructor_avatar);
            tvInstructorName = itemView.findViewById(R.id.tv_instructor_name);
        }
    }

    public interface OnInstructorClickListener {
        void onInstructorClick(UserModel instructor);
    }
}
