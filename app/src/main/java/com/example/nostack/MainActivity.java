package com.example.nostack;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;

import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import org.checkerframework.checker.units.qual.A;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    private FirebaseFirestore db;
    private CollectionReference userRef;
    private ArrayList<Attendee> attendeeList;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        db = FirebaseFirestore.getInstance();
        userRef = db.collection("users");

        attendeeList = new ArrayList<>();

        userRef.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                if (error != null) {
                    Snackbar.make(findViewById(android.R.id.content), "No users found", Snackbar.LENGTH_LONG).show();
                    Log.e("MainActivity", "onEvent: ", error);
                    return;
                }

                for (QueryDocumentSnapshot doc : value) {
                    if (doc.get("name") != null) {
                        Snackbar.make(findViewById(android.R.id.content), "Name: " + doc.get("name"), Snackbar.LENGTH_LONG).show();
                    }
                }
            }
        });

    }
}