package com.example.nostack.viewmodels;

import android.location.Location;
import android.net.Uri;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.nostack.controllers.EventController;
import com.example.nostack.controllers.ImageController;
import com.example.nostack.controllers.QrCodeController;
import com.example.nostack.handlers.CurrentUserHandler;
import com.example.nostack.models.Event;
import com.example.nostack.models.QrCode;
import com.example.nostack.services.ImageUploader;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;

import java.util.ArrayList;
import java.util.Currency;
import java.util.Date;
import java.util.List;

public class EventViewModel extends ViewModel {
    private final MutableLiveData<List<Event>> allEventsLiveData = new MutableLiveData<>();
    private final MutableLiveData<List<Event>> attendeeEventsLiveData = new MutableLiveData<>();
    private final MutableLiveData<List<Event>> organizerEventsLiveData = new MutableLiveData<>();
    private final MutableLiveData<Event> eventLiveData = new MutableLiveData<>();
    private final MutableLiveData<String> errorLiveData = new MutableLiveData<>();
    private final EventController eventController = EventController.getInstance();
    private final ImageController imageController = ImageController.getInstance();
    private final QrCodeController qrCodeController = QrCodeController.getInstance();
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

    public void addEvent(Event event, Boolean reuseQr, @Nullable Uri imageUri) {
        event.setCreatedDate(new Date());
        Runnable addEventRunnable = () -> {
            if (!reuseQr) {
                QrCode qrCode = new QrCode(event.getId());
                qrCodeController.addQrCode(qrCode)
                        .addOnSuccessListener(aVoid -> {
                            event.setCheckInQr(qrCode.getId());
                            addEventToFirestore(event);
                        }).addOnFailureListener(e -> {
                            Log.e("EventViewModel", "Error adding qr code", e);
                            errorLiveData.postValue(e.getMessage());
                        });
            } else {
                addEventToFirestore(event);
            }
        };

        if (imageUri != null) {
            String storagePath = "event/banner";
            imageController.addImage(storagePath, imageUri)
                    .addOnSuccessListener(imageUrl -> {
                        event.setEventBannerImgUrl(imageUrl);
                        addEventRunnable.run();
                    }).addOnFailureListener(e -> {
                        Log.e("EventViewModel", "Error uploading image", e);
                        errorLiveData.postValue(e.getMessage());
                    });
        } else {
            addEventRunnable.run();
        }
        fetchEvent(event.getId());
    }

    public void updateEvent(Event event, @Nullable Uri imageUri) {
        event.setCreatedDate(new Date());
        Runnable addEventRunnable = () -> eventController.addEvent(event)
                .addOnSuccessListener(a -> {
                    String userId = currentUserHandler.getCurrentUserId();
                    fetchAllEvents();
                    fetchOrganizerEvents(userId);
                }).addOnFailureListener( e-> {
                    Log.e("EventViewModel", "Error adding event", e);
                    errorLiveData.postValue(e.getMessage());
                });

        if (imageUri != null) {
            String storagePath = "event/banner";
            imageController.addImage(storagePath, imageUri)
                    .addOnSuccessListener(imageUrl -> {
                        event.setEventBannerImgUrl(imageUrl);
                        addEventRunnable.run();
                    }).addOnFailureListener(e -> {
                        Log.e("EventViewModel", "Error uploading image", e);
                        errorLiveData.postValue(e.getMessage());
                    });
        } else {
            addEventRunnable.run();
        }

        fetchEvent(event.getId());
    }

    public void deleteEvent(String eventId) {
        eventController.deleteEvent(eventId)
                .addOnSuccessListener(aVoid -> getAllEvents())
                .addOnFailureListener(e -> {
                    // Handle failure
                });
    }

    public Task<Void> registerToEvent(String userId, String eventId) {
        return eventController.registerToEvent(eventId, userId);
    }

    public Task<Void> unregisterToEvent(String userId, String eventId) {
        return eventController.unregisterToEvent(eventId, userId);
    }

    public Task<Void> eventCheckIn(String userId, String eventId, @Nullable Location location) {
        return eventController.eventCheckIn(userId, eventId, location);
    }

    public void endEvent(String eventId, String userId) {
        eventController.endEvent(eventId)
                .addOnSuccessListener(aVoid -> {
                    fetchEvent(eventId);
                    fetchOrganizerEvents(userId);
                })
                .addOnFailureListener(e -> {
                    Log.e("EventViewModel", "Error ending event", e);
                    errorLiveData.postValue(e.getMessage());
                });
    }

    private void addEventToFirestore(Event event) {
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
}
