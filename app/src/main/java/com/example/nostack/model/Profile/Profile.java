package com.example.nostack.model.Profile;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.example.nostack.model.State.UserViewModel;
import com.example.nostack.model.User.User;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/*
 * User profile class
 * 
 * Checks if user is logged in, if not, generate a new UUID to store in local storage & firestore
 * If user is logged in, retrieve user data from firestore by UUID
 * 
 */
public class Profile  {
    public Activity activity;
    private FirebaseFirestore db;
    private CollectionReference userRef;
    private SharedPreferences preferences;

    private String name;
    private String email;
    private String uuid;

    public Profile(Activity activity){
        this.activity = activity;
        // Check if UUID exists in local storage
        // Check shared preferences for UUID

        preferences = activity.getApplicationContext().getSharedPreferences("com.example.nostack", Context.MODE_PRIVATE);
        String uuid = preferences.getString("uuid", null);

        db = FirebaseFirestore.getInstance();
        userRef = db.collection("users");

        if (uuid == null) {
            // If UUID does not exist, create a new profile
            createProfile();
        }
        else{
            // Retrieve user data from firestore by checking if Document ID of the UUID exists
            retrieveProfile(uuid);

        }
    }

    public Profile(String name, String email, String uuid) {
        this.name = name;
        this.email = email;
        this.uuid = uuid;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public String getUuid() {
        return uuid;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }
    
    public void createProfile(){
        uuid = UUID.randomUUID().toString();
        preferences.edit().putString("uuid", uuid).apply();

        // Add new user to firestore
        User user = new User("", "", "", "", "", uuid);

        // Add a new document with the UUID as the document ID
        userRef.document(uuid).set(user);

        Snackbar.make(activity.findViewById(android.R.id.content), "New user profile created.", Snackbar.LENGTH_LONG).show();
    }

    public void retrieveProfile(String uuid){
        UserViewModel userViewModel = new ViewModelProvider((AppCompatActivity) activity).get(UserViewModel.class);

        userRef.whereEqualTo("uuid", uuid).addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                if (error != null) {
                    Log.w("Profile", "Listen failed.", error);
                    Snackbar.make(activity.findViewById(android.R.id.content), "UUID does not exist. Creating new profile.", Snackbar.LENGTH_LONG).show();
                    createProfile();
                    return;
                }

                for (QueryDocumentSnapshot doc : value) {
                    if (doc.exists()) {
                        setName(doc.getString("first_name"));
                        setEmail(doc.getString("email"));
                        setUuid(doc.getString("uuid"));

                        User user = new User(getName(), "", "", getEmail(), "", getUuid());
                        userViewModel.setUser(user);
                    }
                }
                Snackbar.make(activity.findViewById(android.R.id.content), "Welcome, " + getName(), Snackbar.LENGTH_LONG).show();
            }
        });
    }
}
