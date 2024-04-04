package com.example.nostack.viewmodels;

import android.util.Log;

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

public class QrCodeViewModel extends ViewModel {
    private final MutableLiveData<QrCode> qrCodeLiveData = new MutableLiveData<>();
    private final MutableLiveData<String> errorLiveData = new MutableLiveData<>();
    private final QrCodeController qrCodeController = QrCodeController.getInstance();
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
}
