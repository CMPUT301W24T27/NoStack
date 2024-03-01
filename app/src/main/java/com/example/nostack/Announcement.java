package com.example.nostack;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;

public class Announcement {
    private String announcement_message;
    private LocalDateTime announcement_dateTime;
    private String priority;

    public String getAnnouncement_message() {
        return announcement_message;
    }

    public void setAnnouncement_message(String announcement_message) {
        this.announcement_message = announcement_message;
    }

    public LocalDateTime getAnnouncement_dateTime() {
        return announcement_dateTime;
    }

    public void setAnnouncement_dateTime(LocalDateTime announcement_dateTime) {
        this.announcement_dateTime = announcement_dateTime;
    }

    public String getPriority() {
        return priority;
    }

    public void setPriority(String priority) {
        this.priority = priority;
    }

    public Announcement(String announcement_message, LocalDateTime announcement_dateTime, String priority) {
        this.announcement_message = announcement_message;
        this.announcement_dateTime = announcement_dateTime;
        this.priority = priority;
    }
}

