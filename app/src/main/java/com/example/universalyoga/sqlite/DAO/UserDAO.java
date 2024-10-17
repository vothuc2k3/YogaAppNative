package com.example.universalyoga.sqlite.DAO;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.universalyoga.models.UserModel;
import com.example.universalyoga.sqlite.AppDatabaseHelper;

import java.util.ArrayList;
import java.util.List;

public class UserDAO {

    public static final String TABLE_USER = "users";
    public static final String COLUMN_USER_ID = "uid";
    public static final String COLUMN_USER_NAME = "name";
    public static final String COLUMN_USER_EMAIL = "email";
    public static final String COLUMN_USER_PHONE = "phoneNumber";
    public static final String COLUMN_USER_PROFILE_IMAGE = "profileImage";
    public static final String COLUMN_USER_ROLE = "role";

    private final SQLiteOpenHelper dbHelper;
    private SQLiteDatabase db;

    public UserDAO(Context context) {
        dbHelper = new AppDatabaseHelper(context);
    }

    private void openWritableDb() {
        if (db == null || !db.isOpen()) {
            db = dbHelper.getWritableDatabase();
        }
    }

    private void openReadableDb() {
        if (db == null || !db.isOpen()) {
            db = dbHelper.getReadableDatabase();
        }
    }

    public void close() {
        if (db != null && db.isOpen()) {
            db.close();
        }
    }

    public long addUser(UserModel user) {
        openWritableDb();
        ContentValues values = new ContentValues();
        values.put(COLUMN_USER_ID, user.getUid());
        values.put(COLUMN_USER_NAME, user.getName());
        values.put(COLUMN_USER_EMAIL, user.getEmail());
        values.put(COLUMN_USER_PHONE, user.getPhoneNumber());
        values.put(COLUMN_USER_PROFILE_IMAGE, user.getProfileImage());
        values.put(COLUMN_USER_ROLE, user.getRole());

        long result = db.insert(TABLE_USER, null, values);
        close();
        return result;
    }

    public UserModel getUserByUid(String uid) {
        openReadableDb();
        Cursor cursor = null;
        UserModel user = null;
        try {
            cursor = db.query(TABLE_USER, null, COLUMN_USER_ID + "=?", new String[]{uid}, null, null, null);

            if (cursor != null && cursor.moveToFirst()) {
                user = new UserModel();
                user.setUid(cursor.getString(cursor.getColumnIndex(COLUMN_USER_ID)));
                user.setName(cursor.getString(cursor.getColumnIndex(COLUMN_USER_NAME)));
                user.setEmail(cursor.getString(cursor.getColumnIndex(COLUMN_USER_EMAIL)));
                user.setPhoneNumber(cursor.getString(cursor.getColumnIndex(COLUMN_USER_PHONE)));
                user.setProfileImage(cursor.getString(cursor.getColumnIndex(COLUMN_USER_PROFILE_IMAGE)));
                user.setRole(cursor.getString(cursor.getColumnIndex(COLUMN_USER_ROLE)));
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null && !cursor.isClosed()) {
                cursor.close();
            }
            close();
        }
        return user;
    }


    public int updateUser(UserModel user) {
        openWritableDb();
        ContentValues values = new ContentValues();
        values.put(COLUMN_USER_NAME, user.getName());
        values.put(COLUMN_USER_EMAIL, user.getEmail());
        values.put(COLUMN_USER_PHONE, user.getPhoneNumber());
        values.put(COLUMN_USER_PROFILE_IMAGE, user.getProfileImage());
        values.put(COLUMN_USER_ROLE, user.getRole());

        int rowsAffected = db.update(TABLE_USER, values, COLUMN_USER_ID + "=?", new String[]{user.getUid()});
        close();
        return rowsAffected;
    }

    public void deleteUser(String uid) {
        openWritableDb();
        db.delete(TABLE_USER, COLUMN_USER_ID + "=?", new String[]{uid});
        close();
    }

    public List<UserModel> getAllUsers() {
        List<UserModel> userList = new ArrayList<>();

        openReadableDb();

        Cursor cursor = db.query(TABLE_USER, null, null, null, null, null, null);

        if (cursor != null && cursor.moveToFirst()) {
            do {
                UserModel user = new UserModel();
                user.setUid(cursor.getString(cursor.getColumnIndex(COLUMN_USER_ID)));
                user.setName(cursor.getString(cursor.getColumnIndex(COLUMN_USER_NAME)));
                user.setEmail(cursor.getString(cursor.getColumnIndex(COLUMN_USER_EMAIL)));
                user.setPhoneNumber(cursor.getString(cursor.getColumnIndex(COLUMN_USER_PHONE)));
                user.setProfileImage(cursor.getString(cursor.getColumnIndex(COLUMN_USER_PROFILE_IMAGE)));
                user.setRole(cursor.getString(cursor.getColumnIndex(COLUMN_USER_ROLE)));

                userList.add(user);
            } while (cursor.moveToNext());
        }

        if (cursor != null) {
            cursor.close();
        }
        close();

        return userList;
    }

    public List<UserModel> searchUsersByName(String nameQuery) {
        openReadableDb();
        List<UserModel> userList = new ArrayList<>();
        String query = "SELECT * FROM " + TABLE_USER + " WHERE " + COLUMN_USER_NAME + " LIKE ?";
        Cursor cursor = db.rawQuery(query, new String[]{"%" + nameQuery + "%"});

        if (cursor.moveToFirst()) {
            do {
                UserModel user = new UserModel();
                user.setUid(cursor.getString(cursor.getColumnIndex(COLUMN_USER_ID)));
                user.setName(cursor.getString(cursor.getColumnIndex(COLUMN_USER_NAME)));
                user.setEmail(cursor.getString(cursor.getColumnIndex(COLUMN_USER_EMAIL)));
                user.setPhoneNumber(cursor.getString(cursor.getColumnIndex(COLUMN_USER_PHONE)));
                user.setProfileImage(cursor.getString(cursor.getColumnIndex(COLUMN_USER_PROFILE_IMAGE)));
                user.setRole(cursor.getString(cursor.getColumnIndex(COLUMN_USER_ROLE)));
                userList.add(user);
            } while (cursor.moveToNext());
        }
        cursor.close();
        close();
        return userList;
    }

    public List<UserModel> getAllInstructors() {
        openReadableDb();
        List<UserModel> instructorList = new ArrayList<>();
        String query = "SELECT * FROM " + TABLE_USER + " WHERE " + COLUMN_USER_ROLE + " = ?";
        Cursor cursor = db.rawQuery(query, new String[]{"instructor"});

        if (cursor.moveToFirst()) {
            do {
                UserModel instructor = new UserModel();
                instructor.setUid(cursor.getString(cursor.getColumnIndex(COLUMN_USER_ID)));
                instructor.setName(cursor.getString(cursor.getColumnIndex(COLUMN_USER_NAME)));
                instructor.setEmail(cursor.getString(cursor.getColumnIndex(COLUMN_USER_EMAIL)));
                instructor.setPhoneNumber(cursor.getString(cursor.getColumnIndex(COLUMN_USER_PHONE)));
                instructor.setProfileImage(cursor.getString(cursor.getColumnIndex(COLUMN_USER_PROFILE_IMAGE)));
                instructor.setRole(cursor.getString(cursor.getColumnIndex(COLUMN_USER_ROLE)));
                instructorList.add(instructor);
            } while (cursor.moveToNext());
        }

        if (cursor != null) {
            cursor.close();
        }
        close();
        return instructorList;
    }
}
