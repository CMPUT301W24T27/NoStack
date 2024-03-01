package com.example.nostack;

import java.util.ArrayList;
import java.util.Date;

public class Event {
    private String event_name;
    private String event_location;
    private String event_id;
    private ArrayList<Attendee> attendees;
    private Date event_date;
    private qrCode qrcode;

    public qrCode getQrcode() {
        return qrcode;
    }

    public void setQrcode(qrCode qrcode) {
        this.qrcode = qrcode;
    }

    public Date getEvent_date() {
        return event_date;
    }

    public void setEvent_date(Date event_date) {
        this.event_date = event_date;
    }

    public String getEvent_name() {
        return event_name;
    }

    public void setEvent_name(String event_name) {
        this.event_name = event_name;
    }

    public String getEvent_location() {
        return event_location;
    }

    public void setEvent_location(String event_location) {
        this.event_location = event_location;
    }

    public String getEvent_id() {
        return event_id;
    }

    public void setEvent_id(String event_id) {
        this.event_id = event_id;
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

    public Event(String event_name, String event_id) {
        this.event_name = event_name;
        this.event_id = event_id;
        this.attendees = new ArrayList<>();
    }

    public Event(String event_name, String event_id, String event_location) {
        this.event_name = event_name;
        this.event_id = event_id;
        this.event_location = event_location;
        this.attendees = new ArrayList<>();
    }
}
