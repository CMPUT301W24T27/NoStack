package com.example.nostack.ui.organizer;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;

import androidx.core.graphics.drawable.RoundedBitmapDrawable;
import androidx.core.graphics.drawable.RoundedBitmapDrawableFactory;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;

import com.example.nostack.R;
import com.example.nostack.model.Events.Event;
import com.example.nostack.model.Events.EventAttendeesArrayAdapter;
import com.example.nostack.utils.Attendance;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 * Creates the fragment for the organizer to be able to see the event attendee list
 */
public class OrganizerEventAttendeeList extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private Event event;
    private FirebaseFirestore db;
    private CollectionReference attendanceRef;
    private EventAttendeesArrayAdapter attendeesArrayAdapter;
    private ArrayList<Attendance> dataList;
    private ListView attendeeList;

    public OrganizerEventAttendeeList() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @return A new instance of fragment OrganizerEventAttendeeList.
     */
    // TODO: Rename and change types and number of parameters
    public static OrganizerEventAttendeeList newInstance(Event param1) {
        OrganizerEventAttendeeList fragment = new OrganizerEventAttendeeList();
        Bundle args = new Bundle();
        args.putSerializable(ARG_PARAM1, param1);
        fragment.setArguments(args);
        return fragment;
    }

    /**
     * This method is called when the fragment is being created and then sets up the variables for the view
     * @param savedInstanceState If the fragment is being re-created from
     * a previous saved state, this is the state.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            event = (Event) getArguments().getSerializable("eventData");
        }

        db = FirebaseFirestore.getInstance();
        attendanceRef = db.collection("attendance");
        dataList = new ArrayList<>();
    }

    /**
     * This method is called when the fragment is being created and then sets up the view for the fragment
     * @param inflater The LayoutInflater object that can be used to inflate
     * any views in the fragment,
     * @param container If non-null, this is the parent view that the fragment's
     * UI should be attached to.  The fragment should not add the view itself,
     * but this can be used to generate the LayoutParams of the view.
     * @param savedInstanceState If non-null, this fragment is being re-constructed
     * from a previous saved state as given here.
     *
     * @return
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_organizer_event_attendee_list, container, false);

        attendeeList = view.findViewById(R.id.event_attendee_list_listview);
        attendeesArrayAdapter = new EventAttendeesArrayAdapter(getContext(), dataList, this);
        attendeeList.setAdapter(attendeesArrayAdapter);

        attendanceRef.whereEqualTo("eventId", event.getId()).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                for (QueryDocumentSnapshot doc : task.getResult()) {
                    Attendance attendance = doc.toObject(Attendance.class);
                    if (!attendeesArrayAdapter.containsAttendance(attendance)) {
                        attendeesArrayAdapter.addAttendance(attendance);
                    }
                }
            }
        });

        updateScreenInformation(view);
        return view;
    }

    private void updateScreenInformation(View view) {
        ImageView eventBanner = view.findViewById(R.id.event_attendee_list_event_banner);
        if (event.getEventBannerImgUrl() != null) {
            // Get image from firebase storage
            StorageReference storageRef = FirebaseStorage.getInstance().getReferenceFromUrl(event.getEventBannerImgUrl());
            final long ONE_MEGABYTE = 1024 * 1024;

            storageRef.getBytes(ONE_MEGABYTE).addOnSuccessListener(bytes -> {
                Bitmap bmp = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                RoundedBitmapDrawable d = RoundedBitmapDrawableFactory.create(this.getResources(), bmp);
                d.setCornerRadius(0f);
                eventBanner.setImageDrawable(d);
            }).addOnFailureListener(exception -> {
                Log.w("User Profile", "Error getting profile image", exception);
            });
        }

        view.findViewById(R.id.backButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                bundle.putSerializable("eventData", event);

                NavHostFragment.findNavController(OrganizerEventAttendeeList.this)
                        .navigate(R.id.action_organizerEventAttendeeList_to_organizer_event, bundle);
            }
        });
    }
}