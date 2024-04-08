package com.example.nostack.services;

import android.content.ContentResolver;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.ImageDecoder;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.Toast;

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

    /**
     * Compress image
     *
     * @param imageUri The URI of the image to compress
     * @param quality  The quality (in decimal 0 < quality <= 1) of the compressed image
     * @return The URI of the compressed image
     */
    public static Uri compressImage(Uri imageUri, double quality, Context context) throws IOException{
        // Check if quality is within domain
        if(quality <= 0 || quality > 1) {
            throw new IllegalArgumentException("Quality must be in the range: 0 < quality <= 1");
        }

        // Check if imageUri is null
        if (imageUri == null) {
            throw new IllegalArgumentException("Image URI cannot be null");
        }

        // Convert to bitmap
        Bitmap imageBitmap = MediaStore.Images.Media.getBitmap(context.getContentResolver(), imageUri);
        Log.d("ImageSize",String.format("%d bytes",imageBitmap.getByteCount()));
        if (imageBitmap.getByteCount() >=  150 * 1000 * 1000) {Toast.makeText(context, "Cannot upload as image size is too large.", Toast.LENGTH_SHORT).show();}
        assert (imageBitmap.getByteCount() < 150 * 1000 * 1000); // Image greater than ~ 35mb might crash app.

        // Create temporary file to cache compressed image
        String filename = UUID.randomUUID().toString() + ".jpg";
        File file = new File(context.getCacheDir(), filename);

        // Compress image
        FileOutputStream out = new FileOutputStream(file);
        imageBitmap.compress(Bitmap.CompressFormat.JPEG, (int)(quality * 100), out);
        out.flush();
        out.close();

        // Return URI to compressed image
        return Uri.fromFile(file);
    }
}