package com.example.nostack.views.attendee;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.nostack.R;
import com.example.nostack.controllers.EventController;
import com.example.nostack.models.Event;
import com.example.nostack.services.NavbarConfig;
import com.example.nostack.services.SkeletonProvider;
import com.example.nostack.viewmodels.EventViewModel;
import com.example.nostack.viewmodels.UserViewModel;
import com.example.nostack.views.event.adapters.EventArrayAdapterRecycleView;
import com.example.nostack.views.event.adapters.EventArrayRecycleViewInterface;
import com.faltenreich.skeletonlayout.Skeleton;
import com.faltenreich.skeletonlayout.SkeletonLayoutUtils;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;

/**
 * Creates the AttendeeBrowse fragment which is used to display the events that the user can attend
 */
public class AttendeeBrowse extends Fragment implements EventArrayRecycleViewInterface {
    private EventArrayAdapterRecycleView eventArrayAdapter;
    private RecyclerView eventList;
    private ArrayList<Event> dataList;
    private EventViewModel eventViewModel;
    private NavbarConfig navbarConfig;
    private Skeleton skeleton;
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
        eventViewModel = new ViewModelProvider(requireActivity()).get(EventViewModel.class);
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
        View rootView = inflater.inflate(R.layout.fragment_attendee_home_browse, container, false);
        eventList = rootView.findViewById(R.id.listView_yourEvents);
        eventArrayAdapter = new EventArrayAdapterRecycleView(getContext(), dataList, this, this);
        eventList.setAdapter(eventArrayAdapter);
        eventList.setItemViewCacheSize(100);
        navbarConfig = NavbarConfig.getSingleton();

        // Skeleton
        skeleton = SkeletonProvider.getSingleton().eventListSkeleton(eventList);
        skeleton.showSkeleton();

        return rootView;
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
        eventViewModel.fetchAllEvents();
        eventViewModel.getAllEvents().observe(getViewLifecycleOwner(), events -> {
            eventArrayAdapter.clear();
            for (Event event : events) {
                eventArrayAdapter.addEvent(event);
            }
            eventArrayAdapter.notifyDataSetChanged();
            skeleton.showOriginal();
        });
        eventList.setLayoutManager(new LinearLayoutManager(getContext()));
    }

    @Override
    public void OnItemClick(int position) {
        Event event = eventArrayAdapter.getEvent(position);
        eventViewModel.fetchEvent(event.getId());
        Bundle bundle = new Bundle();
        bundle.putSerializable("event", event);

        NavHostFragment.findNavController(AttendeeBrowse.this)
            .navigate(R.id.action_attendeeHome_to_attendeeEvent, bundle);

        navbarConfig.setInvisible();
    };
}
