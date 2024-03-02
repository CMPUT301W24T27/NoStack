package com.example.nostack;

/**
 * Admin can
 *      Browse and display Profiles, Events, and Images,
 *      Remove Profiles, Events, and Images
 */
public class Admin extends User{
    public Admin(String firstName, String lastName, String username, String emailAddress, String phoneNumber) {
        super(firstName, lastName, username, emailAddress, phoneNumber);
        setRole("Admin");
    }
}
