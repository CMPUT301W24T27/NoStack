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

public class QrCodeController {
    private static QrCodeController singleInstance = null;
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    private final CollectionReference qrCollectionReference = FirebaseFirestore.getInstance().collection("qr-codes");
    private final CurrentUserHandler currentUserHandler = CurrentUserHandler.getSingleton();

    public static QrCodeController getInstance() {
        if (singleInstance == null) {
            singleInstance = new QrCodeController();
        }
        return singleInstance;
    }

    public QrCodeController() {
    }

    public Task<QuerySnapshot> getAllQrCodes() {
        return qrCollectionReference.get();
    }

    public Task<DocumentSnapshot> getQrCode(String qrCodeId) {
        return qrCollectionReference.document(qrCodeId).get();
    }

    public Task<QuerySnapshot> getInactiveQrCodes() {
        return qrCollectionReference
                .whereEqualTo("active", false)
                .get();
    }

    public Task<Void> reuseQrCode(String eventId, String qrCodeId) {
        Map<String, Object> updates = new HashMap<>();
        updates.put("eventId", eventId);
        updates.put("active", true);
        return qrCollectionReference.document(qrCodeId).update(updates);
    }

    public Task<Void> deactivateQrCode(String qrCodeId) {
        Map<String, Object> updates = new HashMap<>();
        updates.put("active", false);
        return qrCollectionReference.document(qrCodeId).update(updates);
    }

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

    public Task<Void> addQrCode(QrCode qrCode) {
        return qrCollectionReference.document(qrCode.getId()).set(qrCode);
    }

    // TODO: Deleting a QrCode, may be a little too nuanced, will be done later on.
    public Task<Void> deleteQrCode(String qrCodeId) {
        return Tasks.whenAll();
    }
}