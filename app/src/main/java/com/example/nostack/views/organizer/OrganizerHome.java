package com.example.nostack.views.organizer;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.drawable.RoundedBitmapDrawable;
import androidx.core.graphics.drawable.RoundedBitmapDrawableFactory;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.nostack.R;
import com.example.nostack.handlers.CurrentUserHandler;
import com.example.nostack.handlers.ImageViewHandler;
import com.example.nostack.models.Event;
import com.example.nostack.services.GenerateProfileImage;
import com.example.nostack.viewmodels.EventViewModel;
import com.example.nostack.viewmodels.UserViewModel;
import com.example.nostack.views.event.adapters.EventArrayAdapterRecycleView;
import com.example.nostack.views.event.adapters.EventArrayRecycleViewInterface;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 * Creates the OrganizerHome fragment which is used to display the events that the organizer has created
 */
public class OrganizerHome extends Fragment implements EventArrayRecycleViewInterface {

    private ArrayList<Event> dataList;
    private RecyclerView eventList;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private TextView userWelcome;
    private CurrentUserHandler currentUserHandler;
    private EventViewModel eventViewModel;
    private EventArrayAdapterRecycleView eventArrayAdapter;
    private ImageViewHandler imageViewHandler;


    public OrganizerHome() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment organizer_home.
     */
    // TODO: Rename and change types and number of parameters
    public static OrganizerHome newInstance(String param1, String param2) {
        OrganizerHome fragment = new OrganizerHome();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
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
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }

        eventViewModel = new ViewModelProvider(requireActivity()).get(EventViewModel.class);
        currentUserHandler = CurrentUserHandler.getSingleton();
        imageViewHandler = ImageViewHandler.getSingleton();
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
     * @return
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_organizer_home, container, false);

        eventList = view.findViewById(R.id.organizerEventList);
        eventArrayAdapter = new EventArrayAdapterRecycleView(getContext(),dataList,this, this);
        eventList.setAdapter(eventArrayAdapter);
        eventList.setLayoutManager(new LinearLayoutManager(getContext()));

        // Watch for errors
        eventViewModel.getErrorLiveData().observe(getViewLifecycleOwner(), errorMessage -> {
            if (errorMessage != null && !errorMessage.isEmpty()) {
                Toast.makeText(getContext(), errorMessage, Toast.LENGTH_SHORT).show();
                eventViewModel.clearErrorLiveData();
            }
        });

        // Fetch and get events
        eventViewModel.fetchOrganizerEvents(currentUserHandler.getCurrentUserId());
        eventViewModel.getOrganizerEvents().observe(getViewLifecycleOwner(), events -> {
            eventArrayAdapter.clear();
            for (Event event : events) {
                eventArrayAdapter.addEvent(event);
            }
            eventArrayAdapter.notifyDataSetChanged();

        });

        view.findViewById(R.id.AddEventButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NavHostFragment.findNavController(OrganizerHome.this)
                        .navigate(R.id.action_organizerHome_to_organizerEvent);
            }
        });

        return view;
    }

    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        view.findViewById(R.id.admin_profileButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NavHostFragment.findNavController(OrganizerHome.this)
                        .navigate(R.id.action_organizerHome_to_userProfile);
            }
        });

        ImageButton profileImage = getView().findViewById(R.id.admin_profileButton);
        imageViewHandler.setUserProfileImage(currentUserHandler.getCurrentUser(), profileImage);
    }

    @Override
    public void OnItemClick(int position) {
        Event event = eventArrayAdapter.getEvent(position);
        Bundle bundle = new Bundle();
        bundle.putSerializable("event", event);
        NavHostFragment.findNavController(OrganizerHome.this)
                .navigate(R.id.action_organizerHome_to_organizer_event, bundle);
    }
}