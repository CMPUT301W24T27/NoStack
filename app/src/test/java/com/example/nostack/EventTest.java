package com.example.nostack;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Assert;
import org.junit.Test;

import com.example.nostack.model.Events.Event;
import com.example.nostack.utils.QrCode;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.zxing.qrcode.encoder.QRCode;

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
        Event mockEvent = new Event("Test Name", "Test Location", "Test Description", startDate, endDate, checkInQR, "Test Organizer ID");
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
        assertEquals("Test QR Code", event.getCheckInQr().getId());
        assertEquals("Test Organizer ID", event.getOrganizerId());

    }

    @Test
    public void testAddAttendee() {

        // Create a new mockEvent
        Event event = mockEvent();

        event.addAttendee("Test Attendee ID");

        assertTrue(event.addAttendee("Test Attendee ID"));
    }

    @Test
    public void testRemoveAttendee() {

        // Create a new mockEvent
        Event event = mockEvent();

        // Add an attendee to the event and check that the attendee was added
        event.addAttendee("Test Attendee ID");
        assertTrue(event.getAttendees().contains("Test Attendee ID"));

        // Remove the attendee from the event and check that the attendee was removed
        event.removeAttendee("Test Attendee ID");
        assertFalse(event.getAttendees().contains("Test Attendee ID"));
    }

    @Test
    public void testCapacity (){
        Event event = mockEvent();

        // set the capacity and current capacity of the event
        event.setCapacity(1);
        event.setCurrentCapacity(2);

        // check if the event is full
        // the event is full if the current capacity is greater than or equal to the capacity
        assertFalse(event.addAttendee("Attendee ID 1"));

        // check that adding an attendee increments the current capacity of the event
        // setting the capacity to 4 will pass the first condition in the addAttendee method, so we can check the second condition
        event.setCapacity(4);
        event.addAttendee("Attendee ID 3");
        assertEquals(3, event.getCurrentCapacity());
    }

}
