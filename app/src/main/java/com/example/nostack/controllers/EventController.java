package com.example.nostack.controllers;

import android.location.Location;
import android.util.Log;

import androidx.annotation.Nullable;

import com.example.nostack.handlers.CurrentUserHandler;
import com.example.nostack.models.Event;
import com.example.nostack.models.Attendance;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.Transaction;

import java.util.Date;
import java.util.List;

public class EventController {
    private static EventController singleInstance = null;
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    private final CollectionReference eventCollectionReference = FirebaseFirestore.getInstance().collection("events");
    private final CollectionReference attendanceCollectionReference = FirebaseFirestore.getInstance().collection("attendance");
    private final AttendanceController attendanceController = AttendanceController.getInstance();
    private final CurrentUserHandler currentUserHandler = CurrentUserHandler.getSingleton();

    public static EventController getInstance() {
        if (singleInstance == null) {
            singleInstance = new EventController();
        }
        return singleInstance;
    }

    public EventController() {
    }

    public Task<QuerySnapshot> getAllEvents() {
        return eventCollectionReference
                .orderBy("createdDate", Query.Direction.DESCENDING)
                .get();
    }

    public Task<QuerySnapshot> getOrganizerEvents(String organizerId) {
        return eventCollectionReference
                .whereEqualTo("organizerId", organizerId)
                .orderBy("createdDate", Query.Direction.DESCENDING)
                .get();
    }

    public Task<QuerySnapshot> getAttendeeEvents(String attendeeId) {
        return eventCollectionReference
                .whereArrayContains("attendees", attendeeId)
                .orderBy("createdDate", Query.Direction.DESCENDING)
                .get();
    }

    public Task<DocumentSnapshot> getEvent(String eventId) {
        return eventCollectionReference.document(eventId).get();
    }

    public Task<Void> addEvent(Event newEvent) {
        return eventCollectionReference.document(newEvent.getId()).set(newEvent);
    }

    public Task<Void> registerToEvent(String eventId) {
        String userId = currentUserHandler.getCurrentUserId();
        return registerToEvent(eventId, userId);
    }

    public Task<Void> registerToEvent(String eventId, String userId) {
        DocumentReference eventRef = eventCollectionReference.document(eventId);

        return db.runTransaction((Transaction.Function<Void>) transaction -> {
            DocumentSnapshot eventSnapshot = transaction.get(eventRef);
            String eventName = eventSnapshot.getString("name");
            Boolean isActive = eventSnapshot.getBoolean("active");
            Date endDate = eventSnapshot.getDate("endDate");
            Date currentDate = new Date();
            if (isActive == null || !isActive || endDate.before(currentDate)) {
                throw new RuntimeException("The event " + eventName+  " is no longer active or has already ended.");
            }

            long maxCap = eventSnapshot.getLong("capacity");
            long currCap = eventSnapshot.getLong("currentCapacity");
            List<String> attendees = (List<String>) eventSnapshot.get("attendees");

            if ((maxCap > 0) && (currCap >= maxCap)) {
                throw new RuntimeException("The event" +eventName+" is at full capacity.");
            }

            if (attendees != null && attendees.contains(userId)) {
                throw new RuntimeException("The user has already registered for the event.");
            }

            transaction.update(eventRef, "currentCapacity", FieldValue.increment(1));
            transaction.update(eventRef, "attendees", FieldValue.arrayUnion(userId));
            return null;
        }).continueWithTask(task -> {
            if (task.isSuccessful()) {
                return attendanceController.createAttendance(userId, eventId, null);
            } else {
                Exception e = task.getException();
                String errorMessage = e != null ? e.getMessage() : "Unknown error";
                throw new RuntimeException("(Failed to create attendance) " + errorMessage);

            }
        }).addOnSuccessListener(aVOid -> {
            Log.d("Event Controller", "Successfully registered user.");
        }).addOnFailureListener(aVoid -> {
            Log.d("Event Controller", "Failed to register user.");
        });
    }

    public Task<Void> unregisterToEvent(String eventId, String userId) {
        DocumentReference eventRef = eventCollectionReference.document(eventId);
        return db.runTransaction(transaction -> {
            DocumentSnapshot eventSnapshot = transaction.get(eventRef);
            List<String> attendees = (List<String>) eventSnapshot.get("attendees");

            if (attendees != null && attendees.contains(userId)) {
                transaction.update(eventRef, "attendees", FieldValue.arrayRemove(userId));
                return null;
            } else {
                throw new RuntimeException("User is not registered for this event.");
            }
        }).continueWithTask(task -> {
            if (task.isSuccessful()) {
                String attendanceId = Attendance.buildAttendanceId(eventId, userId);
                return attendanceController.deleteAttendance(attendanceId);
            } else {
                throw new RuntimeException("Failed to delete attendance.");
            }
        }).addOnSuccessListener(aVoid -> {
            Log.d("Event Controller", "Successfully unregistered user from event.");
        }).addOnFailureListener(e -> {
            Log.e("Event Controller", "Failed to unregister user from event.", e);
        });
    }

    public Task<Void> eventCheckIn(String eventId) {
        return eventCheckIn(currentUserHandler.getCurrentUserId(), eventId, null);
    }

    public Task<Void> eventCheckIn(String userId, String eventId, @Nullable Location location) {
        return getEvent(eventId).continueWithTask(task -> {
            if (!task.isSuccessful() || task.getResult() == null) {
                throw new RuntimeException("Failed to get event details.");
            }

            DocumentSnapshot eventSnapshot = task.getResult();
            String eventName = eventSnapshot.getString("name");
            Boolean isActive = eventSnapshot.getBoolean("active");
            Date endDate = eventSnapshot.getDate("endDate");
            Date currentDate = new Date();
            if (isActive == null || !isActive || endDate.before(currentDate)) {
                throw new RuntimeException("The event " + eventName+  " is no longer active or has already ended.");
            }

            List<String> attendees = (List<String>) eventSnapshot.get("attendees");

            if (attendees != null && attendees.contains(userId)) {
                return attendanceController.attendanceCheckIn(Attendance.buildAttendanceId(eventId, userId), location);
            } else {
                // For now, if the user is not registered, when checking in, it automatically
                // registers them.
                return registerToEvent(eventId, userId).continueWithTask(registerTask -> {
                    if (!registerTask.isSuccessful()) {
                        throw new RuntimeException("Failed to register for the event before checking in.");
                    }
                    return attendanceController.attendanceCheckIn(Attendance.buildAttendanceId(eventId, userId), location);
                });
            }
        }).addOnSuccessListener(aVOid -> {
            Log.d("Event Controller", "Successfully checked-in user.");
        }).addOnFailureListener(aVoid -> {
            Log.d("Event Controller", "Failed to check-in user.");
        });
    }

    public Task<Void> updateEvent(Event event) {
        return eventCollectionReference.document(event.getId()).set(event);
    }

    public Task<Void> removeEventImage(String eventId) {
        DocumentReference eventRef = eventCollectionReference.document(eventId);

        return eventRef.update("eventBannerImgUrl", null)
                .addOnSuccessListener(aVoid -> Log.d("EventController", "Event image successfully removed."))
                .addOnFailureListener(e -> Log.e("EventController", "Failed to remove event image.", e));
    }

    public Task<Void> endEvent(String eventId) {
        return eventCollectionReference.document(eventId).update("active", false);
    };

    // TODO: Deleting an event, may be a little too nuanced, will be done later on.
    public Task<Void> deleteEvent(String eventId) {
        return Tasks.whenAll();
    }
}