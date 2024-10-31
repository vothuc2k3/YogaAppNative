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
    private static final String COLUMN_UID = "uid";
    private static final String COLUMN_STATUS = "status";
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

    // Add a booking with status
    public long addBooking(BookingModel booking) {
        openWritableDb();
        ContentValues values = new ContentValues();
        values.put(COLUMN_ID, booking.getId());
        values.put(COLUMN_UID, booking.getUid());
        values.put(COLUMN_STATUS, booking.getStatus());  // Save status (pending, confirmed, rejected)
        values.put(COLUMN_CREATED_AT, booking.getCreatedAt());

        long result = db.insertWithOnConflict(TABLE_NAME, null, values, SQLiteDatabase.CONFLICT_REPLACE);
        closeDb();
        return result;
    }

    // Get all bookings with status
    public List<BookingModel> getAllBookings() {
        List<BookingModel> bookings = new ArrayList<>();
        openReadableDb();
        Cursor cursor = db.query(TABLE_NAME, null, null, null, null, null, COLUMN_CREATED_AT + " DESC");

        if (cursor != null && cursor.moveToFirst()) {
            do {
                BookingModel booking = new BookingModel();
                booking.setId(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_ID)));
                booking.setUid(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_UID)));
                booking.setStatus(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_STATUS)));  // Get status
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

    public int updateBooking(BookingModel booking) {
        openWritableDb();
        ContentValues values = new ContentValues();
        values.put(COLUMN_UID, booking.getUid());
        values.put(COLUMN_STATUS, booking.getStatus());  // Update status
        values.put(COLUMN_CREATED_AT, booking.getCreatedAt());

        int rowsAffected = db.update(TABLE_NAME, values, COLUMN_ID + "=?", new String[]{booking.getId()});
        closeDb();
        return rowsAffected;
    }

    public void resetTable() {
        openWritableDb();
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        db.execSQL("CREATE TABLE bookings ("
            + "id TEXT PRIMARY KEY, "
            + "createdAt INTEGER, "
            + "status TEXT, "
            + "uid TEXT, "
            + "FOREIGN KEY (uid) REFERENCES users(uid))");
        closeDb();
    }
}
