package com.example.nostack.model.Events;

import com.example.nostack.model.User.User;
import com.example.nostack.utils.Announcement;
import com.example.nostack.utils.QrCode;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.UUID;

/**
 * This class is to create the object of an Event
 */
public class Event implements Serializable {
    private String name;
    private String location;
    private String description;
    private String organizerId;
    private String eventBannerImgUrl;
    private String id;
    private ArrayList<String> attendees;
    private ArrayList<Announcement> announcements;
    private Date startDate;
    private Date endDate;
    private QrCode checkInQr;
    private QrCode eventQr;
    private int capacity;
    private int currentCapacity;

    /**
     *This creates an event using just an eventName and an eventId
     * @param eventName The name of the event
     * @param eventId The Id for the event
     */
    public Event(String eventName, String eventId) {
        name = eventName;
        id = eventId;
        attendees = new ArrayList<>();
    }

    public Event() {
    }

    /**
     *This creates an event using an eventName, eventId and an eventLocation
     * @param eventName This is the name of the event
     * @param eventId This is the Id for the event
     * @param eventLocation This is the location of the event
     */
    public Event(String eventName, String eventId, String eventLocation) {
        name = eventName;
        id = eventId;
        location = eventLocation;
        attendees = new ArrayList<>();
        currentCapacity = 0;
        capacity = -1;
    }

    /**
     * This creates an event using a name, location, description, start and end date,
     *       a Qr to check in and an Id for the organizer
     * @param name This is the of name the event
     * @param location This is the location of the event
     * @param description This is the description of the event
     * @param startDate This is the starting date of the event
     * @param endDate This is the ending date of the event
     * @param checkInQr This is the of Qr code to check into the event
     * @param organizerId This is the Id of the organizer of the event
     */
    public Event(String name, String location, String description, Date startDate, Date endDate, QrCode checkInQr, String organizerId) {
        this.name = name;
        this.location = location;
        this.description = description;
        this.attendees = new ArrayList<String>();
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

    /**
     * This creates an event using a name, location, description, start and end date,
     *       and an Id for the organizer
     * @param name This is the of name the event
     * @param location This is the location of the event
     * @param description This is the description of the event
     * @param startDate This is the starting date of the event
     * @param endDate This is the ending date of the event
     * @param organizerId This is the Id of the organizer of the event
     */
    public Event(String name, String location, String description, Date startDate, Date endDate, String organizerId) {
        this.name = name;
        this.location = location;
        this.description = description;
        this.attendees = new ArrayList<String>();
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

    /**
     * This function gets the name of the event
     * @return
     *      Returns the name of the event
     */
    public String getName() {
        return name;
    }

    /**
     * This function sets the name of the event
     * @param name
     */
    public void setName(String name) {
        this.name = name;
    }
    /**
     * This function gets the location of the event
     * @return
     *      Returns the location of the event
     */
    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }
    /**
     * This function gets the description of the event
     * @return
     *      Returns the description of the event
     */
    public String getDescription() {
        return description;
    }
    /**
     * This function sets the description of the event
     * @param description
     */
    public void setDescription(String description) {
        this.description = description;
    }
    /**
     * This function gets the Id of the event
     * @return
     *      Returns the Id of the event
     */
    public String getId() {
        return id;
    }
    /**
     * This function sets the Id of the event
     * @param id
     */
    public void setId(String id) {
        this.id = id;
    }
    /**
     * This function gets an ArrayList of the attendees at the event
     * @return
     *      Returns an ArrayList of the attendees at event
     */
    public ArrayList<String> getAttendees() {
        return attendees;
    }
    /**
     * This function adds the attendees at the event as long as the event is not at capacity
     * @param attendee The attendee to be added to the event
     */
    public boolean addAttendee(String attendee) {

        if ((capacity > 0) && (currentCapacity >= capacity)) {
            return false;
        }

        if (!attendees.contains(attendee)) {
            attendees.add(attendee);
            currentCapacity++;
        }

        return true;
    }
    /**
     * This function removes the attendee at the event
     * @param attendee The attendee to be removed from the event
     */
    public void removeAttendeee(User attendee) {
        if (!attendees.contains(attendee)) {
            attendees.remove(attendee);
            currentCapacity--;
        }
    }

    /**
     * This function gets an ArrayList of the announcements at the event
     * @return Returns an ArrayList of the announcements at event
     */
    public ArrayList<Announcement> getAnnouncements() {
        return announcements;
    }
    /**
     * This function adds the announcements to the event
     * @param announcement The announcement to be added to the event
     */
    public void addAnnouncement(Announcement announcement) {
        announcements.add(announcement);
    }
    /**
     * This function gets the start date of the event
     * @return Returns the start date of the event
     */
    public Date getStartDate() {
        return startDate;
    }
    /**
     * This function sets the start date of the event
     * @param startDate
     */
    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }
    /**
     * This function gets the end date of the event
     * @return Returns the end date of the event
     */
    public Date getEndDate() {
        return endDate;
    }
    /**
     * This function sets the end date of the event
     * @param endDate
     */
    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }
    /**
     * This function gets the Qr code to check into the event
     * @return Returns the Qr code to check into the event
     */
    public QrCode getCheckInQr() {
        return checkInQr;
    }
    /**
     * This function sets the Qr code to check into the event
     * @param checkInQr
     */
    public void setCheckInQr(QrCode checkInQr) {
        this.checkInQr = checkInQr;
    }
    /**
     * This function gets the Qr code for the event
     * @return Returns the Qr code for the event
     */
    public QrCode getEventQr() {
        return eventQr;
    }
    /**
     * This function sets the Qr code for the event
     * @param eventQr
     */
    public void setEventQr(QrCode eventQr) {
        this.eventQr = eventQr;
    }
    /**
     * This function gets the Id of the organizer of the event
     * @return Returns the Id of the organizer of the event
     */
    public String getOrganizerId() {
        return organizerId;
    }
    /**
     * This function sets the Id of the organizer of the event
     * @param organizerId
     */
    public void setOrganizerId(String organizerId) {
        this.organizerId = organizerId;
    }
    /**
     * This function gets the URL of the banner image for the event
     * @return Returns the URL of the banner image for the event
     */
    public String getEventBannerImgUrl() {
        return eventBannerImgUrl;
    }
    /**
     * This function sets the URL of the banner image for the event
     * @param eventBannerImgUrl
     */
    public void setEventBannerImgUrl(String eventBannerImgUrl) {
        this.eventBannerImgUrl = eventBannerImgUrl;
    }
    /**
     * This function gets the capacity of the event
     * @return Returns the capacity of the event
     */
    public int getCapacity() {
        return capacity;
    }
    /**
     * This function sets the capacity of the event
     * @param capacity The capacity of the event as an Integer
     */
    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }
    /**
     * This function gets the current capacity of the event
     * @return Returns the current capacity of the event
     */
    public int getCurrentCapacity() {
        return currentCapacity;
    }
    /**
     * This function sets the current capacity of the event
     * @param currentCapacity The current capacity of the event as an Integer
     */
    public void setCurrentCapacity(int currentCapacity) {
        this.currentCapacity = currentCapacity;
    }
}
