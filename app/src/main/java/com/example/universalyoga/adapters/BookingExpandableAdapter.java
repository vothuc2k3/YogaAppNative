package com.example.universalyoga.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.universalyoga.R;
import com.example.universalyoga.models.BookingModel;
import com.example.universalyoga.models.ClassSessionModel;
import com.example.universalyoga.models.UserModel;
import com.example.universalyoga.sqlite.DAO.BookingDAO;
import com.example.universalyoga.sqlite.DAO.BookingSessionDAO;
import com.example.universalyoga.sqlite.DAO.ClassSessionDAO;
import com.example.universalyoga.sqlite.DAO.UserDAO;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.List;

public class BookingExpandableAdapter extends BaseExpandableListAdapter {

    private final Context context;
    private final List<BookingModel> bookings;
    private final HashMap<BookingModel, List<ClassSessionModel>> sessionMap;
    private final UserDAO userDAO;
    private final BookingDAO bookingDAO;
    private final BookingSessionDAO bookingSessionDAO;
    private final ClassSessionDAO classSessionDAO;

    public BookingExpandableAdapter(Context context, List<BookingModel> bookings,
                                    HashMap<BookingModel, List<ClassSessionModel>> sessionMap) {
        this.context = context;
        this.bookings = bookings;
        this.sessionMap = sessionMap;
        this.userDAO = new UserDAO(context);
        this.bookingDAO = new BookingDAO(context);
        this.bookingSessionDAO = new BookingSessionDAO(context);
        this.classSessionDAO = new ClassSessionDAO(context);
    }

    @Override
    public int getGroupCount() {
        return bookings.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return sessionMap.get(bookings.get(groupPosition)).size();
    }

    @Override
    public BookingModel getGroup(int groupPosition) {
        return bookings.get(groupPosition);
    }

    @Override
    public ClassSessionModel getChild(int groupPosition, int childPosition) {
        return sessionMap.get(bookings.get(groupPosition)).get(childPosition);
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        BookingModel booking = getGroup(groupPosition);
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.item_expandable_booking, parent, false);
        }

        ImageView ivAvatar = convertView.findViewById(R.id.user_avatar);
        TextView tvUserName = convertView.findViewById(R.id.tv_user_name);
        TextView tvBookingTitle = convertView.findViewById(R.id.tv_booking_title);
        TextView tvBookingStatus = convertView.findViewById(R.id.tv_booking_status);
        TextView tvTotalPrice = convertView.findViewById(R.id.tv_total_price);
        Button btnConfirm = convertView.findViewById(R.id.btn_confirm);
        Button btnReject = convertView.findViewById(R.id.btn_decline);

        tvBookingTitle.setText("Booking #" + (groupPosition + 1));

        UserModel user = userDAO.getUserByUid(booking.getUid());
        if (user != null) {
            tvUserName.setText(user.getName());
            Picasso.get()
                    .load(user.getProfileImage())
                    .placeholder(R.drawable.ic_default_profile_image)
                    .into(ivAvatar);
        } else {
            tvUserName.setText("Unknown User");
            ivAvatar.setImageResource(R.drawable.ic_default_profile_image);
        }

        switch (booking.getStatus()) {
            case "confirmed":
                tvBookingStatus.setText("Confirmed");
                tvBookingStatus.setTextColor(context.getResources().getColor(R.color.colorAccent));
                btnConfirm.setText("Confirmed");
                btnConfirm.setEnabled(false);
                btnConfirm.setAlpha(0.5f);
                btnReject.setVisibility(View.GONE);
                break;

            case "pending":
                tvBookingStatus.setText("Pending");
                tvBookingStatus.setTextColor(context.getResources().getColor(R.color.yellow));
                btnConfirm.setText("Confirm");
                btnConfirm.setEnabled(true);
                btnConfirm.setAlpha(1.0f);
                btnConfirm.setOnClickListener(v -> {
                    booking.setStatus("confirmed");
                    bookingDAO.updateBooking(booking);
                    notifyDataSetChanged();
                });
                btnReject.setOnClickListener(v -> {
                    booking.setStatus("rejected");
                    bookingDAO.updateBooking(booking);
                    notifyDataSetChanged();
                });
                break;

            case "rejected":
                tvBookingStatus.setText("Rejected");
                tvBookingStatus.setTextColor(context.getResources().getColor(R.color.colorError));
                btnConfirm.setText("Rejected");
                btnConfirm.setEnabled(false);
                btnConfirm.setAlpha(0.5f);
                btnReject.setVisibility(View.GONE);
                break;

            default:
                tvBookingStatus.setText("Unknown");
                btnConfirm.setEnabled(false);
                btnConfirm.setAlpha(0.5f);
                btnReject.setVisibility(View.GONE);
                break;
        }

        int totalPrice = 0;
        for (ClassSessionModel session : sessionMap.get(booking)) {
            totalPrice += session.getPrice();
        }
        tvTotalPrice.setText("Total Price: £" + totalPrice);

        return convertView;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        ClassSessionModel session = getChild(groupPosition, childPosition);
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.item_class_session, parent, false);
        }

        ImageView ivInstructor = convertView.findViewById(R.id.icon_instructor_image);
        TextView tvInstructorName = convertView.findViewById(R.id.tv_instructor_name);
        TextView tvSessionNumber = convertView.findViewById(R.id.tv_session_number);
        TextView tvSessionDate = convertView.findViewById(R.id.tv_session_date);
        TextView tvStartTime = convertView.findViewById(R.id.tv_start_time);
        TextView tvEndTime = convertView.findViewById(R.id.tv_end_time);
        TextView tvSessionPrice = convertView.findViewById(R.id.tv_session_price);
        TextView tvSessionRoom = convertView.findViewById(R.id.tv_session_room);
        TextView tvSessionNote = convertView.findViewById(R.id.tv_session_note);

        UserModel instructor = userDAO.getUserByUid(session.getInstructorId());
        if (instructor != null) {
            tvInstructorName.setText(instructor.getName());
            Picasso.get()
                    .load(instructor.getProfileImage())
                    .placeholder(R.drawable.ic_default_profile_image)
                    .into(ivInstructor);
        } else {
            tvInstructorName.setText("Unknown Instructor");
            ivInstructor.setImageResource(R.drawable.ic_default_profile_image);
        }

        tvSessionNumber.setText("Session " + (childPosition + 1));
        tvSessionDate.setText(new SimpleDateFormat("dd MMM yyyy").format(session.getDate()));
        tvStartTime.setText(new SimpleDateFormat("hh:mm a").format(session.getStartTime()));
        tvEndTime.setText(new SimpleDateFormat("hh:mm a").format(session.getEndTime()));
        tvSessionPrice.setText("£" + session.getPrice());
        tvSessionRoom.setText(session.getRoom());
        tvSessionNote.setText(session.getNote());

        return convertView;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }
}
