package com.example.nostack.handlers;
import android.util.Log;

import com.example.nostack.controllers.AttendanceController;
import com.example.nostack.models.Event;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.concurrent.Executor;

public class NotificationHandler {
    private static NotificationHandler singleInstance = null;
    private static final AttendanceController attendanceController = AttendanceController.getInstance();
    public static NotificationHandler getSingleton() {
        if (singleInstance == null) {
            singleInstance = new NotificationHandler();
        }
        return singleInstance;
    }
    public NotificationHandler() {}

    public void sendEventMilestoneNotification(String fcmToken, Event event, String userId) {
        String title = "Congratulations! You have reached a milestone!";
        attendanceController.getPresentAttendance(event.getId()).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                QuerySnapshot queryDocumentSnapshots = task.getResult();
                boolean sendNotif = true;
                for (int i = 0; i < queryDocumentSnapshots.size(); i++) {
                    DocumentSnapshot documentSnapshot = queryDocumentSnapshots.getDocuments().get(i);
                    Long numCheckins = documentSnapshot.getLong("numCheckIn");
                    String attendeeId = documentSnapshot.getString("userId");
                    if (attendeeId != null && attendeeId.equals(userId) && numCheckins != null && numCheckins > 1) {
                        sendNotif = false;
                        break;
                    }
                }
                int numOfAttendees = queryDocumentSnapshots.size();
                if (!sendNotif) {
                    return;
                } else if (sendNotif && event.getCapacity() == -1) {
                    if (numOfAttendees == 2 || numOfAttendees == 10 || numOfAttendees == 25 || numOfAttendees == 50 || numOfAttendees == 100) {
                        String message = "The number of present attendees for the event " + event.getName() + " has reached " + numOfAttendees + "!";
                        sendNotification(fcmToken, message, title);
                    }
                } else if (sendNotif && event.getCapacity() != -1) {
                    int capacity = event.getCapacity();
                    if (numOfAttendees == (int) (capacity * 0.25) || numOfAttendees == (int) (capacity * 0.50) || numOfAttendees == (int) (capacity * 0.75) || numOfAttendees == capacity) {
                        String message = "The number of attendees for the event " + event.getName() + " has reached " + (numOfAttendees * 100 / capacity) + "% of capacity!";
                        sendNotification(fcmToken, message, title);
                    }
                }
            } else {
                Log.d("NotificationHandler", "Error getting attendance: ", task.getException());
            }
        }).addOnFailureListener(e -> {
            Log.d("NotificationHandler", "Error getting attendance: Incomplete ", e);
        });
    }

    private void sendNotification(String fcmToken, String message, String title) {

        JSONObject jsonObject = new JSONObject();
        JSONObject notification = new JSONObject();
        JSONObject data = new JSONObject();
        try {
            notification.put("title", title);
            notification.put("body", message);
            data.put("more", message);

            jsonObject.put("to", fcmToken);
            jsonObject.put("notification", notification);
            jsonObject.put("data", data);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        callFCMApi(jsonObject);
    }

    public void callFCMApi(JSONObject jsonObject) {
        MediaType JSON = MediaType.parse("application/json; charset=utf-8");
        String serverKey = "AAAAFi5a5Bw:APA91bF84egAggmbzLF7AtOljpEr0kanKZD7meP5LiJOu14Juc10vh1u9WWCQMkX4sr74nA2E_ooyKVy3Y5SpE2lWA2dXhO1LzoO70Zp0qgSfev8eiZRYNhKyyDLZrcsp7dUsYnhsJ61";
        OkHttpClient client = new OkHttpClient();
        String url = "https://fcm.googleapis.com/fcm/send";
        RequestBody body = RequestBody.create(jsonObject.toString(), JSON);
        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .header("Authorization", "Bearer " + serverKey)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    Log.d("Notification Handler", "Notification sent successfully");
                } else {
                    Log.d("Notification Handler", "Failed to send notification");
                }
            }
        });
    }
}


