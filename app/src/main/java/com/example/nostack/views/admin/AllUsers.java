package com.example.nostack.views.admin;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.nostack.controllers.UserController;
import com.example.nostack.models.User;
import com.google.firebase.firestore.DocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class AllUsers {
    private final MutableLiveData<String> errorLiveData = new MutableLiveData<>();
    public LiveData<String> getErrorLiveData() {
        return errorLiveData;
    }
    public void clearErrorLiveData() {
        errorLiveData.setValue(null);
    }
    private final UserController userController = UserController.getInstance();

    private final MutableLiveData<List<User>> allUsersLiveData = new MutableLiveData<>();

    public void fetchAllUsers(){
        userController.getAllUsers()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    List<User> users = new ArrayList<>();
                    for (DocumentSnapshot document:queryDocumentSnapshots) {
                        User user = document.toObject(User.class);
                        Log.d("UserViewModel - get profiles", user.getUuid());
                        if (user != null){
                            Log.d("UserVM", "User not null, added");
                            users.add(user);
                            //Log.d("UserViewModel - pass", user.getFirstName());
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
