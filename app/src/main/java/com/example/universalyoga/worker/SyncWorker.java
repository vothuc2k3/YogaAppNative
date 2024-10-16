package com.example.universalyoga.worker;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.work.ListenableWorker;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.example.universalyoga.models.BookingModel;
import com.example.universalyoga.models.BookingSessionModel;
import com.example.universalyoga.models.ClassModel;
import com.example.universalyoga.models.ClassSessionModel;
import com.example.universalyoga.models.UserModel;
import com.example.universalyoga.sqlite.DAO.BookingDAO;
import com.example.universalyoga.sqlite.DAO.BookingSessionDAO;
import com.example.universalyoga.sqlite.DAO.ClassSessionDAO;
import com.example.universalyoga.sqlite.DAO.UserDAO;
import com.example.universalyoga.sqlite.DAO.ClassDAO;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.Timestamp;

import java.sql.Time;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;

public class SyncWorker extends Worker {
    private final String USERS_COLLECTION = "users";
    private final String CLASSES_COLLECTION = "classes";
    private final String CLASS_SESSIONS_COLLECTION = "class_sessions";
    private final String BOOKINGS_COLLECTION = "bookings";
    private static String TAG = "SyncWorker";

    private FirebaseFirestore db;
    private UserDAO userDAO;
    private ClassDAO classDAO;
    private ClassSessionDAO classSessionDAO;
    private BookingDAO bookingDAO;
    private BookingSessionDAO bookingSessionDAO;

    public SyncWorker(@NonNull Context context, @NonNull WorkerParameters workerParameters) {
        super(context, workerParameters);
        db = FirebaseFirestore.getInstance();
        userDAO = new UserDAO(context);
        classDAO = new ClassDAO(context);
        classSessionDAO = new ClassSessionDAO(context);
        bookingDAO = new BookingDAO(context);
        bookingSessionDAO = new BookingSessionDAO(context);
    }

    @NonNull
    @Override
    public Result doWork() {
        try {
            syncFromSQLiteToFirestore(this::syncFromFirestoreToSQLite);
            return Result.success();
        } catch (Exception e) {
            e.printStackTrace();
            return Result.failure();
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
                classModel.setSessionCount(document.getLong("sessionCount").intValue());
                classModel.setType(document.getString("type"));
                classModel.setDescription(document.getString("description"));
                classModel.setStatus(document.getString("status"));
                classModel.setDayOfWeek(document.getString("dayOfWeek"));
                classModel.setTimeStart(new Time(document.getLong("timeStart")));
                classModel.setCreatedAt(document.getTimestamp("createdAt").toDate().getTime());
                classModel.setStartAt(document.getTimestamp("startAt").toDate().getTime());
                classModel.setEndAt(document.getTimestamp("endAt").toDate().getTime());
                classModel.setDeleted(document.getBoolean("isDeleted"));

                classDAO.addClass(classModel);
                Log.d(TAG, "Class updated in SQLite: " + classModel.getId());
            }
        }).addOnFailureListener(e -> Log.e(TAG, "Failed to fetch classes from Firestore", e));

        CollectionReference classSessionsRef = db.collection(CLASS_SESSIONS_COLLECTION);
        classSessionsRef.get().addOnSuccessListener(queryDocumentSnapshots -> {
            for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                ClassSessionModel classSessionModel = new ClassSessionModel();

                classSessionModel.setId(document.getString("id"));
                classSessionModel.setClassId(document.getString("classId"));
                classSessionModel.setSessionNumber(document.getLong("sessionNumber").intValue());
                classSessionModel.setInstructorId(document.getString("instructorId"));
                classSessionModel.setDate(document.getLong("date"));
                classSessionModel.setPrice(document.getLong("price").intValue());
                classSessionModel.setRoom(document.getString("room"));
                classSessionModel.setNote(document.getString("note"));

                classSessionDAO.updateClassSession(classSessionModel);
                Log.d(TAG, "Class session updated in SQLite: " + classSessionModel.getId());
            }
        }).addOnFailureListener(e -> Log.e(TAG, "Failed to fetch class sessions from Firestore", e));
    }

    private void syncFromSQLiteToFirestore(Runnable onComplete) {
        List<ClassModel> localClasses = classDAO.getAllClasses();
        List<ClassSessionModel> localClassSessions = classSessionDAO.getAllClassSessions();

        int totalTasks = localClasses.size() + localClassSessions.size();

        if (totalTasks == 0) {
            onComplete.run();
            return;
        }

        final int[] completedTasks = {0};

        for (ClassModel localClass : localClasses) {
            Map<String, Object> classData = localClass.toMap();
            classData.put("timeStart", localClass.getTimeStart().getTime());
            classData.put("createdAt", new Timestamp(new Date(localClass.getCreatedAt())));
            classData.put("startAt", new Timestamp(new Date(localClass.getStartAt())));
            classData.put("endAt", new Timestamp(new Date(localClass.getEndAt())));
            classData.put("isDeleted", localClass.isDeleted());

            db.collection(CLASSES_COLLECTION)
                    .document(localClass.getId())
                    .set(classData)
                    .addOnSuccessListener(aVoid -> {
                        Log.d(TAG, "Class uploaded to Firestore: " + localClass.getId());
                        completedTasks[0]++;
                        if (completedTasks[0] == totalTasks) {
                            onComplete.run();
                        }
                    })
                    .addOnFailureListener(e -> {
                        Log.e(TAG, "Failed to upload class to Firestore", e);
                        completedTasks[0]++;
                        if (completedTasks[0] == totalTasks) {
                            onComplete.run();
                        }
                    });
        }
        // Sync class sessions to Firestore
        for (ClassSessionModel localClassSession : localClassSessions) {
            Map<String, Object> sessionData = localClassSession.toMap();
            db.collection(CLASS_SESSIONS_COLLECTION)
                    .document(localClassSession.getId())
                    .set(sessionData)
                    .addOnSuccessListener(aVoid -> {
                        Log.d(TAG, "Class session uploaded to Firestore: " + localClassSession.getId());
                        completedTasks[0]++;
                        if (completedTasks[0] == totalTasks) {
                            onComplete.run();
                        }
                    })
                    .addOnFailureListener(e -> {
                        Log.e(TAG, "Failed to upload class session to Firestore", e);
                        completedTasks[0]++;
                        if (completedTasks[0] == totalTasks) {
                            onComplete.run();
                        }
                    });
        }
    }
}
