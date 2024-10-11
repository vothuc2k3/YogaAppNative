package com.example.universalyoga.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.universalyoga.R;
import com.example.universalyoga.models.ClassModel;
import com.example.universalyoga.models.UserModel;
import com.example.universalyoga.sqlite.DAO.UserDAO;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ClassAdapter extends RecyclerView.Adapter<ClassAdapter.ClassViewHolder> {

    private List<ClassModel> classList;
    private Context context;
    private OnItemClickListener onItemClickListener;

    public ClassAdapter(List<ClassModel> classList, Context context, OnItemClickListener onItemClickListener) {
        this.classList = classList;
        this.context = context;
        this.onItemClickListener = onItemClickListener;
    }

    @NonNull
    @Override
    public ClassViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_class, parent, false);
        return new ClassViewHolder(view, onItemClickListener);
    }

    @Override
    public void onBindViewHolder(@NonNull ClassViewHolder holder, int position) {
        UserDAO userDAO = new UserDAO(this.context);

        ClassModel classModel = classList.get(position);

        UserModel instructorModel = userDAO.getUserByUid(classModel.getInstructorUid());

        holder.classNameTextView.setText(classModel.getType());

        holder.classDescriptionTextView.setText(classModel.getDescription());

        holder.instructorTextView.setText("Instructor: " + instructorModel.getName());

        holder.capacityTextView.setText("Capacity: " + classModel.getCapacity());

        SimpleDateFormat sdf = new SimpleDateFormat("dd MMM yyyy, HH:mm", Locale.getDefault());

        if (classModel.getStartAt() > 0) {  // Kiểm tra nếu startAt hợp lệ
            holder.startTimeTextView.setText("Start: " + sdf.format(new Date(classModel.getStartAt())));
        } else {
            holder.startTimeTextView.setText("Start: N/A");
        }

        holder.durationTextView.setText("Duration: " + classModel.getDuration() + " minutes");

        holder.priceTextView.setText("Price: $" + classModel.getPrice());

        holder.itemView.setOnClickListener(v -> {
            if (onItemClickListener != null) {
                onItemClickListener.onItemClick(classModel);
            }
        });
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

    public void updateClassList(List<ClassModel> newClassList) {
        this.classList = newClassList;
        notifyDataSetChanged();
    }

    public static class ClassViewHolder extends RecyclerView.ViewHolder {
        TextView classNameTextView;
        TextView classDescriptionTextView;
        TextView instructorTextView;
        TextView capacityTextView;
        TextView startTimeTextView;
        TextView durationTextView;
        TextView priceTextView;

        public ClassViewHolder(@NonNull View itemView, OnItemClickListener onItemClickListener) {
            super(itemView);
            classNameTextView = itemView.findViewById(R.id.class_name);
            classDescriptionTextView = itemView.findViewById(R.id.class_description);
            instructorTextView = itemView.findViewById(R.id.instructor_name);
            capacityTextView = itemView.findViewById(R.id.capacity_value);
            startTimeTextView = itemView.findViewById(R.id.start_time_value);
            durationTextView = itemView.findViewById(R.id.duration_value);
            priceTextView = itemView.findViewById(R.id.price_value);
        }
    }

    public interface OnItemClickListener {
        void onItemClick(ClassModel classModel);
    }
}
