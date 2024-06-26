package com.example.nostack.controllers;

import android.location.Location;
import android.util.Log;

import androidx.annotation.Nullable;

import com.example.nostack.handlers.CurrentUserHandler;
import com.example.nostack.models.Event;
import com.example.nostack.models.Attendance;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.TaskCompletionSource;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.Transaction;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Controller class for handling events
 */
public class EventController {
    private static EventController singleInstance = null;
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    private final CollectionReference eventCollectionReference = FirebaseFirestore.getInstance().collection("events");
    private final CollectionReference attendanceCollectionReference = FirebaseFirestore.getInstance().collection("attendance");
    private final AttendanceController attendanceController = AttendanceController.getInstance();
    private final QrCodeController qrCodeController = QrCodeController.getInstance();
    private final CurrentUserHandler currentUserHandler = CurrentUserHandler.getSingleton();

    /**
     * Get the instance of the EventController
     * @return EventController The instance of the EventController
     */
    public static EventController getInstance() {
        if (singleInstance == null) {
            singleInstance = new EventController();
        }
        return singleInstance;
    }

    /**
     * Empty public constructor
     */
    public EventController() {
    }

    /**
     * Get all events
     * @return Task<QuerySnapshot> The event collection
     */
    public Task<QuerySnapshot> getAllEvents() {
        return eventCollectionReference
                .orderBy("startDate", Query.Direction.ASCENDING)
                .get();
    }

    /**
     * Get all events that are active by organizer id
     * @param organizerId The organizer id
     * @return Task<QuerySnapshot> The event collection
     */
    public Task<QuerySnapshot> getOrganizerEvents(String organizerId) {
        return eventCollectionReference
                .whereEqualTo("organizerId", organizerId)
                .orderBy("startDate", Query.Direction.ASCENDING)
                .get();
    }

    /**
     * Get all events that are active by attendee id
     * @param attendeeId The attendee id
     * @return Task<QuerySnapshot> The event collection
     */
    public Task<QuerySnapshot> getAttendeeEvents(String attendeeId) {
        return eventCollectionReference
                .whereArrayContains("attendees", attendeeId)
                .orderBy("startDate", Query.Direction.ASCENDING)
                .get();
    }

    /**
     * Get event by id
     * @param eventId The event id
     * @return Task<DocumentSnapshot> The event document
     */
    public Task<DocumentSnapshot> getEvent(String eventId) {
        return eventCollectionReference.document(eventId).get();
    }

    /**
     * Add an event
     * @param newEvent The event to add
     * @return void
     */
    public Task<Void> addEvent(Event newEvent) {
        return eventCollectionReference.document(newEvent.getId()).set(newEvent);
    }

    /**
     * Register the current user to an event
     * @param eventId The event id
     * @return void
     */
    public Task<Void> registerToEvent(String eventId) {
        String userId = currentUserHandler.getCurrentUserId();
        return registerToEvent(eventId, userId);
    }

    /**
     * Register a user to an event
     * @param eventId The event id
     * @param userId The user id
     * @return void
     */
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

    /**
     * Unregister the current user from an event
     * @param eventId The event id
     * @param userId The user id
     * @return void
     */
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

    /**
     * Check in the current user to an event
     * @param eventId The event id
     * @return void
     */
    public Task<Void> eventCheckIn(String eventId) {
        return eventCheckIn(currentUserHandler.getCurrentUserId(), eventId, null);
    }

    /**
     * Check in a user to an event
     * @param userId The user id
     * @param eventId The event id
     * @param location The location
     * @return void
     */
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
                        Exception e = task.getException();
                        String errorMessage = e != null ? e.getMessage() : "Unknown error";
                        throw new RuntimeException(errorMessage);
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

    /**
     * Update the event
     * @param event The event to update
     * @return void
     */
    public Task<Void> updateEvent(Event event) {
        return eventCollectionReference.document(event.getId()).set(event);
    }

    /**
     * Remove the event image
     * @param eventId The event id
     * @return void
     */
    public Task<Void> removeEventImage(String eventId) {
        DocumentReference eventRef = eventCollectionReference.document(eventId);

        return eventRef.update("eventBannerImgUrl", null)
                .addOnSuccessListener(aVoid -> Log.d("EventController", "Event image successfully removed."))
                .addOnFailureListener(e -> Log.e("EventController", "Failed to remove event image.", e));
    }

    /**
     * Reactivate an event that has ended.
     * @param eventId The event id
     * @return void
     */
    public Task<Void> reactivateEvent(String eventId) {
        DocumentReference eventRef = eventCollectionReference.document(eventId);
        Map<String, Object> updates = new HashMap<>();
        updates.put("active", true);
        return eventRef.update(updates);
    }

    /**
     * End an event.
     * @param eventId The event id
     * @return void
     */
    public Task<Void> endEvent(String eventId) {
        DocumentReference eventRef = eventCollectionReference.document(eventId);
        Map<String, Object> updates = new HashMap<>();
        updates.put("active", false);
        return eventRef.update(updates);
    };

    /**
     * Get the FCM tokens of all attendees of an event.
     * @param eventId The event id
     * @return Task<List<String>> The list of FCM tokens
     */
    public Task<List<String>> getEventAttendeesFcmTokens(String eventId) {
        UserController userController = UserController.getInstance();
        DocumentReference eventRef = eventCollectionReference.document(eventId);
        return eventRef.get().continueWithTask(task -> {
            if (!task.isSuccessful() || task.getResult() == null) {
                Log.d("EventController", "(getEventAttendeesFcmTokens) Failed to get event details.");
                throw new RuntimeException("Failed to get event details.");
            }
            DocumentSnapshot eventSnapshot = task.getResult();
            List<String> attendees = (List<String>) eventSnapshot.get("attendees");
            if (attendees == null) {
                Log.d("EventController", "(getEventAttendeesFcmTokens) No attendees for this event.");
                throw new RuntimeException("No attendees for this event.");
            }
            List<Task<String>> tasks = new ArrayList<>();
            for (String userId : attendees) {
                tasks.add(userController.getUserFcmToken(userId));
            }
            return Tasks.whenAllSuccess(tasks);
        }).continueWith(task -> {
            if (!task.isSuccessful()) {
                Log.d("EventController", "(getEventAttendeesFcmTokens) Failed to get user details.");
                throw new RuntimeException("Failed to get user details.");
            }
            List<?> result = task.getResult();
            if (result == null) {
                return new ArrayList<String>();
            }
            List<String> fcmTokens = new ArrayList<>();
            for (Object object : result) {
                if (object instanceof String) {
                    fcmTokens.add((String) object);
                } else {
                    Log.d("EventController", "(getEventAttendeesFcmTokens) Expected a string.");
                }
            }
            return fcmTokens;
        });
    }


    /**
     * Delete an event and unregister all attendees.
     * @param eventId The event id
     * @return void
     */
    public Task<Void> deleteEvent(String eventId) {
        DocumentReference eventRef = eventCollectionReference.document(eventId);
        return eventRef.get().continueWithTask(task -> {
            if (!task.isSuccessful() || task.getResult() == null) {
                Log.d("EventController", "(deleteEvent) Failed to get event details.");
                throw new RuntimeException("Failed to get event details.");
            }
            DocumentSnapshot eventSnapshot = task.getResult();

            // Unregister attendees
            List<String> attendees = (List<String>) eventSnapshot.get("attendees");
            if (attendees != null) {
                List<Task<Void>> tasks = new ArrayList<>();
                for (String userId : attendees) {
                    tasks.add(unregisterToEvent(eventId, userId));
                }
                return Tasks.whenAll(tasks);
            }
            else{
                Log.d("EventController", "(deleteEvent) No attendees to unregister.");
            }

            return Tasks.whenAll(new ArrayList<Task<Void>>());
        }).continueWithTask(task -> {
            if (task.isSuccessful()) {
                return eventRef.delete();
            } else {
                Log.d("EventController", "(deleteEvent) Failed to unregister attendees.");
                throw new RuntimeException("Failed to unregister attendees.");
            }
        });
    }

    /**
     * Delete all events created by a user.
     * @param userId The user id
     * @return void
     */
    public Task<Void> deleteEventsByUser(String userId) {
        return getOrganizerEvents(userId).continueWithTask(task -> {
            if (!task.isSuccessful()) {
                Log.d("EventController", "(deleteEventsByUser) Failed to get events.");
                throw new RuntimeException("Failed to get events.");
            }
            List<Task<Void>> tasks = new ArrayList<>();
            for (DocumentSnapshot eventSnapshot : task.getResult().getDocuments()) {
                String eventId = eventSnapshot.getId();
                tasks.add(deleteEvent(eventId));
            }
            return Tasks.whenAll(tasks);
        });
    }

    /**
     * get User Announcement
     * @param eventId The event id
     * @return Task<ArrayList<HashMap<String, String>>> The list of announcements
     */
    public Task<ArrayList<HashMap<String, String>>> getUserAnnouncement(String eventId) {
        TaskCompletionSource<ArrayList<HashMap<String, String>>> taskCompletionSource = new TaskCompletionSource<>();
        DocumentReference userRef = eventCollectionReference.document(eventId);

        userRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                if (document.exists()) {
                    // Extract the announcement HashMap from the document
                    ArrayList<HashMap<String, String>> announcement = (ArrayList<HashMap<String, String>>) document.get("announcements");
                    if (announcement != null) {
                        taskCompletionSource.setResult(announcement);
                        Log.d("UserController", "User announcement successfully retrieved.");
                    } else {
                        // If there's no announcement data
                        taskCompletionSource.setResult(new ArrayList<HashMap<String, String>>()); // Return an empty HashMap
                        Log.d("UserController", "User does not have an announcement.");
                    }
                } else {
                    taskCompletionSource.setException(new Exception("No such user."));
                    Log.e("UserController", "Failed to retrieve user announcement. User does not exist.");
                }
            } else {
                taskCompletionSource.setException(task.getException());
                Log.e("UserController", "Failed to retrieve user announcement.", task.getException());
            }
        });

        return taskCompletionSource.getTask();
    }

    /**
     * Add an announcement to an event
     * @param eventId The event id
     * @param announcement The announcement to add
     * @return void
     */
    public Task<Void> addAnnouncement(String eventId, HashMap<String, String> announcement) {
        return eventCollectionReference.document(eventId)
                .update("announcements", FieldValue.arrayUnion(announcement));
    }
}