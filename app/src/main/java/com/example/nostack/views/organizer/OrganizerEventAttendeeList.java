package com.example.nostack.views.organizer;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.graphics.drawable.RoundedBitmapDrawable;
import androidx.core.graphics.drawable.RoundedBitmapDrawableFactory;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.fragment.NavHostFragment;

import com.example.nostack.R;
import com.example.nostack.handlers.ImageViewHandler;
import com.example.nostack.models.Event;
import com.example.nostack.models.Image;
import com.example.nostack.viewmodels.AttendanceViewModel;
import com.example.nostack.viewmodels.EventViewModel;
import com.example.nostack.views.event.adapters.EventAttendeesArrayAdapter;
import com.example.nostack.models.Attendance;
import com.example.nostack.models.AttendeeLocations;
import com.example.nostack.models.GeoLocation;
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
    private AttendanceViewModel attendanceViewModel;
    private EventViewModel eventViewModel;
    private EventAttendeesArrayAdapter attendeesArrayAdapter;
    private ArrayList<Attendance> dataList;
    private ArrayList<Attendance> presentAttendees;
    private ImageViewHandler imageViewHandler;
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
     *
     * @param savedInstanceState If the fragment is being re-created from
     *                           a previous saved state, this is the state.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        dataList = new ArrayList<>();
        presentAttendees = new ArrayList<>();
        attendanceViewModel = new ViewModelProvider(requireActivity()).get(AttendanceViewModel.class);
        eventViewModel = new ViewModelProvider(requireActivity()).get(EventViewModel.class);
        imageViewHandler = ImageViewHandler.getSingleton();
    }

    /**
     * This method is called when the fragment is being created and then sets up the view for the fragment
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
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_organizer_event_attendee_list, container, false);

        attendeeList = view.findViewById(R.id.event_signup_list_listview);
        attendeesArrayAdapter = new EventAttendeesArrayAdapter(getContext(), dataList, this);
        attendeeList.setAdapter(attendeesArrayAdapter);

        // Watch for errors
        attendanceViewModel.getErrorLiveData().observe(getViewLifecycleOwner(), errorMessage -> {
            if (errorMessage != null && !errorMessage.isEmpty()) {
                Toast.makeText(getContext(), errorMessage, Toast.LENGTH_SHORT).show();
                attendanceViewModel.clearErrorLiveData();
            }
        });

        // Fetch and get attendance, present attendance, and event details
        eventViewModel.getEvent().observe(getViewLifecycleOwner(), event -> {
            this.event = event;
            attendanceViewModel.fetchAttendanceByEvent(event.getId());
            attendanceViewModel.fetchPresentAttByEvent(event.getId());
        });

        attendanceViewModel.getAttendanceByEvent().observe(getViewLifecycleOwner(), attendances -> {
            attendeesArrayAdapter.clear();
            for (Attendance attendance : attendances) {
                attendeesArrayAdapter.addAttendance(attendance);
            }
            attendeesArrayAdapter.notifyDataSetChanged();
            updateScreenInformation(view);
        });

        attendanceViewModel.getPresentAttByEvent().observe(getViewLifecycleOwner(), attendances -> {
            presentAttendees = new ArrayList<>();
            for (Attendance attendance : attendances) {
                presentAttendees.add(attendance);
            }
            ProgressBar pb = view.findViewById(R.id.progress_attendee_bar);
            TextView mileStoneText = view.findViewById(R.id.progress_attendee_milestone);
            if (event.getCapacity() != -1) {
                int progress = (int) ((presentAttendees.size() * 100) / event.getCapacity());
                pb.setProgress(progress);
                mileStoneText.setText(presentAttendees.size() + "/" + event.getCapacity() + " Present Attendees!");
            } else {
                pb.setProgress(presentAttendees.size());
                mileStoneText.setText(presentAttendees.size() + "/" + 100 + " Present Attendees!");
            }
        });
        return view;
    }

    private void updateScreenInformation(View view) {
        ImageView eventBanner = view.findViewById(R.id.event_attendee_list_event_banner);
        imageViewHandler.setEventImage(event, eventBanner);

        view.findViewById(R.id.backButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                eventViewModel.fetchEvent(event.getId());
                NavHostFragment.findNavController(OrganizerEventAttendeeList.this).popBackStack();
            }
        });

        view.findViewById(R.id.event_attendee_list_show_map).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AttendeeLocations locations = new AttendeeLocations();

                for (int i = 0; i < attendeesArrayAdapter.getCount(); i++) {
                    Attendance att = attendeesArrayAdapter.getItem(i);
                    GeoLocation l = att.getGeoLocation();
                    if (l != null) {
                        locations.addLocations(att.getGeoLocation());
                    }
                }

                Bundle bundle = new Bundle();
                bundle.putSerializable("locations", locations);

                NavHostFragment.findNavController(OrganizerEventAttendeeList.this)
                        .navigate(R.id.action_organizerEventAttendeeList_to_organizerAttendeeMap, bundle);
            }
        });
    }
}