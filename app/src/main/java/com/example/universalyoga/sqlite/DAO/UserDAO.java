package com.example.universalyoga.sqlite.DAO;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.universalyoga.models.UserModel;
import com.example.universalyoga.sqlite.AppDatabaseHelper;

public class UserDAO {
    public static final String TABLE_USER = "users";

    public static final String COLUMN_USER_ID = "uid";
    public static final String COLUMN_USER_NAME = "name";
    public static final String COLUMN_USER_EMAIL = "email";
    public static final String COLUMN_USER_PHONE = "phoneNumber";
    public static final String COLUMN_USER_PROFILE_IMAGE = "profileImage";
    public static final String COLUMN_USER_ROLE = "role";

    private SQLiteDatabase db;
    private SQLiteOpenHelper dbHelper;

    public UserDAO(Context context) {
        dbHelper = new AppDatabaseHelper(context);
        db = dbHelper.getWritableDatabase();
    }

    public long addUser(UserModel user) {
        ContentValues values = new ContentValues();
        values.put(COLUMN_USER_ID, user.getUid());
        values.put(COLUMN_USER_NAME, user.getName());
        values.put(COLUMN_USER_EMAIL, user.getEmail());
        values.put(COLUMN_USER_PHONE, user.getPhoneNumber());
        values.put(COLUMN_USER_PROFILE_IMAGE, user.getProfileImage());
        values.put(COLUMN_USER_ROLE, user.getRole());

        return db.insert(TABLE_USER, null, values);
    }

    public UserModel getUserByUid(String uid) {
        Cursor cursor = db.query(TABLE_USER, null, COLUMN_USER_ID + "=?", new String[]{uid}, null, null, null);
        if (cursor != null && cursor.moveToFirst()) {
            UserModel user = new UserModel();
            user.setUid(cursor.getString(cursor.getColumnIndex(COLUMN_USER_ID)));
            user.setName(cursor.getString(cursor.getColumnIndex(COLUMN_USER_NAME)));
            user.setEmail(cursor.getString(cursor.getColumnIndex(COLUMN_USER_EMAIL)));
            user.setPhoneNumber(cursor.getString(cursor.getColumnIndex(COLUMN_USER_PHONE)));
            user.setProfileImage(cursor.getString(cursor.getColumnIndex(COLUMN_USER_PROFILE_IMAGE)));
            user.setRole(cursor.getString(cursor.getColumnIndex(COLUMN_USER_ROLE)));
            cursor.close();
            return user;
        }
        return null;
    }

    public int updateUser(UserModel user) {
        ContentValues values = new ContentValues();
        values.put(COLUMN_USER_NAME, user.getName());
        values.put(COLUMN_USER_EMAIL, user.getEmail());
        values.put(COLUMN_USER_PHONE, user.getPhoneNumber());
        values.put(COLUMN_USER_PROFILE_IMAGE, user.getProfileImage());
        values.put(COLUMN_USER_ROLE, user.getRole());

        return db.update(TABLE_USER, values, COLUMN_USER_ID + "=?", new String[]{user.getUid()});
    }

    public void deleteUser(String uid) {
        db.delete(TABLE_USER, COLUMN_USER_ID + "=?", new String[]{uid});
    }
}
