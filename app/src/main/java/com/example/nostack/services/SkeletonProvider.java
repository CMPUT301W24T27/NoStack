package com.example.nostack.services;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.nostack.R;
import com.faltenreich.skeletonlayout.Skeleton;
import com.faltenreich.skeletonlayout.SkeletonLayoutUtils;

public class SkeletonProvider {
    private static SkeletonProvider singleInstance = null;
    private static AppCompatActivity ownerActivity;
    private Skeleton skeleton;

    public static void setSingleton() {
        if (ownerActivity == null) {
            throw new RuntimeException("Owner activity must be set in MainActivity.");
        }
        singleInstance = new SkeletonProvider();
    }

    public static SkeletonProvider getSingleton() {
        if (singleInstance == null) {
            setSingleton();
        }
        return singleInstance;
    }

    public static void setOwnerActivity(AppCompatActivity activity) {
        ownerActivity = activity;
    }

    public static AppCompatActivity getOwnerActivity() {
        return ownerActivity;
    }

    public Skeleton eventListSkeleton(RecyclerView eventList){
        skeleton = SkeletonLayoutUtils.applySkeleton(eventList, R.layout.eventlistcontent, 5);
        skeleton.setMaskColor(ContextCompat.getColor(ownerActivity.getApplicationContext(), R.color.grey));
        skeleton.setMaskCornerRadius(50);
        skeleton.setShimmerDurationInMillis(500);

        return skeleton;
    }

    public Skeleton adminImageSkeleton(RecyclerView imageList){
        skeleton = SkeletonLayoutUtils.applySkeleton(imageList, R.layout.imagelistcontent, 5);
        skeleton.setMaskColor(ContextCompat.getColor(ownerActivity.getApplicationContext(), R.color.grey));
        skeleton.setMaskCornerRadius(50);
        skeleton.setShimmerDurationInMillis(500);

        return skeleton;
    }

    public Skeleton adminProfileSkeleton(RecyclerView userList){
        skeleton = SkeletonLayoutUtils.applySkeleton(userList, R.layout.profilelistcontent, 5);
        skeleton.setMaskColor(ContextCompat.getColor(ownerActivity.getApplicationContext(), R.color.grey));
        skeleton.setMaskCornerRadius(50);
        skeleton.setShimmerDurationInMillis(500);

        return skeleton;
    }
}
