package com.example.universalyoga.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.universalyoga.R;
import com.example.universalyoga.models.BookingModel;
import com.example.universalyoga.models.ClassModel;
import com.example.universalyoga.models.ClassSessionModel;
import com.example.universalyoga.sqlite.DAO.BookingSessionDAO;
import com.example.universalyoga.sqlite.DAO.ClassDAO;
import com.example.universalyoga.sqlite.DAO.ClassSessionDAO;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class BookingAdapter extends RecyclerView.Adapter<BookingAdapter.BookingViewHolder> {

    private List<BookingModel> bookings;
    private ClassSessionDAO classSessionDAO;
    private ClassDAO classDAO;
    private BookingSessionDAO bookingSessionDAO;
    private SimpleDateFormat dateFormat = new SimpleDateFormat("dd MMM yyyy", Locale.getDefault());

    public BookingAdapter(List<BookingModel> bookings, ClassSessionDAO classSessionDAO, ClassDAO classDAO, BookingSessionDAO bookingSessionDAO) {
        this.bookings = bookings;
        this.classSessionDAO = classSessionDAO;
        this.classDAO = classDAO;
        this.bookingSessionDAO = bookingSessionDAO;
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

        holder.tvTotalPrice.setText("Total Price: £" + (sessionIds.size() * 10));

        StringBuilder sessionsInfo = new StringBuilder();
        for (String sessionId : sessionIds) {
            ClassSessionModel session = classSessionDAO.getClassSessionById(sessionId);
            ClassModel classModel = classDAO.getClassById(session.getClassId());

            sessionsInfo.append("Session ").append(session.getSessionNumber()).append(" - ")
                    .append(classModel.getType()).append("\n")
                    .append("Date: ").append(dateFormat.format(new Date(session.getDate()))).append("\n")
                    .append("Price: £").append(session.getPrice()).append("\n\n");
        }

        holder.tvSessionDetails.setText(sessionsInfo.toString());
    }

    @Override
    public int getItemCount() {
        return bookings.size();
    }

    public static class BookingViewHolder extends RecyclerView.ViewHolder {
        TextView tvBookingTitle, tvTotalPrice, tvSessionDetails;

        public BookingViewHolder(@NonNull View itemView) {
            super(itemView);
            tvBookingTitle = itemView.findViewById(R.id.tv_booking_title);
            tvTotalPrice = itemView.findViewById(R.id.tv_total_price);
            tvSessionDetails = itemView.findViewById(R.id.tv_session_details);
        }
    }
}
