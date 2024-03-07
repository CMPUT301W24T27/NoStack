package com.example.nostack;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.example.nostack.model.Profile.Profile;
import com.example.nostack.model.User.Attendee;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    private FirebaseFirestore db;
    private CollectionReference userRef;
    private ArrayList<Attendee> attendeeList;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);



    }


}





