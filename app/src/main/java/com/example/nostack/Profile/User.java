package com.example.nostack.Profile;

/* TODO: 
 * 
 * DELETE THIS CLASS WHEN ISSUE 32 IS MERGED.
 * THIS CLASS IS FOR TESTING ONLY.
 */

/**
 * User will
 *      Contain and can display a user’s personal info,
 *      Manage profile picture upload and deletion,
 *      Generate a profile picture from the profile name.
 */
public class User {
    private String first_name;
    private String last_name;
    private String username;
    private String email_address;
    private String phone_number;
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


    public String getFirst_name() {
        return first_name;
    }

    public void setFirst_name(String first_name) {
        this.first_name = first_name;
    }

    public String getLast_name() {
        return last_name;
    }

    public void setLast_name(String last_name) {
        this.last_name = last_name;
    }

    public String getEmail_address() {
        return email_address;
    }

    public void setEmail_address(String email_address) {
        this.email_address = email_address;
    }

    public String getPhone_number() {
        return phone_number;
    }

    public void setPhone_number(String phone_number) {
        this.phone_number = phone_number;
    }

    public String getUuid() {
        return uuid;
    }
    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public User(String first_name, String last_name, String username, String email_address, String phone_number, String uuid) {
        this.first_name = first_name;
        this.last_name = last_name;
        this.username = username;
        this.email_address = email_address;
        this.phone_number = phone_number;
        this.uuid = uuid;
    }

    public User(){}
}