package com.example.nostack.views.organizer;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.drawable.RoundedBitmapDrawable;
import androidx.core.graphics.drawable.RoundedBitmapDrawableFactory;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.RecyclerView;

import com.example.nostack.R;
import com.example.nostack.models.Event;
import com.example.nostack.services.GenerateProfileImage;
import com.example.nostack.viewmodels.user.UserViewModel;
import com.example.nostack.views.event.adapters.EventArrayAdapter;
import com.example.nostack.views.event.adapters.EventArrayAdapterRecycleView;
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
public class OrganizerHome extends Fragment {

    private ArrayList<Event> dataList;
    private RecyclerView eventList;
    private FirebaseFirestore db;
    private CollectionReference eventsRef;
    private Activity activity;


    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private TextView userWelcome;
    private UserViewModel userViewModel;
    private EventArrayAdapterRecycleView eventArrayAdapter;


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
     * @return
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_organizer_home, container, false);
        TextView userWelcome = (TextView) view.findViewById(R.id.text_userWelcome);

        eventList = view.findViewById(R.id.organizerEventList);
        eventArrayAdapter = new EventArrayAdapterRecycleView(getContext(),dataList,this);
        eventList.setAdapter(eventArrayAdapter);

        userViewModel.getUser().observe(getViewLifecycleOwner(), user -> {
            if (user != null) {
                Log.d("OrganizerHome", "User logged in: " + user.getUsername());
                userWelcome.setText(user.getUsername());

                eventsRef.whereEqualTo("organizerId", user.getUuid()).get().addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            Event event = document.toObject(Event.class);
                            if (!eventArrayAdapter.containsEvent(event)) {
                                eventArrayAdapter.addEvent(event);
                                Log.d("EventAdd", document.toObject(Event.class).getName());
                            }
                        }
                    }
                });
            } else {
                Log.d("OrganizerHome", "User is null");
            }
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

//        eventList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                Event event = eventArrayAdapter.getItem(position);
//                Bundle bundle = new Bundle();
//                bundle.putSerializable("eventData", event);
//
//                NavHostFragment.findNavController(OrganizerHome.this)
//                        .navigate(R.id.action_organizerHome_to_organizer_event, bundle);
//            }
//        });

        eventArrayAdapter.setOnItemClickListener(new EventArrayAdapterRecycleView.OnItemClickListener() {
            @Override
            public void onItemClick(Event event) {
                Bundle bundle = new Bundle();
                bundle.putSerializable("eventData", event);
                NavHostFragment.findNavController(OrganizerHome.this)
                        .navigate(R.id.action_organizerHome_to_organizer_event, bundle);
            }
        });

        view.findViewById(R.id.attendee_profileButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NavHostFragment.findNavController(OrganizerHome.this)
                        .navigate(R.id.action_organizerHome_to_userProfile);
            }
        });

        ImageButton profileImage = getView().findViewById(R.id.attendee_profileButton);
        userViewModel.getUser().observe(getViewLifecycleOwner(), user -> {
            if (user.getProfileImageUrl() != null) {
                String uri = user.getProfileImageUrl();

                // Get image from firebase storage
                StorageReference storageRef = FirebaseStorage.getInstance().getReferenceFromUrl(uri);
                final long ONE_MEGABYTE = 1024 * 1024;

                storageRef.getBytes(ONE_MEGABYTE).addOnSuccessListener(bytes -> {
                    Bitmap bmp = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                    Bitmap scaledBmp = Bitmap.createScaledBitmap(bmp, 72, 72, false);
                    RoundedBitmapDrawable d = RoundedBitmapDrawableFactory.create(getResources(), scaledBmp);
                    d.setCornerRadius(100f);
                    profileImage.setImageDrawable(d);
                }).addOnFailureListener(exception -> {
                    Log.w("User Profile", "Error getting profile image", exception);
                });
            } else {
                // generate profile image if user has no profile image
                Bitmap pfp = GenerateProfileImage.generateProfileImage(user.getFirstName(), user.getLastName());
                Bitmap scaledBmp = Bitmap.createScaledBitmap(pfp, 72, 72, false);
                RoundedBitmapDrawable d = RoundedBitmapDrawableFactory.create(getResources(), scaledBmp);
                d.setCornerRadius(100f);
                profileImage.setImageDrawable(d);
            }
        });
    }
}