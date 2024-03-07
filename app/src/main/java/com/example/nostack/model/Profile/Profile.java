package com.example.nostack.model.Profile;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.util.Log;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.example.nostack.model.State.UserViewModel;
import com.example.nostack.model.User.User;
import com.example.nostack.utils.GenerateProfileImage;
import com.example.nostack.utils.Image;
import com.example.nostack.utils.ImageUploader;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;

/*
 * User profile class
 * 
 * Checks if user is logged in, if not, generate a new UUID to store in local storage & firestore
 * If user is logged in, retrieve user data from firestore by UUID
 * 
 */
public class Profile extends User{
    private static final String PREF_KEY_UUID = "uuid";
    private final Activity activity;
    private final FirebaseFirestore db;
    private final CollectionReference userRef;
    private final SharedPreferences preferences;

    private String uuid;

    /**
     * Constructor for Profile
     * @param activity Activity reference to the current activity
     */
    public Profile(Activity activity){
        this.activity = activity;
        db = FirebaseFirestore.getInstance();
        userRef = db.collection("users");

        // Check if UUID exists in local storage in shared preferences for UUID
        preferences = activity.getApplicationContext().getSharedPreferences("com.example.nostack", Context.MODE_PRIVATE);
        uuid = preferences.getString(PREF_KEY_UUID, null);


//        if (uuid == null) {
//            // If UUID does not exist, create a new profile
//            createProfile();
//        }
//        else{
//            // Retrieve user data from firestore by checking if Document ID of the UUID exists
//            retrieveProfile(uuid);
//        }
    }

    public String getUuid() {
        return uuid;
    }

    public boolean exists(){
        return uuid != null;
    }

    /**
     * Create a new user profile
     */
    public void createProfile(User user){
        uuid = UUID.randomUUID().toString();
        preferences.edit().putString(PREF_KEY_UUID, uuid).apply();

        // Add new user to firestore
        // TODO: Profile set up page to remove the placeholder values
//        User user = new User(
//            "First Name",
//            "Last Name",
//            "username_placeholder",
//            getEmailAddress(),
//            getPhoneNumber(),
//            uuid
//        );

        user.setUuid(uuid);

        // Add a new document with the UUID as the document ID
        userRef.document(uuid).set(user)
            .addOnSuccessListener(unused -> {
                UserViewModel userViewModel = new ViewModelProvider((AppCompatActivity) activity).get(UserViewModel.class);
                userViewModel.setUser(user);
                Snackbar.make(activity.findViewById(android.R.id.content), "New user profile created.", Snackbar.LENGTH_LONG).show();

            })
            .addOnFailureListener(e -> Log.w("Profile Class", "Error creating user profile", e));
    }

    /**
     * Retrieve user profile from firestore by UUID
     * Then store the user data in the ViewModel state
     *
     * @param uuid UUID of the user
     */
    public CompletableFuture<Boolean> retrieveProfile(String uuid){
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
                        Snackbar.make(activity.findViewById(android.R.id.content), "Welcome, " + getFirstName(), Snackbar.LENGTH_LONG).show();
                        res.complete(true);
                    }
                    else {
                        Log.w("Profile class", "Retrieved user data is null");
                        res.complete(false);
                    }
                } else {
                    Log.w("Profile class", "User does not exist: " + uuid);
//                    createProfile();
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
    }
}
