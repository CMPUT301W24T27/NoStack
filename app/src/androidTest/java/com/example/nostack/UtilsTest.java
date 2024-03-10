package com.example.nostack;

import static com.example.nostack.utils.GenerateProfileImage.generateProfileImage;
import static com.google.common.base.CharMatcher.any;
import static com.google.common.base.Verify.verify;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;


import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import com.example.nostack.utils.Attendance;
import com.example.nostack.utils.GenerateProfileImage;
import com.example.nostack.utils.GeoLocation;

import org.junit.Test;

public class UtilsTest {

    @Test
    public void testGenerateProfileImage() {
        // Input data
        String firstName = "John";
        String lastName = "Doe";

        GenerateProfileImage generateProfileImage = new GenerateProfileImage();

        // Call the method to test
        Bitmap resultBitmap = generateProfileImage.generateProfileImage(firstName, lastName);

        // Assert that the method returns a non-null Bitmap
        assertNotNull(resultBitmap);

    }

    @Test
    public void testAttendance() {
        // Input data
        String userId = "123";
        String eventId = "456";

        // Call the method to test
        Attendance attendance = new Attendance(userId, eventId);

        // Assert that the method returns sets and returns the proper values for userId and eventId
        assertEquals("123", attendance.getUserId());
        assertEquals("456", attendance.getEventId());

        // Assert that the method returns the properly formatted attendance ID
        assertEquals("123-456", attendance.getId());

        // Assert that the initialized value of numCheckIn is 1
        assertEquals(1, attendance.getNumCheckIn());

        // Call the checkIn method and check if numCheckIn is incremented
        attendance.checkIn();
        assertEquals(2, attendance.getNumCheckIn());

        // Call the setNumCheckIn method and check if numCheckIn is set to the proper value
        attendance.setNumCheckIn(5);
        assertEquals(5, attendance.getNumCheckIn());

        // Check if GeoLocation is correctly set
        attendance.setGeoLocation(new GeoLocation(1.0, 1.0));
        assertEquals(1.0, attendance.getGeoLocation().getLatitude(), 0.0);
        assertEquals(1.0, attendance.getGeoLocation().getLongitude(), 0.0);

    }

}
