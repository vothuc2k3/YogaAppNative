package com.example.universalyoga.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.universalyoga.R;
import com.example.universalyoga.models.ClassModel;
import com.google.firebase.Timestamp;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

public class ClassAdapter extends RecyclerView.Adapter<ClassAdapter.ClassViewHolder> {

    private List<ClassModel> classList;

    public ClassAdapter(List<ClassModel> classList) {
        this.classList = classList;
    }

    @NonNull
    @Override
    public ClassViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_class, parent, false);
        return new ClassViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ClassViewHolder holder, int position) {
        ClassModel classModel = classList.get(position);

        holder.classNameTextView.setText(classModel.getType());

        holder.classDescriptionTextView.setText(classModel.getDescription());

        holder.instructorTextView.setText("Instructor: " + classModel.getInstructorUid());

        holder.capacityTextView.setText("Capacity: " + classModel.getCapacity());

        SimpleDateFormat sdf = new SimpleDateFormat("dd MMM yyyy, HH:mm", Locale.getDefault());
        if (classModel.getStartAt() != null) {
            Timestamp startAt = classModel.getStartAt();
            holder.startTimeTextView.setText("Start: " + sdf.format(startAt.toDate()));
        } else {
            holder.startTimeTextView.setText("Start: N/A");
        }

        holder.durationTextView.setText("Duration: " + classModel.getDuration() + " minutes");

        // Hiển thị giá tiền (price)
        holder.priceTextView.setText("Price: $" + classModel.getPrice());
    }

    @Override
    public int getItemCount() {
        return classList.size();
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

        public ClassViewHolder(@NonNull View itemView) {
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
}
