package com.example.nostack;
/**
 * Organizer can
 *      Browse own events
 *      Generate a unique QR code for the event
 *      Monitor attendence of their event
 *      Send notifications to attendees
 */

public class Organizer extends User{
    public Organizer(String firstName, String lastName, String username, String emailAddress, String phoneNumber, String uuid) {
        super(firstName, lastName, username, emailAddress, phoneNumber, uuid);
        setRole("Organizer");
    }
}
