package com.example.nostack.modeltests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import com.example.nostack.models.Announcement;
import com.example.nostack.models.Event;
import com.example.nostack.models.QrCode;

import java.util.ArrayList;
import java.util.Date;

public class EventTest {

    private Event mockEvent() {
        // Set known values for start and end date
        Date startDate = new Date();
        startDate.setTime(0);
        Date endDate = new Date();
        endDate.setTime(0);

        // Set known values for QR code
        QrCode checkInQR = new QrCode();
        checkInQR.setId("Test QR Code");

        // Create a new event
        Event mockEvent = new Event();
        mockEvent.setStartDate(startDate);
        mockEvent.setEndDate(endDate);
        mockEvent.setCheckInQr(checkInQR.getId());
        mockEvent.setName("Test Name");
        mockEvent.setLocation("Test Location");
        mockEvent.setDescription("Test Description");
        mockEvent.setOrganizerId("Test Organizer ID");
        return mockEvent;
    }

    @Test
    public void testEventCreation() {

        // Create a new mockEvent
        Event event = mockEvent();

        // Check that the event was created with the correct values
        assertEquals("Test Name", event.getName());
        assertEquals("Test Location", event.getLocation());
        assertEquals("Test Description", event.getDescription());
        assertEquals(0, event.getStartDate().getTime());
        assertEquals(0, event.getEndDate().getTime());
        assertEquals("Test QR Code", event.getCheckInQrId());
        assertEquals("Test Organizer ID", event.getOrganizerId());

    }

    @Test
    public void testAddAnnouncements() {

        // Create a new mockEvent
        Event event = mockEvent();
        ArrayList<Announcement> announcements = new ArrayList<>();
        Announcement announcement = new Announcement();
        announcement.setAnnouncementMessage("hello");
        event.setAnnouncements(announcements);
        event.addAnnouncement(announcement);
        assertEquals(1, event.getAnnouncements().size());
        assertEquals("hello", event.getAnnouncements().get(0).getAnnouncementMessage());
    }

    @Test
    public void testSetCapacity() {

        // Create a new mockEvent
        Event event = mockEvent();

        // set the capacity of the event
        event.setCapacity(100);
        assertEquals(100, event.getCapacity());
    }

}
