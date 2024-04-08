package com.example.nostack.models;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import androidx.core.graphics.drawable.RoundedBitmapDrawable;
import androidx.core.graphics.drawable.RoundedBitmapDrawableFactory;

import com.google.android.gms.tasks.Task;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class Image {
    private String url;
    private String path;
    private String referenceId;
    private String id;
    private long size;
    private String type;
    private String created;
    public Image() {
    }

    public Image(String url, String path, String referenceId, String id, long size, String type, String created) {
        this.url = url;
        this.path = path;
        this.referenceId = referenceId;
        this.id = id;
        this.size = size;
        this.type = type;
        this.created = created;
    }

    public long getSize() {
        return size;
    }

    public void setSize(long size) {
        this.size = size;
    }
    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
    public String getCreated() {
        return created;
    }

    public void setCreated(String created) {
        this.created = created;
    }
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getReferenceId() {
        return referenceId;
    }

    public void setReferenceId(String referenceId) {
        this.referenceId = referenceId;
    }

    /**
     * Get the image from the storage
     * @param context The context of the activity
     * @return  An image in the form of a RoundedBitmapDrawable
     */
    public Task<RoundedBitmapDrawable> getImage(Context context){
        if(url == null) {
            Log.d("Image", "URL is null");
            return null;
        }

        StorageReference storageRef = FirebaseStorage.getInstance().getReferenceFromUrl(url);
        final long ONE_MEGABYTE = 1024 * 1024;
        return storageRef.getBytes(ONE_MEGABYTE).continueWith(task -> {
            if (!task.isSuccessful()) {
                throw task.getException();
            }
            byte[] bytes = task.getResult();
            Bitmap bmp = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
            Bitmap scaledBmp = Bitmap.createScaledBitmap(bmp, 250, 250, false);
            RoundedBitmapDrawable d = RoundedBitmapDrawableFactory.create(context.getResources(), scaledBmp);
            d.setCornerRadius(50f);
            return d;
        });
    }
}
