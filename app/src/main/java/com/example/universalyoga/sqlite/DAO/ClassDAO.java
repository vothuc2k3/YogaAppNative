package com.example.universalyoga.sqlite.DAO;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.universalyoga.models.ClassModel;
import com.example.universalyoga.sqlite.AppDatabaseHelper;

import java.sql.Time;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ClassDAO {

    public static final String TABLE_CLASS = "classes";
    public static final String COLUMN_CLASS_ID = "id";
    public static final String COLUMN_INSTRUCTOR_UID = "instructorUid";
    public static final String COLUMN_CAPACITY = "capacity";
    public static final String COLUMN_DURATION = "duration";
    public static final String COLUMN_PRICE = "price";
    public static final String COLUMN_TYPE = "type";
    public static final String COLUMN_DESCRIPTION = "description";
    public static final String COLUMN_STATUS = "status";
    public static final String COLUMN_CREATED_AT = "createdAt";
    public static final String COLUMN_START_AT = "startAt";
    public static final String COLUMN_END_AT = "endAt";
    public static final String COLUMN_DAY_OF_WEEK = "dayOfWeek";  // Thêm dayOfWeek
    public static final String COLUMN_TIME_START = "timeStart";   // Thêm timeStart

    private final SQLiteOpenHelper dbHelper;
    private SQLiteDatabase db;

    public ClassDAO(Context context) {
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

    private ClassModel populateClassModel(Cursor cursor) {
        ClassModel classModel = new ClassModel();
        classModel.setId(cursor.getString(cursor.getColumnIndex(COLUMN_CLASS_ID)));
        classModel.setInstructorUid(cursor.getString(cursor.getColumnIndex(COLUMN_INSTRUCTOR_UID)));
        classModel.setCapacity(cursor.getInt(cursor.getColumnIndex(COLUMN_CAPACITY)));
        classModel.setDuration(cursor.getInt(cursor.getColumnIndex(COLUMN_DURATION)));
        classModel.setPrice(cursor.getInt(cursor.getColumnIndex(COLUMN_PRICE)));
        classModel.setType(cursor.getString(cursor.getColumnIndex(COLUMN_TYPE)));
        classModel.setDescription(cursor.getString(cursor.getColumnIndex(COLUMN_DESCRIPTION)));
        classModel.setStatus(cursor.getString(cursor.getColumnIndex(COLUMN_STATUS)));

        classModel.setDayOfWeek(cursor.getString(cursor.getColumnIndex(COLUMN_DAY_OF_WEEK)));

        // Xử lý timeStart
        long timeStartMillis = cursor.getLong(cursor.getColumnIndex(COLUMN_TIME_START));
        if (timeStartMillis > 0) {
            classModel.setTimeStart(new Time(timeStartMillis));
        }

        // Xử lý các trường thời gian (createdAt, startAt, endAt)
        classModel.setCreatedAt(cursor.getLong(cursor.getColumnIndex(COLUMN_CREATED_AT)));
        classModel.setStartAt(cursor.getLong(cursor.getColumnIndex(COLUMN_START_AT)));
        classModel.setEndAt(cursor.getLong(cursor.getColumnIndex(COLUMN_END_AT)));

        return classModel;
    }

    // Thêm class mới
    public long addClass(ClassModel classModel) {
        openWritableDb();
        ContentValues values = new ContentValues();
        values.put(COLUMN_CLASS_ID, classModel.getId());
        values.put(COLUMN_INSTRUCTOR_UID, classModel.getInstructorUid());
        values.put(COLUMN_CAPACITY, classModel.getCapacity());
        values.put(COLUMN_DURATION, classModel.getDuration());
        values.put(COLUMN_PRICE, classModel.getPrice());
        values.put(COLUMN_TYPE, classModel.getType());
        values.put(COLUMN_DESCRIPTION, classModel.getDescription());
        values.put(COLUMN_STATUS, classModel.getStatus());
        values.put(COLUMN_DAY_OF_WEEK, classModel.getDayOfWeek());

        // Lưu các giá trị thời gian dạng long
        values.put(COLUMN_CREATED_AT, classModel.getCreatedAt());
        values.put(COLUMN_START_AT, classModel.getStartAt());
        values.put(COLUMN_END_AT, classModel.getEndAt());

        if (classModel.getTimeStart() != null) {
            values.put(COLUMN_TIME_START, classModel.getTimeStart().getTime());
        }

        long result = db.insert(TABLE_CLASS, null, values);
        close();
        return result;
    }

    // Lấy lớp học theo ID
    public ClassModel getClassById(String classId) {
        openReadableDb();
        Cursor cursor = db.query(TABLE_CLASS, null, COLUMN_CLASS_ID + "=?", new String[]{classId}, null, null, null);
        ClassModel classModel = null;
        if (cursor != null && cursor.moveToFirst()) {
            classModel = populateClassModel(cursor);
        }
        if (cursor != null) {
            cursor.close();
        }
        close();
        return classModel;
    }

    // Cập nhật class
    public int updateClass(ClassModel classModel) {
        openWritableDb();
        ContentValues values = new ContentValues();
        values.put(COLUMN_INSTRUCTOR_UID, classModel.getInstructorUid());
        values.put(COLUMN_CAPACITY, classModel.getCapacity());
        values.put(COLUMN_DURATION, classModel.getDuration());
        values.put(COLUMN_PRICE, classModel.getPrice());
        values.put(COLUMN_TYPE, classModel.getType());
        values.put(COLUMN_DESCRIPTION, classModel.getDescription());
        values.put(COLUMN_STATUS, classModel.getStatus());
        values.put(COLUMN_DAY_OF_WEEK, classModel.getDayOfWeek());

        // Cập nhật giá trị thời gian dạng long
        values.put(COLUMN_CREATED_AT, classModel.getCreatedAt());
        values.put(COLUMN_START_AT, classModel.getStartAt());
        values.put(COLUMN_END_AT, classModel.getEndAt());

        if (classModel.getTimeStart() != null) {
            values.put(COLUMN_TIME_START, classModel.getTimeStart().getTime());
        }

        int rowsAffected = db.update(TABLE_CLASS, values, COLUMN_CLASS_ID + "=?", new String[]{classModel.getId()});
        close();
        return rowsAffected;
    }

    // Lấy tất cả các lớp học
    public List<ClassModel> getAllClasses() {
        List<ClassModel> classList = new ArrayList<>();
        openReadableDb();
        Cursor cursor = db.query(TABLE_CLASS, null, null, null, null, null, null);

        if (cursor != null && cursor.moveToFirst()) {
            do {
                classList.add(populateClassModel(cursor));
            } while (cursor.moveToNext());
        }

        if (cursor != null) {
            cursor.close();
        }
        close();
        return classList;
    }

    // Tìm kiếm lớp học theo tên
    public List<ClassModel> searchClassesByName(String query) {
        List<ClassModel> classList = new ArrayList<>();
        openReadableDb();
        String sqlQuery = "SELECT * FROM " + TABLE_CLASS + " WHERE " + COLUMN_TYPE + " LIKE ?";
        Cursor cursor = db.rawQuery(sqlQuery, new String[]{"%" + query + "%"});

        if (cursor.moveToFirst()) {
            do {
                classList.add(populateClassModel(cursor));
            } while (cursor.moveToNext());
        }

        if (cursor != null) {
            cursor.close();
        }
        close();
        return classList;
    }
}
