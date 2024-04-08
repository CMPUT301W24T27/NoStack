package com.example.nostack.uitests;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.Espresso.pressBack;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.replaceText;
import static androidx.test.espresso.action.ViewActions.scrollTo;
import static androidx.test.espresso.action.ViewActions.swipeLeft;
import static androidx.test.espresso.action.ViewActions.swipeUp;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withContentDescription;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.CoreMatchers.anything;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertEquals;

import android.app.Activity;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.test.espresso.action.GeneralClickAction;
import androidx.test.espresso.action.GeneralLocation;
import androidx.test.espresso.action.ViewActions;
import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;
import androidx.test.rule.GrantPermissionRule;

import com.example.nostack.MainActivity;
import com.example.nostack.R;


import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import kotlin.jvm.JvmField;

@RunWith(AndroidJUnit4.class)
@LargeTest
public class TestUS02 extends UiTest{
    @Rule
    public ActivityScenarioRule<MainActivity> scenario = new ActivityScenarioRule<MainActivity>(MainActivity.class);
    @Rule public GrantPermissionRule permissionLocation = GrantPermissionRule.grant(android.Manifest.permission.ACCESS_FINE_LOCATION);
    // Tests for US 02.
    // As an attendee, I want to edit my profile
    @Test
    public void testAttendeeEditProfile() {
        sleepForX(3000);
        onView(withId(R.id.AttendeeSignInButton)).perform(click());
        sleepForX(2000);
        onView(withId(R.id.nav_profile)).perform(click());
        onView(withText("Your Profile")).check(matches(isDisplayed()));
        onView(withText("First Name")).check(matches(isDisplayed()));
        onView(withText("Last Name")).check(matches(isDisplayed()));
        onView(withText("Email")).check(matches(isDisplayed()));
        onView(withText("Phone Number")).check(matches(isDisplayed()));
        onView(withId(R.id.editProfileButton)).perform(click());
        sleepForX(1000);
        onView(withId(R.id.userFirstName)).perform(replaceText("John"));
        onView(withId(R.id.userLastName)).perform(replaceText("Doe"));
        sleepForX(1000);
        onView(withId(R.id.userEmail)).perform(typeText("JohnDoe@email.com"));
        pressBack();
        onView(withId(R.id.userPhoneNumber)).perform(typeText("7805551234"));
        pressBack();
        onView(withId(R.id.saveChangesButton)).perform(click());
        onView(withText("John")).check(matches(isDisplayed()));
        onView(withText("Doe")).check(matches(isDisplayed()));
        onView(withText("JohnDoe@email.com")).check(matches(isDisplayed()));
        onView(withText("7805551234")).check(matches(isDisplayed()));
        sleepForX(1000);
        onView(withId(R.id.editProfileButton)).perform(click());
        sleepForX(1000);
        onView(withId(R.id.userFirstName)).perform(replaceText("Richard"));
        onView(withId(R.id.userLastName)).perform(replaceText("Roe"));
        sleepForX(1000);
        onView(withId(R.id.userEmail)).perform(replaceText("Richardroe@email.com"));
        onView(withId(R.id.userPhoneNumber)).perform(replaceText("1234567890"));
        onView(withId(R.id.saveChangesButton)).perform(click());
        onView(withText("Richard")).check(matches(isDisplayed()));
        onView(withText("Roe")).check(matches(isDisplayed()));
        onView(withText("Richardroe@email.com")).check(matches(isDisplayed()));
        onView(withText("1234567890")).check(matches(isDisplayed()));
    }
    /**
     * Tests for US 02
     * As an attendee, I want to register for an event
     */
    @Test
    public void testAttendeeRegisterForEvent() {
        sleepForX(2000);
        onView(withId(R.id.AttendeeSignInButton)).perform(click());
        sleepForX(2000);
        onView(withId(R.id.listView_yourEvents)).perform(click());
        onView(withId(R.id.AttendeeEventRegisterButton)).perform(click());
        sleepForX(1000);
        onView(withText("Unregister")).check(matches(isDisplayed()));
        sleepForX(3000);
        pressBack();


    }

}