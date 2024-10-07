package com.example.universalyoga.sqlite;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class AppDatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "yoga_app.db";
    private static final int DATABASE_VERSION = 1;

    // Define tables
    public static final String TABLE_USER = "users";

    // Define columns for User table
    public static final String COLUMN_USER_ID = "uid";
    public static final String COLUMN_USER_NAME = "name";
    public static final String COLUMN_USER_EMAIL = "email";
    public static final String COLUMN_USER_PHONE = "phoneNumber";
    public static final String COLUMN_USER_PROFILE_IMAGE = "profileImage";
    public static final String COLUMN_USER_ROLE = "role";

    public AppDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USER);
        onCreate(db);
    }
    @Override
    public void onCreate(SQLiteDatabase db) {
    }
}

