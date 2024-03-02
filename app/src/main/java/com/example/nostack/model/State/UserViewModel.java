package com.example.nostack.model.State;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.nostack.model.User.User;

public class UserViewModel extends ViewModel
{
    private MutableLiveData<User> user = new MutableLiveData<>();

    public void setUser(User user) {
        this.user.setValue(user);
        Log.d("UserViewModel", "User data set" + user.getFirstName());
    }

    public LiveData<User> getUser() {
        return user;
    }
}