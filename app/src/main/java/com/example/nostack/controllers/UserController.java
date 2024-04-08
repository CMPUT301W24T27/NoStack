package com.example.nostack.controllers;

import android.util.Log;

import com.example.nostack.handlers.CurrentUserHandler;
import com.example.nostack.models.Attendance;
import com.example.nostack.models.Event;
import com.example.nostack.models.User;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.TaskCompletionSource;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.Transaction;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class UserController {
    private static UserController singleInstance = null;
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    private final CollectionReference userCollectionReference = FirebaseFirestore.getInstance().collection("users");

    public static UserController getInstance() {
        if (singleInstance == null) {
            singleInstance = new UserController();
        }
        return singleInstance;
    }

    public UserController() {
    }

    public Task<QuerySnapshot> getAllUsers() {
        return userCollectionReference.get();
    }

    public Task<QuerySnapshot> getUser(String userId) {
        return userCollectionReference.whereEqualTo("uuid", userId).get();
    }

    public Task<Void> updateUser(User user) {
        return userCollectionReference.document(user.getUuid()).set(user);
    }

    public Task<Void> addUser(User user) {
        return userCollectionReference.document(user.getUuid()).set(user);
    }

    public Task<Void> removeUserProfileImage(String userId) {
        DocumentReference userRef = userCollectionReference.document(userId);
        return userRef.update("profileImageUrl", null)
                .addOnSuccessListener(aVoid -> Log.d("UserController", "User profile image successfully removed."))
                .addOnFailureListener(e -> Log.e("UserController", "Failed to remove user profile image.", e));
    }

    public Task<String> getUserFcmToken(String userId) {
        TaskCompletionSource<String> taskCompletionSource = new TaskCompletionSource<>();

        userCollectionReference.document(userId).get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document != null && document.exists()) {
                            String fcmToken = document.getString("fcmToken");
                            taskCompletionSource.setResult(fcmToken);
                            Log.d("UserController", "User FCM token successfully retrieved.");
                        } else {
                            taskCompletionSource.setException(new Exception("No such user."));
                            Log.d("UserController", "Failed to retrieve User FCM token.");
                        }
                    } else {
                        Log.d("UserController", "Failed to retrieve User FCM token. User does not exist.");
                        taskCompletionSource.setException(task.getException());
                    }
                });

        return taskCompletionSource.getTask();
    }

    public Task<Void> setUserFcmToken(String userId, String fcmToken) {
        DocumentReference userRef = userCollectionReference.document(userId);
        return userRef.update("fcmToken", fcmToken)
                .addOnSuccessListener(aVoid -> Log.d("UserController", "User FCM token successfully updated."))
                .addOnFailureListener(e -> Log.e("UserController", "Failed to update user FCM token.", e));
    }

    public Task<Void> addNotification(String userId, HashMap<String, String> announcement) {
        return userCollectionReference.document(userId)
                .update("announcements", FieldValue.arrayUnion(announcement));
    }

    public Task<ArrayList<HashMap<String, String>>> getUserAnnouncement(String userId) {
        TaskCompletionSource<ArrayList<HashMap<String, String>>> taskCompletionSource = new TaskCompletionSource<>();
        DocumentReference userRef = userCollectionReference.document(userId);

        userRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                if (document.exists()) {
                    // Extract the announcement HashMap from the document
                    ArrayList<HashMap<String, String>> announcement = (ArrayList<HashMap<String, String>>) document.get("announcements");
                    if (announcement != null) {
                        taskCompletionSource.setResult(announcement);
                        Log.d("UserController", "User announcement successfully retrieved.");
                    } else {
                        // If there's no announcement data
                        taskCompletionSource.setResult(new ArrayList<HashMap<String, String>>()); // Return an empty HashMap
                        Log.d("UserController", "User does not have an announcement.");
                    }
                } else {
                    taskCompletionSource.setException(new Exception("No such user."));
                    Log.e("UserController", "Failed to retrieve user announcement. User does not exist.");
                }
            } else {
                taskCompletionSource.setException(task.getException());
                Log.e("UserController", "Failed to retrieve user announcement.", task.getException());
            }
        });

        return taskCompletionSource.getTask();
    }

    // TODO: Deleting a user, may be a little too nuanced, will be done later on.
    public Task<Void> deleteUser(String userId) {
        return Tasks.whenAll();
    }
}