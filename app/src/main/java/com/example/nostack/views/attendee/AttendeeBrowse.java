package com.example.nostack.views.attendee;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.fragment.NavHostFragment;

import com.example.nostack.R;
import com.example.nostack.models.Event;
import com.example.nostack.views.event.adapters.EventArrayAdapter;
import com.example.nostack.viewmodels.user.UserViewModel;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;

/**
 * Creates the AttendeeBrowse fragment which is used to display the events that the user can attend
 */
public class AttendeeBrowse extends Fragment {
    private EventArrayAdapter eventArrayAdapter;
    private ListView eventList;
    private ArrayList<Event> dataList;
    private UserViewModel userViewModel;
    private FirebaseFirestore db;
    private CollectionReference eventsRef;
    private Activity activity;

    public AttendeeBrowse() {
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
        // Inflate the layout for this fragment only once
        View rootView = inflater.inflate(R.layout.fragment_attendee_home_browse, container, false);

        eventList = rootView.findViewById(R.id.listView_yourEvents);
        eventArrayAdapter = new EventArrayAdapter(getContext(), dataList, this);
        eventList.setAdapter(eventArrayAdapter);

        Log.d("AttendeeHome", "UserViewModel: " + userViewModel.getUser().getValue());

        eventsRef.orderBy("startDate", Query.Direction.ASCENDING)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            Event event = document.toObject(Event.class);
                            eventArrayAdapter.addEvent(event);
                            Log.d("EventAdd", document.toObject(Event.class).getName());
                        }
                    }
                });

        eventList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Event event = eventArrayAdapter.getItem(position);
                Bundle bundle = new Bundle();
                bundle.putSerializable("event", event);

                NavHostFragment.findNavController(AttendeeBrowse.this)
                        .navigate(R.id.action_attendeeHome_to_attendeeEvent, bundle);
            }
        });

        // Return the modified layout
        return rootView;
    }

}
