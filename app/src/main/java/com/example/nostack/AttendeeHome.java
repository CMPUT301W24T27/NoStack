package com.example.nostack;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.nostack.Profile.Profile;

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

        // Change text_userWelcome to the user's name
        Profile profile = new Profile(getActivity());
        TextView userWelcome = (TextView) rootView.findViewById(R.id.text_userWelcome);
        Log.d("AttendeeHome", "Welcome, " + profile.getUuid() + "!");
        userWelcome.setText("Welcome, " + profile.getUuid() + "!");

        // Return the modified layout
        return rootView;
    }
}