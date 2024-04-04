package com.example.nostack.viewmodels;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.nostack.controllers.ImageController;
import com.example.nostack.models.Image;
import com.google.firebase.firestore.DocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

/*
 *  ImageViewModel is a ViewModel class that is used to store and manage state for UI components that require Image data
 * */
public class ImageViewModel extends ViewModel {
    private final MutableLiveData<Image> image = new MutableLiveData<>();
    private final MutableLiveData<Image> imageLiveData = new MutableLiveData<>();
    private final MutableLiveData<List<Image>> allImagesLiveData = new MutableLiveData<>();
    private final  MutableLiveData<String> errorLiveData = new MutableLiveData<>();
    private final ImageController imageController = ImageController.getInstance();
    public LiveData<String> getErrorLiveData() {
        return errorLiveData;
    }
    public void clearErrorLiveData() {
        errorLiveData.setValue(null);
    }
    /**
     * Set the image object to be stored in the ViewModel state
     *
     * @param image the image object to be stored
     */
    public void setImage(Image image) {
        this.image.setValue(image);
        Log.d("ImageViewModel", "the uri is" + image.getUrl());
    }

    /**
     * Get the image object stored in the ViewModel state
     *
     * @return LiveData<image>
     */
    public LiveData<Image> getImage() {
        return image;
    }

//    /**
//     * Updates the image object stored in Firestore
//     *
//     * @param user new user object to be stored
//     */
//    public void updateUser(User user) {
//        final FirebaseFirestore db;
//        final CollectionReference userRef;
//        // Update user object in firestore
//
//        db = FirebaseFirestore.getInstance();
//        userRef = db.collection("users");
//        userRef.document(user.getUuid()).set(user);
//    }

    public void fetchImage(String id) {
        imageController.getImage(id)
                .addOnSuccessListener(documentSnapshot -> {
                    Image image = documentSnapshot.toObject(Image.class);
                    imageLiveData.postValue(image);
                }).addOnFailureListener(e -> {
                    Log.e("imageViewModel", "Error fetching image", e);
                    errorLiveData.postValue(e.getMessage());
                });
    }

    public void fetchAllImages(){
        imageController.getAllImages()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    List<Image> images = new ArrayList<>();
                    for (DocumentSnapshot document:queryDocumentSnapshots) {
                        Image image = document.toObject(Image.class);
                        //Log.d("ImageViewModel - get Images", Image.);
                        if (image != null){
                            Log.d("ImageVM", "Image not null, added");
                            images.add(image);
                            //Log.d("UserViewModel - pass", user.getFirstName());
                        }
                    }
                    allImagesLiveData.postValue(images);
                }).addOnFailureListener(e -> {
                    Log.e("ImageVM", "Error fetching image", e);
                    errorLiveData.postValue(e.getMessage());
                });
    }
    public LiveData<List<Image>> getAllImages() {
        return allImagesLiveData;
    }
}