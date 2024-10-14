package com.example.universalyoga.adapters;

import android.app.DatePickerDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.universalyoga.R;
import com.example.universalyoga.models.ClassSessionModel;
import com.example.universalyoga.sqlite.DAO.ClassDAO;
import com.example.universalyoga.sqlite.DAO.ClassSessionDAO;
import com.google.android.material.textfield.TextInputEditText;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class EditClassSessionAdapter extends RecyclerView.Adapter<EditClassSessionAdapter.EditClassSessionViewHolder> {

    private List<ClassSessionModel> sessionList;
    private Context context;
    private OnSaveSessionListener onSaveSessionListener;
    private SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
    private long classStartDate;
    private int dayOfWeek;

    public EditClassSessionAdapter(List<ClassSessionModel> sessionList, Context context, long classStartDate, int dayOfWeek, OnSaveSessionListener onSaveSessionListener) {
        this.sessionList = sessionList;
        this.context = context;
        this.classStartDate = classStartDate;
        this.dayOfWeek = dayOfWeek;
        this.onSaveSessionListener = onSaveSessionListener;
    }

    @NonNull
    @Override
    public EditClassSessionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_edit_class_session, parent, false);
        return new EditClassSessionViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull EditClassSessionViewHolder holder, int position) {
        ClassSessionModel sessionModel = sessionList.get(position);

        holder.etSessionNumber.setText(String.valueOf(sessionModel.getSessionNumber()));
        holder.etSessionDate.setText(dateFormat.format(new Date(sessionModel.getDate())));
        holder.etSessionPrice.setText(String.valueOf(sessionModel.getPrice()));
        holder.etSessionRoom.setText(sessionModel.getRoom());
        holder.etSessionNote.setText(sessionModel.getNote());

        // Bấm vào trường ngày sẽ mở DatePicker
        holder.etSessionDate.setOnClickListener(v -> {
            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(sessionModel.getDate());

            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH);
            int dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);

            DatePickerDialog datePickerDialog = new DatePickerDialog(context, (view, year1, month1, dayOfMonth1) -> {
                Calendar selectedDate = Calendar.getInstance();
                selectedDate.set(year1, month1, dayOfMonth1);

                String formattedDate = dateFormat.format(selectedDate.getTime());
                holder.etSessionDate.setText(formattedDate);

                sessionModel.setDate(selectedDate.getTimeInMillis());
            }, year, month, dayOfMonth);

            datePickerDialog.show();
        });

        holder.btnSaveSession.setOnClickListener(v -> {
            try {
                // Lấy các thông tin từ các trường nhập liệu
                int sessionNumber = Integer.parseInt(holder.etSessionNumber.getText().toString().trim());
                String sessionDate = holder.etSessionDate.getText().toString().trim();
                double sessionPrice = Double.parseDouble(holder.etSessionPrice.getText().toString().trim());
                String sessionRoom = holder.etSessionRoom.getText().toString().trim();
                String sessionNote = holder.etSessionNote.getText().toString().trim();

                Date date = dateFormat.parse(sessionDate);
                long dateMillis = date != null ? date.getTime() : System.currentTimeMillis();

                // Kiểm tra ngày có trùng với thứ trong tuần quy định của lớp hay không
                Calendar calendar = Calendar.getInstance();
                calendar.setTimeInMillis(dateMillis);
                int selectedDayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);

                if (selectedDayOfWeek != dayOfWeek) {
                    Toast.makeText(context, "Session must be on the same day of the week as the class.", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Kiểm tra xem có session nào khác đã được lên lịch cùng ngày hay không
                for (ClassSessionModel session : sessionList) {
                    if (!session.getId().equals(sessionModel.getId()) && isSameDay(session.getDate(), dateMillis)) {
                        Toast.makeText(context, "Another session is already scheduled on this date.", Toast.LENGTH_SHORT).show();
                        return;
                    }
                }

                sessionModel.setSessionNumber(sessionNumber);
                sessionModel.setDate(dateMillis);
                sessionModel.setPrice((int) sessionPrice);
                sessionModel.setRoom(sessionRoom);
                sessionModel.setNote(sessionNote);

                if (onSaveSessionListener != null) {
                    onSaveSessionListener.onSaveSession(sessionModel);

                    ClassDAO classDAO = new ClassDAO(context);
                    classDAO.updateClassStartAndEndDate(sessionModel.getClassId());

                    ClassSessionDAO classSessionDAO = new ClassSessionDAO(context);
                    classSessionDAO.updateClassSessionNumber(sessionModel.getClassId());
                }

                Toast.makeText(context, "Session updated!", Toast.LENGTH_SHORT).show();
            } catch (ParseException e) {
                Toast.makeText(context, "Invalid date format!", Toast.LENGTH_SHORT).show();
            } catch (NumberFormatException e) {
                Toast.makeText(context, "Invalid input!", Toast.LENGTH_SHORT).show();
            }
        });
    }


    private boolean isSameDay(long date1, long date2) {
        Calendar cal1 = Calendar.getInstance();
        Calendar cal2 = Calendar.getInstance();
        cal1.setTimeInMillis(date1);
        cal2.setTimeInMillis(date2);
        return cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) &&
                cal1.get(Calendar.DAY_OF_YEAR) == cal2.get(Calendar.DAY_OF_YEAR);
    }

    @Override
    public int getItemCount() {
        return sessionList.size();
    }

    public static class EditClassSessionViewHolder extends RecyclerView.ViewHolder {
        TextInputEditText etSessionNumber, etSessionDate, etSessionPrice, etSessionRoom, etSessionNote;
        Button btnSaveSession;

        public EditClassSessionViewHolder(@NonNull View itemView) {
            super(itemView);

            etSessionNumber = itemView.findViewById(R.id.et_session_number);
            etSessionDate = itemView.findViewById(R.id.et_session_date);
            etSessionPrice = itemView.findViewById(R.id.et_session_price);
            etSessionRoom = itemView.findViewById(R.id.et_session_room);
            etSessionNote = itemView.findViewById(R.id.et_session_note);
            btnSaveSession = itemView.findViewById(R.id.btn_save_session);
        }
    }

    public interface OnSaveSessionListener {
        void onSaveSession(ClassSessionModel updatedSession);
    }
}
