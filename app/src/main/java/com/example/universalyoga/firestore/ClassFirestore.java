package com.example.universalyoga.firestore;

import android.util.Log;

import com.google.firebase.firestore.FirebaseFirestore;

import java.util.concurrent.atomic.AtomicInteger;

public class ClassFirestore {

    private FirebaseFirestore db;
    private static final String CLASSES_COLLECTION = "classes";

    public ClassFirestore() {
        this.db = FirebaseFirestore.getInstance();
    }

    public void getClassesCount(ClassesCountCallback callback) {
        Log.d("getClassesCount", "Start querying");
        AtomicInteger totalClasses = new AtomicInteger();
        db.collection(CLASSES_COLLECTION)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        int resultSize = task.getResult().size();
                        Log.d("ClassFirestore", "Number of classes: " + resultSize);
                        totalClasses.set(resultSize);
                        callback.onSuccess(totalClasses.get());
                    } else {
                        Log.e("ClassFirestore", "Error fetching classes", task.getException());  // Log lỗi
                        callback.onFailure(task.getException());
                    }
                });
    }


    public interface ClassesCountCallback {
        void onSuccess(int totalClasses);

        void onFailure(Exception e);
    }
}