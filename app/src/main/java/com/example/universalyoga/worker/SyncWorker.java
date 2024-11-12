package com.example.universalyoga.worker;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.example.universalyoga.models.BookingModel;
import com.example.universalyoga.models.BookingSessionModel;
import com.example.universalyoga.models.ClassCategoryModel;
import com.example.universalyoga.models.ClassModel;
import com.example.universalyoga.models.ClassSessionModel;
import com.example.universalyoga.models.UserModel;
import com.example.universalyoga.sqlite.DAO.BookingDAO;
import com.example.universalyoga.sqlite.DAO.BookingSessionDAO;
import com.example.universalyoga.sqlite.DAO.CategoryDAO;
import com.example.universalyoga.sqlite.DAO.ClassSessionDAO;
import com.example.universalyoga.sqlite.DAO.UserDAO;
import com.example.universalyoga.sqlite.DAO.ClassDAO;
import com.example.universalyoga.utils.Util;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.Timestamp;

import org.checkerframework.checker.units.qual.C;

import java.sql.Time;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class SyncWorker extends Worker {
    private final String USERS_COLLECTION = "users";
    private final String CLASSES_COLLECTION = "classes";
    private final String CLASS_SESSIONS_COLLECTION = "class_sessions";
    private final String BOOKINGS_COLLECTION = "bookings";
    private final String CATEGORIES_COLLECTION = "categories";
    private static final String TAG = "SyncWorker";

    private final FirebaseFirestore db;
    private final UserDAO userDAO;
    private final ClassDAO classDAO;
    private final ClassSessionDAO classSessionDAO;
    private final BookingDAO bookingDAO;
    private final BookingSessionDAO bookingSessionDAO;
    private final CategoryDAO categoryDAO;

    private final Context context;

    public SyncWorker(@NonNull Context context, @NonNull WorkerParameters workerParameters) {
        super(context, workerParameters);
        db = FirebaseFirestore.getInstance();
        userDAO = new UserDAO(context);
        classDAO = new ClassDAO(context);
        classSessionDAO = new ClassSessionDAO(context);
        bookingDAO = new BookingDAO(context);
        bookingSessionDAO = new BookingSessionDAO(context);
        categoryDAO = new CategoryDAO(context);
        this.context = context;
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
        syncCategoriesFromFirestore();
        syncClassesFromFirestore();
        syncClassSessionsFromFirestore();
        syncUsersFromFirestore();
        syncBookingsFromFirestore();
    }

    private void syncCategoriesFromFirestore() {
        CollectionReference categoriesRef = db.collection(CATEGORIES_COLLECTION);
        categoriesRef.get().addOnSuccessListener(queryDocumentSnapshots -> {
            for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                ClassCategoryModel categoryModel = new ClassCategoryModel(
                        document.getString("id"),
                        document.getString("name"),
                        document.getString("description")
                );
                categoryDAO.addCategory(categoryModel);
                Log.d(TAG, "Class updated in SQLite: " + categoryModel.getId());
            }
        }).addOnFailureListener(e -> Log.e(TAG, "Failed to fetch classes from Firestore", e));
    }

    private void syncClassesFromFirestore() {
        CollectionReference classesRef = db.collection(CLASSES_COLLECTION);
        classesRef.get().addOnSuccessListener(queryDocumentSnapshots -> {
            for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                ClassModel classModel = new ClassModel();
                classModel.setId(document.getString("id"));
                classModel.setCapacity(document.getLong("capacity").intValue());
                classModel.setDuration(document.getLong("duration").intValue());
                classModel.setSessionCount(document.getLong("sessionCount").intValue());
                classModel.setTypeId(document.getString("typeId"));
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
    }

    private void syncClassSessionsFromFirestore() {
        CollectionReference classSessionsRef = db.collection(CLASS_SESSIONS_COLLECTION);
        classSessionsRef.get().addOnSuccessListener(queryDocumentSnapshots -> {
            for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                ClassSessionModel classSessionModel = new ClassSessionModel();
                classSessionModel.setId(document.getString("id"));
                classSessionModel.setClassId(document.getString("classId"));
                classSessionModel.setInstructorId(document.getString("instructorId"));
                classSessionModel.setDate(document.getLong("date"));
                classSessionModel.setStartTime(document.getLong("startTime"));
                classSessionModel.setEndTime(document.getLong("endTime"));
                classSessionModel.setPrice(document.getLong("price").intValue());
                classSessionModel.setRoom(document.getString("room"));
                classSessionModel.setNote(document.getString("note"));

                classSessionDAO.addClassSession(classSessionModel);
                Log.d(TAG, "Class session updated in SQLite: " + classSessionModel.getId());
            }
        }).addOnFailureListener(e -> Log.e(TAG, "Failed to fetch class sessions from Firestore", e));
    }

    private void syncUsersFromFirestore() {
        CollectionReference usersRef = db.collection(USERS_COLLECTION);
        usersRef.get().addOnSuccessListener(queryDocumentSnapshots -> {
            for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                Map<String, Object> userMap = document.getData();
                UserModel userModel = new UserModel(userMap);
                userDAO.addUser(userModel);
            }
        });
    }

    private void syncBookingsFromFirestore() {
        CollectionReference bookingsRef = db.collection(BOOKINGS_COLLECTION);
        bookingsRef.get().addOnSuccessListener(queryDocumentSnapshots -> {
            for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                Map<String, Object> bookingMap = document.getData();
                BookingModel bookingModel = new BookingModel(
                        document.getString("id"),
                        document.getString("uid"),
                        document.getString("status"),
                        document.getTimestamp("createdAt").toDate().getTime());
                bookingDAO.addBooking(bookingModel);
                List<String> sessionIds = (List<String>) document.get("sessionIds");
                if (sessionIds != null) {
                    for (String sessionId : sessionIds) {
                        bookingSessionDAO.addBookingSession(bookingModel.getId(), sessionId);
                    }
                }
            }
        });
    }

    private void syncFromSQLiteToFirestore(Runnable onComplete) {
        List<ClassModel> localClasses = classDAO.getAllClasses();
        List<ClassSessionModel> localClassSessions = classSessionDAO.getAllClassSessions();
        List<UserModel> localUsers = userDAO.getAllUsers();
        List<BookingModel> localBookings = bookingDAO.getAllBookings();
        List<BookingSessionModel> localBookingSessions = bookingSessionDAO.getAllBookingSessions();
        List<ClassCategoryModel> localCategories = categoryDAO.getAllCategories();
        int totalTasks = localClasses.size()
                + localClassSessions.size()
                + localUsers.size()
                + localBookings.size()
                + localCategories.size();
        if (totalTasks == 0) {
            onComplete.run();
            return;
        }
        final int[] completedTasks = {0};
        uploadClassesToFirestore(localClasses, completedTasks, totalTasks, onComplete);
        uploadClassSessionsToFirestore(localClassSessions, completedTasks, totalTasks, onComplete);
        uploadUsersToFirestore(localUsers, completedTasks, totalTasks, onComplete);
        uploadBookingsToFirestore(localBookings, localBookingSessions, completedTasks, totalTasks, onComplete);
        uploadCategoriesToFirestore(localCategories, completedTasks, totalTasks, onComplete);
    }

    private void uploadClassesToFirestore(List<ClassModel> localClasses, int[] completedTasks, int totalTasks, Runnable onComplete) {
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
    }

    private void uploadClassSessionsToFirestore(List<ClassSessionModel> localClassSessions, int[] completedTasks, int totalTasks, Runnable onComplete) {
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

    private void uploadUsersToFirestore(List<UserModel> localUsers, int[] completedTasks, int totalTasks, Runnable onComplete) {
        for (UserModel localUser : localUsers) {
            Map<String, Object> userData = localUser.toMap();
            db.collection(USERS_COLLECTION)
                    .document(localUser.getUid())
                    .set(userData)
                    .addOnSuccessListener(aVoid -> {
                        Log.d(TAG, "User uploaded to Firestore: " + localUser.getUid());
                        completedTasks[0]++;
                        if (completedTasks[0] == totalTasks) {
                            onComplete.run();
                        }
                    })
                    .addOnFailureListener(e -> {
                        Log.e(TAG, "Failed to upload user to Firestore", e);
                        completedTasks[0]++;
                        if (completedTasks[0] == totalTasks) {
                            onComplete.run();
                        }
                    });
        }
    }

    private void uploadBookingsToFirestore(List<BookingModel> localBookings, List<BookingSessionModel> localBookingSessions, int[] completedTasks, int totalTasks, Runnable onComplete) {
        for (BookingModel localBooking : localBookings) {
            Map<String, Object> bookingData = localBooking.toMap();
            List<String> sessionIds = localBookingSessions.stream()
                    .filter(bookingSession -> bookingSession.getBookingId().equals(localBooking.getId()))
                    .map(BookingSessionModel::getSessionId)
                    .collect(Collectors.toList());
            bookingData.put("sessionIds", sessionIds);
            db.collection(BOOKINGS_COLLECTION).document(localBooking.getId()).set(bookingData).addOnSuccessListener(aVoid -> {
                        Log.d(TAG, "Booking uploaded to Firestore: " + localBooking.getId());
                        completedTasks[0]++;
                        if (completedTasks[0] == totalTasks) {
                            onComplete.run();
                        }
                    })
                    .addOnFailureListener(e -> {
                        Log.e(TAG, "Failed to upload booking to Firestore", e);
                        completedTasks[0]++;
                        if (completedTasks[0] == totalTasks) {
                            onComplete.run();
                        }
                    });
        }
    }

    private void uploadCategoriesToFirestore(List<ClassCategoryModel> localCategories, int[] completedTasks, int totalTasks, Runnable onComplete) {
        for (ClassCategoryModel localCategory : localCategories) {
            Map<String, Object> categoryData = localCategory.toMap();
            String CATEGORIES_COLLECTION = "categories";
            db.collection(CATEGORIES_COLLECTION)
                    .document(localCategory.getId())
                    .set(categoryData)
                    .addOnSuccessListener(aVoid -> {
                        Log.d(TAG, "Category uploaded to Firestore: " + localCategory.getId());
                        completedTasks[0]++;
                        if (completedTasks[0] == totalTasks) {
                            onComplete.run();
                        }
                    }).addOnFailureListener(e -> {
                        Log.e(TAG, "Failed to upload category to Firestore", e);
                        completedTasks[0]++;
                        if (completedTasks[0] == totalTasks) {
                            onComplete.run();
                        }
                    });
        }
    }
}
