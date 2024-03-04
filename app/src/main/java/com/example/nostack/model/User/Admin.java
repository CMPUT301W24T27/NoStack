package com.example.nostack.model.User;

/**
 * Admin can
 *      Browse and display Profiles, Events, and Images,
 *      Remove Profiles, Events, and Images
 */
public class Admin extends User{
    public Admin(String firstName, String lastName, String username, String emailAddress, String phoneNumber, String uuid) {
        super(firstName, lastName, username, emailAddress, phoneNumber, uuid);
        setRole("Admin");
    }
}
