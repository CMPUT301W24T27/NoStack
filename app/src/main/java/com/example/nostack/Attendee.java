package com.example.nostack;

public class Attendee extends User{
    public Attendee(String firstName, String lastName, String username, String emailAddress, String phoneNumber) {
        super(firstName, lastName, username, emailAddress, phoneNumber);
        setRole("Attendee");
    }
}
