package com.example.nostack.views.admin.adapters;

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

    public Image getImage(int position) {
        return ourImages.get(position);
    }

    public void addImage(Image image) {
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

        TextView imageName = view.findViewById(R.id.ImageListContentNameText);
        TextView imageSize = view.findViewById(R.id.ImageListContentSizeText);
        TextView imageCreated = view.findViewById(R.id.ImageListContentCreatedText);
        TextView imageType = view.findViewById(R.id.ImageListContentTypeText);

        if (image != null) {
            imageName.setText(image.getId());
            imageSize.setText(image.getSize() + "bytes");
            imageType.setText(image.getType());
            imageCreated.setText(image.getCreated());
        }
        return view;
    }

}
