package com.example.nostack.utils;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

public class EventCheckinHandler {
    private FirebaseFirestore db;
    private CollectionReference attendanceRef;
    public EventCheckinHandler() {
        db = FirebaseFirestore.getInstance();
        attendanceRef = db.collection("attendance");
    }
}
