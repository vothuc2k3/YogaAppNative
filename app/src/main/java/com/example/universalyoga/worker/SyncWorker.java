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
                Long sessionCountValue = document.getLong("sessionCount");
                if (sessionCountValue != null) {
                    classModel.setSessionCount(sessionCountValue.intValue());
                }

                classModel.setType(document.getString("type"));
                classModel.setDescription(document.getString("description"));
                classModel.setStatus(document.getString("status"));
                classModel.setDayOfWeek(document.getString("dayOfWeek"));

                Long timeStartMillis = document.getLong("timeStart");
                if (timeStartMillis != null) {
                    classModel.setTimeStart(new Time(timeStartMillis));
                }

                Timestamp createdAtTimestamp = document.getTimestamp("createdAt");
                if (createdAtTimestamp != null) {
                    classModel.setCreatedAt(createdAtTimestamp.toDate().getTime());
                } else {
                    Long createdAtLong = document.getLong("createdAt");
                    if (createdAtLong != null) {
                        classModel.setCreatedAt(createdAtLong);
                    }
                }

                Timestamp startAtTimestamp = document.getTimestamp("startAt");
                if (startAtTimestamp != null) {
                    classModel.setStartAt(startAtTimestamp.toDate().getTime());
                } else {
                    Long startAtLong = document.getLong("startAt");
                    if (startAtLong != null) {
                        classModel.setStartAt(startAtLong);
                    }
                }

                Timestamp endAtTimestamp = document.getTimestamp("endAt");
                if (endAtTimestamp != null) {
                    classModel.setEndAt(endAtTimestamp.toDate().getTime());
                } else {
                    Long endAtLong = document.getLong("endAt");
                    if (endAtLong != null) {
                        classModel.setEndAt(endAtLong);
                    }
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
                Map<String, Object> sessionData = document.getData();
                sessionData.put("id", document.getId());

                ClassSessionModel classSessionModel = ClassSessionModel.fromMap(sessionData);  // Sử dụng hàm fromMap để chuyển đổi

                if (classSessionDAO.getClassSessionById(classSessionModel.getId()) == null) {
                    classSessionDAO.addClassSession(classSessionModel);
                    Log.d(TAG, "Class session added to SQLite: " + classSessionModel.getId());
                }
            }
        }).addOnFailureListener(e -> Log.e(TAG, "Failed to fetch class sessions from Firestore", e));



        CollectionReference bookingsRef = db.collection(BOOKINGS_COLLECTION);
        bookingsRef.get().addOnSuccessListener(queryDocumentSnapshots -> {
            for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                BookingModel bookingModel = new BookingModel();
                bookingModel.setId(document.getString("id"));
                bookingModel.setUid(document.getString("uid"));

                Timestamp createdAtTimestamp = document.getTimestamp("createdAt");
                if (createdAtTimestamp != null) {
                    bookingModel.setCreatedAt(createdAtTimestamp.toDate().getTime());
                }

                List<String> sessionIds = (List<String>) document.get("sessionIds");
                if (sessionIds != null) {
                    for (String sessionId : sessionIds) {
                        BookingSessionModel bookingSessionModel = new BookingSessionModel();
                        bookingSessionModel.setBookingId(bookingModel.getId());
                        bookingSessionModel.setSessionId(sessionId);
                        bookingSessionDAO.addBookingSession(bookingSessionModel.getBookingId(), bookingSessionModel.getSessionId());
                    }
                }

                if (bookingDAO.getBookingById(bookingModel.getId()) == null) {
                    bookingDAO.addBooking(bookingModel);
                    Log.d(TAG, "Booking added to SQLite: " + bookingModel.getId());
                }
            }
        }).addOnFailureListener(e -> Log.e(TAG, "Failed to fetch bookings from Firestore", e));
    }

    private void syncFromSQLiteToFirestore() {
        List<ClassModel> localClasses = classDAO.getAllClasses();
        for (ClassModel localClass : localClasses) {
            Map<String, Object> classData = localClass.toMap();

            classData.put("timeStart", localClass.getTimeStart().getTime());
            classData.put("createdAt", new Timestamp(new Date(localClass.getCreatedAt())));
            classData.put("startAt", new Timestamp(new Date(localClass.getStartAt())));
            classData.put("endAt", new Timestamp(new Date(localClass.getEndAt())));

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
            Map<String, Object> sessionData = localClassSession.toMap();
            db.collection(CLASS_SESSIONS_COLLECTION)
                    .document(localClassSession.getId())
                    .set(sessionData)
                    .addOnSuccessListener(aVoid -> Log.d(TAG, "Class session uploaded to Firestore: " + localClassSession.getId()))
                    .addOnFailureListener(e -> Log.e(TAG, "Failed to upload class session to Firestore", e));
        }

        List<BookingModel> localBookings = bookingDAO.getAllBookings();
        for (BookingModel localBooking : localBookings) {
            Map<String, Object> bookingData = localBooking.toMap();
            bookingData.put("createdAt", new Timestamp(new Date(localBooking.getCreatedAt())));

            db.collection(BOOKINGS_COLLECTION)
                    .document(localBooking.getId())
                    .set(bookingData)
                    .addOnSuccessListener(aVoid -> Log.d(TAG, "Booking uploaded to Firestore: " + localBooking.getId()))
                    .addOnFailureListener(e -> Log.e(TAG, "Failed to upload booking to Firestore", e));

            List<String> sessionIds = bookingSessionDAO.getSessionIdsByBookingId(localBooking.getId());
            db.collection(BOOKINGS_COLLECTION)
                    .document(localBooking.getId())
                    .update("sessionIds", sessionIds)
                    .addOnSuccessListener(aVoid -> Log.d(TAG, "Booking sessions updated: " + localBooking.getId()))
                    .addOnFailureListener(e -> Log.e(TAG, "Failed to update booking sessions: " + localBooking.getId()));
        }
    }
}
