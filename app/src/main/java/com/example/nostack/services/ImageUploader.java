package com.example.nostack.services;

import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;

import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.UUID;

/**
 * ImageUploader
 * Upload an image to Firebase Cloud Storage then return the URL of the uploaded image
 */
public class ImageUploader {
    private static final String TAG = "ImageUploader";

    public interface UploadListener {
        void onUploadSuccess(String imageUrl);

        void onUploadFailure(Exception exception);
    }

    /**
     * Upload an image to Firebase Cloud Storage
     *
     * @param path     The path to store the image in Firebase Cloud Storage ex. user profile, event image, etc.
     * @param imageUri The URI of the image to upload
     * @param listener The listener to handle the upload success or failure
     */
    public void uploadImage(String path, Uri imageUri, UploadListener listener) {
        if (imageUri == null) {
            listener.onUploadFailure(new IllegalArgumentException("Image URI cannot be null"));
            return;
        }

        // Get a reference to Cloud Storage
        StorageReference storageRef = FirebaseStorage.getInstance().getReference();

        // Create a reference to the image file in Cloud Storage
        StorageReference imageRef = storageRef.child(path + UUID.randomUUID().toString());

        // Upload the image to Cloud Storage
        imageRef.putFile(imageUri)
                .addOnSuccessListener(taskSnapshot -> {
                    // Get the download URL of the uploaded image
                    taskSnapshot.getMetadata().getReference().getDownloadUrl()
                            .addOnCompleteListener(urlTask -> {
                                if (urlTask.isSuccessful()) {
                                    String imageUrl = urlTask.getResult().toString();
                                    listener.onUploadSuccess(imageUrl);
                                } else {
                                    listener.onUploadFailure(urlTask.getException());
                                }
                            });
                })
                .addOnFailureListener(listener::onUploadFailure);
    }

    /**
     * Save image to local storage
     *
     * @param imageBitmap The bitmap of the image to save
     * @param filename    The filename to save the image as
     */
    public static Uri saveImage(Bitmap imageBitmap, String filename) {
        // Save to app's own directory
        String PATH = Environment.getDataDirectory() + "/NoStack";
        filename = PATH + "/" + filename;

        try {
            FileOutputStream out = new FileOutputStream(filename);
            imageBitmap.compress(Bitmap.CompressFormat.PNG, 100, out);
            out.flush();
            out.close();
        } catch (IOException e) {
            Log.e(TAG, "Error saving image to local storage", e);
        }

        // Return URI to image
        File image = new File(filename);
        Uri imageUri = Uri.fromFile(image);

        return imageUri;
    }


}