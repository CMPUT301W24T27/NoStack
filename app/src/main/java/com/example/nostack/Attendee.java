package com.example.nostack;

public class Attendee extends User{
    public Attendee(String first_name, String last_name, String username, String email_address, String phone_number) {
        super(first_name, last_name, username, email_address, phone_number);
        setRole("Attendee");
    }
}
