package com.example.nostack.services;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.net.Uri;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.fragment.NavHostFragment;

import com.example.nostack.R;
import com.example.nostack.handlers.CurrentUserHandler;
import com.example.nostack.handlers.ImageViewHandler;
import com.example.nostack.models.Image;
import com.example.nostack.models.ImageDimension;
import com.example.nostack.viewmodels.UserViewModel;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class NavbarConfig {
    private static NavbarConfig singleInstance = null;
    private static AppCompatActivity ownerActivity;
    private static CurrentUserHandler currentUserHandler = CurrentUserHandler.getSingleton();
    private static ImageViewHandler imageViewHandler = ImageViewHandler.getSingleton();
    private ImageButton scanQRButton = ownerActivity.findViewById(R.id.nav_qr);
    private ImageButton homeButton = ownerActivity.findViewById(R.id.nav_home);
    private ImageButton profileButton = ownerActivity.findViewById(R.id.nav_profile);
    private BottomNavigationView navbar = ownerActivity.findViewById(R.id.nav_bar);
    private NavHostFragment navHostFragment = (NavHostFragment) ownerActivity.getSupportFragmentManager().findFragmentById(R.id.fragmentContainerView);

    /**
     * Action to be performed by the hero button
     */
    public interface HeroAction {
        void callback();
    }

    /**
     * Set the singleton instance of NavbarConfig
     */
    public static void setSingleton() {
        if (ownerActivity == null) {
            throw new RuntimeException("Owner activity must be set in MainActivity.");
        }
        singleInstance = new NavbarConfig();
    }

    /**
     * Get the singleton instance of NavbarConfig
     * @return NavbarConfig
     */
    public static NavbarConfig getSingleton() {
        if (singleInstance == null) {
            setSingleton();
        }
        return singleInstance;
    }

    /**
     * Set the owner activity of NavbarConfig
     * @param activity
     */
    public static void setOwnerActivity(AppCompatActivity activity) {
        ownerActivity = activity;
    }

    /**
     * Get the owner activity of NavbarConfig
     * @return AppCompatActivity
     */
    public static AppCompatActivity getOwnerActivity() {
        return ownerActivity;
    }

    /**
     * Set the navbar for the attendee
     * @param resources
     */
    public void setAttendee(Resources resources){
        setCommonActions(resources);
    }

    /**
     * Set the navbar for the organizer
     * @param resources
     */
    public void setOrganizer(Resources resources){
        // Set common actions
        setCommonActions(resources);
    }

    /**
     * Set the navbar for the admin
     * @param resources
     */
    public void setAdmin(Resources resources) {
        // Set common actions
        setCommonActions(resources);

        // Currently disable hero button for admin
        scanQRButton.setVisibility(scanQRButton.INVISIBLE);

        // Home button re-enable hero button
        homeButton.setOnClickListener(v -> {
            scanQRButton.setVisibility(scanQRButton.VISIBLE);
            navHostFragment.getNavController().navigate(R.id.startUp);
            setInvisible();
        });
    }

    /**
     * Set the hero button action
     * @param heroAction callback to the action
     */
    public void setHeroAction(HeroAction heroAction){
        scanQRButton.setOnClickListener(v -> {
            heroAction.callback();
        });
    }

    public void setCommonActions(Resources resources){
        // Set home button action
        homeButton.setOnClickListener(v -> {
            navHostFragment.getNavController().navigate(R.id.startUp);
            setInvisible();
        });

        // Set profile button action
        profileButton.setOnClickListener(v -> {
            navHostFragment.getNavController().navigate(R.id.userProfile);
            setInvisible();
        });

        // Set profile picture
        imageViewHandler.setUserProfileImage(currentUserHandler.getCurrentUser(), profileButton, resources, new ImageDimension(100, 100));

        // Set visibility of navbar
        setVisible();
    }

    /**
     * Set the visibility of the navbar
     */
    public void setVisible(){
        navbar.setVisibility(navbar.VISIBLE);
    }

    /**
     * Set the invisibility of the navbar
     */
    public void setInvisible(){
        navbar.setVisibility(navbar.INVISIBLE);
    }
}
