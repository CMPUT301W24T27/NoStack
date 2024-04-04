package com.example.nostack.viewmodels;

import android.util.Log;
import android.util.Pair;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.nostack.controllers.AttendanceController;
import com.example.nostack.controllers.EventController;
import com.example.nostack.controllers.QrCodeController;
import com.example.nostack.handlers.CurrentUserHandler;
import com.example.nostack.models.Attendance;
import com.example.nostack.models.Event;
import com.example.nostack.models.QrCode;
import com.google.firebase.firestore.DocumentSnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.WeakHashMap;

public class QrCodeViewModel extends ViewModel {
    private final MutableLiveData<QrCode> qrCodeLiveData = new MutableLiveData<>();
    private final MutableLiveData<String> errorLiveData = new MutableLiveData<>();
    private final MutableLiveData<List<Pair<Event, QrCode>>> inactiveQrEventList = new MutableLiveData<>();
    private final QrCodeController qrCodeController = QrCodeController.getInstance();
    private final EventController eventController = EventController.getInstance();
    public LiveData<String> getErrorLiveData() {
        return errorLiveData;
    }
    public void clearErrorLiveData() {
        errorLiveData.setValue(null);
    }
    public void fetchQrCode(String qrCodeId) {
        qrCodeController.getQrCode(qrCodeId)
                .addOnSuccessListener(documentSnapshot -> {
                    QrCode event = documentSnapshot.toObject(QrCode.class);
                    qrCodeLiveData.postValue(event);
                }).addOnFailureListener(e -> {
                    Log.e("QrCodeViewModel", "Error fetching QrCode", e);
                    errorLiveData.postValue(e.getMessage());
                });
    }

    public LiveData<QrCode> getQrCode() {
        return qrCodeLiveData;
    }
    public void fetchInactiveQrCodes() {
        qrCodeController.getInactiveQrCodes()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    List<Pair<Event, QrCode>> qrEventList = new ArrayList<>();
                    for (DocumentSnapshot document : queryDocumentSnapshots.getDocuments()) {
                        QrCode qrCode = document.toObject(QrCode.class);
                        eventController.getEvent(qrCode.getEventId())
                                .addOnSuccessListener(documentSnapshot -> {
                                    Event event = documentSnapshot.toObject(Event.class);
                                    Log.d("QrCodeViewModel", "Fetched event for QR code");
                                    qrEventList.add(new Pair<>(event, qrCode));
                                }).addOnFailureListener(e -> {
                                    Log.e("QrCodeViewModel", "Error fetching event for QR code", e);
                                    errorLiveData.postValue(e.getMessage());
                                });
                    }
                    inactiveQrEventList.postValue(qrEventList);
                }).addOnFailureListener(e -> {
                    Log.e("QrCodeViewModel", "Error fetching inactive QrCodes", e);
                    errorLiveData.postValue(e.getMessage());
                });
    }

    public MutableLiveData<List<Pair<Event, QrCode>>> getInactiveQrCodes() {
        return inactiveQrEventList;
    }


}
