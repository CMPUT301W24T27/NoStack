package com.example.nostack.controllers;

import com.example.nostack.handlers.CurrentUserHandler;
import com.example.nostack.model.Events.Event;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.zxing.qrcode.encoder.QRCode;

import java.util.ArrayList;
import java.util.HashMap;

public class EventController {
    private static EventController singleInstance = null;
    private final CollectionReference eventCollectionReference = FirebaseFirestore.getInstance().collection("events");
    private final CurrentUserHandler currentUserHandler = CurrentUserHandler.getSingleton();

    /**
     * Singleton constructor
     * @return returns the singular object
     */
    public static EventController getInstance() {
        if (singleInstance == null) {
            singleInstance = new EventController();
        }
        return singleInstance;
    }

    public EventController() {}

    public Task<QuerySnapshot> getAllEvents() {
        return eventCollectionReference.get();
    }

    public Task<QuerySnapshot> getOrganizerEvents(String organizerId) {
        return eventCollectionReference.whereEqualTo("organizerId", organizerId).get();
    }

    public Task<QuerySnapshot> getAtte3ndeeEvents(String attendeeId) {
        return eventCollectionReference.whereArrayContains("attendees", attendeeId).get();
    }

    public Task<DocumentSnapshot> getEvent(String eventId) {
        return eventCollectionReference.document(eventId).get();
    }

    public Task<Void> addEvent(Event newEvent) {
        return eventCollectionReference.document(newEvent.getId()).set(newEvent);
    }

    public Task<Void> checkAttendeeIn(Event event) {
        String userId = currentUserHandler.getCurrentUserId();
        return checkAttendeeIn(event, userId);
        return;
    }

    public Task<Void> checkAttendeeIn(Event event, String userId) {
        return;
    }




    public Task<Void> addUser(User newUserToAdd) {
        return userCollectionReference
                .document(newUserToAdd.getUsername())
                .set(newUserToAdd);
    }
    public
    public class EventContro {
        private static En
        private static F3ireStoreController singleInstance = null;

        /**
         * Singleton constructor
         * @return returns the singular object
         */
        public static FireStoreController getInstance() {
            System.out.println("FirestoreID = " + CurrentUserHelper.getInstance().getFirebaseId());
            if (singleInstance == null) {
                singleInstance = new FireStoreController();
            }
            return singleInstance;
        }

        public FireStoreController() {

        }
        // Helper Var
        CurrentUserHelper currentUserHelper = CurrentUserHelper.getInstance();

        // Firestore Variables
        private final CollectionReference userCollectionReference = FirebaseFirestore.getInstance().collection("Users");
        private final CollectionReference qrCollectionReference = FirebaseFirestore.getInstance().collection("Codes");
        private final CollectionReference commentsCollectionReference = FirebaseFirestore.getInstance().collection("Comments");

        /**
         * Get all QR codes which have a location
         * @return a Task object with the QuerySnapshot inside
         */
        public Task<QuerySnapshot> getAllCodesWithLocation() {
            return qrCollectionReference.whereNotEqualTo("coordinates", new ArrayList<>()).get();
        }

        /**
         * Get all QR codes ( no filters )
         * @return a Task object with the QuerySnapshot inside
         */
        public Task<QuerySnapshot> getAllCodes(){
            return qrCollectionReference.get();
        }

        /**
         * Gets all players, no owners included
         * @returna Task object with the QuerySnapshot inside
         */
        public Task<QuerySnapshot> getAllPlayers() {
            return userCollectionReference.whereNotEqualTo("isOwner", true).get();
        }

        /**
         * Gets all QR codes, ordered by worth
         * @return a Task object with the QuerySnapshot inside
         */
        public Task<QuerySnapshot> getAllQRCodes() {
            return qrCollectionReference.orderBy("worth").get();
        }

        /**
         * Gets all users, ( No filters, owners included )
         * @return a Task object with the QuerySnapshot inside
         */
        public Task<QuerySnapshot> getAllUsers() {
            return userCollectionReference.get();
        }

        /**
         * gets a specific users QR codes
         * @param username user
         * @return a Task object with the QuerySnapshot inside
         */
        public Task<QuerySnapshot> getSpecifiedUsersCodes(String username) {
            return qrCollectionReference.whereArrayContains("players", username).get();
        }

        /**
         * Gets the full info of a single QR code
         * @param qrCodeId the code ID
         * @return a Task object with the DocumentSnapshot inside
         */
        public Task<DocumentSnapshot> getSingleQRCode(String qrCodeId) {
            return qrCollectionReference.document(qrCodeId).get();
        }

        /**
         * Transfer current users profile to the provided username's profile
         * @param newUserNameToSwitchTo new user to transfer to
         * @return a Task object with a Void inside. So no return values.
         */
        public Task<Void> switchProfile(String newUserNameToSwitchTo){
            Task<Void> removeFromCurrentUserProfile = userCollectionReference.document(currentUserHelper.getFirebaseId()).update("devices", FieldValue.arrayRemove(currentUserHelper.getUniqueID()));
            Task<Void> addToNewUserProfile = userCollectionReference.document(newUserNameToSwitchTo).update("devices", FieldValue.arrayUnion(currentUserHelper.getUniqueID()));
            return Tasks.whenAll(removeFromCurrentUserProfile, addToNewUserProfile);
        }

        /**
         * Add user to db
         * @param newUserToAdd User object to add to db
         * @return a Task object with a Void inside. So no return values.
         */
        public Task<Void> addUser(User newUserToAdd) {
            return userCollectionReference
                    .document(newUserToAdd.getUsername())
                    .set(newUserToAdd);
        }

        /**
         * Find user based on unique android id stored in currentUserHelper
         * @return a Task object with the QuerySnapshot inside
         */
        public Task<QuerySnapshot> findUserBasedOnDeviceId(){
            return userCollectionReference.whereArrayContains("devices", currentUserHelper.getUniqueID()).get();
        }

        /**
         * check if QR code exists
         * @param id id of QR code
         * @return a Task object with the QuerySnapshot inside
         */
        public Task<QuerySnapshot> checkQRExists(String id) {
            return qrCollectionReference.whereEqualTo("id",id).get();
        }

        /**
         * Saves a new code to the DB by doing the following:
         * 1. Stores the QR code
         * 2. Updates the player's profile whoever found it
         * @param codeToSave the QR code to save
         * @return A Task which is completed once both of these subtasks are done.
         */
        public Task<Void> saveNewQrCode(QRCode codeToSave){

            HashMap<String, Object> updates = new HashMap<>();
            updates.put("collectedCodes", FieldValue.arrayUnion(codeToSave.getId()));
            updates.put("totalScore", FieldValue.increment(codeToSave.getWorth()));

            return Tasks.whenAll(
                    qrCollectionReference.document(codeToSave.getId()).set(codeToSave),
                    FirebaseFirestore.getInstance().collection("Users").document(currentUserHelper.getFirebaseId()).update(updates)
            );
        }

        /**
         * Updates a code in the DB by doing the following:
         * 1. Adding player name to QR code
         * 2. Updates the player's profile whoever found it
         * @param codeToSave QRCode to update
         * @return A Task which is completed once both of these subtasks are done.
         */
        public Task<Void> updateExistingQrCode(QRCode codeToSave){
            HashMap<String, Object> updates = new HashMap<>();
            updates.put("collectedCodes", FieldValue.arrayUnion(codeToSave.getId()));
            updates.put("totalScore", FieldValue.increment(codeToSave.getWorth()));
            return Tasks.whenAll(
                    qrCollectionReference.document(codeToSave.getId()).update("players", FieldValue.arrayUnion(currentUserHelper.getUsername())),
                    FirebaseFirestore.getInstance().collection("Users").document(currentUserHelper.getFirebaseId()).update(updates)
            );
        }

        /**
         * Delete a QR code.
         * @param qrCodeToDelete QRCode to delete, with ID and score fields
         * @return A Task which is completed once both of these subtasks are done.
         */
        public Task<Void> deleteQRCode(QRCode qrCodeToDelete) {

            // All pending tasks
            ArrayList<Task<Void>> allTasks = new ArrayList<>();

            // Decreasing score and qrCode
            HashMap<String, Object> updates = new HashMap<>();
            updates.put("collectedCodes", FieldValue.arrayRemove(qrCodeToDelete.getId()));
            updates.put("totalScore", FieldValue.increment( -1 * qrCodeToDelete.getWorth()));

            // Put all updates in one tasks
            qrCodeToDelete.getPlayers().forEach(playerUsername -> {
                Task<Void> updateTask =  userCollectionReference.document(playerUsername).update(updates);
                allTasks.add(updateTask);
            });
            // Delete qr code.
            allTasks.add(qrCollectionReference.document(qrCodeToDelete.getId()).delete());
            //
            return Tasks.whenAll(allTasks);
        }

        /**
         * Removes a QR code from the db
         * @param qrCode qrCode to delete
         * @return A Task which is completed once both of these subtasks are done.
         */
        public Task<Void> removeUserFromQRCode(QRCode qrCode) {

            HashMap<String, Object> updates = new HashMap<>();
            updates.put("collectedCodes", FieldValue.arrayRemove(qrCode.getId()));
            updates.put("totalScore", FieldValue.increment( -1 * qrCode.getWorth()));

            return Tasks.whenAll(
                    userCollectionReference.document(currentUserHelper.getFirebaseId()).update(updates),
                    qrCollectionReference.document(qrCode.getId()).update("players", FieldValue.arrayRemove(currentUserHelper.getUsername()))
            );
        }

    }
}
