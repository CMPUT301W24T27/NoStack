package com.example.nostack.views.user;

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
import com.example.nostack.models.User;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.sql.Array;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

public class UserArrayAdapter extends ArrayAdapter<User> {
    private ConstraintLayout layout;
    private Fragment currFragment;
    private ArrayList<User> ourUsers;
    public UserArrayAdapter(@NonNull Context context, ArrayList<User> User, Fragment currfragment) {
        super(context, 0,User);
        currFragment = currfragment;
        ourUsers = User;
    }

    public boolean containsUser(User user) {
        boolean contained = false;
        for (User user1:ourUsers) {
            if (user.getUuid().equals(user1.getUuid())) {return true;}
        }
        return contained;
    }

    public void addUser(User user) {
        if (!containsUser(user)) {
            add(user);
        }
    }
    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View view;
        if (convertView == null) {
            view = LayoutInflater.from(getContext()).inflate(R.layout.profilelistcontent,parent,false);
        } else {
            view = convertView;
        }

        User user = getItem(position);

        TextView profileName = view.findViewById(R.id.ProfileListContentNameText);
        TextView profileEmail = view.findViewById(R.id.ProfileListContentEmailText);
        TextView profilePhoneNumber = view.findViewById(R.id.ProfileListContentPhoneNumberText);
        //TextView eventLocationTitle = view.findViewById(R.id.EventListContentLocationText);
        ImageView profileImage = view.findViewById(R.id.ProfileListContentPosterImage);

        if (user != null) {

//            DateFormat df = new SimpleDateFormat("EEE, MMM d, yyyy");
//            DateFormat tf = new SimpleDateFormat("h:mm a");

            //String startDate = df.format(user.getStartDate());
            //String endDate = df.format(user.getEndDate());
            //String startTime = tf.format(user.getStartDate());
            //String endTime = tf.format(user.getEndDate());

//            if (!startDate.equals(endDate)) {
//                eventStartDateTitle.setText(startDate + " to");
//                eventTimeTitle.setText(endDate);
//            } else {
//                eventStartDateTitle.setText(startDate);
//                eventTimeTitle.setText(startTime + " - " + endTime);
//            }
//
//            if (user != null){
//                if (user.getUsername() == null){
//                    user.setUsername("Null");
//                } else {
//                    //Log.d("UserArrayAdapter", user.getUsername());
//                    //Log.d("UserArrayAdapter", user.getUuid());
//                    profileName.setText(user.getUsername());
//                    //Log.d("UserArrayAdapter", user.getUsername());
//                }
//                if (user.getEmailAddress() == null){
//                    user.setEmailAddress("Null");
//                } else {
//                    profileEmail.setText(user.getEmailAddress());
//                }
//                if (user.getPhoneNumber() == null){
//                    Log.d("UserArrayAdapter", user.getUsername());
//                    Log.d("UserArrayAdapter", user.getUuid());
//                    user.setPhoneNumber("Null");
//                } else {
//                    Log.d("UserArrayAdapter", user.getUsername());
//                    Log.d("UserArrayAdapter", user.getUuid());
//                    profilePhoneNumber.setText(user.getPhoneNumber());
//                }
//            }

            profileName.setText(user.getUsername());
            profileEmail.setText(user.getEmailAddress());
            profilePhoneNumber.setText(user.getPhoneNumber());

            String uri = user.getProfileImageUrl();

            if (uri != null) {
                // Get image from firebase storage
                StorageReference storageRef = FirebaseStorage.getInstance().getReferenceFromUrl(uri);
                final long ONE_MEGABYTE = 1024 * 1024;

                storageRef.getBytes(ONE_MEGABYTE).addOnSuccessListener(bytes -> {
                    Bitmap bmp = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                    Bitmap scaledBmp = Bitmap.createScaledBitmap(bmp, 250, 250, false);
                    RoundedBitmapDrawable d = RoundedBitmapDrawableFactory.create(currFragment.getResources(), scaledBmp);
                    d.setCornerRadius(50f);
                    profileImage.setImageDrawable(d);
                }).addOnFailureListener(exception -> {
                    Log.w("User Profile", "Error getting profile image", exception);
                });
            }
        }
        return view;
    }

}
