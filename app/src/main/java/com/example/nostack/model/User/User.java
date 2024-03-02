package com.example.nostack;

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

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }


    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmailAddress() {
        return emailAddress;
    }

    public void setEmailAddress(String emailAddress) {
        this.emailAddress = emailAddress;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getUuid() {
        return uuid;
    }
    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

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
