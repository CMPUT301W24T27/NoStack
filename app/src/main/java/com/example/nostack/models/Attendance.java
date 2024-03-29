package com.example.nostack.models;

import java.io.Serializable;

/**
 * Represents the attendance of a user at an event
 */
public class Attendance implements Serializable {
    private String id;
    private String userId;
    private String eventId;
    private int numCheckIn;
    private GeoLocation geoLocation;
    public Attendance() {}
    public Attendance(String userId, String eventId) {
        id = buildAttendanceId(eventId, userId);
        this.userId = userId;
        this.eventId  = eventId;
        this.numCheckIn = 0;
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

    public GeoLocation getGeoLocation() {
        return geoLocation;
    }

    public void setGeoLocation(GeoLocation gl) {
        this.geoLocation = gl;
    }
}

