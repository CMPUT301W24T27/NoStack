package com.example.nostack.utils;

public class Attendance {
    private String id;
    private String userId;
    private String eventId;
    private int numCheckIn;

    public Attendance() {}
    public Attendance(String userId, String eventId) {
        id = userId + "-" + eventId;
        this.userId = userId;
        this.eventId  = eventId;
        this.numCheckIn = 1;
    }
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getEventId() {
        return eventId;
    }

    public void setEventId(String eventId) {
        this.eventId = eventId;
    }

    public int getNumCheckIn() {
        return numCheckIn;
    }

    public void setNumCheckIn(int numCheckIn) {
       this.numCheckIn = numCheckIn;
    }

    public void checkIn() {
        numCheckIn++;
    }

    public static String buildAttendanceId(String eventId, String userId) {
        return userId + "-" + eventId;
    }
}
