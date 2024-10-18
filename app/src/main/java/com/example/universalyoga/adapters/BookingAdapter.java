package com.example.universalyoga.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.example.universalyoga.R;
import com.example.universalyoga.models.BookingModel;
import com.example.universalyoga.models.ClassModel;
import com.example.universalyoga.models.ClassSessionModel;
import com.example.universalyoga.models.UserModel;
import com.example.universalyoga.sqlite.DAO.BookingDAO;
import com.example.universalyoga.sqlite.DAO.BookingSessionDAO;
import com.example.universalyoga.sqlite.DAO.ClassDAO;
import com.example.universalyoga.sqlite.DAO.ClassSessionDAO;
import com.example.universalyoga.sqlite.DAO.UserDAO;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import android.widget.Toast;

public class BookingAdapter extends RecyclerView.Adapter<BookingAdapter.BookingViewHolder> {

    private List<BookingModel> bookings;
    private Context context;  // Thêm biến context
    private BookingDAO bookingDAO;
    private UserDAO userDAO;
    private BookingSessionDAO bookingSessionDAO;
    private ClassSessionDAO classSessionDAO;
    private ClassDAO classDAO;
    private SimpleDateFormat dateFormat;  // Thêm biến dateFormat

    public BookingAdapter(List<BookingModel> bookings, Context context, UserDAO userDAO, BookingDAO bookingDAO,BookingSessionDAO bookingSessionDAO, ClassSessionDAO classSessionDAO, ClassDAO classDAO) {
        this.bookings = bookings;
        this.context = context;
        this.userDAO = userDAO;
        this.bookingDAO = bookingDAO;
        this.bookingSessionDAO = bookingSessionDAO;
        this.classSessionDAO = classSessionDAO;
        this.classDAO = classDAO;
        this.dateFormat = new SimpleDateFormat("dd MMM yyyy", Locale.getDefault());
    }

    @Override
    public void onBindViewHolder(@NonNull BookingViewHolder holder, int position) {
        BookingModel booking = bookings.get(position);

        holder.tvBookingTitle.setText("Booking #" + (position + 1));

        List<String> sessionIds = bookingSessionDAO.getSessionIdsByBookingId(booking.getId());

        int totalPrice = 0;
        StringBuilder sessionsInfo = new StringBuilder();
        for (String sessionId : sessionIds) {
            ClassSessionModel session = classSessionDAO.getClassSessionById(sessionId);
            ClassModel classModel = classDAO.getClassById(session.getClassId());

            totalPrice += session.getPrice();

            sessionsInfo.append("Session ").append("").append(" - ")
                    .append(classModel.getType()).append("\n")
                    .append("Date: ").append(dateFormat.format(new Date(session.getDate()))).append("\n")  // Sử dụng dateFormat
                    .append("Price: £").append(session.getPrice()).append("\n\n");
        }

        holder.tvTotalPrice.setText("Total Price: £" + totalPrice);
        holder.tvSessionDetails.setText(sessionsInfo.toString());

        UserModel user = userDAO.getUserByUid(booking.getUid());
        if (user != null) {
            holder.tvUserName.setText(user.getName());

            Picasso.get()
                    .load(user.getProfileImage())
                    .placeholder(R.drawable.ic_default_profile_image)
                    .into(holder.ivAvatar);
        } else {
            holder.tvUserName.setText("Unknown User");
            holder.ivAvatar.setImageResource(R.drawable.ic_default_profile_image);
        }

        holder.tvBookingStatus = holder.itemView.findViewById(R.id.tv_booking_status);
        if (booking.isConfirmed()) {
            holder.tvBookingStatus.setText("Confirmed");
            holder.tvBookingStatus.setTextColor(context.getResources().getColor(R.color.colorAccent));
        } else {
            holder.tvBookingStatus.setText("Pending");
            holder.tvBookingStatus.setTextColor(context.getResources().getColor(R.color.yellow));
        }

        if (booking.isConfirmed()) {
            holder.tvBookingStatus.setText("Confirmed");
            holder.tvBookingStatus.setTextColor(context.getResources().getColor(R.color.colorAccent));

            holder.btnConfirm.setText("Confirmed");
            holder.btnConfirm.setEnabled(false);
            holder.btnConfirm.setAlpha(0.5f);
        } else {
            holder.tvBookingStatus.setText("Pending");
            holder.tvBookingStatus.setTextColor(context.getResources().getColor(R.color.yellow));

            holder.btnConfirm.setText("Confirm");
            holder.btnConfirm.setEnabled(true);
            holder.btnConfirm.setAlpha(1.0f);
            // Set up the confirmation dialog
            holder.btnConfirm.setOnClickListener(v -> {
                new AlertDialog.Builder(context)
                        .setTitle("Confirm Booking")
                        .setMessage("Are you sure you want to confirm this booking?")
                        .setPositiveButton("Yes", (dialog, which) -> {
                            confirmBooking(booking, position);
                        })
                        .setNegativeButton("No", (dialog, which) -> {
                            dialog.dismiss();
                        })
                        .show();
            });
        }
    }

    private void confirmBooking(BookingModel booking, int position) {
        booking.setConfirmed(true);
        bookingDAO.updateBooking(booking);
        notifyItemChanged(position);
        Toast.makeText(context, "Booking confirmed!", Toast.LENGTH_SHORT).show();
    }


    @NonNull
    @Override
    public BookingViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_booking, parent, false);
        return new BookingViewHolder(view);
    }

    @Override
    public int getItemCount() {
        return bookings.size();
    }

    public static class BookingViewHolder extends RecyclerView.ViewHolder {
        TextView tvBookingTitle, tvTotalPrice, tvSessionDetails, tvUserName, tvBookingStatus;
        ImageView ivAvatar;
        Button btnConfirm;

        public BookingViewHolder(@NonNull View itemView) {
            super(itemView);
            tvBookingTitle = itemView.findViewById(R.id.tv_booking_title);
            tvTotalPrice = itemView.findViewById(R.id.tv_total_price);
            tvSessionDetails = itemView.findViewById(R.id.tv_session_details);
            tvUserName = itemView.findViewById(R.id.tv_user_name);
            tvBookingStatus = itemView.findViewById(R.id.tv_booking_status);
            ivAvatar = itemView.findViewById(R.id.user_avatar);
            btnConfirm = itemView.findViewById(R.id.btn_confirm);
        }
    }
}
