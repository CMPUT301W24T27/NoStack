package com.example.nostack.viewmodels;

import android.location.Location;
import android.net.Uri;
import android.util.Log;
import android.util.Pair;

import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.nostack.controllers.EventController;
import com.example.nostack.controllers.ImageController;
import com.example.nostack.controllers.QrCodeController;
import com.example.nostack.controllers.UserController;
import com.example.nostack.handlers.CurrentUserHandler;
import com.example.nostack.handlers.NotificationHandler;
import com.example.nostack.models.Event;
import com.example.nostack.models.QrCode;
import com.example.nostack.services.ImageUploader;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.auth.User;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import org.checkerframework.checker.units.qual.N;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Currency;
import java.util.Date;
import java.util.List;

public class EventViewModel extends ViewModel {
    private final MutableLiveData<List<Event>> allEventsLiveData = new MutableLiveData<>();
    private final MutableLiveData<List<Event>> attendeeEventsLiveData = new MutableLiveData<>();
    private final MutableLiveData<List<Event>> organizerEventsLiveData = new MutableLiveData<>();
    private final MutableLiveData<Pair<Event, Uri>> eventWithReuse = new MutableLiveData<>();
    private final MutableLiveData<Event> eventLiveData = new MutableLiveData<>();
    private final MutableLiveData<String> errorLiveData = new MutableLiveData<>();
    private final EventController eventController = EventController.getInstance();
    private final ImageController imageController = ImageController.getInstance();
    private final QrCodeController qrCodeController = QrCodeController.getInstance();
    private final UserController userController = UserController.getInstance();
    private final CurrentUserHandler currentUserHandler = CurrentUserHandler.getSingleton();
    private final NotificationHandler notificationHandler = NotificationHandler.getSingleton();

    /**
     * Delete event callback
     */
    public interface DeleteEventCallback {
        void onEventDeleted();
        void onEventDeleteFailed();
    }

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
                    if (event != null) {
                        eventLiveData.postValue(event);
                        return;
                    }
                    errorLiveData.postValue("Error fetching null event");
                    eventLiveData.postValue(null);
                }).addOnFailureListener(e -> {
                    Log.e("EventViewModel", "Error fetching event", e);
                    errorLiveData.postValue("Error fetching event");
                });
    }

    public LiveData<Event> getEvent() {
        return eventLiveData;
    }
    public void clearEventLiveData() {
        eventLiveData.setValue(null);
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
            String storagePath = "event/banner/";
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
        setEventWithReuse(null, null);
        fetchEvent(event.getId());
    }

    public interface UpdateImageCallback {
        void onImageUpdated(Uri imageUrl);
        void onImageUpdateFailed();
    }

    public void updateEvent(Event event, @Nullable Uri imageUri, @Nullable UpdateImageCallback callback) {
        event.setCreatedDate(new Date());
        Runnable addEventRunnable = () -> eventController.addEvent(event)
                .addOnSuccessListener(a -> {
                    String userId = currentUserHandler.getCurrentUserId();
                    fetchEvent(event.getId());
                }).addOnFailureListener( e-> {
                    Log.e("EventViewModel", "Error adding event", e);
                    errorLiveData.postValue(e.getMessage());
                });

        if (imageUri != null) {
            String storagePath = "event/banner/";
            imageController.addImage(storagePath, imageUri)
                    .addOnSuccessListener(imageUrl -> {
                        event.setEventBannerImgUrl(imageUrl);
                        addEventRunnable.run();
                        Uri imageURI = Uri.parse(imageUrl);
                        callback.onImageUpdated(imageURI);
                    }).addOnFailureListener(e -> {
                        Log.e("EventViewModel", "Error uploading image", e);
                        errorLiveData.postValue(e.getMessage());
                        callback.onImageUpdateFailed();
                    });
        } else {
            addEventRunnable.run();
        }
    }

    /**
     * Delete an event
     * @param event event to delete
     * @param callback callback to handle success or failure
     */
    public void deleteEvent(Event event, DeleteEventCallback callback) {
        eventController.deleteEvent(event.getId())
            .addOnSuccessListener(aVoid -> {
                getAllEvents();

                // Delete event banners
                if (event.getEventBannerImgUrl() != null) {
                    FirebaseStorage storage = FirebaseStorage.getInstance();
                    StorageReference storageRef = storage.getReferenceFromUrl(event.getEventBannerImgUrl());

                    storageRef.delete()
                        .addOnSuccessListener(a -> {
                            callback.onEventDeleted();
                        })
                        .addOnFailureListener(e -> {
                            Log.d("EventViewModel", "Error deleting event banner, image", e);
                            callback.onEventDeleteFailed();
                        });
                }
                else{
                    callback.onEventDeleted();
                }

            })
            .addOnFailureListener(e -> {
                // Handle failure
                Log.d("EventViewModel", "Error deleting event banner, event", e);

                callback.onEventDeleteFailed();
            });
    }

    public Task<Void> registerToEvent(String userId, String eventId) {
        return eventController.registerToEvent(eventId, userId);
    }

    public Task<Void> unregisterToEvent(String userId, String eventId) {
        return eventController.unregisterToEvent(eventId, userId);
    }

    public Task<Void> eventCheckIn(String userId, String eventId, @Nullable Location location) {
        return eventController.eventCheckIn(userId, eventId, location)
            .addOnSuccessListener(aVoid -> {
                eventController.getEvent(eventId)
                    .addOnCompleteListener(eventTask -> {
                        if (eventTask.isSuccessful()) {
                            DocumentSnapshot documentSnapshot = eventTask.getResult();
                            Event event;
                            if (documentSnapshot != null) {
                                event = documentSnapshot.toObject(Event.class);
                            } else {
                                event = null;
                            }
                            String organizerId = event.getOrganizerId();

                            userController.getUserFcmToken(organizerId)
                                .addOnCompleteListener(tokenTask -> {
                                    if (tokenTask.isSuccessful()) {
                                        String fcmToken = tokenTask.getResult();
                                        Log.d("EventViewModel", "Successfully retrieved organizer FCM token");
                                        notificationHandler.sendEventMilestoneNotification(fcmToken, event, userId);
                                    }
                                })
                                .addOnFailureListener(e -> {
                                    Log.e("EventViewModel", "Error sending notification", e);
                                    errorLiveData.postValue(e.getMessage());
                                });
                        }
                    });
            });
    }

    public void endEvent(String eventId, String userId) {
        eventController.endEvent(eventId)
                .addOnSuccessListener(aVoid -> {
                    fetchEvent(eventId);
                    fetchOrganizerEvents(userId);
                    qrCodeController.deactivateQrCodeByEventId(eventId)
                            .addOnSuccessListener(a -> {
                                Log.d("EventViewModel", "Successfully deactivated qr code");
                            }).addOnFailureListener(e -> {
                                Log.e("EventViewModel", "Error deactivating qr code", e);
                                errorLiveData.postValue(e.getMessage());
                            });
                })
                .addOnFailureListener(e -> {
                    Log.e("EventViewModel", "Error ending event", e);
                    errorLiveData.postValue(e.getMessage());
                });
    }

    public void reactivateEvent(String eventId, String userId) {
        eventController.reactivateEvent(eventId)
                .addOnSuccessListener(aVoid -> {
                    fetchEvent(eventId);
                    fetchOrganizerEvents(userId);
                    qrCodeController.reactivateQrCodeByEventId(eventId)
                            .addOnSuccessListener(a -> {
                                Log.d("EventViewModel", "Successfully reactivated qr code");
                            }).addOnFailureListener(e -> {
                                Log.e("EventViewModel", "Error reactivating qr code", e);
                                errorLiveData.postValue(e.getMessage());
                            });
                })
                .addOnFailureListener(e -> {
                    Log.e("EventViewModel", "Error reactivating event", e);
                    errorLiveData.postValue(e.getMessage());
                });
    }

    public void setEventWithReuse(Event event, @Nullable Uri uri) {
        Pair<Event, Uri> pair = new Pair<>(event, uri);
        eventWithReuse.postValue(pair);
    }

    public LiveData<Pair<Event, Uri>> getEventWithReuse() {
        return eventWithReuse;
    }

    public void addEventWithReuse() {
        addEvent(eventWithReuse.getValue().first, true, eventWithReuse.getValue().second);
        qrCodeController.reuseQrCode(eventWithReuse.getValue().first.getId(), eventWithReuse.getValue().first.getCheckInQrId())
                .addOnSuccessListener(a -> {
                    Log.d("EventViewModel", "Successfully reused qr code");
                }).addOnFailureListener(e -> {
                    Log.e("EventViewModel", "Error reusing qr code", e);
                    errorLiveData.postValue(e.getMessage());
                });
    }

    public void updateEventWithReuse(String qrCodeId) {
        Pair<Event, Uri> currentPair = eventWithReuse.getValue();
        if (currentPair != null) {
            Event currentEvent = currentPair.first;
            currentEvent.setCheckInQr(qrCodeId);
            Pair<Event, Uri> updatedPair = new Pair<>(currentEvent, currentPair.second);
            eventWithReuse.postValue(updatedPair);
        }
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
