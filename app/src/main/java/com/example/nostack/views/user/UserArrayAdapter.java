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

        if (user != null) {
            if (user.getUsername() != null){
                profileName.setText(user.getUsername().trim());
            } else {
                profileName.setText("User Name: N/A");
            }
            if (user.getEmailAddress() != null){
                profileEmail.setText(user.getEmailAddress());
            } else {
                profileEmail.setText("Email Address: N/A");
            }
            if (user.getPhoneNumber() != null){
                profilePhoneNumber.setText(user.getPhoneNumber());
            } else {
                profilePhoneNumber.setText("Phone Number: N/A");
            }
        }
        return view;
    }

}
