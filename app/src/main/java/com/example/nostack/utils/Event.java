package com.example.nostack.utils;

import com.example.nostack.model.User.Attendee;
import com.example.nostack.qrCode;

import java.util.ArrayList;
import java.util.Date;

public class Event {
    private String eventName;
    private String eventLocation;
    private String eventId;
    private ArrayList<Attendee> attendees;
    private Date eventDate;
    private qrCode qrcode;

    public qrCode getQrcode() {
        return qrcode;
    }

    public void setQrcode(qrCode qrcode) {
        this.qrcode = qrcode;
    }

    public Date getEventDate() {
        return eventDate;
    }

    public void setEventDate(Date eventDate) {
        this.eventDate = eventDate;
    }

    public String getEventName() {
        return eventName;
    }

    public void setEventName(String eventName) {
        this.eventName = eventName;
    }

    public String getEventLocation() {
        return eventLocation;
    }

    public void setEventLocation(String eventLocation) {
        this.eventLocation = eventLocation;
    }

    public String getEventId() {
        return eventId;
    }

    public void setEventId(String eventId) {
        this.eventId = eventId;
    }

    public ArrayList<Attendee> getAttendeeList() {
        return attendees;
    }

    public void addAttendee(Attendee attendee) {
        if (! attendees.contains(attendee)) {
            attendees.add(attendee);
        }
    }

    public void removeAttendee(Attendee attendee) {
        attendees.remove(attendee);
    }

    public Event(String eventName, String eventId) {
        this.eventName = eventName;
        this.eventId = eventId;
        this.attendees = new ArrayList<>();
    }

    public Event(String eventName, String eventId, String eventLocation) {
        this.eventName = eventName;
        this.eventId = eventId;
        this.eventLocation = eventLocation;
        this.attendees = new ArrayList<>();
    }
}
