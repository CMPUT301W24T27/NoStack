package com.example.nostack;

public class Attendee extends User{
    public Attendee(String firstName, String lastName, String username, String emailAddress, String phoneNumber, String uuid) {
        super(firstName, lastName, username, emailAddress, phoneNumber, uuid);
        setRole("Attendee");
    }
}
