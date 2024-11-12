package com.example.universalyoga.sqlite.DAO;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.universalyoga.models.BookingSessionModel;
import com.example.universalyoga.sqlite.AppDatabaseHelper;

import java.util.ArrayList;
import java.util.List;

public class BookingSessionDAO {

    private static final String TABLE_NAME = "booking_sessions";
    private static final String COLUMN_BOOKING_ID = "bookingId";
    private static final String COLUMN_SESSION_ID = "sessionId";

    private AppDatabaseHelper dbHelper;
    private SQLiteDatabase db;

    public BookingSessionDAO(Context context) {
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

    public List<BookingSessionModel> getAllBookingSessions() {
        List<BookingSessionModel> bookingSessions = new ArrayList<>();
        openReadableDb();
        Cursor cursor = db.query(TABLE_NAME, null, null, null, null, null, null);
        if (cursor != null && cursor.moveToFirst()) {
            do {
                String bookingId = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_BOOKING_ID));
                String sessionId = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_SESSION_ID));

                BookingSessionModel bookingSession = new BookingSessionModel(bookingId, sessionId);
                bookingSessions.add(bookingSession);
            } while (cursor.moveToNext());
        }
        if (cursor != null) {
            cursor.close();
        }
        closeDb();
        return bookingSessions;
    }

    public void addBookingSession(String bookingId, String sessionId) {
        openWritableDb();
        ContentValues values = new ContentValues();
        values.put("bookingId", bookingId);
        values.put("sessionId", sessionId);
        Cursor cursor = db.query("booking_sessions", null, "bookingId=? AND sessionId=?",
                new String[]{bookingId, sessionId}, null, null, null);
        if (cursor.getCount() > 0) {
            cursor.close();
            closeDb();
            return;
        }
        cursor.close();
        db.insertWithOnConflict(
                "booking_sessions",
                null,
                values,
                SQLiteDatabase.CONFLICT_REPLACE);
        closeDb();

    }

    public List<String> getSessionIdsByBookingId(String bookingId) {
        List<String> sessionIds = new ArrayList<>();
        openReadableDb();
        Cursor cursor = db.query(TABLE_NAME, new String[]{COLUMN_SESSION_ID}, COLUMN_BOOKING_ID + "=?",
                new String[]{bookingId}, null, null, null);
        if (cursor != null && cursor.moveToFirst()) {
            do {
                sessionIds.add(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_SESSION_ID)));
            } while (cursor.moveToNext());
        }
        if (cursor != null) {
            cursor.close();
        }
        closeDb();
        return sessionIds;
    }

    public void resetTable() {
        openWritableDb();
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        db.execSQL("CREATE TABLE booking_sessions ("
                + "bookingId TEXT, "
                + "sessionId TEXT, "
                + "PRIMARY KEY (bookingId, sessionId), "
                + "FOREIGN KEY (bookingId) REFERENCES bookings(id), "
                + "FOREIGN KEY (sessionId) REFERENCES class_sessions(id))");
        closeDb();
    }
}
