package com.example.universalyoga.sqlite.DAO;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.example.universalyoga.models.ClassModel;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import com.example.universalyoga.utils.Util;

public class ClassDAO extends SQLiteOpenHelper {

    // Database info
    private static final String DATABASE_NAME = "yoga_app.db";
    private static final int DATABASE_VERSION = 1;

    // Table name and columns
    private static final String TABLE_CLASSES = "classes";
    private static final String COLUMN_ID = "id";
    private static final String COLUMN_CREATOR_UID = "creatorUid";
    private static final String COLUMN_INSTRUCTOR_UID = "instructorUid";
    private static final String COLUMN_CAPACITY = "capacity";
    private static final String COLUMN_DURATION = "duration";
    private static final String COLUMN_PRICE = "price";
    private static final String COLUMN_TYPE = "type";
    private static final String COLUMN_DESCRIPTION = "description";
    private static final String COLUMN_STATUS = "status";
    private static final String COLUMN_START_AT = "startAt";
    private static final String COLUMN_CREATED_AT = "createdAt";
    private static final String COLUMN_END_AT = "endAt";

    public ClassDAO(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    SimpleDateFormat sdf = new SimpleDateFormat("EEE MMM dd HH:mm:ss zzz yyyy", Locale.ENGLISH);

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_CLASSES_TABLE = "CREATE TABLE " + TABLE_CLASSES + "("
                + COLUMN_ID + " TEXT PRIMARY KEY, "
                + COLUMN_CREATOR_UID + " TEXT, "
                + COLUMN_INSTRUCTOR_UID + " TEXT, "
                + COLUMN_CAPACITY + " INTEGER, "
                + COLUMN_DURATION + " INTEGER, "
                + COLUMN_PRICE + " REAL, "
                + COLUMN_TYPE + " TEXT, "
                + COLUMN_DESCRIPTION + " TEXT, "
                + COLUMN_STATUS + " TEXT, "
                + COLUMN_START_AT + " TEXT, "
                + COLUMN_END_AT + " TEXT, "
                + COLUMN_CREATED_AT + " TEXT)";
        db.execSQL(CREATE_CLASSES_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_CLASSES);
        onCreate(db);
    }

    // Insert a new class into the database
    public boolean addClass(ClassModel classModel) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(COLUMN_ID, classModel.getId());
        values.put(COLUMN_CREATOR_UID, classModel.getCreatorUid());
        values.put(COLUMN_INSTRUCTOR_UID, classModel.getInstructorUid());
        values.put(COLUMN_CAPACITY, classModel.getCapacity());
        values.put(COLUMN_DURATION, classModel.getDuration());
        values.put(COLUMN_PRICE, classModel.getPrice());
        values.put(COLUMN_TYPE, classModel.getType());
        values.put(COLUMN_DESCRIPTION, classModel.getDescription());
        values.put(COLUMN_STATUS, classModel.getStatus());
        values.put(COLUMN_START_AT, classModel.getStartAt().toDate().toString());
        values.put(COLUMN_CREATED_AT, classModel.getCreatedAt().toDate().toString());
        values.put(COLUMN_END_AT, classModel.getEndAt().toDate().toString());

        long result = db.insert(TABLE_CLASSES, null, values);
        db.close();

        return result != -1;
    }

    // Retrieve a class by ID
    public ClassModel getClassById(String classId) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_CLASSES, null, COLUMN_ID + "=?", new String[]{classId}, null, null, null);

        if (cursor != null && cursor.moveToFirst()) {
            ClassModel classModel = new ClassModel();
            classModel.setId(cursor.getString(cursor.getColumnIndex(COLUMN_ID)));
            classModel.setCreatorUid(cursor.getString(cursor.getColumnIndex(COLUMN_CREATOR_UID)));
            classModel.setInstructorUid(cursor.getString(cursor.getColumnIndex(COLUMN_INSTRUCTOR_UID)));
            classModel.setCapacity(cursor.getInt(cursor.getColumnIndex(COLUMN_CAPACITY)));
            classModel.setDuration(cursor.getInt(cursor.getColumnIndex(COLUMN_DURATION)));
            classModel.setPrice(cursor.getDouble(cursor.getColumnIndex(COLUMN_PRICE)));
            classModel.setType(cursor.getString(cursor.getColumnIndex(COLUMN_TYPE)));
            classModel.setDescription(cursor.getString(cursor.getColumnIndex(COLUMN_DESCRIPTION)));
            classModel.setStatus(cursor.getString(cursor.getColumnIndex(COLUMN_STATUS)));

            cursor.close();
            return classModel;
        } else {
            return null;
        }
    }

    // Update a class
    public boolean updateClass(ClassModel classModel) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(COLUMN_CREATOR_UID, classModel.getCreatorUid());
        values.put(COLUMN_INSTRUCTOR_UID, classModel.getInstructorUid());
        values.put(COLUMN_CAPACITY, classModel.getCapacity());
        values.put(COLUMN_DURATION, classModel.getDuration());
        values.put(COLUMN_PRICE, classModel.getPrice());
        values.put(COLUMN_TYPE, classModel.getType());
        values.put(COLUMN_DESCRIPTION, classModel.getDescription());
        values.put(COLUMN_STATUS, classModel.getStatus());
        values.put(COLUMN_START_AT, classModel.getStartAt().toDate().toString());
        values.put(COLUMN_CREATED_AT, classModel.getCreatedAt().toDate().toString());
        values.put(COLUMN_END_AT, classModel.getEndAt().toDate().toString());

        int result = db.update(TABLE_CLASSES, values, COLUMN_ID + "=?", new String[]{classModel.getId()});
        db.close();
        return result > 0;
    }

    // Delete a class by ID
    public boolean deleteClass(String classId) {
        SQLiteDatabase db = this.getWritableDatabase();
        int result = db.delete(TABLE_CLASSES, COLUMN_ID + "=?", new String[]{classId});
        db.close();
        return result > 0;
    }

    public List<ClassModel> getAllClasses() {
        List<ClassModel> classList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_CLASSES, null);

        if (cursor.moveToFirst()) {
            do {
                ClassModel classModel = new ClassModel();
                classModel.setId(cursor.getString(cursor.getColumnIndex(COLUMN_ID)));
                classModel.setCreatorUid(cursor.getString(cursor.getColumnIndex(COLUMN_CREATOR_UID)));
                classModel.setInstructorUid(cursor.getString(cursor.getColumnIndex(COLUMN_INSTRUCTOR_UID)));
                classModel.setCapacity(cursor.getInt(cursor.getColumnIndex(COLUMN_CAPACITY)));
                classModel.setDuration(cursor.getInt(cursor.getColumnIndex(COLUMN_DURATION)));
                classModel.setPrice(cursor.getDouble(cursor.getColumnIndex(COLUMN_PRICE)));
                classModel.setType(cursor.getString(cursor.getColumnIndex(COLUMN_TYPE)));
                classModel.setDescription(cursor.getString(cursor.getColumnIndex(COLUMN_DESCRIPTION)));
                classModel.setStatus(cursor.getString(cursor.getColumnIndex(COLUMN_STATUS)));

                // Convert TEXT from SQLite back to Firebase Timestamp
                String createdAtString = cursor.getString(cursor.getColumnIndex(COLUMN_CREATED_AT));
                classModel.setCreatedAt(Util.convertStringToTimestamp(createdAtString));

                String startAtString = cursor.getString(cursor.getColumnIndex(COLUMN_START_AT));
                classModel.setStartAt(Util.convertStringToTimestamp(startAtString));

                String endAtString = cursor.getString(cursor.getColumnIndex(COLUMN_END_AT));
                classModel.setEndAt(Util.convertStringToTimestamp(endAtString));

                classList.add(classModel);
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();
        return classList;
    }

    public List<ClassModel> searchClassesByName(String query) {
        List<ClassModel> classList = new ArrayList<>();

        SQLiteDatabase db = this.getReadableDatabase();
        String sqlQuery = "SELECT * FROM " + TABLE_CLASSES + " WHERE " + COLUMN_TYPE + " LIKE ?";
        Cursor cursor = db.rawQuery(sqlQuery, new String[]{"%" + query + "%"});

        if (cursor.moveToFirst()) {
            do {
                ClassModel classModel = new ClassModel();
                classModel.setId(cursor.getString(cursor.getColumnIndex(COLUMN_ID)));
                classModel.setCreatorUid(cursor.getString(cursor.getColumnIndex(COLUMN_CREATOR_UID)));
                classModel.setInstructorUid(cursor.getString(cursor.getColumnIndex(COLUMN_INSTRUCTOR_UID)));
                classModel.setCapacity(cursor.getInt(cursor.getColumnIndex(COLUMN_CAPACITY)));
                classModel.setDuration(cursor.getInt(cursor.getColumnIndex(COLUMN_DURATION)));
                classModel.setPrice(cursor.getDouble(cursor.getColumnIndex(COLUMN_PRICE)));
                classModel.setType(cursor.getString(cursor.getColumnIndex(COLUMN_TYPE)));
                classModel.setDescription(cursor.getString(cursor.getColumnIndex(COLUMN_DESCRIPTION)));
                classModel.setStatus(cursor.getString(cursor.getColumnIndex(COLUMN_STATUS)));

                // Convert TEXT to Firebase Timestamp for startAt and endAt
                String startAtString = cursor.getString(cursor.getColumnIndex(COLUMN_START_AT));
                classModel.setStartAt(Util.convertStringToTimestamp(startAtString));

                String endAtString = cursor.getString(cursor.getColumnIndex(COLUMN_END_AT));
                classModel.setEndAt(Util.convertStringToTimestamp(endAtString));

                // Convert TEXT to Firebase Timestamp for createdAt
                String createdAtString = cursor.getString(cursor.getColumnIndex(COLUMN_CREATED_AT));
                classModel.setCreatedAt(Util.convertStringToTimestamp(createdAtString));

                classList.add(classModel);
            } while (cursor.moveToNext());
        }

        Log.d("Class Searching Query", String.valueOf(classList.size()));

        cursor.close();
        db.close();
        return classList;
    }


}
