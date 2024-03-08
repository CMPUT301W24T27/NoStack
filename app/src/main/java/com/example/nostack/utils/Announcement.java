package com.example.nostack.utils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;
/**
 * This class is used to create the announcement object for an event
 */
public class Announcement {
    private String announcementMessage;
    private LocalDateTime announcementDateTime;
    private String priority;

    public String getAnnouncementMessage() {
        return announcementMessage;
    }

    public void setAnnouncementMessage(String announcementMessage) {
        this.announcementMessage = announcementMessage;
    }

    public LocalDateTime getAnnouncementFateTime() {
        return announcementDateTime;
    }

    public void setAnnouncementDateTime(LocalDateTime announcementDateTime) {
        this.announcementDateTime = announcementDateTime;
    }

    public String getPriority() {
        return priority;
    }

    public void setPriority(String priority) {
        this.priority = priority;
    }

    public Announcement(String announcementMessage, LocalDateTime announcementDateTime, String priority) {
        this.announcementMessage = announcementMessage;
        this.announcementDateTime = announcementDateTime;
        this.priority = priority;
    }

    public Announcement() {
    }
}

