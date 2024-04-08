package com.example.nostack.handlers;

import android.app.Activity;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.drawable.RoundedBitmapDrawable;
import androidx.core.graphics.drawable.RoundedBitmapDrawableFactory;
import androidx.lifecycle.ViewModelProvider;

import com.example.nostack.controllers.EventController;
import com.example.nostack.controllers.UserController;
import com.example.nostack.models.Event;
import com.example.nostack.models.ImageDimension;
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
        eventBanner.setVisibility(View.INVISIBLE);
        if (uri_eventBanner != null) {
            StorageReference storageRef = FirebaseStorage.getInstance().getReferenceFromUrl(uri_eventBanner);
            final long ONE_MEGABYTE = 1024 * 1024;
            storageRef.getBytes(ONE_MEGABYTE).addOnSuccessListener(bytes -> {
                Bitmap bmp = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                RoundedBitmapDrawable d = RoundedBitmapDrawableFactory.create(ownerActivity.getResources(), bmp);
                eventBanner.setImageDrawable(d);
                eventBanner.setScaleType(ImageView.ScaleType.CENTER_CROP);
                eventBanner.setVisibility(View.VISIBLE);
            }).addOnFailureListener(exception -> {
                Log.w("Event Image", "Error getting event image, removing reference", exception);
                EventController eventController = EventController.getInstance();
                eventController.removeEventImage(event.getId())
                        .addOnSuccessListener(aVoid -> Log.d("Event Image", "Event image reference successfully removed."))
                        .addOnFailureListener(e -> Log.e("Event Image", "Failed to remove event image reference.", e));

            });
        }
        eventBanner.setVisibility(View.VISIBLE);
        eventBanner.setScaleType(ImageView.ScaleType.FIT_CENTER);
    }

    /**
     * Set user profile image
     *
     * @param user
     * @param imageView
     */
    public void setUserProfileImage(User user, ImageView imageView, Resources resource, @Nullable ImageDimension imageDimension) {
        int width;
        int height;
        if (imageDimension != null) {
            width = imageDimension.getWidth();
            height = imageDimension.getHeight();
        } else {
            width = 72;
            height = 72;
        }

        if (user.getProfileImageUrl() != null) {
            String uri = user.getProfileImageUrl();

            // Get image from firebase storage
            StorageReference storageRef = FirebaseStorage.getInstance().getReferenceFromUrl(uri);
            final long ONE_MEGABYTE = 1024 * 1024;

            storageRef.getBytes(ONE_MEGABYTE).addOnSuccessListener(bytes -> {
                Bitmap bmp = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                Bitmap scaledBmp = Bitmap.createScaledBitmap(bmp, width, height, false);
                RoundedBitmapDrawable d = RoundedBitmapDrawableFactory.create(resource, scaledBmp);
                d.setCornerRadius(100f);
                imageView.setImageDrawable(d);
            }).addOnFailureListener(exception -> {
                Log.w("User Profile", "Error getting profile image(or deleted) " + user.getUsername());

                // Generate profile image if user has no profile image
                GenerateProfileImage(user.getFirstName(), user.getLastName(), imageView);
            });
        } else {
            // generate profile image if user has no profile image
            GenerateProfileImage(user.getFirstName(), user.getLastName(), imageView);
        }
    }

    /**
     * Generate profile image
     * @param firstName
     * @param lastName
     * @param imageView
     */
    private void GenerateProfileImage(String firstName, String lastName, ImageView imageView) {
        Bitmap pfp = GenerateProfileImage.generateProfileImage(firstName, lastName);
        Bitmap scaledBmp = Bitmap.createScaledBitmap(pfp, 72, 72, false);
        RoundedBitmapDrawable d = RoundedBitmapDrawableFactory.create(ownerActivity.getResources(), scaledBmp);
        d.setCornerRadius(100f);
        imageView.setImageDrawable(d);
    }
}
