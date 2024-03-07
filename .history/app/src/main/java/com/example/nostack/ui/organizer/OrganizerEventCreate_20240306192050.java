package com.example.nostack.ui.organizer;

import android.app.Activity;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;

import com.example.nostack.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.firestore.FirebaseFirestore;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link OrganizerEventCreate#newInstance} factory method to
 * create an instance of this fragment.
 */
public class OrganizerEventCreate extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private Activity activity;
    private FirebaseFirestore db;
    private FloatingActionButton backButton;
    private TextInputEditText eventTitleEditText;
    private TextInputEditText eventStartEditText;
    private TextInputEditText eventEndEditText;
    private TextInputEditText eventLocationEditText;
    private TextInputEditText eventDescEditText;
    private CheckBox eventReuseQrCheckBox;
    private ImageView eventImageView;

    public OrganizerEventCreate() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment OrganizerEvent.
     */
    // TODO: Rename and change types and number of parameters
    public static OrganizerEventCreate newInstance(String param1, String param2) {
        OrganizerEventCreate fragment = new OrganizerEventCreate();
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

        db = FirebaseFirestore.getInstance();

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
<<<<<<< HEAD:app/src/main/java/com/example/nostack/ui/organizer/OrganizerEvent.java
        return inflater.inflate(R.layout.fragment_organizer_event, container, false);
=======

//        TODO: HAVE EDIT DATE/TIME BUTTONS DO THE FOLLOWING: https://www.youtube.com/watch?v=guTycx3L9I4&ab_channel=TechnicalCoding


        return inflater.inflate(R.layout.fragment_organizer_event_create, container, false);
>>>>>>> b60e46f (setup for adding events):app/src/main/java/com/example/nostack/ui/organizer/OrganizerEventCreate.java
    }
}
