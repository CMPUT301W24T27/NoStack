package com.example.nostack.models;


import java.util.UUID;
/**
 * This class is used to create the QR code object for an event
 */
public class QrCode {
    private String id;
    private String eventId;
    private boolean active;
    private boolean isCustom;

    public QrCode() {}
    public QrCode(String eventId) {
        id = UUID.randomUUID().toString();
        this.eventId = eventId;
        this.active = true;
        this.isCustom = false;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getEventId() {
        return eventId;
    }

    public void setEventId(String eventId) {
        this.eventId = eventId;
    }

    public boolean getActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public boolean getIsCustom() {
        return isCustom;
    }
    public void setIsCustom(boolean isCustom) {
        this.isCustom = isCustom;
    }
}
