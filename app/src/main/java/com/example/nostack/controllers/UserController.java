package com.example.nostack.controllers;

import android.util.Log;

import com.example.nostack.handlers.CurrentUserHandler;
import com.example.nostack.models.Attendance;
import com.example.nostack.models.Event;
import com.example.nostack.models.User;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.Transaction;

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

    // TODO: Deleting a user, may be a little too nuanced, will be done later on.
    public Task<Void> deleteUser(String userId) {
        return Tasks.whenAll();
    }
}