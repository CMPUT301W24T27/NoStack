package com.example.nostack.views.admin;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.graphics.drawable.RoundedBitmapDrawable;
import androidx.core.graphics.drawable.RoundedBitmapDrawableFactory;
import androidx.fragment.app.Fragment;

import com.example.nostack.R;
import com.example.nostack.models.Event;
import com.example.nostack.models.Image;
import com.example.nostack.models.User;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.sql.Array;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

public class ImageArrayAdapter extends ArrayAdapter<Image> {
    private ConstraintLayout layout;
    private Fragment currFragment;
    private ArrayList<Image> ourImages;
    public ImageArrayAdapter(@NonNull Context context, ArrayList<Image> Image, Fragment currfragment) {
        super(context, 0,Image);
        currFragment = currfragment;
        ourImages = Image;
    }

    public boolean containsImage(Image image) {
        boolean contained = false;
        for (Image image1:ourImages) {
            if (image.getId().equals(image1.getId())) {return true;}
        }
        return contained;
    }

    public void addImage(Image image) {
        Log.d("ImageArray Adapter - set Images", "Adding image to array adapter");
        if (!containsImage(image)) {
            add(image);
        }
    }
    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View view;
        if (convertView == null) {
            view = LayoutInflater.from(getContext()).inflate(R.layout.imagelistcontent,parent,false);
        } else {
            view = convertView;
        }

        Image image = getItem(position);
        Log.d("ImageArrayAdapter - Image", String.valueOf(image));

        TextView imageName = view.findViewById(R.id.ImageListContentNameText);
//        TextView imageSize = view.findViewById(R.id.ImageListContentSizeText);
//        TextView imageCreated = view.findViewById(R.id.ImageListContentCreatedText);
//        TextView imageType = view.findViewById(R.id.ImageListContentTypeText);
        ImageView posterImage = view.findViewById(R.id.ImageListContentPosterImage);

        if (image != null) {

            imageName.setText(image.getReferenceId());
//            profileEmail.setText(user.getEmailAddress());
//            profilePhoneNumber.setText(user.getPhoneNumber());

            String uri = image.getUrl();

            if (uri != null) {
                // Get image from firebase storage
                StorageReference storageRef = FirebaseStorage.getInstance().getReferenceFromUrl(uri);
                final long ONE_MEGABYTE = 1024 * 1024;

                storageRef.getBytes(ONE_MEGABYTE).addOnSuccessListener(bytes -> {
                    Bitmap bmp = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                    Bitmap scaledBmp = Bitmap.createScaledBitmap(bmp, 250, 250, false);
                    RoundedBitmapDrawable d = RoundedBitmapDrawableFactory.create(currFragment.getResources(), scaledBmp);
                    d.setCornerRadius(50f);
                    posterImage.setImageDrawable(d);
                }).addOnFailureListener(exception -> {
                    Log.w("Image Profile", "Error getting Image banner", exception);
                });
            }
        }
        return view;
    }

}
