package com.example.universalyoga.sqlite;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class AppDatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "yoga_app.db";
    private static final int DATABASE_VERSION = 1;

    // Table for Users
    private static final String CREATE_TABLE_USERS = "CREATE TABLE users ("
            + "uid TEXT PRIMARY KEY, "
            + "name TEXT, "
            + "email TEXT, "
            + "phoneNumber TEXT, "
            + "profileImage TEXT, "
            + "role TEXT)";

    // Table for Classes
    private static final String CREATE_TABLE_CLASS = "CREATE TABLE classes ("
            + "id TEXT PRIMARY KEY, "
            + "instructorUid TEXT, "
            + "dayOfWeek TEXT, "
            + "timeStart TEXT, "
            + "capacity INTEGER, "
            + "duration INTEGER, "
            + "sessionCount INTEGER, "
            + "type TEXT, "
            + "status TEXT, "
            + "description TEXT, "
            + "createdAt INTEGER, "
            + "startAt INTEGER, "
            + "endAt INTEGER, "
            + "lastSyncTime INTEGER, "
            + "isDeleted INTEGER DEFAULT 0)";

    // Table for Class Sessions
    private static final String CREATE_TABLE_CLASS_SESSION = "CREATE TABLE class_sessions ("
            + "id TEXT PRIMARY KEY, "
            + "sessionNumber INTEGER, "
            + "classId TEXT, "
            + "instructorId TEXT, "
            + "date INTEGER, "
            + "price INTEGER, "
            + "room TEXT, "
            + "note TEXT, "
            + "lastSyncTime INTEGER, "
            + "isDeleted INTEGER DEFAULT 0, "
            + "FOREIGN KEY (classId) REFERENCES classes(id))";


    // Table for Bookings
    private static final String CREATE_TABLE_BOOKINGS = "CREATE TABLE bookings ("
            + "id TEXT PRIMARY KEY, "
            + "createdAt INTEGER, "  // Long for createdAt
            + "totalPrice INTEGER, "
            + "uid TEXT, "
            + "FOREIGN KEY (uid) REFERENCES users(uid))";

    // Table for Booking Sessions (for storing sessionIds related to a booking)
    private static final String CREATE_TABLE_BOOKING_SESSIONS = "CREATE TABLE booking_sessions ("
            + "bookingId TEXT, "
            + "sessionId TEXT, "
            + "PRIMARY KEY (bookingId, sessionId), "
            + "FOREIGN KEY (bookingId) REFERENCES bookings(id), "
            + "FOREIGN KEY (sessionId) REFERENCES class_sessions(id))";

    public AppDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE_USERS);
        db.execSQL(CREATE_TABLE_CLASS);
        db.execSQL(CREATE_TABLE_CLASS_SESSION);
        db.execSQL(CREATE_TABLE_BOOKINGS);
        db.execSQL(CREATE_TABLE_BOOKING_SESSIONS);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS users");
        db.execSQL("DROP TABLE IF EXISTS classes");
        db.execSQL("DROP TABLE IF EXISTS class_sessions");
        db.execSQL("DROP TABLE IF EXISTS bookings");
        db.execSQL("DROP TABLE IF EXISTS booking_sessions");
        onCreate(db);
    }
}
