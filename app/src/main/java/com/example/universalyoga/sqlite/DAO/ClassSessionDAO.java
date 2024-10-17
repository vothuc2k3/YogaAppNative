package com.example.universalyoga.sqlite.DAO;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.universalyoga.models.ClassSessionModel;
import com.example.universalyoga.sqlite.AppDatabaseHelper;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class ClassSessionDAO {

    public static final String TABLE_CLASS_SESSION = "class_sessions";
    public static final String COLUMN_SESSION_ID = "id";
    public static final String COLUMN_CLASS_ID = "classId";
    public static final String COLUMN_INSTRUCTOR_ID = "instructorId";
    public static final String COLUMN_DATE = "date";
    public static final String COLUMN_START_TIME = "startTime";  // Thêm trường startTime
    public static final String COLUMN_END_TIME = "endTime";      // Thêm trường endTime
    public static final String COLUMN_PRICE = "price";
    public static final String COLUMN_ROOM = "room";
    public static final String COLUMN_NOTE = "note";
    public static final String COLUMN_IS_DELETED = "isDeleted";

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

    // Hàm lấy các sessions theo Instructor Id
    public List<ClassSessionModel> getSessionsByInstructorId(String instructorId) {
        List<ClassSessionModel> sessionList = new ArrayList<>();
        openReadableDb();

        Cursor cursor = db.query(TABLE_CLASS_SESSION, null,
                COLUMN_INSTRUCTOR_ID + "=? AND " + COLUMN_IS_DELETED + "=?",
                new String[]{instructorId, "0"}, null, null, null);

        if (cursor != null && cursor.moveToFirst()) {
            do {
                ClassSessionModel session = new ClassSessionModel(
                        cursor.getString(cursor.getColumnIndex(COLUMN_SESSION_ID)),
                        cursor.getString(cursor.getColumnIndex(COLUMN_CLASS_ID)),
                        cursor.getString(cursor.getColumnIndex(COLUMN_INSTRUCTOR_ID)),
                        cursor.getLong(cursor.getColumnIndex(COLUMN_DATE)),
                        cursor.getLong(cursor.getColumnIndex(COLUMN_START_TIME)),
                        cursor.getLong(cursor.getColumnIndex(COLUMN_END_TIME)),
                        cursor.getInt(cursor.getColumnIndex(COLUMN_PRICE)),
                        cursor.getString(cursor.getColumnIndex(COLUMN_ROOM)),
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

    // Hàm thêm một ClassSession mới
    public long addClassSession(ClassSessionModel session) {
        openWritableDb();
        ContentValues values = new ContentValues();
        values.put(COLUMN_SESSION_ID, session.getId());
        values.put(COLUMN_CLASS_ID, session.getClassId());
        values.put(COLUMN_INSTRUCTOR_ID, session.getInstructorId());
        values.put(COLUMN_DATE, session.getDate());
        values.put(COLUMN_START_TIME, session.getStartTime());
        values.put(COLUMN_END_TIME, session.getEndTime());
        values.put(COLUMN_PRICE, session.getPrice());
        values.put(COLUMN_ROOM, session.getRoom());
        values.put(COLUMN_NOTE, session.getNote());

        long result = db.insert(TABLE_CLASS_SESSION, null, values);
        close();
        return result;
    }

    // Hàm lấy ClassSession theo Id
    public ClassSessionModel getClassSessionById(String id) {
        openReadableDb();
        Cursor cursor = db.query(TABLE_CLASS_SESSION, null, COLUMN_SESSION_ID + "=?", new String[]{id}, null, null, null);
        ClassSessionModel session = null;
        if (cursor != null && cursor.moveToFirst()) {
            session = new ClassSessionModel(
                    cursor.getString(cursor.getColumnIndex(COLUMN_SESSION_ID)),
                    cursor.getString(cursor.getColumnIndex(COLUMN_CLASS_ID)),
                    cursor.getString(cursor.getColumnIndex(COLUMN_INSTRUCTOR_ID)),
                    cursor.getLong(cursor.getColumnIndex(COLUMN_DATE)),
                    cursor.getLong(cursor.getColumnIndex(COLUMN_START_TIME)),
                    cursor.getLong(cursor.getColumnIndex(COLUMN_END_TIME)),
                    cursor.getInt(cursor.getColumnIndex(COLUMN_PRICE)),
                    cursor.getString(cursor.getColumnIndex(COLUMN_ROOM)),
                    cursor.getString(cursor.getColumnIndex(COLUMN_NOTE))
            );
        }
        if (cursor != null) {
            cursor.close();
        }
        close();
        return session;
    }

    // Hàm cập nhật một ClassSession
    public int updateClassSession(ClassSessionModel session) {
        openWritableDb();
        ContentValues values = new ContentValues();
        values.put(COLUMN_CLASS_ID, session.getClassId());
        values.put(COLUMN_INSTRUCTOR_ID, session.getInstructorId());
        values.put(COLUMN_DATE, session.getDate());
        values.put(COLUMN_START_TIME, session.getStartTime());
        values.put(COLUMN_END_TIME, session.getEndTime());
        values.put(COLUMN_PRICE, session.getPrice());
        values.put(COLUMN_ROOM, session.getRoom());
        values.put(COLUMN_NOTE, session.getNote());

        int rowsAffected = db.update(TABLE_CLASS_SESSION, values, COLUMN_SESSION_ID + "=?", new String[]{session.getId()});
        close();
        return rowsAffected;
    }

    // Hàm xóa mềm một ClassSession
    public void softDeleteClassSession(String id) {
        openWritableDb();
        ContentValues values = new ContentValues();
        values.put(COLUMN_IS_DELETED, 1);
        db.update(TABLE_CLASS_SESSION, values, COLUMN_SESSION_ID + "=?", new String[]{id});
        close();
    }

    // Hàm xóa tất cả sessions theo ClassId
    public void deleteSessionsByClassId(String classId) {
        openWritableDb();
        db.delete(TABLE_CLASS_SESSION, COLUMN_CLASS_ID + "=?", new String[]{classId});
        close();
    }

    // Hàm lấy tất cả ClassSessions
    public List<ClassSessionModel> getAllClassSessions() {
        List<ClassSessionModel> sessionList = new ArrayList<>();
        openReadableDb();
        Cursor cursor = db.query(TABLE_CLASS_SESSION, null, null, null, null, null, null);

        if (cursor != null && cursor.moveToFirst()) {
            do {
                ClassSessionModel session = new ClassSessionModel(
                        cursor.getString(cursor.getColumnIndex(COLUMN_SESSION_ID)),
                        cursor.getString(cursor.getColumnIndex(COLUMN_CLASS_ID)),
                        cursor.getString(cursor.getColumnIndex(COLUMN_INSTRUCTOR_ID)),
                        cursor.getLong(cursor.getColumnIndex(COLUMN_DATE)),
                        cursor.getLong(cursor.getColumnIndex(COLUMN_START_TIME)),
                        cursor.getLong(cursor.getColumnIndex(COLUMN_END_TIME)),
                        cursor.getInt(cursor.getColumnIndex(COLUMN_PRICE)),
                        cursor.getString(cursor.getColumnIndex(COLUMN_ROOM)),
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

    // Hàm lấy ClassSessions theo ClassId
    public List<ClassSessionModel> getClassSessionsByClassId(String classId) {
        List<ClassSessionModel> sessionList = new ArrayList<>();
        openReadableDb();

        Cursor cursor = db.query(TABLE_CLASS_SESSION, null, COLUMN_CLASS_ID + "=? AND isDeleted=?",
                new String[]{classId, "0"}, null, null, null);

        if (cursor != null && cursor.moveToFirst()) {
            do {
                ClassSessionModel session = new ClassSessionModel(
                        cursor.getString(cursor.getColumnIndex(COLUMN_SESSION_ID)),
                        cursor.getString(cursor.getColumnIndex(COLUMN_CLASS_ID)),
                        cursor.getString(cursor.getColumnIndex(COLUMN_INSTRUCTOR_ID)),
                        cursor.getLong(cursor.getColumnIndex(COLUMN_DATE)),
                        cursor.getLong(cursor.getColumnIndex(COLUMN_START_TIME)),
                        cursor.getLong(cursor.getColumnIndex(COLUMN_END_TIME)),
                        cursor.getInt(cursor.getColumnIndex(COLUMN_PRICE)),
                        cursor.getString(cursor.getColumnIndex(COLUMN_ROOM)),
                        cursor.getString(cursor.getColumnIndex(COLUMN_NOTE))
                );
                sessionList.add(session);
            } while (cursor.moveToNext());
        }
        if (cursor != null) {
            cursor.close();
        }
        close();

        Collections.sort(sessionList, Comparator.comparingLong(ClassSessionModel::getDate));

        return sessionList;
    }
}
