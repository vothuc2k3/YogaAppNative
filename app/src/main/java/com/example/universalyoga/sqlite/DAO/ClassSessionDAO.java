package com.example.universalyoga.sqlite.DAO;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.universalyoga.models.ClassSessionModel;
import com.example.universalyoga.sqlite.AppDatabaseHelper;
import com.example.universalyoga.utils.Util;

import java.util.ArrayList;
import java.util.List;

public class ClassSessionDAO {

    public static final String TABLE_CLASS_SESSION = "class_sessions";
    public static final String COLUMN_SESSION_ID = "id";
    public static final String COLUMN_CLASS_ID = "classId";
    public static final String COLUMN_START_AT = "startAt";
    public static final String COLUMN_END_AT = "endAt";
    public static final String COLUMN_NOTE = "note";

    private final SQLiteOpenHelper dbHelper;
    private SQLiteDatabase db;

    public ClassSessionDAO(Context context) {
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

    // Thêm một ClassSession mới
    public long addClassSession(ClassSessionModel session) {
        openWritableDb();
        ContentValues values = new ContentValues();
        values.put(COLUMN_SESSION_ID, session.getId());
        values.put(COLUMN_CLASS_ID, session.getClassId());
        values.put(COLUMN_START_AT, session.getStartAt().toDate().toString());  // Lưu thời gian dạng chuỗi
        values.put(COLUMN_END_AT, session.getEndAt().toDate().toString());
        values.put(COLUMN_NOTE, session.getNote());

        long result = db.insert(TABLE_CLASS_SESSION, null, values);
        close();
        return result;
    }

    // Lấy một ClassSession theo ID
    public ClassSessionModel getClassSessionById(String id) {
        openReadableDb();
        Cursor cursor = db.query(TABLE_CLASS_SESSION, null, COLUMN_SESSION_ID + "=?", new String[]{id}, null, null, null);
        ClassSessionModel session = null;
        if (cursor != null && cursor.moveToFirst()) {
            session = new ClassSessionModel(
                    cursor.getString(cursor.getColumnIndex(COLUMN_SESSION_ID)),
                    cursor.getString(cursor.getColumnIndex(COLUMN_CLASS_ID)),
                    Util.convertStringToTimestamp(cursor.getString(cursor.getColumnIndex(COLUMN_START_AT))),
                    Util.convertStringToTimestamp(cursor.getString(cursor.getColumnIndex(COLUMN_END_AT))),
                    cursor.getString(cursor.getColumnIndex(COLUMN_NOTE))
            );
        }
        if (cursor != null) {
            cursor.close();
        }
        close();
        return session;
    }

    // Cập nhật một ClassSession
    public int updateClassSession(ClassSessionModel session) {
        openWritableDb();
        ContentValues values = new ContentValues();
        values.put(COLUMN_CLASS_ID, session.getClassId());
        values.put(COLUMN_START_AT, session.getStartAt().toDate().toString());
        values.put(COLUMN_END_AT, session.getEndAt().toDate().toString());
        values.put(COLUMN_NOTE, session.getNote());

        int rowsAffected = db.update(TABLE_CLASS_SESSION, values, COLUMN_SESSION_ID + "=?", new String[]{session.getId()});
        close();
        return rowsAffected;
    }

    // Xóa một ClassSession
    public void deleteClassSession(String id) {
        openWritableDb();
        db.delete(TABLE_CLASS_SESSION, COLUMN_SESSION_ID + "=?", new String[]{id});
        close();
    }

    // Lấy tất cả ClassSessions
    public List<ClassSessionModel> getAllClassSessions() {
        List<ClassSessionModel> sessionList = new ArrayList<>();
        openReadableDb();
        Cursor cursor = db.query(TABLE_CLASS_SESSION, null, null, null, null, null, null);

        if (cursor != null && cursor.moveToFirst()) {
            do {
                ClassSessionModel session = new ClassSessionModel(
                        cursor.getString(cursor.getColumnIndex(COLUMN_SESSION_ID)),
                        cursor.getString(cursor.getColumnIndex(COLUMN_CLASS_ID)),
                        Util.convertStringToTimestamp(cursor.getString(cursor.getColumnIndex(COLUMN_START_AT))),
                        Util.convertStringToTimestamp(cursor.getString(cursor.getColumnIndex(COLUMN_END_AT))),
                        cursor.getString(cursor.getColumnIndex(COLUMN_NOTE))
                );
                sessionList.add(session);
            } while (cursor.moveToNext());
        }

        if (cursor != null) {
            cursor.close();
        }
        close();
        return sessionList;
    }
}
