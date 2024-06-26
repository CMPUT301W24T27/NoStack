package com.example.nostack.controllers;

import android.util.Log;

import com.example.nostack.handlers.CurrentUserHandler;
import com.example.nostack.models.Attendance;
import com.example.nostack.models.Event;
import com.example.nostack.models.QrCode;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.Transaction;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Class for handling QR codes
 */
public class QrCodeController {
    private static QrCodeController singleInstance = null;
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    private final CollectionReference qrCollectionReference = FirebaseFirestore.getInstance().collection("qr-codes");
    private final CurrentUserHandler currentUserHandler = CurrentUserHandler.getSingleton();

    /**
     * Get the instance of the QrCodeController
     * @return void
     */
    public static QrCodeController getInstance() {
        if (singleInstance == null) {
            singleInstance = new QrCodeController();
        }
        return singleInstance;
    }

    /**
     * Empty public constructor
     */
    public QrCodeController() {
    }

    /**
     * Get all QR codes
     * @return Task<QuerySnapshot> The QR code collection
     */
    public Task<QuerySnapshot> getAllQrCodes() {
        return qrCollectionReference.get();
    }

    /**
     * Get QR codes by by id
     * @param qrCodeId The QR code id
     * @return Task<QuerySnapshot> The QR codes by id
     */
    public Task<DocumentSnapshot> getQrCode(String qrCodeId) {
        try {
            return qrCollectionReference.document(qrCodeId).get().addOnSuccessListener(task -> {
                Log.d("QrCodeController", "Successfully fetched QrCode");
            }).addOnFailureListener(e -> {
                Log.e("QrCodeController", "Error fetching QrCode", e);
            });
        } catch (Exception e) {
            Log.e("QrCodeController", "Error fetching QrCode", e);
            return null;
        }
    }

    /**
     * get inactive  QR codes
     * @return Task<QuerySnapshot> The inactive QR codes
     */
    public Task<QuerySnapshot> getInactiveQrCodes() {
        return qrCollectionReference
                .whereEqualTo("active", false)
                .get();
    }

    /**
     * Reuse a QR code
     * @param eventId The event ID
     * @param qrCodeId The QR code ID
     * @return void
     */
    public Task<Void> reuseQrCode(String eventId, String qrCodeId) {
        Map<String, Object> updates = new HashMap<>();
        updates.put("eventId", eventId);
        updates.put("active", true);
        return qrCollectionReference.document(qrCodeId).update(updates);
    }

    /**
     * Deactivate a QR code
     * @param qrCodeId The QR code ID
     * @return void
     */
    public Task<Void> deactivateQrCode(String qrCodeId) {
        Map<String, Object> updates = new HashMap<>();
        updates.put("active", false);
        return qrCollectionReference.document(qrCodeId).update(updates);
    }

    /**
     * Reactivate a QR code
     * @param qrCodeId The QR code ID
     * @return  void
     */
    public Task<Void> reactivateQrCode(String qrCodeId) {
        Map<String, Object> updates = new HashMap<>();
        updates.put("active", true);
        return qrCollectionReference.document(qrCodeId).update(updates);
    }

    /**
     * Reactivate all QR codes by event ID
     * @param eventId The event ID
     * @return void
     */
    public Task<Void> reactivateQrCodeByEventId(String eventId) {
        return qrCollectionReference
                .whereEqualTo("eventId", eventId)
                .get()
                .onSuccessTask(queryDocumentSnapshots -> {
                    List<DocumentSnapshot> qrCodes = queryDocumentSnapshots.getDocuments();
                    Task<Void> task = Tasks.whenAll();
                    for (DocumentSnapshot qrCode : qrCodes) {
                        task = task.continueWithTask(task1 -> {
                            return reactivateQrCode(qrCode.getId());
                        });
                    }
                    return task;
                });
    }


    /**
     * Deactivate all QR codes by event ID
     * @param eventId The event ID
     * @return void
     */
    public Task<Void> deactivateQrCodeByEventId(String eventId) {
        return qrCollectionReference
                .whereEqualTo("eventId", eventId)
                .get()
                .onSuccessTask(queryDocumentSnapshots -> {
                    List<DocumentSnapshot> qrCodes = queryDocumentSnapshots.getDocuments();
                    Task<Void> task = Tasks.whenAll();
                    for (DocumentSnapshot qrCode : qrCodes) {
                        task = task.continueWithTask(task1 -> {
                            return deactivateQrCode(qrCode.getId());
                        });
                    }
                    return task;
                });
    }

    /**
     * Add a QR code
     * @param qrCode The QR code
     * @return void
     */
    public Task<Void> addQrCode(QrCode qrCode) {
        return qrCollectionReference.document(qrCode.getId()).set(qrCode);
    }

    /**
     * Update a QR code
     * @param qrCodeId The QR code ID
     * @return void
     */
    // TODO: Deleting a QrCode, may be a little too nuanced, will be done later on.
    public Task<Void> deleteQrCode(String qrCodeId) {
        return Tasks.whenAll();
    }
}