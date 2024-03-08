package com.example.nostack.utils;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.DisplayMetrics;
import android.util.Log;
import android.widget.ImageView;

import androidx.core.graphics.drawable.RoundedBitmapDrawable;
import androidx.core.graphics.drawable.RoundedBitmapDrawableFactory;

import com.example.nostack.model.Events.Event;
import com.example.nostack.model.User.User;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class Image {

    private final int screenWidth;
    private final int screenHeight;
    private final Activity activity;

    public Image(Activity activity) {
        this.activity = activity;
        DisplayMetrics metrics = new DisplayMetrics();
        activity.getWindowManager().getDefaultDisplay().getMetrics(metrics);

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
            // Get image from firebase storage
            StorageReference storageRef = FirebaseStorage.getInstance().getReferenceFromUrl(uri_eventBanner);
            final long ONE_MEGABYTE = 1024 * 1024;

            storageRef.getBytes(ONE_MEGABYTE).addOnSuccessListener(bytes -> {
                Bitmap bmp = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                Bitmap scaledBmp = Bitmap.createScaledBitmap(bmp, screenWidth, screenHeight, false);
                RoundedBitmapDrawable d = RoundedBitmapDrawableFactory.create(activity.getResources(), scaledBmp);
                eventBanner.setImageDrawable(d);
            }).addOnFailureListener(exception -> {
                Log.w("User Profile", "Error getting profile image", exception);
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
                RoundedBitmapDrawable d = RoundedBitmapDrawableFactory.create(activity.getResources(), scaledBmp);
                d.setCornerRadius(100f);
                imageView.setImageDrawable(d);
            }).addOnFailureListener(exception -> {
                Log.w("User Profile", "Error getting profile image", exception);
            });
        } else {
            // generate profile image if user has no profile image
            Bitmap pfp = GenerateProfileImage.generateProfileImage(user.getFirstName(), user.getLastName());
            Bitmap scaledBmp = Bitmap.createScaledBitmap(pfp, 300, 300, false);
            RoundedBitmapDrawable d = RoundedBitmapDrawableFactory.create(activity.getResources(), scaledBmp);
            d.setCornerRadius(100f);
            imageView.setImageDrawable(d);
        }
    }
}
