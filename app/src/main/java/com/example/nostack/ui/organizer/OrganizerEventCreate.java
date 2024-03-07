package com.example.nostack.ui.organizer;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;

import com.example.nostack.R;
import com.example.nostack.utils.Event;
import com.example.nostack.utils.ImageUploader;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Date;
import java.util.HashMap;

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
    private ImageUploader imageUploader;
    private FirebaseFirestore db;
    private CollectionReference eventsRef;
    private FloatingActionButton backButton;
    private TextInputEditText eventTitleEditText;
    private TextInputEditText eventStartEditText;
    private TextInputLayout eventStartLayout;
    private TextInputEditText eventEndEditText;
    private TextInputLayout eventEndLayout;
    private TextInputEditText eventLocationEditText;
    private TextInputEditText eventDescEditText;
    private CheckBox eventReuseQrCheckBox;
    private ImageView eventImageView;
    private Button eventCreateButton;
    private View view;
    private CollectionReference userRef;
    private SharedPreferences preferences;
    private String userUUID;

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
        eventsRef = db.collection("events");
        activity = getActivity();
        imageUploader = new ImageUploader();
        preferences = activity.getApplicationContext().getSharedPreferences("com.example.nostack", Context.MODE_PRIVATE);
        userUUID = preferences.getString("uuid", null);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_organizer_event_create,container,false);

        eventTitleEditText = view.findViewById(R.id.EventCreationTitleEditText);
        eventStartEditText = view.findViewById(R.id.EventCreationStartDateTimeEditText);
        eventStartLayout = view.findViewById(R.id.EventCreationStartDateTimeLayout);
        eventEndEditText = view.findViewById(R.id.EventCreationEndDateTimeEditText);
        eventEndLayout = view.findViewById(R.id.EventCreationEndDateTimeLayout);
        eventLocationEditText = view.findViewById(R.id.EventCreationLocationEditText);
        eventDescEditText = view.findViewById(R.id.EventCreationDescriptionEditText);
        eventReuseQrCheckBox = view.findViewById(R.id.EventCreationReuseQRCheckBox);
        eventImageView = view.findViewById(R.id.EventCreationEventImageView);
        view.findViewById(R.id.EventCreationCreateEventButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CreateOrganization();
                NavHostFragment.findNavController(OrganizerEventCreate.this)
                        .navigate(R.id.action_organizerEventCreate_to_organizerHome);
            }
        });


        // TODO: ADD THE FUNCTIONALITY TO REUSE QR CODES


//        TODO: HAVE EDIT DATE/TIME BUTTONS DO THE FOLLOWING: https://www.youtube.com/watch?v=guTycx3L9I4&ab_channel=TechnicalCoding


        return view;
    }

    private void CreateOrganization() {
        Event newEvent = new Event(
                eventTitleEditText.getText().toString(),
                eventLocationEditText.getText().toString(),
                eventDescEditText.getText().toString(),
                new Date(2012,1,3),
                new Date(2012,2,3),
                userUUID
        );

        eventsRef
                .document(newEvent.getId())
                .set(newEvent)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Log.w("Firestore", "New event successfully added!");
                        Snackbar.make(activity.findViewById(android.R.id.content), "New event created.", Snackbar.LENGTH_LONG).show();
                    }
                })
                .addOnFailureListener(e -> Log.w("Firestore", "Error creating event", e));;
    }
}