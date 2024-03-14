package com.example.nostack.handlers;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModelProvider;

import com.example.nostack.model.State.UserViewModel;
import com.example.nostack.model.User.User;

public class CurrentUserHandler {
    private static CurrentUserHandler singleInstance = null;
    private static UserViewModel userViewModel;
    private static AppCompatActivity ownerActivity;

    public static void setSingleton() {
        if (ownerActivity == null) {
            throw new RuntimeException("Owner activity must be set in MainActivity.");
        }
        userViewModel = new ViewModelProvider(ownerActivity).get(UserViewModel.class);
    }

    public static CurrentUserHandler getSingleton() {
        if (singleInstance == null) {
            setSingleton();
        }
        return singleInstance;
    }

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
}
