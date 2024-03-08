package com.example.nostack.utils;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.provider.Settings;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.nostack.model.Events.Event;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.android.gms.maps.model.LatLng;

/**
 * Handles the check-in process for an event
 */
public class EventCheckinHandler {
    public static final int LOCATION_PERMISSION_REQUEST_CODE = 1001;
    private FirebaseFirestore db;
    private CollectionReference attendanceRef;
    private CollectionReference eventsRef;
    private LocationManager locationManager;
    private Context context;
    private Activity activity;
    private LocationHandler locationHandler;

    public EventCheckinHandler(Activity activity, Context context) {
        this.db = FirebaseFirestore.getInstance();
        this.attendanceRef = db.collection("attendance");
        this.eventsRef = db.collection("events");
        this.activity = activity;
        this.context = context;
        this.locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        this.locationHandler = new LocationHandler(this.context, this.activity, this.locationManager);
    }
    /**
     * Checks in a user to an event
     * @param eventId The ID of the event
     * @param userId The ID of the user
     */
    public void checkInUser(String eventId, String userId) {

        Location location = locationHandler.getLocation();

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

                                            if (location != null) {
                                                GeoLocation latlng = new GeoLocation(location.getLatitude(), location.getLongitude());
                                                attendance.setGeoLocation(latlng);
                                            }
                                            attendanceDocRef.set(attendance);
                                        } else {
                                            Attendance attendance = new Attendance(userId, eventId);
                                            if (location != null) {
                                                GeoLocation latlng = new GeoLocation(location.getLatitude(), location.getLongitude());
                                                attendance.setGeoLocation(latlng);
                                            }
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
