package com.example.nostack.controllers;

import android.location.Location;

import com.example.nostack.handlers.CurrentUserHandler;
import com.example.nostack.handlers.LocationHandler;
import com.example.nostack.models.Attendance;
import com.example.nostack.models.GeoLocation;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

public class AttendanceController {
    private static AttendanceController singleInstance = null;
    private final CollectionReference attendanceCollectionReference = FirebaseFirestore.getInstance().collection("attendance");
    private final CurrentUserHandler currentUserHandler = CurrentUserHandler.getSingleton();
    private final LocationHandler locationHandler = LocationHandler.getSingleton();

    public static AttendanceController getInstance() {
        if (singleInstance == null) {
            singleInstance = new AttendanceController();
        }
        return singleInstance;
    }

    public AttendanceController() {
    }

    public Task<QuerySnapshot> getAllAttendance() {
        return attendanceCollectionReference.get();
    }

    public Task<QuerySnapshot> getAttendanceByEvent(String eventId) {
        return attendanceCollectionReference.whereEqualTo("eventId", eventId).get();
    }

    public Task<QuerySnapshot>getAttendanceByUser() {
        return getAttendanceByUser(currentUserHandler.getCurrentUserId());
    }

    public Task<QuerySnapshot> getAttendanceByUser(String userId) {
        return attendanceCollectionReference.whereEqualTo("userId", userId).get();
    }

    public Task<DocumentSnapshot> getAttendanceById(String id) {
        return attendanceCollectionReference.document(id).get();
    }

    public Task<Void> createAttendance(String eventId) {
        return createAttendance(currentUserHandler.getCurrentUserId(), eventId);
    }

    public Task<Void> createAttendance(String userId, String eventId) {
        Attendance newAtt = new Attendance(userId, eventId);
        Location location = locationHandler.getLocation();
        if (location != null) {
            GeoLocation latlng = new GeoLocation(location.getLatitude(), location.getLongitude());
            newAtt.setGeoLocation(latlng);
        }
        return attendanceCollectionReference.document(newAtt.getId()).set(newAtt);
    }

    public Task<Void> attendanceCheckIn(String id) {
        return attendanceCollectionReference.document(id).update("numCheckIn", FieldValue.increment(1));
    }

    public Task<Void> deleteAttendance(String attendanceId) {
        return attendanceCollectionReference.document(attendanceId).delete();
    }
}