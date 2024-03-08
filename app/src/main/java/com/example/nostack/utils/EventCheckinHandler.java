package com.example.nostack.utils;

import android.util.Log;

import androidx.annotation.NonNull;

import com.example.nostack.model.Events.Event;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
/**
 * Handles the check-in process for an event
 */
public class EventCheckinHandler {
    private FirebaseFirestore db;
    private CollectionReference attendanceRef;
    private CollectionReference eventsRef;
    public EventCheckinHandler() {
        db = FirebaseFirestore.getInstance();
        attendanceRef = db.collection("attendance");
        eventsRef = db.collection("events");
    }
    /**
     * Checks in a user to an event
     * @param eventId The ID of the event
     * @param userId The ID of the user
     */
    public void checkInUser(String eventId, String userId) {

        DocumentReference eventDocRef = eventsRef.document(eventId);
        DocumentReference attendanceDocRef = attendanceRef.document(Attendance.buildAttendanceId(eventId, userId));
        eventDocRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        Event event = document.toObject(Event.class);
                        boolean attendeeAdded = event.addAttendee(userId);
                        if (attendeeAdded) {
                            eventDocRef.set(event);
                            attendanceDocRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                    if (task.isSuccessful()) {
                                        DocumentSnapshot attendanceDoc = task.getResult();
                                        if (attendanceDoc.exists()) {
                                            Attendance attendance = attendanceDoc.toObject(Attendance.class);
                                            attendance.checkIn();
                                            attendanceDocRef.set(attendance);
                                        } else {
                                            Attendance attendance = new Attendance(userId, eventId);
                                            attendanceRef
                                                    .document(attendance.getId())
                                                    .set(attendance)
                                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                        @Override
                                                        public void onSuccess(Void unused) {
                                                            Log.w("Firestore", "New attendance successfully added!");
                                                        }
                                                    })
                                                    .addOnFailureListener(e -> Log.w("Firestore", "Error creating attendance", e));
                                        }
                                    } else {
                                        Log.d("Attendance", "Error getting attendance");
                                    }
                                }
                            });
                        } else {
                            throw new RuntimeException(event.getName() + " is at full capacity");
                        }

                    } else {
                        Log.d("User Check-in", "Event with ID" + eventId + "does not exist.");
                    }
                }
            }
        });
    }
}
