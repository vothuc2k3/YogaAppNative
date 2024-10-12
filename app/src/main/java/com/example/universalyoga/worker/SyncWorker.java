package com.example.universalyoga.worker;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.work.ListenableWorker;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.example.universalyoga.models.ClassModel;
import com.example.universalyoga.models.ClassSessionModel;
import com.example.universalyoga.models.UserModel;
import com.example.universalyoga.sqlite.DAO.ClassSessionDAO;
import com.example.universalyoga.sqlite.DAO.UserDAO;
import com.example.universalyoga.sqlite.DAO.ClassDAO;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.Timestamp;

import java.util.Date;
import java.util.List;
import java.util.Map;

public class SyncWorker extends Worker {
    private final String USERS_COLLECTION = "users";
    private final String CLASSES_COLLECTION = "classes";
    private final String CLASS_SESSIONS_COLLECTION = "class_sessions";
    private static String TAG = "SyncWorker";

    private FirebaseFirestore db;
    private UserDAO userDAO;
    private ClassDAO classDAO;
    private ClassSessionDAO classSessionDAO;

    public SyncWorker(@NonNull Context context, @NonNull WorkerParameters workerParameters) {
        super(context, workerParameters);
        db = FirebaseFirestore.getInstance();
        userDAO = new UserDAO(context);
        classDAO = new ClassDAO(context);
        classSessionDAO = new ClassSessionDAO(context);
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
                ClassModel classModel = new ClassModel();

                classModel.setId(document.getString("id"));
                classModel.setInstructorUid(document.getString("instructorUid"));
                classModel.setCapacity(document.getLong("capacity").intValue());
                classModel.setDuration(document.getLong("duration").intValue());
                classModel.setSessionCount(document.getLong("sessionCount").intValue()); // Cập nhật sessionCount
                classModel.setType(document.getString("type"));
                classModel.setDescription(document.getString("description"));
                classModel.setStatus(document.getString("status"));
                classModel.setDayOfWeek(document.getString("dayOfWeek"));

                Timestamp createdAtTimestamp = document.getTimestamp("createdAt");
                Timestamp startAtTimestamp = document.getTimestamp("startAt");
                Timestamp endAtTimestamp = document.getTimestamp("endAt");

                if (createdAtTimestamp != null) {
                    classModel.setCreatedAt(createdAtTimestamp.toDate().getTime());
                }
                if (startAtTimestamp != null) {
                    classModel.setStartAt(startAtTimestamp.toDate().getTime());
                }
                if (endAtTimestamp != null) {
                    classModel.setEndAt(endAtTimestamp.toDate().getTime());
                }

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

        CollectionReference classSessionsRef = db.collection(CLASS_SESSIONS_COLLECTION);
        classSessionsRef.get().addOnSuccessListener(queryDocumentSnapshots -> {
            for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                ClassSessionModel classSessionModel = new ClassSessionModel();

                classSessionModel.setId(document.getString("id"));
                classSessionModel.setClassId(document.getString("classId"));
                classSessionModel.setInstructorId(document.getString("instructorId")); // Thêm instructorId
                classSessionModel.setDate(document.getTimestamp("date").toDate().getTime()); // Đổi sang epoch time
                classSessionModel.setPrice(document.getLong("price").intValue()); // Cập nhật giá trị price
                classSessionModel.setRoom(document.getString("room")); // Cập nhật giá trị room
                classSessionModel.setNote(document.getString("note")); // Cập nhật giá trị note

                if (classSessionDAO.getClassSessionById(classSessionModel.getId()) == null) {
                    classSessionDAO.addClassSession(classSessionModel);
                    Log.d(TAG, "Class session added to SQLite: " + classSessionModel.getId());
                }
            }
        }).addOnFailureListener(e -> Log.e(TAG, "Failed to fetch class sessions from Firestore", e));
    }

    private void syncFromSQLiteToFirestore() {
        List<ClassModel> localClasses = classDAO.getAllClasses();
        for (ClassModel localClass : localClasses) {
            long createdAtLong = localClass.getCreatedAt();
            long startAtLong = localClass.getStartAt();
            long endAtLong = localClass.getEndAt();
            Map<String, Object> classData = localClass.toMap();
            classData.put("createdAt", new Timestamp(new Date(createdAtLong)));
            classData.put("startAt", new Timestamp(new Date(startAtLong)));
            classData.put("endAt", new Timestamp(new Date(endAtLong)));
            db.collection(CLASSES_COLLECTION)
                    .document(localClass.getId())
                    .set(classData)
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

        List<ClassSessionModel> localClassSessions = classSessionDAO.getAllClassSessions();
        for (ClassSessionModel localClassSession : localClassSessions) {
            db.collection(CLASS_SESSIONS_COLLECTION)
                    .document(localClassSession.getId())
                    .set(localClassSession)
                    .addOnSuccessListener(aVoid -> Log.d(TAG, "Class sessions uploaded to Firestore: " + localClassSession.getId()))
                    .addOnFailureListener(e -> Log.e(TAG, "Failed to upload class sessions to Firestore", e));
        }
    }
}
