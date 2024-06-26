package com.example.nostack.views.admin;

import android.app.Dialog;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.graphics.drawable.RoundedBitmapDrawable;
import androidx.core.graphics.drawable.RoundedBitmapDrawableFactory;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.nostack.R;
import com.example.nostack.handlers.ImageViewHandler;
import com.example.nostack.models.Event;
import com.example.nostack.models.User;
import com.example.nostack.services.NavbarConfig;
import com.example.nostack.services.SkeletonProvider;
import com.example.nostack.viewmodels.EventViewModel;
import com.example.nostack.views.event.adapters.EventArrayAdapter;
import com.example.nostack.views.event.adapters.EventArrayAdapterRecycleView;
import com.example.nostack.views.event.adapters.EventArrayRecycleViewInterface;
import com.faltenreich.skeletonlayout.Skeleton;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * Creates the AttendeeBrowse fragment which is used to display the events that the user can attend
 */
public class AdminBrowseEvents extends Fragment implements EventArrayRecycleViewInterface {
//    private EventArrayAdapter eventArrayAdapter;
    private RecyclerView eventList;
    private EventArrayAdapterRecycleView eventArrayAdapter;

    private ArrayList<Event> dataList;
    private EventViewModel eventViewModel;
    private ImageViewHandler imageViewHandler;
    private Skeleton skeleton;

    public AdminBrowseEvents() {
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
     * @return Returns the modified view
     */
    @Override
//    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
//        View rootView = inflater.inflate(R.layout.fragment_admin_home_browseevents, container, false);
//        eventList = rootView.findViewById(R.id.admin_viewPager2);
//        eventArrayAdapter = new EventArrayAdapter(getContext(), dataList, this);
//        eventList.setAdapter(eventArrayAdapter);
//        return rootView;
//    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_admin_home_browseevents, container, false);
        eventList = rootView.findViewById(R.id.admin_viewPager2);
        eventArrayAdapter = new EventArrayAdapterRecycleView(getContext(), dataList, this, this);
        eventList.setAdapter(eventArrayAdapter);
        eventList.setItemViewCacheSize(100);
        eventList.setLayoutManager(new LinearLayoutManager(getContext()));

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

//        eventList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
//                showDialog(EventArrayAdapter.getEvent(position));
//            }
//        });
    }
    private void showDialog(Event event){
        Dialog dialog = new Dialog(getActivity());
        dialog.setContentView(R.layout.fragment_admin_events_dialog);
        dialog.show();

        ImageView eventBanner = dialog.findViewById(R.id.admin_eventDialog);
        TextView eventTitle = dialog.findViewById(R.id.admin_eventDialogTitle);
        TextView eventStartDate = dialog.findViewById(R.id.admin_eventDialogStartDate);
        TextView eventEndDate = dialog.findViewById(R.id.admin_eventDialogEndDate);
        TextView eventLocation = dialog.findViewById(R.id.admin_eventDialogLocation);
        TextView eventCapacity = dialog.findViewById(R.id.admin_eventDialogCapacity);
        TextView eventDescription = dialog.findViewById(R.id.admin_eventDialogDescription);


        if (event.getName() != null){
            eventTitle.setText(event.getName());
        } else {
            eventTitle.setText("Event Name: N/A");
        }
        Date startDate = event.getStartDate();
        Date endDate = event.getEndDate();
        SimpleDateFormat formatter = new SimpleDateFormat("MM/dd/yyyy hh:mm a");

        eventStartDate.setText("Start Date: " + formatter.format(startDate));
        eventEndDate.setText("End Date: " + formatter.format(endDate));
        eventLocation.setText("Location: " + event.getLocation());
        eventCapacity.setText("Capacity: " + (event.getCapacity() <= 0 ? "Unlimited" : event.getCapacity()));
        eventDescription.setText(event.getDescription() == null ? "No description" : event.getDescription());

        imageViewHandler.setEventImage(event, eventBanner);

        dialog.findViewById(R.id.admin_deleteEventButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteEvent(event);
                dialog.dismiss();
            }
        });
    }

    /**
     * Delete an event
     * @param event
     * @return void
     */
    public void deleteEvent(Event event) {
        eventViewModel.deleteEvent(event, new EventViewModel.DeleteEventCallback() {
            @Override
            public void onEventDeleted() {
                dataList.remove(event);
                eventArrayAdapter.notifyDataSetChanged();
                Toast.makeText(getContext(), "Event deleted", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onEventDeleteFailed() {
                Toast.makeText(getContext(), "Failed to delete event", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void OnItemClick(int position) {
        showDialog(eventArrayAdapter.getEvent(position));
    }
}
