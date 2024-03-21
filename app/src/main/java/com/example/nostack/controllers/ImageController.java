package com.example.nostack.controllers;

import android.net.Uri;

import com.example.nostack.handlers.CurrentUserHandler;
import com.example.nostack.models.Image;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class ImageController {
    private static ImageController singleInstance = null;
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    private final FirebaseStorage storage = FirebaseStorage.getInstance();
    private final CollectionReference imageCollectionReference = FirebaseFirestore.getInstance().collection("images");
    private final CurrentUserHandler currentUserHandler = CurrentUserHandler.getSingleton();

    public static ImageController getInstance() {
        if (singleInstance == null) {
            singleInstance = new ImageController();
        }
        return singleInstance;
    }

    public ImageController() {
    }

    public Task<String> addImage(String storagePath, Uri imageUri, String referenceId) {

        String uuid = UUID.randomUUID().toString();
        StorageReference storageRef = storage.getReference().child(storagePath + uuid);

        return storageRef.putFile(imageUri)
                .continueWithTask(task -> {
                    if (!task.isSuccessful()) {
                        throw task.getException();
                    }
                    return storageRef.getDownloadUrl();
                })
                .continueWithTask(task -> {
                    if (!task.isSuccessful()) {
                        throw task.getException();
                    }
                    String downloadUrl = task.getResult().toString();

                    Image newImage = new Image();
                    newImage.setPath(storagePath);
                    newImage.setUrl(downloadUrl);
                    newImage.setReferenceId(referenceId);
                    newImage.setId(uuid);

                    return imageCollectionReference.document(uuid).set(newImage)
                            .continueWithTask(ignoredTask -> Tasks.forResult(downloadUrl));
                });
    }

    public Task<Void> removeReference(String imageId) {
        Map<String, Object> updates = new HashMap<>();
        updates.put("referenceId", null);
        updates.put("path", null);
        return imageCollectionReference.document(imageId).update(updates);
    }

    // TODO: Deleting an Image, may be a little too nuanced, will be done later on.
    public Task<Void> deleteImage(String imageId) {
        return Tasks.whenAll();
    }
}