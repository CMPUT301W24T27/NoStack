package com.example.nostack.services;

import android.content.Intent;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.example.nostack.handlers.CurrentUserHandler;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

public class MyFirebaseMessagingService extends FirebaseMessagingService {
    private CurrentUserHandler currentUserHandler = CurrentUserHandler.getSingleton();
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        Log.d("MyFirebaseMessagingService", "From: " + remoteMessage.getFrom());
        Log.d("MyFirebaseMessagingService", "Notification Title: " + remoteMessage.getNotification().getTitle());
        Log.d("MyFirebaseMessagingService", "Notification Message Body: " + remoteMessage.getNotification().getBody());

        Intent intent = new Intent("Notification");
        intent.putExtra("message", remoteMessage.getNotification().getBody());
        intent.putExtra("title", remoteMessage.getNotification().getTitle());

        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }

    @Override
    public void onNewToken(@NonNull String token) {
        super.onNewToken(token);
        Log.d("FCM", "New token: " + token);
        if (currentUserHandler.getCurrentUserId() != null) {
            currentUserHandler.updateUserFcmTokenOnRefresh(token);
        }
    }
}
