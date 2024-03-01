package com.example.nostack;
/**
 * Organizer can
 *      Browse own events
 *      Generate a unique QR code for the event
 *      Monitor attendence of their event
 *      Send notifications to attendees
 */

public class Organizer extends User{
    public Organizer(String first_name, String last_name, String username, String email_address, String phone_number) {
        super(first_name, last_name, username, email_address, phone_number);
        setRole("Organizer");
    }
}
