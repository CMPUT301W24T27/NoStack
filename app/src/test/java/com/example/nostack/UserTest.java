package com.example.nostack;

import static org.junit.Assert.assertEquals;

import com.example.nostack.model.User.User;

import org.junit.Test;

public class UserTest {

    private User mockUser() {
        User mockUser = new User("John", "Doe", "johndoe", "johnDoe@gmail.com", "1234567890", "UniqueID");
        return mockUser;
    }

    @Test
    public void testUserCreation() {
        User user = mockUser();

        assertEquals("John", user.getFirstName());
        assertEquals("Doe", user.getLastName());
        assertEquals("johndoe", user.getUsername());
        assertEquals("johnDoe@gmail.com", user.getEmailAddress());
        assertEquals("1234567890", user.getPhoneNumber());
        assertEquals("UniqueID", user.getUuid());
    }

    @Test
    public void testUserProfileImage() {
        User user = mockUser();
        user.setProfileImageUrl("https://www.example.com/profile.jpg");
        assertEquals("https://www.example.com/profile.jpg", user.getProfileImageUrl());
    }




}
