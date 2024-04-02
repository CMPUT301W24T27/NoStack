package com.example.nostack.handlers;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.DisplayMetrics;
import android.util.Log;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.drawable.RoundedBitmapDrawable;
import androidx.core.graphics.drawable.RoundedBitmapDrawableFactory;
import androidx.lifecycle.ViewModelProvider;

import com.example.nostack.controllers.EventController;
import com.example.nostack.controllers.UserController;
import com.example.nostack.models.Event;
import com.example.nostack.models.User;
import com.example.nostack.services.GenerateProfileImage;
import com.example.nostack.viewmodels.UserViewModel;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class ImageViewHandler {

    private static int screenWidth;
    private static int screenHeight;
    private static ImageViewHandler singleInstance = null;
    private static AppCompatActivity ownerActivity;
    private static EventController eventController = EventController.getInstance();
    private static UserController userController = UserController.getInstance();

    public static void setSingleton() {
        if (ownerActivity == null) {
            throw new RuntimeException("Owner activity must be set in MainActivity.");
        }
        singleInstance = new ImageViewHandler();
    }

    public static ImageViewHandler getSingleton() {
        if (singleInstance == null) {
            setSingleton();
        }
        return singleInstance;
    }

    public static void setOwnerActivity(AppCompatActivity activity) {
        ownerActivity = activity;
    }

    public ImageViewHandler() {
        DisplayMetrics metrics = new DisplayMetrics();
        ownerActivity.getWindowManager().getDefaultDisplay().getMetrics(metrics);
        screenWidth = metrics.widthPixels;
        screenHeight = metrics.heightPixels;
    }

    /**
     * Set the event image for the event
     *
     * @param event
     * @param eventBanner
     */
    public void setEventImage(Event event, ImageView eventBanner) {
        String uri_eventBanner = event.getEventBannerImgUrl();

        if (uri_eventBanner != null) {
            StorageReference storageRef = FirebaseStorage.getInstance().getReferenceFromUrl(uri_eventBanner);
            final long ONE_MEGABYTE = 1024 * 1024;

            storageRef.getBytes(ONE_MEGABYTE).addOnSuccessListener(bytes -> {
                Bitmap bmp = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                RoundedBitmapDrawable d = RoundedBitmapDrawableFactory.create(ownerActivity.getResources(), bmp);
                eventBanner.setImageDrawable(d);
            }).addOnFailureListener(exception -> {
                Log.w("Event Image", "Error getting event image, removing reference", exception);
                EventController eventController = EventController.getInstance();
                eventController.removeEventImage(event.getId())
                        .addOnSuccessListener(aVoid -> Log.d("Event Image", "Event image reference successfully removed."))
                        .addOnFailureListener(e -> Log.e("Event Image", "Failed to remove event image reference.", e));

            });
        }
    }

    /**
     * Set user profile image
     *
     * @param user
     * @param imageView
     */
    public void setUserProfileImage(User user, ImageView imageView) {
        if (user.getProfileImageUrl() != null) {
            // Get image from firebase storage
            StorageReference storageRef = FirebaseStorage.getInstance().getReferenceFromUrl(user.getProfileImageUrl());
            final long ONE_MEGABYTE = 1024 * 1024;

            storageRef.getBytes(ONE_MEGABYTE).addOnSuccessListener(bytes -> {
                Bitmap bmp = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                Bitmap scaledBmp = Bitmap.createScaledBitmap(bmp, 300, 300, false);
                RoundedBitmapDrawable d = RoundedBitmapDrawableFactory.create(ownerActivity.getResources(), scaledBmp);
                d.setCornerRadius(100f);
                imageView.setImageDrawable(d);
            }).addOnFailureListener(exception -> {
                Log.w("User Profile", "Error getting profile image, removing reference", exception);
                UserController userController = UserController.getInstance();
                userController.removeUserProfileImage(user.getUuid())
                        .addOnSuccessListener(aVoid -> Log.d("User Profile", "User profile image reference successfully removed."))
                        .addOnFailureListener(e -> Log.e("User Profile", "Failed to remove user profile image reference.", e));
            });
        } else {
            // generate profile image if user has no profile image
            Bitmap pfp = GenerateProfileImage.generateProfileImage(user.getFirstName(), user.getLastName());
            Bitmap scaledBmp = Bitmap.createScaledBitmap(pfp, 300, 300, false);
            RoundedBitmapDrawable d = RoundedBitmapDrawableFactory.create(ownerActivity.getResources(), scaledBmp);
            d.setCornerRadius(100f);
            imageView.setImageDrawable(d);
        }
    }
}
