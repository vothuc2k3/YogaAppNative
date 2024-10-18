package com.example.universalyoga.sqlite.DAO;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.universalyoga.models.BookingModel;
import com.example.universalyoga.sqlite.AppDatabaseHelper;

import java.util.ArrayList;
import java.util.List;

public class BookingDAO {

    private static final String TABLE_NAME = "bookings";
    private static final String COLUMN_ID = "id";
    private static final String COLUMN_UID = "uid";  // User ID
    private static final String COLUMN_IS_CONFIRMED = "isConfirmed"; // Boolean column
    private static final String COLUMN_CREATED_AT = "createdAt";

    private AppDatabaseHelper dbHelper;
    private SQLiteDatabase db;

    public BookingDAO(Context context) {
        dbHelper = new AppDatabaseHelper(context);
    }

    private void openWritableDb() {
        db = dbHelper.getWritableDatabase();
    }

    private void openReadableDb() {
        db = dbHelper.getReadableDatabase();
    }

    private void closeDb() {
        if (db != null && db.isOpen()) {
            db.close();
        }
    }

    // Thêm một booking vào SQLite
    public long addBooking(BookingModel booking) {
        openWritableDb();
        ContentValues values = new ContentValues();
        values.put(COLUMN_ID, booking.getId());
        values.put(COLUMN_UID, booking.getUid());
        values.put(COLUMN_IS_CONFIRMED, booking.isConfirmed() ? 1 : 0); // Thêm isConfirmed
        values.put(COLUMN_CREATED_AT, booking.getCreatedAt());

        long result = db.insert(TABLE_NAME, null, values);
        closeDb();
        return result;
    }

    // Lấy tất cả bookings từ SQLite
    public List<BookingModel> getAllBookings() {
        List<BookingModel> bookings = new ArrayList<>();
        openReadableDb();
        Cursor cursor = db.query(TABLE_NAME, null, null, null, null, null, COLUMN_CREATED_AT + " DESC");

        if (cursor != null && cursor.moveToFirst()) {
            do {
                BookingModel booking = new BookingModel();
                booking.setId(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_ID)));
                booking.setUid(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_UID)));
                booking.setConfirmed(cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_IS_CONFIRMED)) == 1); // Lấy isConfirmed
                booking.setCreatedAt(cursor.getLong(cursor.getColumnIndexOrThrow(COLUMN_CREATED_AT)));

                bookings.add(booking);
            } while (cursor.moveToNext());
        }

        if (cursor != null) {
            cursor.close();
        }

        closeDb();
        return bookings;
    }

    // Lấy một booking theo ID
    public BookingModel getBookingById(String id) {
        openReadableDb();
        BookingModel booking = null;
        Cursor cursor = db.query(TABLE_NAME, null, COLUMN_ID + "=?", new String[]{id}, null, null, null);

        if (cursor != null && cursor.moveToFirst()) {
            booking = new BookingModel();
            booking.setId(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_ID)));
            booking.setUid(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_UID)));
            booking.setConfirmed(cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_IS_CONFIRMED)) == 1); // Lấy isConfirmed
            booking.setCreatedAt(cursor.getLong(cursor.getColumnIndexOrThrow(COLUMN_CREATED_AT)));
        }

        if (cursor != null) {
            cursor.close();
        }

        closeDb();
        return booking;
    }

    // Cập nhật booking
    public int updateBooking(BookingModel booking) {
        openWritableDb();
        ContentValues values = new ContentValues();
        values.put(COLUMN_UID, booking.getUid());
        values.put(COLUMN_IS_CONFIRMED, booking.isConfirmed() ? 1 : 0); // Cập nhật isConfirmed
        values.put(COLUMN_CREATED_AT, booking.getCreatedAt());

        int rowsAffected = db.update(TABLE_NAME, values, COLUMN_ID + "=?", new String[]{booking.getId()});
        closeDb();
        return rowsAffected;
    }

    // Xóa một booking theo ID
    public int deleteBooking(String id) {
        openWritableDb();
        int rowsDeleted = db.delete(TABLE_NAME, COLUMN_ID + "=?", new String[]{id});
        closeDb();
        return rowsDeleted;
    }
}
