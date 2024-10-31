package com.example.universalyoga.sqlite.DAO;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.universalyoga.models.ClassCategoryModel;
import com.example.universalyoga.sqlite.AppDatabaseHelper;

import java.util.ArrayList;
import java.util.List;

public class CategoryDAO {
    private static final String TABLE_NAME = "categories";
    private static final String COLUMN_ID = "id";
    private static final String COLUMN_NAME = "name";
    private static final String COLUMN_DESCRIPTION = "description";

    private final AppDatabaseHelper dbHelper;
    private SQLiteDatabase db;

    public CategoryDAO(Context context) {
        dbHelper = new AppDatabaseHelper(context);
    }

    private void openWritableDb() {
        db = dbHelper.getWritableDatabase();
    }

    private void openReadableDb() {
        db = dbHelper.getReadableDatabase();
    }

    private void closeDb() {
        if (db != null && db.isOpen()) {
            db.close();
        }
    }

    public long addCategory(ClassCategoryModel category) {
        openWritableDb();
        ContentValues values = new ContentValues();
        values.put(COLUMN_ID, category.getId());
        values.put(COLUMN_NAME, category.getName());
        values.put(COLUMN_DESCRIPTION, category.getDescription());

        long result = db.insertWithOnConflict(TABLE_NAME, null, values, SQLiteDatabase.CONFLICT_REPLACE);
        closeDb();
        return result;
    }

    public List<ClassCategoryModel> getAllCategories() {
        List<ClassCategoryModel> categories = new ArrayList<>();
        openReadableDb();
        Cursor cursor = db.query(TABLE_NAME, null, null, null, null, null, COLUMN_NAME + " ASC");

        if (cursor != null && cursor.moveToFirst()) {
            do {
                String id = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_ID));
                String name = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NAME));
                String description = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_DESCRIPTION));

                ClassCategoryModel category = new ClassCategoryModel(id, name, description);
                categories.add(category);
            } while (cursor.moveToNext());
            cursor.close();
        }

        closeDb();
        return categories;
    }

    public int updateCategory(ClassCategoryModel category) {
        openWritableDb();
        ContentValues values = new ContentValues();
        values.put(COLUMN_NAME, category.getName());
        values.put(COLUMN_DESCRIPTION, category.getDescription());

        int rowsAffected = db.update(TABLE_NAME, values, COLUMN_ID + "=?", new String[]{category.getId()});
        closeDb();
        return rowsAffected;
    }

    public int deleteCategory(String categoryId) {
        openWritableDb();
        int rowsDeleted = db.delete(TABLE_NAME, COLUMN_ID + "=?", new String[]{categoryId});
        closeDb();
        return rowsDeleted;
    }

    public ClassCategoryModel getCategoryById(String categoryId) {
        openReadableDb();
        Cursor cursor = db.query(TABLE_NAME, null, COLUMN_ID + "=?", new String[]{categoryId}, null, null, null);

        if (cursor != null && cursor.moveToFirst()) {
            String id = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_ID));
            String name = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NAME));
            String description = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_DESCRIPTION));

            cursor.close();
            closeDb();
            return new ClassCategoryModel(id, name, description);
        }

        closeDb();
        return null;
    }

    public boolean isCategoryUsed(String categoryId) {
        openReadableDb();
        Cursor cursor = db.query("classes", null, "typeId = ?", new String[]{categoryId}, null, null, null);
        boolean isUsed = cursor != null && cursor.getCount() > 0;
        if (cursor != null) {
            cursor.close();
        }
        closeDb();
        return isUsed;
    }


    public void resetTable() {
        openWritableDb();
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        db.execSQL("CREATE TABLE categories ("
                + "id TEXT PRIMARY KEY, "
                + "name TEXT, "
                + "description TEXT)");
        closeDb();
    }
}
