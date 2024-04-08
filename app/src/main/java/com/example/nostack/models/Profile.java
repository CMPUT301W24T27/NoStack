package com.example.nostack.models;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.example.nostack.viewmodels.UserViewModel;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;

/*
 * User profile class
 *
 * Checks if user is logged in, if not, generate a new UUID to store in local storage & firestore
 * If user is logged in, retrieve user data from firestore by UUID
 *
 */
public class Profile extends User {
    private static final String PREF_KEY_UUID = "uuid";
    private final Activity activity;
    private final FirebaseFirestore db;
    private final CollectionReference userRef;
    private final SharedPreferences preferences;

    private String uuid;

    /**
     * Constructor for Profile
     *
     * @param activity Activity reference to the current activity
     */
    public Profile(Activity activity) {
        this.activity = activity;
        db = FirebaseFirestore.getInstance();
        userRef = db.collection("users");

        // Check if UUID exists in local storage in shared preferences for UUID
        preferences = activity.getApplicationContext().getSharedPreferences("com.example.nostack", Context.MODE_PRIVATE);
        uuid = preferences.getString(PREF_KEY_UUID, null);
    }

    /**
     * Get the UUID of the user
     *
     * @return Returns the UUID of the user
     */
    public String getUuid() {
        return uuid;
    }

    public boolean exists() {
        return uuid != null;
    }

    /**
     * Create a new user profile
     *
     * @param user The user data to create a new profile
     */
    public void createProfile(User user) {
        uuid = UUID.randomUUID().toString();
        preferences.edit().putString(PREF_KEY_UUID, uuid).apply();
        user.setUuid(uuid);

        // Add a new document with the UUID as the document ID
        userRef.document(uuid).set(user)
                .addOnSuccessListener(unused -> {
                    UserViewModel userViewModel = new ViewModelProvider((AppCompatActivity) activity).get(UserViewModel.class);
                    userViewModel.setUser(user);
                    Toast.makeText(activity, "New user profile created.", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> Log.w("Profile Class", "Error creating user profile", e));
    }

    /**
     * Retrieve user profile from firestore by UUID
     * Then store the user data in the ViewModel state
     *
     * @param uuid UUID of the user
     */
    public CompletableFuture<Boolean> retrieveProfile(String uuid) {
        DocumentReference docRef = userRef.document(uuid);
        CompletableFuture<Boolean> res = new CompletableFuture<>();

        docRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                if (document.exists()) {
                    User user = document.toObject(User.class);

                    Log.d("Profile class", "Image" + user.getProfileImageUrl());
                    if (user != null) {
                        updateUserFields(user);
                        UserViewModel userViewModel = new ViewModelProvider((AppCompatActivity) activity).get(UserViewModel.class);
                        userViewModel.setUser(this);
                        Toast.makeText(activity, "Welcome, " + getFirstName(), Toast.LENGTH_SHORT).show();
                        res.complete(true);
                    } else {
                        Log.w("Profile class", "Retrieved user data is null");
                        res.complete(false);
                    }
                } else {
                    Log.w("Profile class", "User does not exist: " + uuid);
                    res.complete(false);
                }
            } else {
                Log.w("Profile class", "Error retrieving user profile:", task.getException());
                res.completeExceptionally(task.getException());
            }
        });

        return res;
    }

    /**
     * Updates the Profile instance's fields with the retrieved user data.
     *
     * @param user User data retrieved from Firestore
     */
    private void updateUserFields(User user) {
        setFirstName(user.getFirstName());
        setEmailAddress(user.getEmailAddress());
        setPhoneNumber(user.getPhoneNumber());
        setLastName(user.getLastName());
        setUuid(user.getUuid());
        setUsername(user.getUsername());
        setProfileImageUrl(user.getProfileImageUrl());
        setRole(user.getRole());
    }
}
