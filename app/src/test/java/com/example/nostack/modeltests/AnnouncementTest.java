package com.example.nostack.modeltests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import com.example.nostack.models.Announcement;
import com.example.nostack.models.Event;
import com.example.nostack.models.QrCode;

import java.util.ArrayList;
import java.util.Date;

public class AnnouncementTest {

    private Announcement mockAnnouncement() {
        Announcement announcement = new Announcement();

        return announcement;
    }

    @Test
    private void testAddAnnouncement() {

        Announcement announcement = mockAnnouncement();
        announcement.setAnnouncementMessage("Test1");

        assertEquals("hello", announcement.getAnnouncementMessage());
    }

    @Test
    private void testPriority() {

        Announcement announcement = mockAnnouncement();
        announcement.setPriority("High");
        assertEquals("High", announcement.getPriority());
    }
}
