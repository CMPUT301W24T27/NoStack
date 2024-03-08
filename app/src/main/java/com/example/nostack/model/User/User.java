package com.example.nostack.model.User;

import android.util.Log;

/**
 * User will
 *      Contain and can display a user’s personal info,
 *      Manage profile picture upload and deletion,
 *      Generate a profile picture from the profile name.
 */
public class User {
    private String firstName;
    private String lastName;
    private String username;
    private String emailAddress;
    private String phoneNumber;
    private String gender;
    private String role;
    private String uuid;
    private String profileImageUrl;
     /**
     * Get the role of the user
     * @return The role of the user
     */
    public String getRole() {
        return role;
    }
    /**
     * Set the role of the user
     * @param role The role of the user
     */
    public void setRole(String role) {
        this.role = role;
    }
    /**
     * Get the username of the user
     * @return The username of the user
     */
    public String getUsername() {
        return username;
    }
    /**
     * Set the username of the User
     * @param username The username of the user
     */
    public void setUsername(String username) {
        this.username = username;
    }

    public String getGender() {
        return gender;
    }

    /**
     * Sets the gender of the User
     * @param gender The gender of the user
     */
    public void setGender(String gender) {
        this.gender = gender;
    }
    /**
     * Get the first name of the user
     * @return The first name of the user
     */
    public String getFirstName() {
        return firstName;
    }
    /**
     * Set the first name of the user
     * @param firstName The first name of the user
     */
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }
    /**
     * Get the last name of the user
     * @return The last name of the user
     */
    public String getLastName() {
        return lastName;
    }
    /**
     * Set the last name of the user
     * @param lastName The last name of the user
     */
    public void setLastName(String lastName) {
        this.lastName = lastName;
    }
    /**
     * Get the email address of the user
     * @return The email address of the user
     */
    public String getEmailAddress() {
        return emailAddress;
    }
    /**
     * Set the email address of the user
     * @param emailAddress The email address of the user
     */
    public void setEmailAddress(String emailAddress) {
        this.emailAddress = emailAddress;
    }
    /**
     * Get the phone number of the user
     * @return The phone number of the user
     */
    public String getPhoneNumber() {
        return phoneNumber;
    }
    /**
     * Set the phone number of the user
     * @param phoneNumber The phone number of the user
     */
    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }
    /**
     * Get the UUID of the user
     * @return The UUID of the user
     */
    public String getUuid() {
        return uuid;
    }
    /**
     * Set the UUID of the user
     * @param uuid The UUID of the user
     */
    public void setUuid(String uuid) {
        this.uuid = uuid;
    }
    /**
     * Get the profile image URL
     * @return The URL of the profile image
     */
    public String getProfileImageUrl() {
        return profileImageUrl;
    }

    /**
     * Set the profile image URL
     * @param profileImageUrl The URL of the profile image
     */
    public void setProfileImageUrl(String profileImageUrl) {
        this.profileImageUrl = profileImageUrl;
        Log.d("User", "Profile image URL: " + profileImageUrl);
    }

    /**
     * Constructor for User
     * @param firstName The user's first name
     * @param lastName The user's last name
     * @param username The user's username
     * @param emailAddress The user's email address
     * @param phoneNumber The user's phone number
     * @param uuid The user's UUID
     */
    public User(String firstName, String lastName, String username, String emailAddress, String phoneNumber, String uuid) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.username = username;
        this.emailAddress = emailAddress;
        this.phoneNumber = phoneNumber;
        this.uuid = uuid;


    }
    public User(){}
}
