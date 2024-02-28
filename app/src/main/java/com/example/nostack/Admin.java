package com.example.nostack;

/**
 * Admin can
 *      Browse and display Profiles, Events, and Images,
 *      Remove Profiles, Events, and Images
 */
public class Admin extends User{
    public Admin(String first_name, String last_name, String username, String email_address, String phone_number) {
        super(first_name, last_name, username, email_address, phone_number);
        setRole("Admin");
    }
}
