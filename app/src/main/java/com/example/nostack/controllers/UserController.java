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

/**
 * Class for handling users
 */
public class UserController {
    private static UserController singleInstance = null;
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    private final CollectionReference userCollectionReference = FirebaseFirestore.getInstance().collection("users");

    /**
     * Get the instance of the UserController
     * @return void
     */
    public static UserController getInstance() {
        if (singleInstance == null) {
            singleInstance = new UserController();
        }
        return singleInstance;
    }

    /**
     * Empty public constructor
     */
    public UserController() {
    }

    /**
     * Get all users
     * @return Task<QuerySnapshot> The user collection
     */
    public Task<QuerySnapshot> getAllUsers() {
        return userCollectionReference.get();
    }

    /**
     * Get user by id
     * @param userId The user id
     * @return Task<QuerySnapshot> The user by id
     */
    public Task<QuerySnapshot> getUser(String userId) {
        return userCollectionReference.whereEqualTo("uuid", userId).get();
    }

    /**
     * Update user
     * @param user The user object
    * @return Task<Void> The user object
     */
    public Task<Void> updateUser(User user) {
        return userCollectionReference.document(user.getUuid()).set(user);
    }

    /**
     * Add user
     * @param user The user object
     * @return Task<Void> The user object
     */
    public Task<Void> addUser(User user) {
        return userCollectionReference.document(user.getUuid()).set(user);
    }

    /**
     * Remove user profile pic
     * @param userId The user id
     * @return Task<Void> The user object
     */
    public Task<Void> removeUserProfileImage(String userId) {
        DocumentReference userRef = userCollectionReference.document(userId);
        return userRef.update("profileImageUrl", null)
                .addOnSuccessListener(aVoid -> Log.d("UserController", "User profile image successfully removed."))
                .addOnFailureListener(e -> Log.e("UserController", "Failed to remove user profile image.", e));
    }

    /**
     * get user fcm token
     * @param userId The user id
     * @return Task<String> The user fcm token
     */
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

    /**
     * Set user fcm token
     * @param userId The user id
     * @param fcmToken The fcm token
     * @return Task<Void>
     */
    public Task<Void> setUserFcmToken(String userId, String fcmToken) {
        DocumentReference userRef = userCollectionReference.document(userId);
        return userRef.update("fcmToken", fcmToken)
                .addOnSuccessListener(aVoid -> Log.d("UserController", "User FCM token successfully updated."))
                .addOnFailureListener(e -> Log.e("UserController", "Failed to update user FCM token.", e));
    }

    /**
     * add notificaiton to user
     * @param userId The user id
     * @param announcement The announcement
     * @return void
     */
    public Task<Void> addNotification(String userId, HashMap<String, String> announcement) {
        return userCollectionReference.document(userId)
                .update("announcements", FieldValue.arrayUnion(announcement));
    }

    /**
     * Get user announcement
     * @param userId The user id
     * @return Task<ArrayList<HashMap<String, String>>> The user announcement
     */
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

    /**
     * Delete user
     * @param userId the user id
     * @return void
     */
    // TODO: Deleting a user, may be a little too nuanced, will be done later on.
    public Task<Void> deleteUser(String userId) {
        return Tasks.whenAll();
    }
}