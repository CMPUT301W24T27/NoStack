package com.example.nostack.controllers;

import android.net.Uri;
import android.util.Log;

import com.example.nostack.handlers.CurrentUserHandler;
import com.example.nostack.models.Image;
import com.example.nostack.models.User;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.ListResult;
import com.google.firebase.storage.StorageReference;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Class for handling images
 */
public class ImageController {
    private static ImageController singleInstance = null;
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    private final FirebaseStorage storage = FirebaseStorage.getInstance();
    private final CollectionReference imageCollectionReference = FirebaseFirestore.getInstance().collection("images");
    private final CurrentUserHandler currentUserHandler = CurrentUserHandler.getSingleton();

    /**
     * Get the instance of the ImageController
     * @return void
     */
    public static ImageController getInstance() {
        if (singleInstance == null) {
            singleInstance = new ImageController();
        }
        return singleInstance;
    }

    /**
     * Empty public constructor
     */
    public ImageController() {
    }

    /**
     * Get all images
     * @param path
     * @return Task<ListResult> The list of images
     */
    public Task<ListResult> getAllImages(String path) {

        String storagePath = path;
        StorageReference storageRef = storage.getReference(storagePath);
        Log.d("ImageController - get all Images", storageRef.listAll().toString());
        return storageRef.listAll();
    }

    /**
     * Get image by id
     * @param id The id of the image
     * @return Task<DocumentSnapshot> The image
     */
    public Task<DocumentSnapshot> getImage(String id) {
        return imageCollectionReference.document(id).get();
    }

    /**
     * Add image to storage
     * @param storagePath The path to the storage
     * @param imageUri The image uri
     * @return Task<String> The image url
     */
    public Task<String> addImage(String storagePath, Uri imageUri) {
        String uuid = UUID.randomUUID().toString();
        StorageReference storageRef = storage.getReference().child(storagePath + uuid);

        return storageRef.putFile(imageUri)
                .continueWithTask(task -> {
                    if (!task.isSuccessful()) {
                        throw task.getException();
                    }
                    return storageRef.getDownloadUrl();
                })
                .continueWith(task -> {
                    if (!task.isSuccessful()) {
                        throw task.getException();
                    }

                    Log.d("ImageController", task.getResult().toString());
                    return task.getResult().toString();
                });
    }

    /**
     * Remove image from database reference
     * @param image The image to remove
     * @return void
     */
    public Task<Void> removeReference(Image image) {
        Map<String, Object> updates = new HashMap<>();
        updates.put("referenceId", null);
        updates.put("path", null);
        return imageCollectionReference.document(image.getId()).update(updates);
    }

    /**
     * Delete image from storage and database
     * @param image The image to delete
     * @return void
     */
    public Task<Void> deleteImage(Image image) {
        StorageReference storageRef = storage.getReferenceFromUrl(image.getUrl());
        Task<Void> deleteImage = storageRef.delete();
        Task<Void> deleteDocument = imageCollectionReference.document(image.getId()).delete();

        if(image.getReferenceId() != null) {
            removeReference(image);
        }

        // If its the user's own profile picture
        if(currentUserHandler.getCurrentUser().getProfileImageUrl().contains(image.getId())) {
            Log.d("ImageController", "Deleting user profile image.");
            User user = currentUserHandler.getCurrentUser();
            user.setProfileImageUrl(null);
            currentUserHandler.updateUser(user);
        }

        return Tasks.whenAll(deleteImage, deleteDocument);
    }
}