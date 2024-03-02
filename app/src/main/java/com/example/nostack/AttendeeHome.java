package com.example.nostack;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.nostack.Profile.Profile;
import com.example.nostack.Profile.User;
import com.example.nostack.Profile.UserViewModel;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import javax.annotation.Nullable;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link AttendeeHome#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AttendeeHome extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private TextView userWelcome;

    public AttendeeHome() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment AttendeeHome.
     */
    // TODO: Rename and change types and number of parameters
    public static AttendeeHome newInstance(String param1, String param2) {
        AttendeeHome fragment = new AttendeeHome();
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
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment only once
        View rootView = inflater.inflate(R.layout.fragment_attendee_home, container, false);
        TextView userWelcome = (TextView) rootView.findViewById(R.id.text_userWelcome);

        UserViewModel userViewModel = new ViewModelProvider((AppCompatActivity) getActivity() ).get(UserViewModel.class);
        Log.d("AttendeeHome", "UserViewModel: " + userViewModel.getUser().getValue());
        userViewModel.getUser().observe(getViewLifecycleOwner(), user -> {

            if (user != null) {
                Log.d("AttendeeHome", "Welcome, " + user.getFirst_name() + "!");
                userWelcome.setText("Welcome, " + user.getFirst_name() + "!");
            }
            else{
                Log.d("AttendeeHome", "User is null");
            }
        });

        // Change text_userWelcome to the user's name
        // User user = createUserFromDatabase(uuid);
        // Log.d("AttendeeHome", "Welcome, " + user.getFirst_name() + "!");
        // userWelcome.setText("Welcome, " + user.getUuid() + "!");

        // Return the modified layout
        return rootView;
    }

    public User createUserFromDatabase(String uuid) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference userRef = db.collection("users");
        User user = new User();
        userRef.whereEqualTo("uuid", uuid).addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                if (error != null) {
                    Log.w("Profile", "Listen failed.", error);
                    Snackbar.make(getActivity().findViewById(android.R.id.content), "UUID does not exist. Creating new profile.", Snackbar.LENGTH_LONG).show();
                    return;
                }

                for (QueryDocumentSnapshot doc : value) {
                    if (doc.exists()) {
                        user.setEmail_address(doc.getString("email"));
                        user.setFirst_name(doc.getString("first_name"));
                        user.setUuid(doc.getString("uuid"));
                        Snackbar.make(getActivity().findViewById(android.R.id.content), "Hello, " + user.getFirst_name(), Snackbar.LENGTH_LONG).show();
                    }
                }

            }
        });

        return user;
    }
}