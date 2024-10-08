package com.example.universalyoga.worker;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.work.ListenableWorker;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.example.universalyoga.models.ClassModel;
import com.example.universalyoga.models.UserModel;
import com.example.universalyoga.sqlite.DAO.UserDAO;
import com.example.universalyoga.sqlite.DAO.ClassDAO;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.List;

public class SyncWorker extends Worker {
    private final String USERS_COLLECTION = "users";
    private final String CLASSES_COLLECTION = "classes";
    private static String TAG = "SyncWorker";

    private FirebaseFirestore db;
    private UserDAO userDAO;
    private ClassDAO classDAO;

    public SyncWorker(@NonNull Context context, @NonNull WorkerParameters workerParameters) {
        super(context, workerParameters);
        db = FirebaseFirestore.getInstance();
        userDAO = new UserDAO(context);
        classDAO = new ClassDAO(context);
    }

    @NonNull
    @Override
    public ListenableWorker.Result doWork() {
        try {
            syncFromFirestoreToSQLite();
            syncFromSQLiteToFirestore();
            return ListenableWorker.Result.success();
        } catch (Exception e) {
            e.printStackTrace();
            return ListenableWorker.Result.failure();
        }
    }

    private void syncFromFirestoreToSQLite() {
        CollectionReference classesRef = db.collection(CLASSES_COLLECTION);
        classesRef.get().addOnSuccessListener(queryDocumentSnapshots -> {
            for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                ClassModel classModel = document.toObject(ClassModel.class);
                if (classDAO.getClassById(classModel.getId()) == null) {
                    classDAO.addClass(classModel);
                    Log.d(TAG, "Class added to SQLite: " + classModel.getId());
                }
            }
        }).addOnFailureListener(e -> Log.e(TAG, "Failed to fetch classes from Firestore", e));

        CollectionReference usersRef = db.collection(USERS_COLLECTION);
        usersRef.get().addOnSuccessListener(queryDocumentSnapshots -> {
            for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                UserModel userModel = document.toObject(UserModel.class);
                if (userDAO.getUserByUid(userModel.getUid()) == null) {
                    userDAO.addUser(userModel);
                    Log.d(TAG, "User added to SQLite: " + userModel.getUid());
                }
            }
        }).addOnFailureListener(e -> Log.e(TAG, "Failed to fetch users from Firestore", e));
    }

    private void syncFromSQLiteToFirestore() {
        List<ClassModel> localClasses = classDAO.getAllClasses();
        for (ClassModel localClass : localClasses) {
            db.collection(CLASSES_COLLECTION)
                    .document(localClass.getId())
                    .set(localClass)
                    .addOnSuccessListener(aVoid -> Log.d(TAG, "Class uploaded to Firestore: " + localClass.getId()))
                    .addOnFailureListener(e -> Log.e(TAG, "Failed to upload class to Firestore", e));
        }

        List<UserModel> localUsers = userDAO.getAllUsers();
        for (UserModel localUser : localUsers) {
            db.collection(USERS_COLLECTION)
                    .document(localUser.getUid())
                    .set(localUser)
                    .addOnSuccessListener(aVoid -> Log.d(TAG, "User uploaded to Firestore: " + localUser.getUid()))
                    .addOnFailureListener(e -> Log.e(TAG, "Failed to upload user to Firestore", e));
        }
    }
}
