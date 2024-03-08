package com.example.nostack;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.example.nostack.model.User.User;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
/**
 * The main activity for the application
 */
public class MainActivity extends AppCompatActivity {
    private FirebaseFirestore db;
    private CollectionReference userRef;
    private ArrayList<User> attendeeList;

    /**
     * This method is called when the activity is being created and then sets the view for the activity
     * @param savedInstanceState If the activity is being re-created from
     * a previous saved state, this is the state.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);



    }


}





