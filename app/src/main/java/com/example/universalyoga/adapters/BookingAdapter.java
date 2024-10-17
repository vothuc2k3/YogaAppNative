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
import com.example.universalyoga.models.BookingModel;
import com.example.universalyoga.models.ClassModel;
import com.example.universalyoga.models.ClassSessionModel;
import com.example.universalyoga.models.UserModel;
import com.example.universalyoga.sqlite.DAO.BookingSessionDAO;
import com.example.universalyoga.sqlite.DAO.ClassDAO;
import com.example.universalyoga.sqlite.DAO.ClassSessionDAO;
import com.example.universalyoga.sqlite.DAO.UserDAO;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class BookingAdapter extends RecyclerView.Adapter<BookingAdapter.BookingViewHolder> {

    private List<BookingModel> bookings;
    private ClassSessionDAO classSessionDAO;
    private ClassDAO classDAO;
    private BookingSessionDAO bookingSessionDAO;
    private UserDAO userDAO;
    private SimpleDateFormat dateFormat = new SimpleDateFormat("dd MMM yyyy", Locale.getDefault());

    public BookingAdapter(List<BookingModel> bookings, ClassSessionDAO classSessionDAO, ClassDAO classDAO, BookingSessionDAO bookingSessionDAO, UserDAO userDAO) {
        this.bookings = bookings;
        this.classSessionDAO = classSessionDAO;
        this.classDAO = classDAO;
        this.bookingSessionDAO = bookingSessionDAO;
        this.userDAO = userDAO;
    }

    @NonNull
    @Override
    public BookingViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_booking, parent, false);
        return new BookingViewHolder(view);
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
                    .append("Date: ").append(dateFormat.format(new Date(session.getDate()))).append("\n")
                    .append("Price: £").append(session.getPrice()).append("\n\n");
        }

        // Cập nhật tổng giá và chi tiết session
        holder.tvTotalPrice.setText("Total Price: £" + totalPrice);
        holder.tvSessionDetails.setText(sessionsInfo.toString());

        // Lấy thông tin người dùng từ UserDAO
        UserModel user = userDAO.getUserByUid(booking.getUid());
        if (user != null) {
            holder.tvUserName.setText(user.getName());

            Picasso.get()
                    .load(user.getProfileImage())  // Profile image URL từ UserModel
                    .placeholder(R.drawable.ic_default_profile_image)  // Icon mặc định khi chưa có avatar
                    .into(holder.ivAvatar);
        } else {
            holder.tvUserName.setText("Unknown User");
            holder.ivAvatar.setImageResource(R.drawable.ic_default_profile_image);
        }
    }

    @Override
    public int getItemCount() {
        return bookings.size();
    }

    public static class BookingViewHolder extends RecyclerView.ViewHolder {
        TextView tvBookingTitle, tvTotalPrice, tvSessionDetails, tvUserName;
        ImageView ivAvatar;

        public BookingViewHolder(@NonNull View itemView) {
            super(itemView);
            tvBookingTitle = itemView.findViewById(R.id.tv_booking_title);
            tvTotalPrice = itemView.findViewById(R.id.tv_total_price);
            tvSessionDetails = itemView.findViewById(R.id.tv_session_details);
            tvUserName = itemView.findViewById(R.id.tv_user_name);
            ivAvatar = itemView.findViewById(R.id.user_avatar);
        }
    }
}
