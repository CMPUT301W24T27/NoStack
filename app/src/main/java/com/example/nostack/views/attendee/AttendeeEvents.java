package com.example.nostack.views.attendee;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.nostack.R;
import com.example.nostack.models.Event;
import com.example.nostack.viewmodels.user.UserViewModel;
import com.example.nostack.views.event.adapters.EventArrayAdapterRecycleView;
import com.example.nostack.views.event.adapters.EventArrayRecycleViewInterface;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.Filter;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 * Creates the AttendeeEvents fragment which is used to display the events that the attendee is attending
 */
public class AttendeeEvents extends Fragment implements EventArrayRecycleViewInterface {
    private EventArrayAdapterRecycleView eventArrayAdapter;
    private RecyclerView eventList;
    private ArrayList<Event> dataList;
    private UserViewModel userViewModel;
    private FirebaseFirestore db;
    private CollectionReference eventsRef;
    private Activity activity;


    public AttendeeEvents() {
    }

    /**
     * This method is called when the fragment is being created and then sets up the variables for the view
     *
     * @param savedInstanceState If the fragment is being re-created from
     *                           a previous saved state, this is the state.
     */
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        userViewModel = new ViewModelProvider((AppCompatActivity) getActivity()).get(UserViewModel.class);
        db = FirebaseFirestore.getInstance();
        eventsRef = db.collection("events");
        activity = getActivity();
        dataList = new ArrayList<>();
    }

    /**
     * This method is called when the fragment is being created and displays the view for the fragment
     *
     * @param inflater           The LayoutInflater object that can be used to inflate any views in the fragment
     * @param container          If non-null, this is the parent view that the fragment's UI should be attached to.
     *                           The fragment should not add the view itself, but this can be used to generate the LayoutParams of the view.
     * @param savedInstanceState If non-null, this fragment is being re-constructed from a previous saved state as given here.
     * @return The View for the fragment's UI, or null.
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment only once
        View rootView = inflater.inflate(R.layout.fragment_attendee_home_upcoming, container, false);

        eventList = rootView.findViewById(R.id.listView_upcomingEvents);
        eventArrayAdapter = new EventArrayAdapterRecycleView(getContext(), dataList, this, this);
        eventList.setAdapter(eventArrayAdapter);
        eventList.setLayoutManager(new LinearLayoutManager(getContext()));

        eventArrayAdapter.notifyDataSetChanged();

        Log.d("AttendeeHome", "UserViewModel: " + userViewModel.getUser().getValue());
        userViewModel.getUser().observe(getViewLifecycleOwner(), user -> {
            if (user != null) {
                eventsRef.orderBy("startDate", Query.Direction.ASCENDING)
                        .where(Filter.arrayContains("attendees", user.getUuid()))
                        .get().addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                // Reset the list of events
                                eventArrayAdapter.clear();

                                // Add the events to the list
                                for (QueryDocumentSnapshot document : task.getResult()) {
                                    Event event = document.toObject(Event.class);
                                    eventArrayAdapter.addEvent(event);
                                    eventArrayAdapter.notifyDataSetChanged();
                                    Log.d("EventAdd", document.toObject(Event.class).getName());
                                }
                                eventArrayAdapter.notifyItemInserted(0);
                                Log.d("EventAdd", "Event Added");
                            } else {
                                Log.d("EventAdd", "Error getting documents: ", task.getException());
                            }
                        });

            } else {
                Log.d("AttendeeHome", "User is null");
            }
        });

        // Clickable event list
//        eventList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                Event event = eventArrayAdapter.getItem(position);
//                Bundle bundle = new Bundle();
//                bundle.putSerializable("event", event);
//
//                NavHostFragment.findNavController(AttendeeEvents.this)
//                        .navigate(R.id.action_attendeeHome_to_attendeeEvent, bundle);
//            }
//        });

        // Return the modified layout
        return rootView;
    }

    @Override
    public void OnItemClick(int position) {
        Event event = eventArrayAdapter.getEvent(position);
        Bundle bundle = new Bundle();
        bundle.putSerializable("event", event);

        NavHostFragment.findNavController(AttendeeEvents.this)
                .navigate(R.id.action_attendeeHome_to_attendeeEvent, bundle);
    }
}
