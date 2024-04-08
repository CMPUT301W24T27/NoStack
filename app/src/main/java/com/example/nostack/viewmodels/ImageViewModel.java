package com.example.nostack.viewmodels;

import android.net.Uri;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.nostack.controllers.ImageController;
import com.example.nostack.models.Image;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.ListResult;
import com.google.firebase.storage.StorageMetadata;
import com.google.firebase.storage.StorageReference;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

/*
 *  ImageViewModel is a ViewModel class that is used to store and manage state for UI components that require Image data
 * */
public class ImageViewModel extends ViewModel {
    /**
     * Delete image callback
     */
    public interface DeleteImageCallback {
        void onImageDeleted();

        void onImageDeleteFailed();
    }

    private final MutableLiveData<Image> image = new MutableLiveData<>();
    private final MutableLiveData<Image> imageLiveData = new MutableLiveData<>();
    private final MutableLiveData<List<Image>> allImagesLiveData = new MutableLiveData<>();
    private final MutableLiveData<String> errorLiveData = new MutableLiveData<>();
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

    /**
     * Get all images
     *
     * @return void
     */
    public void fetchAllImages() {
        List<String> paths = Arrays.asList("event/banner", "user/profile");
        List<Image> images = new ArrayList<>();

        for (String path : paths) {
            imageController.getAllImages(path).addOnSuccessListener(new OnSuccessListener<ListResult>() {
                @Override
                public void onSuccess(ListResult listResult) {
                    Log.d("ImageViewModel - get from listResult", listResult.toString());
                    for (StorageReference fileRef : listResult.getItems()) {
                        fileRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                StorageReference storageRef = FirebaseStorage.getInstance().getReferenceFromUrl(String.valueOf(uri));
                                storageRef.getMetadata().addOnSuccessListener(new OnSuccessListener<StorageMetadata>() {
                                    @Override
                                    public void onSuccess(StorageMetadata storageMetadata) {
                                        Image image = new Image();
                                        image.setUrl(String.valueOf(storageRef));
                                        image.setPath(String.valueOf(storageRef));
                                        image.setSize(storageMetadata.getSizeBytes());
                                        image.setType(storageMetadata.getContentType());
                                        image.setId(storageMetadata.getName());
                                        long millis = storageMetadata.getCreationTimeMillis();
                                        Date date = new Date(millis);

                                        // Format to MM/DD/YYYY
                                        SimpleDateFormat formatter = new SimpleDateFormat("MM/dd/yyyy");
                                        String formattedDate = formatter.format(date);
                                        image.setCreated(formattedDate);

                                        images.add(image);
//                                        Log.d("ImageViewModel - get Images", String.valueOf(uri));
//                                        Log.d("ImageViewModel - Image", String.valueOf(image));
//                                        Log.d("ImageViewModel - Image", String.valueOf(images.size()));

                                        allImagesLiveData.setValue(images);
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception exception) {
                                        // Uh-oh, an error occurred!
                                    }
                                });
                            }
                        });
                    }


                }
            });
        }
    }

    /**
     * returns the LiveData object containing all images
     *
     * @return LiveData<List < Image>>
     */
    public LiveData<List<Image>> getAllImages() {
        return allImagesLiveData;
    }

    /**
     * Delete image
     *
     * @param image
     * @return void
     */
    public void deleteImage(Image image, DeleteImageCallback callback) {
        imageController.deleteImage(image).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                if (callback != null) {
                    callback.onImageDeleted();
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                errorLiveData.setValue("Image deletion failed");
                if (callback != null) {
                    callback.onImageDeleteFailed();
                }
            }
        });
    }

}