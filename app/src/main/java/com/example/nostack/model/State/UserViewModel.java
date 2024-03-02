package com.example.nostack.Profile;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class UserViewModel extends ViewModel
{
    private MutableLiveData<User> user = new MutableLiveData<>();

    public void setUser(User user) {
        this.user.setValue(user);
        Log.d("UserViewModel", "User data set" + user.getFirst_name());
    }

    public LiveData<User> getUser() {
        return user;
    }
}