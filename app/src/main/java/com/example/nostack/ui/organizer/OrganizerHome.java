package com.example.nostack.ui.organizer;

import android.app.Activity;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.drawable.RoundedBitmapDrawable;
import androidx.core.graphics.drawable.RoundedBitmapDrawableFactory;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.fragment.NavHostFragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.example.nostack.R;
import com.example.nostack.model.Events.Event;
import com.example.nostack.model.Events.EventArrayAdapter;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import android.widget.ImageButton;
import android.widget.TextView;

import com.example.nostack.model.State.UserViewModel;
import com.example.nostack.utils.GenerateProfileImage;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link OrganizerHome#newInstance} factory method to
 * create an instance of this fragment.
 */
public class OrganizerHome extends Fragment {

    private EventArrayAdapter eventArrayAdapter;
    private ArrayList<Event> dataList;
    private ListView eventList;
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

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }

        userViewModel = new ViewModelProvider((AppCompatActivity) getActivity() ).get(UserViewModel.class);
        db = FirebaseFirestore.getInstance();
        eventsRef = db.collection("events");
        activity = getActivity();
        dataList = new ArrayList<>();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_organizer_home,container,false);
        TextView userWelcome = (TextView) view.findViewById(R.id.text_userWelcome);

        eventList = view.findViewById(R.id.organizerEventList);
        eventArrayAdapter = new EventArrayAdapter(getContext(),dataList,this);
        eventList.setAdapter(eventArrayAdapter);

        userViewModel.getUser().observe(getViewLifecycleOwner(), user -> {
            if (user != null) {
                Log.d("OrganizerHome", "User logged in: " + user.getFirstName());
                userWelcome.setText(user.getFirstName());

                eventsRef.whereEqualTo("organizerId",user.getUuid()).get().addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            Event event = document.toObject(Event.class);
                            eventArrayAdapter.addEvent(event);
                            Log.d("EventAdd", "" + document.toObject(Event.class).getName());
                        }
                    }
                });
            }
            else{
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
            }
            else{
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