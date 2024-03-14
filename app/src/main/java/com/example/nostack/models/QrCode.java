package com.example.nostack.models;


import java.util.UUID;
/**
 * This class is used to create the QR code object for an event
 */
public class QrCode {

    private int type; // 0 for check in, 1 for event details
    private String id;
    private String code;
    private String eventId;
    private boolean active;

    public QrCode() {
    }
    /**
     * This method is used to create a new instance of the QR code object using the provided parameters.
     *
     * @param type The type of the QR code
     * @param code The code of the QR code
     * @param eventId The event id of the QR code
     */
    public QrCode(int type, String code, String eventId) {
        this.type = type;
        this.code = code;
        this.eventId = eventId;
        this.active = true;
        id = UUID.randomUUID().toString();
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getEventId() {
        return eventId;
    }

    public void setEventId(String eventId) {
        this.eventId = eventId;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }
}
