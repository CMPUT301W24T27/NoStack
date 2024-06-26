package com.example.nostack.uitests;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.Espresso.pressBack;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.replaceText;
import static androidx.test.espresso.action.ViewActions.swipeLeft;
import static androidx.test.espresso.action.ViewActions.swipeUp;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withClassName;
import static androidx.test.espresso.matcher.ViewMatchers.withContentDescription;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.CoreMatchers.anything;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertEquals;

import android.Manifest;
import android.util.Log;
import android.widget.DatePicker;
import android.widget.TimePicker;

import androidx.test.espresso.action.GeneralClickAction;
import androidx.test.espresso.action.GeneralLocation;
import androidx.test.espresso.action.ViewActions;
import androidx.test.espresso.contrib.PickerActions;
import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;
import androidx.test.rule.GrantPermissionRule;

import com.example.nostack.MainActivity;
import com.example.nostack.R;


import org.hamcrest.Matchers;
import org.junit.FixMethodOrder;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;

import kotlin.jvm.JvmField;

/**
 * This class tests the functionality of the organizer's potential actions.
 * The organizer can create an event, edit the event, check the QR codes, check the attendance,
 * end the event, reuse the QR code, and delete the event.
 * Because these actions all require the organizer to be signed in,
 * the tests will all be run in succession in the order they are written.
 */
@RunWith(AndroidJUnit4.class)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
@LargeTest
public class TestUS01 extends UiTest {
    @Rule
    public ActivityScenarioRule<MainActivity> scenario = new ActivityScenarioRule<MainActivity>(MainActivity.class);
    @Rule
    public GrantPermissionRule permissionLocation = GrantPermissionRule.grant(android.Manifest.permission.ACCESS_FINE_LOCATION);
    @Rule
    public GrantPermissionRule permissionNotification = GrantPermissionRule.grant(Manifest.permission.POST_NOTIFICATIONS);
    /**
     * Tests for US 01.
     * As an organizer, I want to create an event.
     */
    @Test
    public void A_testOrganizerCreateEvent() {
        sleepForX(6000);
        onView(withId(R.id.SignIn_SignUpButton)).perform(click());
        sleepForX(3000);
        onView(withId(R.id.nav_qr)).perform(click());
        sleepForX(3000);
        onView(withId(R.id.EventCreationTitleEditText)).perform(replaceText("Ui Test Event"));

        onView(withContentDescription("StartDateTime")).perform(click());
        onView(withClassName(Matchers.equalTo(DatePicker.class.getName()))).perform(PickerActions.setDate(2024, 5, 10));
        onView(withId(android.R.id.button1)).perform(click());
        onView(withClassName(Matchers.equalTo(TimePicker.class.getName()))).perform(PickerActions.setTime(9, 0));
        onView(withId(android.R.id.button1)).perform(click());

        onView(withContentDescription("EndDateTime")).perform(click());
        onView(withClassName(Matchers.equalTo(DatePicker.class.getName()))).perform(PickerActions.setDate(2024, 5, 11));
        onView(withId(android.R.id.button1)).perform(click());
        onView(withClassName(Matchers.equalTo(TimePicker.class.getName()))).perform(PickerActions.setTime(23, 55));
        onView(withId(android.R.id.button1)).perform(click());
        sleepForX(3000);
        onView(withId(R.id.EventCreationLocationEditText)).perform(replaceText("Everywhere"));
        onView(withId(R.id.EventCreationLimitEditText)).perform(replaceText("100"));
        sleepForX(3000);
        onView(withId(R.id.EventCreationDescriptionEditText)).perform(replaceText("Testing...."));
        onView(withId(R.id.EventCreationCreateEventButton)).perform(click());
        sleepForX(3000);
        onView(withText("Ui Test Event")).perform(click());
        sleepForX(3000);
        onView(withText("Ui Test Event")).check(matches(isDisplayed()));
        onView(withText("Everywhere")).check(matches(isDisplayed()));
        onView(withText("Testing....")).check(matches(isDisplayed()));
    }

    
    /**
     * Tests for US 01.
     * As an organizer, I want to edit an event.
     */
    @Test
    public void B_testOrganizerEditEvent() {
        sleepForX(2000);
        onView(withId(R.id.SignIn_SignUpButton)).perform(click());
        sleepForX(3000);
        onView(withText("Ui Test Event")).perform(click());
        sleepForX(3000);
        onView(withText("Testing....")).check(matches(isDisplayed()));
        onView(withText("Everywhere")).check(matches(isDisplayed()));
        onView(withText("Fri, May 10, 2024 to")).check(matches(isDisplayed()));
        onView(withText("Sat, May 11, 2024")).check(matches(isDisplayed()));

        onView(withId(R.id.editButton)).perform(click());
        onView(withId(R.id.EventCreationTitleEditText)).perform(replaceText("New Ui Test!"));

        onView(withContentDescription("StartDateTime")).perform(click());
        onView(withClassName(Matchers.equalTo(DatePicker.class.getName()))).perform(PickerActions.setDate(2024, 6, 10));
        onView(withId(android.R.id.button1)).perform(click());
        onView(withClassName(Matchers.equalTo(TimePicker.class.getName()))).perform(PickerActions.setTime(9, 0));
        onView(withId(android.R.id.button1)).perform(click());

        onView(withContentDescription("EndDateTime")).perform(click());
        onView(withClassName(Matchers.equalTo(DatePicker.class.getName()))).perform(PickerActions.setDate(2024, 6, 11));
        onView(withId(android.R.id.button1)).perform(click());
        onView(withClassName(Matchers.equalTo(TimePicker.class.getName()))).perform(PickerActions.setTime(23, 55));
        onView(withId(android.R.id.button1)).perform(click());
        sleepForX(3000);
        onView(withId(R.id.EventCreationLocationEditText)).perform(replaceText("Anywhere"));
        onView(withId(R.id.EventCreationLimitEditText)).perform(replaceText("150"));
        sleepForX(3000);
        onView(withId(R.id.EventCreationDescriptionEditText)).perform(replaceText("Brand New!"));
        sleepForX(3000);
        onView(withText("Update Event")).perform(click());
        sleepForX(3000);
        onView(withText("New Ui Test!")).check(matches(isDisplayed()));
        onView(withText("Brand New!")).check(matches(isDisplayed()));
        onView(withText("Mon, Jun 10, 2024 to")).check(matches(isDisplayed()));
        onView(withText("Tue, Jun 11, 2024")).check(matches(isDisplayed()));
        onView(withText("Anywhere")).check(matches(isDisplayed()));
    }

    /**
     * Tests for US 01.
     * As an organizer, I want to check the QR codes for my event.
     */
    @Test
    public void C_testOrganizerCheckQrCodes(){
        sleepForX(2000);
        onView(withId(R.id.SignIn_SignUpButton)).perform(click());
        sleepForX(3000);
        onView(withText("New Ui Test!")).perform(click());
        sleepForX(3000);
        onView(withId(R.id.OrganizerEventQRCodeButton)).perform(click());
        sleepForX(3000);
        onView(withText("Event Check-in Code")).check(matches(isDisplayed()));
        onView(withText("Get Event Page QR Code")).perform(click());
        sleepForX(3000);
        onView(withText("Event Page Code")).check(matches(isDisplayed()));
    }

    /**
     * Tests for US 01.
     * As an organizer, I want to check the attendance for my event.
     */
    @Test
    public void D_testOrganizerCheckAttendance(){
        sleepForX(2000);
        onView(withId(R.id.SignIn_SignUpButton)).perform(click());
        sleepForX(3000);
        onView(withText("New Ui Test!")).perform(click());
        sleepForX(3000);
        onView(withId(R.id.button_see_attendees)).perform(click());
        sleepForX(3000);
        onView(withText("Attendance")).check(matches(isDisplayed()));
        onView(withText("Milestone:")).check(matches(isDisplayed()));
        onView(withText("Attendee Locations")).check(matches(isDisplayed()));
    }

    /**
     * Tests for US 01.
     * As an organizer, I want to end an event.
     */
    @Test
    public void E_testOrganizerEndEvent(){
        sleepForX(2000);
        onView(withId(R.id.SignIn_SignUpButton)).perform(click());
        sleepForX(3000);
        onView(withText("New Ui Test!")).perform(click());
        sleepForX(3000);
        onView(withId(R.id.button_end_event)).perform(click());
        sleepForX(3000);
        onView(withText("New Ui Test! (Ended)")).check(matches(isDisplayed()));
        pressBack();
        onView(withText("New Ui Test!")).perform(click());
        onView(withText("New Ui Test! (Ended)")).check(matches(isDisplayed()));
        onView(withText("Brand New!")).check(matches(isDisplayed()));
        onView(withText("Mon, Jun 10, 2024 to")).check(matches(isDisplayed()));
        onView(withText("Tue, Jun 11, 2024")).check(matches(isDisplayed()));
        onView(withText("Anywhere")).check(matches(isDisplayed()));
    }

    /**
     * Tests for US 01.
     * As an organizer, I want to reuse a QR code for my event.
     */
    @Test
    public void F_testOrganizerReuseQrCode(){
        sleepForX(2000);
        onView(withId(R.id.SignIn_SignUpButton)).perform(click());
        sleepForX(3000);
        onView(withId(R.id.nav_qr)).perform(click());
        sleepForX(3000);
        onView(withId(R.id.EventCreationTitleEditText)).perform(replaceText("Even NEWER Ui Test!"));
        onView(withContentDescription("StartDateTime")).perform(click());
        onView(withClassName(Matchers.equalTo(DatePicker.class.getName()))).perform(PickerActions.setDate(2024, 7, 10));
        onView(withId(android.R.id.button1)).perform(click());
        onView(withClassName(Matchers.equalTo(TimePicker.class.getName()))).perform(PickerActions.setTime(9, 0));
        onView(withId(android.R.id.button1)).perform(click());

        onView(withContentDescription("EndDateTime")).perform(click());
        onView(withClassName(Matchers.equalTo(DatePicker.class.getName()))).perform(PickerActions.setDate(2024, 7, 11));
        onView(withId(android.R.id.button1)).perform(click());
        onView(withClassName(Matchers.equalTo(TimePicker.class.getName()))).perform(PickerActions.setTime(23, 55));
        onView(withId(android.R.id.button1)).perform(click());
        sleepForX(3000);
        onView(withId(R.id.EventCreationLocationEditText)).perform(replaceText("Here"));
        onView(withId(R.id.EventCreationLimitEditText)).perform(replaceText("200"));
        sleepForX(3000);
        onView(withId(R.id.EventCreationDescriptionEditText)).perform(replaceText("It is so new!"));
        onView(withId(R.id.EventCreationReuseQRCheckBox)).perform(click());
        onView(withId(R.id.EventCreationCreateEventButton)).perform(click());
        sleepForX(3000);
        onView(withText("New Ui Test!")).perform(click());
        onView(withId(R.id.create_reuse_event)).perform(click());
        onView(withText("Even NEWER Ui Test!")).perform(click());
    }

    /**
     * Tests for US 01.
     * As an organizer, I want to delete an event.
     */
    @Test
    public void G_testOrganizerDeleteEvent(){
        sleepForX(2000);
        onView(withId(R.id.SignIn_SignUpButton)).perform(click());
        sleepForX(3000);
        onView(withText("New Ui Test!")).perform(click());
        sleepForX(3000);
        onView(withId(R.id.button_end_event)).perform(click());
        sleepForX(3000);
        onView(withText("Confirm")).perform(click());
        sleepForX(3000);
        onView(withText("Even NEWER Ui Test!")).perform(click());
        sleepForX(3000);
        onView(withId(R.id.button_end_event)).perform(click());
        sleepForX(3000);
        onView(withId(R.id.button_end_event)).perform(click());
        onView(withText("Confirm")).perform(click());
    }
}
