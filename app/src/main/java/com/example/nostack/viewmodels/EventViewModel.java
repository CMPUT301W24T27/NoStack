package com.example.nostack.viewmodels;

import android.util.Log;

import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.nostack.controllers.EventController;
import com.example.nostack.handlers.CurrentUserHandler;
import com.example.nostack.models.Event;
import com.google.firebase.firestore.DocumentSnapshot;

import java.util.ArrayList;
import java.util.Currency;
import java.util.List;

public class EventViewModel extends ViewModel {
    private final MutableLiveData<List<Event>> allEventsLiveData = new MutableLiveData<>();
    private final MutableLiveData<List<Event>> attendeeEventsLiveData = new MutableLiveData<>();
    private final MutableLiveData<List<Event>> organizerEventsLiveData = new MutableLiveData<>();
    private final MutableLiveData<Event> eventLiveData = new MutableLiveData<>();
    private final MutableLiveData<String> errorLiveData = new MutableLiveData<>();
    private final EventController eventController = EventController.getInstance();
    private final CurrentUserHandler currentUserHandler = CurrentUserHandler.getSingleton();

    public LiveData<String> getErrorLiveData() {
        return errorLiveData;
    }

    public void clearErrorLiveData() {
        errorLiveData.setValue(null);
    }

    public void fetchEvent(String eventId) {
        eventController.getEvent(eventId)
                .addOnSuccessListener(documentSnapshot -> {
                    Event event = documentSnapshot.toObject(Event.class);
                    eventLiveData.postValue(event);
                }).addOnFailureListener(e -> {
                    Log.e("EventViewModel", "Error fetching event", e);
                    errorLiveData.postValue(e.getMessage());
                });
    }

    public LiveData<Event> getEvent() {
        return eventLiveData;
    }

    public void fetchAllEvents() {
        eventController.getAllEvents()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    List<Event> events = new ArrayList<>();
                    for (DocumentSnapshot document:queryDocumentSnapshots) {
                        Event event = document.toObject(Event.class);
                        events.add(event);
                        Log.d("EventViewModel", document.toObject(Event.class).getName());
                    }
                    allEventsLiveData.postValue(events);
                }).addOnFailureListener(e -> {
                    Log.e("EventViewModel", "Error fetching events", e);
                    errorLiveData.postValue(e.getMessage());
                });
    }

    public LiveData<List<Event>> getAllEvents() {
        return allEventsLiveData;
    }

    public void fetchAttendeeEvents(@Nullable String userId) {
        if (userId == null) {
            userId = currentUserHandler.getCurrentUserId();
        }
        eventController.getAttendeeEvents(userId)
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    List<Event> events = new ArrayList<>();
                    for (DocumentSnapshot document:queryDocumentSnapshots) {
                        Event event = document.toObject(Event.class);
                        events.add(event);
                        Log.d("EventViewModel", document.toObject(Event.class).getName());
                    }
                    attendeeEventsLiveData.postValue(events);
                }).addOnFailureListener(e -> {
                    Log.e("EventViewModel", "Error fetching events", e);
                    errorLiveData.postValue(e.getMessage());
                });
    }

    public LiveData<List<Event>> getAttendeeEvents() {
        return attendeeEventsLiveData;
    }

    public void fetchOrganizerEvents(@Nullable String userId) {
        if (userId == null) {
            userId = currentUserHandler.getCurrentUserId();
        }
        eventController.getOrganizerEvents(userId)
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    List<Event> events = new ArrayList<>();
                    for (DocumentSnapshot document:queryDocumentSnapshots) {
                        Event event = document.toObject(Event.class);
                        events.add(event);
                        Log.d("EventViewModel", document.toObject(Event.class).getName());
                    }
                    organizerEventsLiveData.postValue(events);
                }).addOnFailureListener(e -> {
                    Log.e("EventViewModel", "Error fetching events", e);
                    errorLiveData.postValue(e.getMessage());
                });
    }

    public LiveData<List<Event>> getOrganizerEvents() {
        return organizerEventsLiveData;
    }

    public void addEvent(Event event, String imageUri) {
        eventController.addEvent(event)
                .addOnSuccessListener(a -> {
                    String userId = currentUserHandler.getCurrentUserId();
                    fetchAllEvents();
                    fetchOrganizerEvents(userId);

                }).addOnFailureListener( e-> {
                    Log.e("EventViewModel", "Error adding event", e);
                    errorLiveData.postValue(e.getMessage());
                });
    }

    public void updateEvent(Event event) {
        eventController.updateEvent(event)
                .addOnSuccessListener(a -> {
                    String userId = currentUserHandler.getCurrentUserId();
                    fetchAllEvents();
                    fetchOrganizerEvents(userId);
                }).addOnFailureListener( e-> {
                    Log.e("EventViewModel", "Error updating event", e);
                    errorLiveData.postValue(e.getMessage());
                });
    }

    public void deleteEvent(String eventId) {
        eventController.deleteEvent(eventId)
                .addOnSuccessListener(aVoid -> getAllEvents())
                .addOnFailureListener(e -> {
                    // Handle failure
                });
    }

    public void registerToEvent(String userId, String eventId) {
        eventController.registerToEvent(eventId, userId)
                .addOnSuccessListener(aVoid -> {
                    fetchAttendeeEvents(userId);
                    fetchEvent(eventId);
                })
                .addOnFailureListener(e -> {
                    Log.e("EventViewModel", "Error registering to event", e);
                    errorLiveData.postValue(e.getMessage());
                });
    }

    public void unregisterToEvent(String userId, String eventId) {
        eventController.unregisterToEvent(eventId, userId)
                .addOnSuccessListener(aVoid -> {
                    fetchAttendeeEvents(userId);
                    fetchEvent(eventId);
                })
                .addOnFailureListener(e -> {
                    Log.e("EventViewModel", "Error unregistering to event", e);
                    errorLiveData.postValue(e.getMessage());
                });
    }

    public void eventCheckIn(String userId, String eventId) {
        eventController.eventCheckIn(userId, eventId)
                .addOnSuccessListener(aVoid -> {
                    fetchAttendeeEvents(userId);
                })
                .addOnFailureListener(e -> {
                    Log.e("EventViewModel", "Error checking in to event", e);
                    errorLiveData.postValue(e.getMessage());
                });
    }
}
