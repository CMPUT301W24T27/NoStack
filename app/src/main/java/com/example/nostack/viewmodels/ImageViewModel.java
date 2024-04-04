package com.example.nostack.viewmodels;

import android.net.Uri;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.nostack.controllers.ImageController;
import com.example.nostack.models.Image;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.ListResult;
import com.google.firebase.storage.StorageReference;

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

//    public void fetchImage(String id) {
//        imageController.getImage(id)
//                .addOnSuccessListener(documentSnapshot -> {
//                    Image image = documentSnapshot.toObject(Image.class);
//                    imageLiveData.postValue(image);
//                }).addOnFailureListener(e -> {
//                    Log.e("imageViewModel", "Error fetching image", e);
//                    errorLiveData.postValue(e.getMessage());
//                });
//    }

    public void fetchAllImages(){
        imageController.getAllImages().addOnSuccessListener(new OnSuccessListener<ListResult>() {
            List<Image> images = new ArrayList<>();
            @Override
            public void onSuccess(ListResult listResult) {
                for (StorageReference fileRef : listResult.getItems()) {
                    fileRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            StorageReference storageRef = FirebaseStorage.getInstance().getReferenceFromUrl(String.valueOf(uri));
                            Image image = new Image();
                            image.setUrl(String.valueOf(storageRef));
                            image.setPath(String.valueOf(storageRef));
                            image.setId(String.valueOf((storageRef)));
                            images.add(image);
                            //images.add(uri.toString());
                            Log.d("ImageViewModel - get Images", String.valueOf(uri));
                            Log.d("ImageViewModel - Image", String.valueOf(image));
                            Log.d("ImageViewModel - Image", String.valueOf(images.size()));

                            // Check if all images are fetched, then update LiveData
                            if (images.size() == listResult.getItems().size()) {
                                allImagesLiveData.postValue(images);
                                Log.d("ImageViewModel - LiveData", String.valueOf(allImagesLiveData.getValue()));
                            }
                        }
                        //allImagesLiveData.postValue(images);
                    });
//                  allImagesLiveData.postValue(images);
//                  Log.d("ImageViewModel - Livedata", String.valueOf(allImagesLiveData.getValue()));
                }
//                allImagesLiveData.postValue(images);
//                Log.d("ImageViewModel - Livedata", String.valueOf(allImagesLiveData.getValue()));
            }
        });
//                } -> {
//                    List<Image> images = new ArrayList<>();
//                    for (ListResult listResult:ListResult) {
//                        Image image = document.toObject(Image.class);
//                        Log.d("ImageViewModel - get Images", image.getId());
//                        if (image != null){
//                            Log.d("ImageVM", "Image not null, added");
//                            images.add(image);
//                        }
//                    }
//                    allImagesLiveData.postValue(images);
//                }).addOnFailureListener(e -> {
//                    Log.e("ImageVM", "Error fetching image", e);
//                    errorLiveData.postValue(e.getMessage());
//                });
    }
    public LiveData<List<Image>> getAllImages() {
        return allImagesLiveData;
    }
}