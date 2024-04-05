package com.example.nostack.viewmodels;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.nostack.controllers.UserController;
import com.example.nostack.handlers.CurrentUserHandler;
import com.example.nostack.models.User;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

/*
 *  UserViewModel is a ViewModel class that is used to store and manage state for UI components that require user data
 * */
public class UserViewModel extends ViewModel {
    private final MutableLiveData<User> user = new MutableLiveData<>();
    private final UserController userController = UserController.getInstance();
    private final CurrentUserHandler currentUserHandler = CurrentUserHandler.getSingleton();

    /**
     * Set the user object to be stored in the ViewModel state
     *
     * @param user the user object to be stored
     */
    public void setUser(User user) {
        this.user.setValue(user);
        Log.d("UserViewModel", "First name set " + user.getFirstName());
        Log.d("UserViewModel", "Last name set " + user.getLastName());
        Log.d("UserViewModel", "Email set " + user.getEmailAddress());
        Log.d("UserViewModel", "Img URL set " + user.getProfileImageUrl());
        currentUserHandler.checkAndUpdateFcmToken();
    }

    /**
     * Get the user object stored in the ViewModel state
     *
     * @return LiveData<User>
     */
    public LiveData<User> getUser() {
        return user;
    }

    /**
     * Updates the user object stored in Firestore
     *
     * @param user new user object to be stored
     */
    public void updateUser(User user) {
        final FirebaseFirestore db;
        final CollectionReference userRef;
        // Update user object in firestore

        db = FirebaseFirestore.getInstance();
        userRef = db.collection("users");
        userRef.document(user.getUuid()).set(user);
    }

    public Task<Void> updateUserFcmToken(String userId, String fcmToken) {
        return userController.setUserFcmToken(userId, fcmToken);
    }
}