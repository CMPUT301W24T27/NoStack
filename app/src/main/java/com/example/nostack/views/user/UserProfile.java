package com.example.nostack.views.user;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.fragment.NavHostFragment;

import com.example.nostack.R;
import com.example.nostack.handlers.ImageViewHandler;
import com.example.nostack.models.Image;
import com.example.nostack.models.ImageDimension;
import com.example.nostack.services.ImageUploader;
import com.example.nostack.viewmodels.UserViewModel;

import java.io.IOException;

import javax.annotation.Nullable;

/**
 * A simple {@link Fragment} subclass.
 * Creates the UserProfile fragment which is used to display the user's profile and allow for editing of the profile
 * and the profile picture
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
    private ImageViewHandler imageViewHandler;

    public UserProfile() {
        // Required empty public constructor
    }

    /**
     * Allows the user to select a profile picture
     */
    private void selectProfilePicture() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, IMAGE_PICK_CODE);
    }

    /**
     * This method allows for the class to get the Image URI and upload the profile image
     *
     * @param requestCode The integer request code originally supplied to startActivityForResult(),
     *                    allowing you to identify who this result came from
     * @param resultCode  The integer result code returned by the child activity
     *                    through its setResult().
     * @param data        An Intent, which can return result data to the caller
     *                    (various data can be attached to Intent "extras").
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == IMAGE_PICK_CODE && resultCode == AppCompatActivity.RESULT_OK && data != null) {
            Uri imageUri = data.getData();
            uploadProfileImage(imageUri);
        }
    }

    /**
     * Allows the user to upload a profile picture using the imageUri
     *
     * @param imageUri The Uri of the image to be uploaded
     */
    private void uploadProfileImage(Uri imageUri) {
        Uri compressedImageUri = null;

        try{
            compressedImageUri = imageUploader.compressImage(imageUri, 0.5, getContext());
        }
        catch (IOException e){
            Log.w("User edit", "Profile image compression failed:", e);
        }

        imageUploader.uploadImage("user/profile/", compressedImageUri, new ImageUploader.UploadListener() {
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

    /**
     * Updates the user's profile with the imageUrl
     *
     * @param imageUrl The URL of the image to be uploaded
     */
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
     * Creates the view for the UserProfile fragment
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

    /**
     * This method is called when the fragment is being created and then sets up the variables for the view
     *
     * @param savedInstanceState If the fragment is being re-created from
     *                           a previous saved state, this is the state.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }

        imageUploader = new ImageUploader();
        imageViewHandler = ImageViewHandler.getSingleton();
    }

    /**
     * This method is called when the fragment is being created and then sets up the view for the fragment
     * and sets up the buttons for the user to edit their profile
     *
     * @param inflater           The LayoutInflater object that can be used to inflate
     *                           any views in the fragment,
     * @param container          If non-null, this is the parent view that the fragment's
     *                           UI should be attached to.  The fragment should not add the view itself,
     *                           but this can be used to generate the LayoutParams of the view.
     * @param savedInstanceState If non-null, this fragment is being re-constructed
     *                           from a previous saved state as given here.
     * @return
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_user_profile, container, false);
        userViewModel = new ViewModelProvider((AppCompatActivity) getActivity()).get(UserViewModel.class);

        view.findViewById(R.id.backButton).setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                NavHostFragment.findNavController(UserProfile.this).popBackStack();
            }
        });

        view.findViewById(R.id.editProfileButton).setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                View editProfileButton = view.findViewById(R.id.editProfileButton);
                View saveProfileButton = view.findViewById(R.id.saveChangesButton);
                View editProfilePictureButtons = view.findViewById(R.id.editProfilePictureButtons);

                EditText userFirstName = view.findViewById(R.id.userFirstName);
                EditText userLastName = view.findViewById(R.id.userLastName);
                EditText userEmail = view.findViewById(R.id.userEmail);
                EditText userPhoneNumber = view.findViewById(R.id.userPhoneNumber);

                editProfileButton.setVisibility(View.GONE);
                saveProfileButton.setVisibility(View.VISIBLE);
                editProfilePictureButtons.setVisibility(View.VISIBLE);
                userFirstName.setEnabled(true);
                userLastName.setEnabled(true);
                userEmail.setEnabled(true);
                userPhoneNumber.setEnabled(true);
            }
        });

        view.findViewById(R.id.saveChangesButton).setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                View editProfileButton = view.findViewById(R.id.editProfileButton);
                View saveProfileButton = view.findViewById(R.id.saveChangesButton);
                View editProfilePictureButtons = view.findViewById(R.id.editProfilePictureButtons);
                EditText userFirstName = view.findViewById(R.id.userFirstName);
                EditText userLastName = view.findViewById(R.id.userLastName);
                EditText userEmail = view.findViewById(R.id.userEmail);
                EditText userPhoneNumber = view.findViewById(R.id.userPhoneNumber);

                if (userFirstName.getText().toString().isEmpty()) {
                    String errorMsg = "First name cannot be blank";
                    userFirstName.setError(errorMsg);
                    Toast.makeText(getContext(), errorMsg, Toast.LENGTH_SHORT).show();
                } else {
                    editProfileButton.setVisibility(View.VISIBLE);
                    saveProfileButton.setVisibility(View.GONE);
                    editProfilePictureButtons.setVisibility(View.GONE);
                    userFirstName.setEnabled(false);
                    userLastName.setEnabled(false);
                    userEmail.setEnabled(false);
                    userPhoneNumber.setEnabled(false);

                    userViewModel.getUser().observe(getViewLifecycleOwner(), user -> {
                        user.setFirstName(userFirstName.getText().toString());
                        user.setLastName(userLastName.getText().toString());
                        user.setEmailAddress(userEmail.getText().toString());
                        user.setPhoneNumber(userPhoneNumber.getText().toString());
                        userViewModel.updateUser(user);
                    });
                }
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
                ((TextView) view.findViewById(R.id.userFirstName)).setText(user.getFirstName());
                ((TextView) view.findViewById(R.id.userLastName)).setText(user.getLastName());
                ((TextView) view.findViewById(R.id.userEmail)).setText(user.getEmailAddress());
                ((TextView) view.findViewById(R.id.userPhoneNumber)).setText(user.getPhoneNumber());
                ((TextView) view.findViewById(R.id.profilename)).setText(user.getUsername());

                // Set profile image from URL
                updateProfilePicture();

            } else {
                Log.d("AttendeeHome", "User is null");
            }
        });

        // Inflate the layout for this fragment
        return view;
    }

    /**
     * Updates the profile picture in the view with the user's profile image URL
     */
    public void updateProfilePicture() {
        // Set profile image from URL
        ImageButton profileImage = getView().findViewById(R.id.profileImage);
        userViewModel.getUser().observe(getViewLifecycleOwner(), user -> {
            if (user != null) {
                imageViewHandler.setUserProfileImage(user, profileImage, getResources(), new ImageDimension(300, 300));
            }
        });
    }
}