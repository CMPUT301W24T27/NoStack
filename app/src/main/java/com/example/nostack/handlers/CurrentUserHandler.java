package com.example.nostack.handlers;

import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModelProvider;

import com.example.nostack.controllers.UserController;
import com.example.nostack.viewmodels.UserViewModel;
import com.example.nostack.models.User;
import com.google.firebase.messaging.FirebaseMessaging;

public class CurrentUserHandler {
    private static CurrentUserHandler singleInstance = null;
    private static UserViewModel userViewModel;
    private static AppCompatActivity ownerActivity;
    private static UserController userController = UserController.getInstance();

    public static void setSingleton() {
        if (ownerActivity == null) {
            throw new RuntimeException("Owner activity must be set in MainActivity.");
        }
        singleInstance = new CurrentUserHandler();
        userViewModel = new ViewModelProvider(ownerActivity).get(UserViewModel.class);
    }

    public static CurrentUserHandler getSingleton() {
        if (singleInstance == null) {
            setSingleton();
        }
        return singleInstance;
    }

    public CurrentUserHandler() {}

    public static void setOwnerActivity(AppCompatActivity activity) {
        ownerActivity = activity;
    }

    public String getCurrentUserId() {
        LiveData<User> userLiveData = userViewModel.getUser();
        User user = userLiveData.getValue();
        if (user != null) {
            return user.getUuid();
        } else {
            return null;
        }
    }

    public User getCurrentUser() {
        LiveData<User> userLiveData = userViewModel.getUser();
        return userLiveData.getValue();
    }

    public void checkAndUpdateFcmToken() {
        String userId = getCurrentUserId();

        userController.getUserFcmToken(userId)
            .addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    String fcmToken = task.getResult();
                    Log.d("CurrentUserHandler", "User FCM token: " + fcmToken);
                    if (fcmToken == null || fcmToken.isEmpty()) {
                        FirebaseMessaging.getInstance().getToken()
                            .addOnCompleteListener(tokenTask -> {
                                if (tokenTask.isSuccessful()) {
                                    String newFcmToken = tokenTask.getResult();
                                    userController.setUserFcmToken(userId, newFcmToken)
                                        .addOnSuccessListener(aVoid -> Log.d("UserController", "User FCM token successfully updated."))
                                        .addOnFailureListener(e -> Log.e("UserController", "Failed to update user FCM token.", e));
                                } else {
                                    Log.e("UserController", "Failed to generate new FCM token.", tokenTask.getException());
                                }
                            });
                    }
                } else {
                    // The task failed
                    Log.e("UserController", "Failed to get FCM token.", task.getException());
                }
            });
    }
}
