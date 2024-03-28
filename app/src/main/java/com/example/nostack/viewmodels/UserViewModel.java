package com.example.nostack.viewmodels;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.nostack.controllers.UserController;
import com.example.nostack.models.Event;
import com.example.nostack.models.User;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

/*
 *  UserViewModel is a ViewModel class that is used to store and manage state for UI components that require user data
 * */
public class UserViewModel extends ViewModel {
    private final MutableLiveData<User> user = new MutableLiveData<>();
    private final MutableLiveData<List<User>> allUsersLiveData = new MutableLiveData<>();
    private final  MutableLiveData<String> errorLiveData = new MutableLiveData<>();
    private final UserController userController = UserController.getInstance();
    public LiveData<String> getErrorLiveData() {
        return errorLiveData;
    }
    public void clearErrorLiveData() {
        errorLiveData.setValue(null);
    }
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

//    public void fetchEvent(String eventId) {
//        eventController.getEvent(eventId)
//                .addOnSuccessListener(documentSnapshot -> {
//                    Event event = documentSnapshot.toObject(Event.class);
//                    eventLiveData.postValue(event);
//                }).addOnFailureListener(e -> {
//                    Log.e("EventViewModel", "Error fetching event", e);
//                    errorLiveData.postValue(e.getMessage());
//                });
//    }

    public void fetchAllUsers(){
        userController.getAllUsers()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    List<User> users = new ArrayList<>();
                    for (DocumentSnapshot document:queryDocumentSnapshots) {
                        User user = document.toObject(User.class);
                        if (user.getFirstName() == null){
                            user.setFirstName("Null");
                        }else {
                            users.add(user);
                            Log.d("UserViewModel", document.toObject(User.class).getFirstName());
                        }

                    }
                    allUsersLiveData.postValue(users);
                }).addOnFailureListener(e -> {
                    Log.e("UserViewModel", "Error fetching user", e);
                    errorLiveData.postValue(e.getMessage());
                });
    }
    public LiveData<List<User>> getAllUsers() {
        return allUsersLiveData;
    }
}