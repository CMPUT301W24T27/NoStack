package com.example.nostack.viewmodels;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.nostack.controllers.AttendanceController;
import com.example.nostack.controllers.EventController;
import com.example.nostack.handlers.CurrentUserHandler;
import com.example.nostack.models.Attendance;
import com.example.nostack.models.Event;
import com.google.firebase.firestore.DocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class AttendanceViewModel extends ViewModel {
    private final MutableLiveData<List<Attendance>> attendanceByEventLiveData = new MutableLiveData<>();
    private final MutableLiveData<List<Attendance>> presentAttLiveData = new MutableLiveData<>();
    private final MutableLiveData<String> errorLiveData = new MutableLiveData<>();
    private final EventController eventController = EventController.getInstance();
    private final AttendanceController attendanceController = AttendanceController.getInstance();
    private final CurrentUserHandler currentUserHandler = CurrentUserHandler.getSingleton();

    public LiveData<String> getErrorLiveData() {
        return errorLiveData;
    }

    public void clearErrorLiveData() {
        errorLiveData.setValue(null);
    }
    public void fetchAttendanceByEvent(String eventId) {
        attendanceController.getAttendanceByEvent(eventId)
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    List<Attendance> attendances = new ArrayList<>();
                    for (DocumentSnapshot document:queryDocumentSnapshots) {
                        Attendance attendance = document.toObject(Attendance.class);
                        attendances.add(attendance);
                        Log.d("AttendanceViewModel", document.toObject(Attendance.class).getEventId());
                    }
                    attendanceByEventLiveData.postValue(attendances);
                }).addOnFailureListener(e -> {
                    Log.e("AttendanceViewModel", "Error fetching events", e);
                    errorLiveData.postValue(e.getMessage());
                });
    }
    public LiveData<List<Attendance>> getAttendanceByEvent() {
        return attendanceByEventLiveData;
    }

    public void fetchPresentAttByEvent(String eventId) {
        attendanceController.getPresentAttendance(eventId)
            .addOnSuccessListener(queryDocumentSnapshots -> {
                List<Attendance> attendances = new ArrayList<>();
                for (DocumentSnapshot document:queryDocumentSnapshots) {
                    Attendance attendance = document.toObject(Attendance.class);
                    attendances.add(attendance);
                    Log.d("AttendanceViewModel", document.toObject(Attendance.class).getEventId());
                }
                presentAttLiveData.postValue(attendances);
            }).addOnFailureListener(e -> {
                Log.e("AttendanceViewModel", "Error fetching events", e);
                errorLiveData.postValue(e.getMessage());
            });
    }

    public LiveData<List<Attendance>> getPresentAttByEvent() {
        return presentAttLiveData;
    }
}
