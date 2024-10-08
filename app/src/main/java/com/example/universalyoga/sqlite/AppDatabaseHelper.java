package com.example.universalyoga.sqlite;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class AppDatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "yoga_app.db";
    private static final int DATABASE_VERSION = 1;

    private static final String CREATE_TABLE_USERS = "CREATE TABLE users ("
            + "uid TEXT PRIMARY KEY, "
            + "name TEXT, "
            + "email TEXT, "
            + "phoneNumber TEXT, "
            + "profileImage TEXT, "
            + "role TEXT)";

    private static final String CREATE_TABLE_CLASS = "CREATE TABLE classes ("
            + "id TEXT PRIMARY KEY, "
            + "creatorUid TEXT, "
            + "instructorUid TEXT, "
            + "capacity INTEGER, "
            + "duration INTEGER, "
            + "price REAL, "
            + "type TEXT, "
            + "status TEXT, "
            + "description TEXT, "
            + "createdAt TEXT, "
            + "startAt TEXT, "
            + "endAt TEXT)";

    public AppDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE_USERS);
        db.execSQL(CREATE_TABLE_CLASS);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS users");
        db.execSQL("DROP TABLE IF EXISTS classes");
        onCreate(db);
    }
}
