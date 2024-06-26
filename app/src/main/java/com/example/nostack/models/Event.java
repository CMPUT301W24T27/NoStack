package com.example.nostack.models;

import com.example.nostack.models.Announcement;
import com.example.nostack.models.QrCode;

import java.io.Serializable;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.UUID;

/**
 * Event will
 *      Contain an event’s info,
 *      Manage event attendance, and announcements,
 *      Generate a QR code for event check-in and event page.
 */

public class Event implements Serializable {
    private String name;
    private String location;
    private String description;
    private String organizerId;
    private String eventBannerImgUrl;
    private String id;
    private ArrayList<String> attendees;
    private ArrayList<HashMap<String, String>> announcements;
    private Date startDate;
    private Date endDate;
    private Date createdDate;
    private String checkInQrId;
    private Boolean active;
    private int capacity;
    private int currentCapacity;

    /**
     * This function creates an event object
     */
    public Event() {}
    /**
     * This function gets the name of the event
     * @return Returns the name of the event
     */
    public String getName() {
        return name;
    }

    /**
     * This function sets the name of the event
     * @param name The name of the event
     */
    public void setName(String name) {
        this.name = name;
    }
    /**
     * This function gets the location of the event
     * @return Returns the location of the event
     */
    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }
    /**
     * This function gets the description of the event
     * @return Returns the description of the event
     */
    public String getDescription() {
        return description;
    }
    /**
     * This function sets the description of the event
     * @param description The description of the event
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
     * This function gets an ArrayList of the announcements at the event
     * @return Returns an ArrayList of the announcements at event
     */
    public ArrayList<HashMap<String, String>> getAnnouncements() {
        return announcements;
    }
    /**
     * This function adds the announcements to the event
     * @param announcement The announcement to be added to the event
     */
    public void addAnnouncement(HashMap<String, String> announcement) {
        announcements.add(announcement);
    }
    /**
     * This function gets the start date of the event
     * @return Returns the start date of the event
     */
    public void setAnnouncements(ArrayList<HashMap<String, String>> announcements) {
        this.announcements = announcements;
    }
    public Date getStartDate() {
        return startDate;
    }
    /**
     * This function sets the start date of the event
     * @param startDate The start date of the event
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
     * @param endDate The end date of the event
     */
    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }
    /**
     * This function gets the created date of the event
     * @return Returns the created date of the event
     */
    public Date getCreatedDate() {
        return createdDate;
    }

    /**
     * This function sets the created date of the event
     * @param createdDate The created date of the event
     */
    public void setCreatedDate(Date createdDate) {
        this.createdDate = createdDate;
    }
    /**
     * This function gets the Qr code Id to check into the event
     * @return Returns the Qr code Id to check into the event
     */
    public String getCheckInQrId() {
        return checkInQrId;
    }
    /**
     * This function sets the Qr code to check into the event
     * @param checkInQrId The Qr code Id to check into the event
     */
    public void setCheckInQr(String checkInQrId) {
        this.checkInQrId = checkInQrId;
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
     * @param organizerId The Id of the organizer of the event
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
     * @param eventBannerImgUrl The URL of the banner image for the event
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
    /**
     * This function gets the active status of the event
     * @return Returns the active status of the event
     */
    public Boolean getActive() {
        return active;
    }

    /**
     * This function sets the active status of the event
     * @param active The active status of the event
     */
    public void setActive(Boolean active) {
        this.active = active;
    }
}
