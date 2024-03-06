package com.example.nostack.ui;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.drawable.RoundedBitmapDrawable;
import androidx.core.graphics.drawable.RoundedBitmapDrawableFactory;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.fragment.NavHostFragment;

import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.example.nostack.R;
import com.example.nostack.model.State.UserViewModel;
import com.example.nostack.utils.GenerateProfileImage;
import com.example.nostack.utils.ImageUploader;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import javax.annotation.Nullable;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link UserProfile#newInstance} factory method to
 * create an instance of this fragment.
 */
public class UserProfile extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private ImageUploader imageUploader;
    private static final int IMAGE_PICK_CODE = 100;

    private UserViewModel userViewModel;

    public UserProfile() {
        // Required empty public constructor
    }

    private void selectProfilePicture() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, IMAGE_PICK_CODE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == IMAGE_PICK_CODE && resultCode == AppCompatActivity.RESULT_OK && data != null) {
            Uri imageUri = data.getData();
            uploadProfileImage(imageUri);
        }
    }

    private void uploadProfileImage(Uri imageUri) {
        imageUploader.uploadImage("user/profile/", imageUri, new ImageUploader.UploadListener() {
            @Override
            public void onUploadSuccess(String imageUrl) {
                // Update user profile with the downloaded image URL
                updateUserWithImageUrl(imageUrl);
            }

            @Override
            public void onUploadFailure(Exception exception) {
                Log.w("User edit", "Profile image upload failed:", exception);
                // TODO: Show error to user
            }
        });
    }

    private void updateUserWithImageUrl(String imageUrl) {
        // Update user profile with the downloaded image URL

        UserViewModel userViewModel = new ViewModelProvider(requireActivity()).get(UserViewModel.class);
        userViewModel.getUser().observe(getViewLifecycleOwner(), user -> {
            user.setProfileImageUrl(imageUrl);
            // Update firestore user profile with the new image URL
            userViewModel.updateUser(user);
            updateProfilePicture();
        });


    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment UserProfile.
     */
    // TODO: Rename and change types and number of parameters
    public static UserProfile newInstance(String param1, String param2) {
        UserProfile fragment = new UserProfile();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }

        imageUploader = new ImageUploader();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_user_profile, container, false);
        userViewModel = new ViewModelProvider((AppCompatActivity) getActivity() ).get(UserViewModel.class);

        view.findViewById(R.id.backButton).setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                NavHostFragment.findNavController(UserProfile.this)
                        .navigate(R.id.action_userProfile_to_attendeeHome);
            }
        });

        view.findViewById(R.id.editProfileButton).setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                View editProfileButton = view.findViewById(R.id.editProfileButton);
                View saveProfileButton = view.findViewById(R.id.saveChangesButton);
                View editProfilePictureButtons = view.findViewById(R.id.editProfilePictureButtons);

                editProfileButton.setVisibility(View.GONE);
                saveProfileButton.setVisibility(View.VISIBLE);
                editProfilePictureButtons.setVisibility(View.VISIBLE);

            }
        });

        view.findViewById(R.id.saveChangesButton).setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                View editProfileButton = view.findViewById(R.id.editProfileButton);
                View saveProfileButton = view.findViewById(R.id.saveChangesButton);
                View editProfilePictureButtons = view.findViewById(R.id.editProfilePictureButtons);
                editProfileButton.setVisibility(View.VISIBLE);
                saveProfileButton.setVisibility(View.GONE);
                editProfilePictureButtons.setVisibility(View.GONE);
            }
        });

        view.findViewById(R.id.profileImage).setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                selectProfilePicture();
            }
        });

        view.findViewById(R.id.deleteProfilePictureButton).setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                UserViewModel userViewModel = new ViewModelProvider(requireActivity()).get(UserViewModel.class);
                userViewModel.getUser().observe(getViewLifecycleOwner(), user -> {
                    user.setProfileImageUrl(null);
                    userViewModel.updateUser(user);
                    updateProfilePicture();
                });
            }
        });

        userViewModel.getUser().observe(getViewLifecycleOwner(), user -> {
            if (user != null) {
                ((TextView) view.findViewById(R.id.userName)).setText(user.getFirstName());
                ((TextView) view.findViewById(R.id.userEmail)).setText(user.getEmailAddress());
                ((TextView) view.findViewById(R.id.userPhoneNumber)).setText(user.getPhoneNumber());

                // Set profile image from URL
                updateProfilePicture();

            }
            else{
                Log.d("AttendeeHome", "User is null");
            }
        });

        // Inflate the layout for this fragment
        return view;
    }

    /**
     * Updates the profile picture in the view with the user's profile image URL
     */
    private void updateProfilePicture() {
        // Set profile image from URL
        ImageButton profileImage = getView().findViewById(R.id.profileImage);
        userViewModel.getUser().observe(getViewLifecycleOwner(), user -> {
            if (user.getProfileImageUrl() != null) {
                String uri = user.getProfileImageUrl();

                // Get image from firebase storage
                StorageReference storageRef = FirebaseStorage.getInstance().getReferenceFromUrl(uri);
                final long ONE_MEGABYTE = 1024 * 1024;

                storageRef.getBytes(ONE_MEGABYTE).addOnSuccessListener(bytes -> {
                    Bitmap bmp = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                    Bitmap scaledBmp = Bitmap.createScaledBitmap(bmp, 250, 250, false);
                    RoundedBitmapDrawable d = RoundedBitmapDrawableFactory.create(getResources(), scaledBmp);
                    d.setCornerRadius(100f);
                    profileImage.setImageDrawable(d);
                }).addOnFailureListener(exception -> {
                    Log.w("User Profile", "Error getting profile image", exception);
                });
            }
            else{
                // generate profile image if user has no profile image
                Bitmap pfp = GenerateProfileImage.generateProfileImage(user.getFirstName(), user.getLastName());
                Bitmap scaledBmp = Bitmap.createScaledBitmap(pfp, 250, 250, false);
                RoundedBitmapDrawable d = RoundedBitmapDrawableFactory.create(getResources(), scaledBmp);
                d.setCornerRadius(100f);
                profileImage.setImageDrawable(d);
            }
        });
    }
}