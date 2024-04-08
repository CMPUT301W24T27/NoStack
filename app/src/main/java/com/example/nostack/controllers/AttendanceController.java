package com.example.nostack.controllers;

import android.location.Location;

import androidx.annotation.Nullable;

import com.example.nostack.handlers.CurrentUserHandler;
import com.example.nostack.models.Attendance;
import com.example.nostack.models.GeoLocation;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

/**
 * Class for handling attendance
 */
public class AttendanceController {
    private static AttendanceController singleInstance = null;
    private final CollectionReference attendanceCollectionReference = FirebaseFirestore.getInstance().collection("attendance");
    private final CurrentUserHandler currentUserHandler = CurrentUserHandler.getSingleton();

    /**
     * Get the instance of the AttendanceController
     * @return void
     */
    public static AttendanceController getInstance() {
        if (singleInstance == null) {
            singleInstance = new AttendanceController();
        }
        return singleInstance;
    }

    /**
     * Empty public constructor
     */
    public AttendanceController() {
    }

    /**
     * Get all attendance
     * @return Task<QuerySnapshot> The attendance collection
     */
    public Task<QuerySnapshot> getAllAttendance() {
        return attendanceCollectionReference.get();
    }

    /**
     * Get attendance by event
     * @param eventId
     * @return Task<QuerySnapshot> The attendance by event
     */
    public Task<QuerySnapshot> getAttendanceByEvent(String eventId) {
        return attendanceCollectionReference.whereEqualTo("eventId", eventId).get();
    }

    /**
     * Get attendance by user
     * @return Task<QuerySnapshot> The attendance by user
     */
    public Task<QuerySnapshot>getAttendanceByUser() {
        return getAttendanceByUser(currentUserHandler.getCurrentUserId());
    }

    /**
     * Get attendance by user
     * @param userId The user id
     * @return Task<QuerySnapshot> The attendance by user
     */
    public Task<QuerySnapshot> getAttendanceByUser(String userId) {
        return attendanceCollectionReference.whereEqualTo("userId", userId).get();
    }

    /**
     * Get attendance by id
     * @param id The attendance id
     * @return Task<DocumentSnapshot> The attendance by id
     */
    public Task<DocumentSnapshot> getAttendanceById(String id) {
        return attendanceCollectionReference.document(id).get();
    }

    /**
     * Create attendance
     * @param eventId The event id
     * @return void
     */
    public Task<Void> createAttendance(String eventId) {
        return createAttendance(currentUserHandler.getCurrentUserId(), eventId, null);
    }

    /**
     * Create attendance
     * @param userId The user id
     * @param eventId The event id
     * @param location The location
     * @return void
     */
    public Task<Void> createAttendance(String userId, String eventId, @Nullable Location location) {
        Attendance newAtt = new Attendance(userId, eventId);
        if (location != null) {
            GeoLocation latlng = new GeoLocation(location.getLatitude(), location.getLongitude());
            newAtt.setGeoLocation(latlng);
        }
        return attendanceCollectionReference.document(newAtt.getId()).set(newAtt);
    }

    /**
     * Check in attendance
     * @param id The attendance id
     * @param location The location
     * @return void
     */
    public Task<Void> attendanceCheckIn(String id, @Nullable Location location) {
        if (location != null) {
            GeoLocation latlng = new GeoLocation(location.getLatitude(), location.getLongitude());
            return attendanceCollectionReference.document(id).update("numCheckIn", FieldValue.increment(1), "geoLocation", latlng);
        }
        return attendanceCollectionReference.document(id).update("numCheckIn", FieldValue.increment(1));
    }

    /**
     * Delete attendance
     * @param attendanceId The attendance id
     * @return void
     */
    public Task<Void> deleteAttendance(String attendanceId) {
        return attendanceCollectionReference.document(attendanceId).delete();
    }

    /**
     * Get present attendance
     * @param eventId The event id
     * @return Task<QuerySnapshot> The present attendance
     */
    public Task<QuerySnapshot> getPresentAttendance(String eventId) {
        return attendanceCollectionReference
                .whereEqualTo("eventId", eventId).whereGreaterThan("numCheckIn", 0)
                .get();
    }
}