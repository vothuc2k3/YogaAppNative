package com.example.universalyoga.worker;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.work.ListenableWorker;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.example.universalyoga.models.ClassModel;
import com.example.universalyoga.models.UserModel;
import com.example.universalyoga.sqlite.DAO.UserDAO;
import com.example.universalyoga.sqlite.DAO.ClassDAO;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.List;

public class SyncWorker extends Worker {
    private final String USERS_COLLECTION = "users";
    private final String CLASSES_COLLECTION = "classes";

    private FirebaseFirestore db;
    private UserDAO userDAO;
    private ClassDAO classDAO;
    private FirebaseAuth mAuth;

    public SyncWorker(@NonNull Context context, @NonNull WorkerParameters workerParameters) {
        super(context, workerParameters);
        db = FirebaseFirestore.getInstance();
        userDAO = new UserDAO(context);
        classDAO = new ClassDAO(context);
        mAuth = FirebaseAuth.getInstance();
    }

    @NonNull
    @Override
    public ListenableWorker.Result doWork() {
        try {
            syncCurrentUser();
            syncClasses();
            return ListenableWorker.Result.success();
        } catch (Exception e) {
            e.printStackTrace();
            return ListenableWorker.Result.failure();
        }
    }

    private void syncCurrentUser() {
        FirebaseUser firebaseUser = mAuth.getCurrentUser();

        if (firebaseUser != null) {
            UserModel user = userDAO.getUserByUid(firebaseUser.getUid());

            if (user != null) {
                db.collection(USERS_COLLECTION).document(user.getUid())
                        .set(user.toMap())
                        .addOnSuccessListener(aVoid -> {
                        })
                        .addOnFailureListener(e -> {
                        });
            }
        }
    }

    private void syncClasses() {
        List<ClassModel> classList = classDAO.getAllClasses();
        for (ClassModel classModel : classList) {
            db.collection(CLASSES_COLLECTION)
                    .document(classModel.getId())
                    .set(classModel.toMap())
                    .addOnSuccessListener(v -> {
                    })
                    .addOnFailureListener(e -> {
                    });
        }
    }

}
