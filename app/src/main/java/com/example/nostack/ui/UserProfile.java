package com.example.nostack.ui;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.navigation.NavHost;
import androidx.navigation.fragment.NavHostFragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.nostack.R;

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

    public UserProfile() {
        // Required empty public constructor
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
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_user_profile, container, false);

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

        // Inflate the layout for this fragment
        return view;
    }
}