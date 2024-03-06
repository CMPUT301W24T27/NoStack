package com.example.nostack.utils;

import android.net.Uri;

import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

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
     * @param path The path to store the image in Firebase Cloud Storage ex. user profile, event image, etc.
     * @param imageUri The URI of the image to upload
     * @param listener The listener to handle the upload success or failure
     */
    public void uploadImage(String path,Uri imageUri, UploadListener listener) {
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
}