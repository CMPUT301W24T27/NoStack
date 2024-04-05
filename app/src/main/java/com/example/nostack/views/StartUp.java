package com.example.nostack.views;

import android.app.AlertDialog;
import android.content.Context;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import com.example.nostack.R;
import com.example.nostack.handlers.CurrentUserHandler;
import com.example.nostack.handlers.LocationHandler;
import com.example.nostack.models.Profile;
import com.example.nostack.models.User;
import com.example.nostack.services.GenerateName;
import com.google.android.material.snackbar.Snackbar;

import java.io.IOException;
import java.util.Locale;

/**
 * A simple {@link Fragment} subclass.
 * Creates the StartUp fragment which is used to display the start up page for the user to decide
 * to be an attendee or organizer or login as an admin
 */
public class StartUp extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private Profile profile;
    private AlertDialog dialog;
    private CurrentUserHandler currentUserHandler;

    public StartUp() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment StartUp.
     */
    // TODO: Rename and change types and number of parameters
    public static StartUp newInstance(String param1, String param2) {
        StartUp fragment = new StartUp();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    /**
     * This method is called when the fragment is being created and then sets up the variables for the view
     * and checks if the user profile already exists
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

        // check if user profile exists
        profile = new Profile(getActivity());
    }

    /**
     * This method is called when the fragment is being created and then sets up the view for the fragment
     * also sets up the buttons for the user to decide to be an attendee or organizer or login as an admin
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

        View view = inflater.inflate(R.layout.fragment_start_up, container, false);
        String uuid = profile.getUuid();

        if (!profile.exists()) {
            try {
                CreateProfile();
            } catch (IOException e) {
                Log.e("StartUp", "Error creating user profile", e);
                Snackbar.make(getActivity().findViewById(android.R.id.content), "Error creating user profile.", Snackbar.LENGTH_LONG).show();
            }
            Log.d("StartUp", profile.exists() + "");
        } else {
            Log.d("StartUp", uuid);
            profile.retrieveProfile(uuid)
                .thenApply(success -> {
                    if (!success) {
                       try {
                            CreateProfile();
                        } catch (IOException e) {
                            Snackbar.make(getActivity().findViewById(android.R.id.content), "Error creating user profile.", Snackbar.LENGTH_LONG).show();
                        }
                    }
                    return null;
                });
        }

        view.findViewById(R.id.AttendeeSignInButton).setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                NavHostFragment.findNavController(StartUp.this)
                        .navigate(R.id.action_startUp_to_attendeeHome);
            }
        });

        view.findViewById(R.id.SignIn_SignUpButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NavHostFragment.findNavController(StartUp.this)
                        .navigate(R.id.action_startUp_to_organizerHome);
            }
        });

        view.findViewById(R.id.AdministratorSignInButton).setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                NavHostFragment.findNavController(StartUp.this)
                        .navigate(R.id.action_startUp_to_adminHome);
            }
        });

        // Inflate the layout for this fragment
        return view;
    }

    /**
     * Generates a random name for the user and creates a profile for the user
     * @throws IOException
     * @return void
     */
    private void CreateProfile() throws IOException {
        String[] randomName = new String[3];

        GenerateName.generateNameAsync(new GenerateName.GenerateNameListener() {
            @Override
            public void onNameGenerated(String[] name, Exception e) {
                if (e != null) {
                    Log.e("StartUp", "Error generating name", e);
                    Snackbar.make(getActivity().findViewById(android.R.id.content), "Error generating name.", Snackbar.LENGTH_LONG).show();
                    return;
                }
                randomName[0] = name[0];
                randomName[1] = name[1];
                randomName[2] = name[2];

                User user = new User(
                        randomName[0],
                        randomName[1],
                        randomName[0].toLowerCase(Locale.ROOT) + "-" + randomName[1].toLowerCase(Locale.ROOT) + "-" + randomName[2],
                        null,
                        null,
                        null
                );
                Log.d("StartUp", user.getFirstName() + " " + user.getLastName());

                profile.createProfile(user);
            }
        });
    }
}

