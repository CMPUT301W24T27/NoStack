package com.example.nostack.views.attendee;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.nostack.R;
import com.example.nostack.handlers.CurrentUserHandler;
import com.example.nostack.models.Event;
import com.example.nostack.services.NavbarConfig;
import com.example.nostack.viewmodels.EventViewModel;
import com.example.nostack.viewmodels.UserViewModel;
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
    private EventViewModel eventViewModel;
    private CurrentUserHandler currentUserHandler;
    private NavbarConfig navbarConfig;

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
        eventViewModel = new ViewModelProvider(requireActivity()).get(EventViewModel.class);
        currentUserHandler = CurrentUserHandler.getSingleton();
        navbarConfig = NavbarConfig.getSingleton();
        dataList = new ArrayList<>();
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
     * @return Returns the modified view
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_attendee_home_upcoming, container, false);
        eventList = rootView.findViewById(R.id.listView_upcomingEvents);
        eventArrayAdapter = new EventArrayAdapterRecycleView(getContext(), dataList, this, this);
        eventList.setAdapter(eventArrayAdapter);
        eventList.setLayoutManager(new LinearLayoutManager(getContext()));
        return rootView;
    }

    @Override
    public void OnItemClick(int position) {
        Event event = eventArrayAdapter.getEvent(position);
        Bundle bundle = new Bundle();
        bundle.putSerializable("event", event);
        NavHostFragment.findNavController(AttendeeEvents.this).navigate(R.id.action_attendeeHome_to_attendeeEvent, bundle);
        navbarConfig.setInvisible();
    }

    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Watch for errors
        eventViewModel.getErrorLiveData().observe(getViewLifecycleOwner(), errorMessage -> {
            if (errorMessage != null && !errorMessage.isEmpty()) {
                Toast.makeText(getContext(), errorMessage, Toast.LENGTH_SHORT).show();
                eventViewModel.clearErrorLiveData();
            }
        });

        // Fetch and get events
        eventViewModel.fetchAttendeeEvents(currentUserHandler.getCurrentUserId());
        eventViewModel.getAttendeeEvents().observe(getViewLifecycleOwner(), events -> {
            eventArrayAdapter.clear();
            for (Event event : events) {
                eventArrayAdapter.addEvent(event);
            }
            eventArrayAdapter.notifyDataSetChanged();

        });
    }
}

