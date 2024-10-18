package com.example.universalyoga.sqlite.DAO;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.example.universalyoga.models.ClassModel;
import com.example.universalyoga.models.ClassSessionModel;
import com.example.universalyoga.sqlite.AppDatabaseHelper;

import java.sql.Time;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ClassDAO {

    public static final String TABLE_CLASS = "classes";

    public static final String COLUMN_CLASS_ID = "id";
    public static final String COLUMN_CAPACITY = "capacity";
    public static final String COLUMN_DURATION = "duration";
    public static final String COLUMN_SESSION_COUNT = "sessionCount";
    public static final String COLUMN_TYPE = "type";
    public static final String COLUMN_DESCRIPTION = "description";
    public static final String COLUMN_STATUS = "status";
    public static final String COLUMN_CREATED_AT = "createdAt";
    public static final String COLUMN_START_AT = "startAt";
    public static final String COLUMN_END_AT = "endAt";
    public static final String COLUMN_DAY_OF_WEEK = "dayOfWeek";
    public static final String COLUMN_TIME_START = "timeStart";
    final public static String COLUMN_IS_DELETED = "isDeleted";

    private final ClassSessionDAO classSessionDAO;
    private final SQLiteOpenHelper dbHelper;
    private SQLiteDatabase db;

    public ClassDAO(Context context) {
        dbHelper = new AppDatabaseHelper(context);
        classSessionDAO = new ClassSessionDAO(context);
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

    public List<ClassModel> getClassesWithSessionsByInstructor(String instructorId) {
        List<ClassModel> classList = new ArrayList<>();

        List<ClassSessionModel> instructorSessions = classSessionDAO.getSessionsByInstructorId(instructorId);

        Set<String> classIds = new HashSet<>();
        for (ClassSessionModel session : instructorSessions) {
            classIds.add(session.getClassId());
        }

        for (String classId : classIds) {
            ClassModel classModel = getClassById(classId);
            if (classModel != null) {
                classList.add(classModel);
            }
        }

        return classList;
    }


    public List<ClassModel> searchClassesByNameAndDay(String query, String dayOfWeek) {
        List<ClassModel> classList = new ArrayList<>();
        openReadableDb();
        String sqlQuery = "SELECT * FROM " + TABLE_CLASS + " WHERE " + COLUMN_TYPE + " LIKE ? AND " + COLUMN_DAY_OF_WEEK + "=? AND " + COLUMN_IS_DELETED + "=?";
        Cursor cursor = db.rawQuery(sqlQuery, new String[]{"%" + query + "%", dayOfWeek, "0"});
        if (cursor.moveToFirst()) {
            do {
                classList.add(populateClassModel(cursor));
            } while (cursor.moveToNext());
        }
        cursor.close();
        close();
        return classList;
    }

    private ClassModel populateClassModel(Cursor cursor) {
        ClassModel classModel = new ClassModel();
        classModel.setId(cursor.getString(cursor.getColumnIndex(COLUMN_CLASS_ID)));
        classModel.setCapacity(cursor.getInt(cursor.getColumnIndex(COLUMN_CAPACITY)));
        classModel.setDuration(cursor.getInt(cursor.getColumnIndex(COLUMN_DURATION)));
        classModel.setSessionCount(cursor.getInt(cursor.getColumnIndex(COLUMN_SESSION_COUNT)));
        classModel.setType(cursor.getString(cursor.getColumnIndex(COLUMN_TYPE)));
        classModel.setDescription(cursor.getString(cursor.getColumnIndex(COLUMN_DESCRIPTION)));
        classModel.setStatus(cursor.getString(cursor.getColumnIndex(COLUMN_STATUS)));
        classModel.setDayOfWeek(cursor.getString(cursor.getColumnIndex(COLUMN_DAY_OF_WEEK)));
        classModel.setTimeStart(new Time(cursor.getLong(cursor.getColumnIndex(COLUMN_TIME_START))));
        classModel.setCreatedAt(cursor.getLong(cursor.getColumnIndex(COLUMN_CREATED_AT)));
        classModel.setStartAt(cursor.getLong(cursor.getColumnIndex(COLUMN_START_AT)));
        classModel.setEndAt(cursor.getLong(cursor.getColumnIndex(COLUMN_END_AT)));
        classModel.setDeleted(cursor.getInt(cursor.getColumnIndex(COLUMN_IS_DELETED)) == 1);

        return classModel;
    }

    public long addClass(ClassModel classModel) {
        openWritableDb();
        ContentValues values = new ContentValues();

        values.put(COLUMN_CLASS_ID, classModel.getId());
        values.put(COLUMN_CAPACITY, classModel.getCapacity());
        values.put(COLUMN_DURATION, classModel.getDuration());
        values.put(COLUMN_SESSION_COUNT, classModel.getSessionCount());
        values.put(COLUMN_TYPE, classModel.getType());
        values.put(COLUMN_DESCRIPTION, classModel.getDescription());
        values.put(COLUMN_STATUS, classModel.getStatus());
        values.put(COLUMN_DAY_OF_WEEK, classModel.getDayOfWeek());

        values.put(COLUMN_CREATED_AT, classModel.getCreatedAt());
        values.put(COLUMN_START_AT, classModel.getStartAt());
        values.put(COLUMN_END_AT, classModel.getEndAt());

        if (classModel.getTimeStart() != null) {
            values.put(COLUMN_TIME_START, classModel.getTimeStart().getTime());
        }

        values.put(COLUMN_IS_DELETED, classModel.isDeleted());

        long result = db.insertWithOnConflict(TABLE_CLASS, null, values, SQLiteDatabase.CONFLICT_REPLACE);

        close();
        return result;
    }


    public ClassModel getClassById(String classId) {
        openReadableDb();
        Cursor cursor = db.query(TABLE_CLASS, null, COLUMN_CLASS_ID + "=? AND " + COLUMN_IS_DELETED + "=?", new String[]{classId, "0"}, null, null, null);
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

    public int updateClass(ClassModel classModel) {
        openWritableDb();
        ContentValues values = new ContentValues();
        values.put(COLUMN_CAPACITY, classModel.getCapacity());
        values.put(COLUMN_DURATION, classModel.getDuration());
        values.put(COLUMN_SESSION_COUNT, classModel.getSessionCount());
        values.put(COLUMN_TYPE, classModel.getType());
        values.put(COLUMN_DESCRIPTION, classModel.getDescription());
        values.put(COLUMN_STATUS, classModel.getStatus());
        values.put(COLUMN_DAY_OF_WEEK, classModel.getDayOfWeek());
        values.put(COLUMN_CREATED_AT, classModel.getCreatedAt());
        values.put(COLUMN_START_AT, classModel.getStartAt());
        values.put(COLUMN_END_AT, classModel.getEndAt());

        if (classModel.getTimeStart() != null) {
            values.put(COLUMN_TIME_START, classModel.getTimeStart().getTime());
        }

        values.put(COLUMN_IS_DELETED, classModel.isDeleted());

        int rowsAffected = db.update(TABLE_CLASS, values, COLUMN_CLASS_ID + "=?", new String[]{classModel.getId()});
        close();
        return rowsAffected;
    }

    public void softDeleteClass(String classId) {
        openWritableDb();
        ContentValues values = new ContentValues();
        values.put(COLUMN_IS_DELETED, 1);
        db.update(TABLE_CLASS, values, COLUMN_CLASS_ID + "=?", new String[]{classId});
        close();
    }

    public List<ClassModel> getAllClasses() {
        List<ClassModel> classList = new ArrayList<>();
        openReadableDb();

        Cursor cursor = db.query(TABLE_CLASS, null, null, null, null, null, null);  // Không lọc isDeleted

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

    public List<ClassModel> getAllUndeletedClasses() {
        List<ClassModel> classList = new ArrayList<>();
        openReadableDb();
        Cursor cursor = db.query(TABLE_CLASS, null, COLUMN_IS_DELETED + "=?", new String[]{"0"}, null, null, null);

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

    public void updateClassStartAndEndDate(String classId) {
        List<ClassSessionModel> sessionList = classSessionDAO.getClassSessionsByClassId(classId);

        if (sessionList == null || sessionList.isEmpty()) {
            return;
        }

        long minDate = Long.MAX_VALUE;
        long maxDate = Long.MIN_VALUE;

        for (ClassSessionModel session : sessionList) {
            long sessionDate = session.getDate();
            if (sessionDate < minDate) {
                minDate = sessionDate;
            }
            if (sessionDate > maxDate) {
                maxDate = sessionDate;
            }
        }

        ContentValues values = new ContentValues();
        values.put(COLUMN_START_AT, minDate);
        values.put(COLUMN_END_AT, maxDate);


        openWritableDb();
        db.update(TABLE_CLASS, values, COLUMN_CLASS_ID + "=?", new String[]{classId});
        close();
    }

    public void hardDelete(String classId) {
        openWritableDb();

        db.delete(TABLE_CLASS, COLUMN_CLASS_ID + "=?", new String[]{classId});

        close();
    }
}
