package com.example.nostack.model.Events;

import com.example.nostack.model.User.User;
import com.example.nostack.utils.Announcement;
import com.example.nostack.utils.QrCode;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.UUID;

public class Event implements Serializable {
    private String name;
    private String location;
    private String description;
    private String organizerId;
    private String eventBannerImgUrl;
    private String id;
    private ArrayList<User> attendees;
    private ArrayList<Announcement> announcements;
    private Date startDate;
    private Date endDate;
    private QrCode checkInQr;
    private QrCode eventQr;
    private int capacity;
    private int currentCapacity;

    public Event(String eventName, String eventId) {
        name = eventName;
        id = eventId;
        attendees = new ArrayList<>();
    }

    public Event() {
    }

    public Event(String eventName, String eventId, String eventLocation) {
        name = eventName;
        id = eventId;
        location = eventLocation;
        attendees = new ArrayList<>();
        currentCapacity = 0;
        capacity = -1;
    }

    public Event(String name, String location, String description, Date startDate, Date endDate, QrCode checkInQr, String organizerId) {
        this.name = name;
        this.location = location;
        this.description = description;
        this.attendees = new ArrayList<User>();
        this.announcements = new ArrayList<Announcement>();
        this.startDate = startDate;
        this.endDate = endDate;
        this.checkInQr = checkInQr;
        this.organizerId = organizerId;
        currentCapacity = 0;
        capacity = -1;

        // Generate a unique Id
        id = UUID.randomUUID().toString();

        // Create a QR Code to direct to the event page (WIP)
        this.eventQr = new QrCode(1, id, id);
    }

    public Event(String name, String location, String description, Date startDate, Date endDate, String organizerId) {
        this.name = name;
        this.location = location;
        this.description = description;
        this.attendees = new ArrayList<User>();
        this.announcements = new ArrayList<Announcement>();
        this.startDate = startDate;
        this.endDate = endDate;
        this.checkInQr = checkInQr;
        this.organizerId = organizerId;
        currentCapacity = 0;
        capacity = -1;

        // Generate a unique Id
        id = UUID.randomUUID().toString();

        // Create a QR Code to direct to the event page (WIP)
        this.eventQr = new QrCode(1, id, id);

        // Create a QR Code for checking in
        this.checkInQr = new QrCode(0, id, id);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public ArrayList<User> getAttendees() {
        return attendees;
    }

    public boolean addAttendee(User attendee) {

        if ((capacity > 0) && (currentCapacity >= capacity)) {
            return false;
        }

        if (!attendees.contains(attendee)) {
            attendees.add(attendee);
            currentCapacity++;
        }

        return true;
    }

    public void removeAttendeee(User attendee) {
        if (!attendees.contains(attendee)) {
            attendees.remove(attendee);
            currentCapacity--;
        }
    }


    public ArrayList<Announcement> getAnnouncements() {
        return announcements;
    }

    public void addAnnouncement(Announcement announcement) {
        announcements.add(announcement);
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public QrCode getCheckInQr() {
        return checkInQr;
    }

    public void setCheckInQr(QrCode checkInQr) {
        this.checkInQr = checkInQr;
    }

    public QrCode getEventQr() {
        return eventQr;
    }

    public void setEventQr(QrCode eventQr) {
        this.eventQr = eventQr;
    }
    public String getOrganizerId() {
        return organizerId;
    }

    public void setOrganizerId(String organizerId) {
        this.organizerId = organizerId;
    }


    public String getEventBannerImgUrl() {
        return eventBannerImgUrl;
    }

    public void setEventBannerImgUrl(String eventBannerImgUrl) {
        this.eventBannerImgUrl = eventBannerImgUrl;
    }

    public int getCapacity() {
        return capacity;
    }

    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }

    public int getCurrentCapacity() {
        return currentCapacity;
    }

    public void setCurrentCapacity(int currentCapacity) {
        this.currentCapacity = currentCapacity;
    }
}
