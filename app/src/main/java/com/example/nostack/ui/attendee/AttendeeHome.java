package com.example.nostack.ui.attendee;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.drawable.RoundedBitmapDrawable;
import androidx.core.graphics.drawable.RoundedBitmapDrawableFactory;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.fragment.NavHostFragment;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.nostack.R;
import com.example.nostack.model.Events.Event;
import com.example.nostack.model.Events.EventArrayAdapter;
import com.example.nostack.model.State.UserViewModel;
import com.example.nostack.ui.ScanActivity;
import com.example.nostack.utils.EventCheckinHandler;
import com.example.nostack.utils.GenerateProfileImage;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.journeyapps.barcodescanner.ScanContract;
import com.journeyapps.barcodescanner.ScanOptions;
import com.tbuonomo.viewpagerdotsindicator.DotsIndicator;

import java.util.ArrayList;

import javax.annotation.Nullable;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link AttendeeHome#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AttendeeHome extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private TextView userWelcome;
    private UserViewModel userViewModel;
    private EventArrayAdapter eventArrayAdapter;
    private ArrayList<Event> dataList;
    private ListView eventList;
    private FirebaseFirestore db;
    private CollectionReference eventsRef;
    private Activity activity;

    private static final Class[] fragments = new Class[]{AttendeeBrowse.class, AttendeeEvents.class};
    private ViewPager2 viewPager;
    private DotsIndicator dotsIndicator;



    public AttendeeHome() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment AttendeeHome.
     */
    // TODO: Rename and change types and number of parameters
    public static AttendeeHome newInstance(String param1, String param2) {
        AttendeeHome fragment = new AttendeeHome();
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
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment only once
        View rootView = inflater.inflate(R.layout.fragment_attendee_home, container, false);
        TextView userWelcome = (TextView) rootView.findViewById(R.id.text_userWelcome);
        viewPager = rootView.findViewById(R.id.event_tab_navigation);
        viewPager.setAdapter(new MyFragmentAdapter(this));
        dotsIndicator = rootView.findViewById(R.id.dots_indicator);
        dotsIndicator.attachTo(viewPager);

//        eventList = rootView.findViewById(R.id.listView_yourEvents);
//        eventArrayAdapter = new EventArrayAdapter(getContext(),dataList,this);
//        eventList.setAdapter(eventArrayAdapter);

        
        Log.d("AttendeeHome", "UserViewModel: " + userViewModel.getUser().getValue());
        userViewModel.getUser().observe(getViewLifecycleOwner(), user -> {

            if (user != null) {
                Log.d("AttendeeHome", "User logged in: " + user.getFirstName());
                userWelcome.setText(user.getFirstName());

//                eventsRef.get().addOnCompleteListener(task -> {
//                    if (task.isSuccessful()) {
//                        for (QueryDocumentSnapshot document : task.getResult()) {
//                            Event event = document.toObject(Event.class);
//                            eventArrayAdapter.addEvent(event);
//                            Log.d("EventAdd", "" + document.toObject(Event.class).getName());
//                        }
//                    }
//                });
            }
            else{
                Log.d("AttendeeHome", "User is null");
            }
        });

        // Return the modified layout
        return rootView;
    }

    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        view.findViewById(R.id.attendee_profileButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NavHostFragment.findNavController(AttendeeHome.this)
                        .navigate(R.id.action_attendeeHome_to_userProfile);
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

        view.findViewById(R.id.scanQRButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                scanCode();
            }
        });
    }

    private static class MyFragmentAdapter extends FragmentStateAdapter {

        public MyFragmentAdapter(Fragment fragment) {
            super(fragment);
        }

        @Override
        public Fragment createFragment(int position) {
            try {
                return (Fragment) fragments[position].newInstance();
            } catch (Exception e) {
                return null;
            }
        }

        @Override
        public int getItemCount() {
            return fragments.length;
        }
    }

    private void scanCode() {
        ScanOptions scanOptions = new ScanOptions();
        scanOptions.setPrompt("Scan the QR code");
        scanOptions.setBeepEnabled(true);
        scanOptions.setOrientationLocked(true);
        scanOptions.setCaptureActivity(ScanActivity.class);
        barLauncher.launch(scanOptions);

    }

    ActivityResultLauncher<ScanOptions> barLauncher = registerForActivityResult(new ScanContract(), result -> {
        if (result.getContents() != null) {
            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
            builder.setTitle("Scan Result");
            builder.setMessage(result.getContents());

            // Check if the QR code is for an event description or check-in
            // "result" is a string of type 0.uuid or 1.uuid
            if (result.getContents().charAt(0) == '0') {
                handleCheckInQR(result.getContents().substring(2));
            } else if (result.getContents().charAt(0) == '1') {
                handleEventDescQR(result.getContents().substring(2));
            }
            builder.setPositiveButton("Done", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.dismiss();
                }
            }).show();
        }
    });

    public void handleEventDescQR(String eventUID) {
        DocumentReference docRef = eventsRef.document(eventUID);

        // read action
        docRef.get().addOnCompleteListener(task -> {

            // if read action is successful
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();

                if (document.exists()) {

                    Event event = document.toObject(Event.class);
                    Bundle eventInfo_bundle = new Bundle();

                    //TODO: pass event data not using a bundle
                    // Navigate to event description page, pass event data as bundle in eventInfo_bundle
                    eventInfo_bundle.putString("eventName", event.getName());
                    eventInfo_bundle.putString("eventLocation", event.getLocation());
                    eventInfo_bundle.putString("eventDescription", event.getDescription());
                    eventInfo_bundle.putString("eventBannerImgUrl", event.getEventBannerImgUrl());
                    eventInfo_bundle.putString("eventID", eventUID);
                    NavHostFragment.findNavController(AttendeeHome.this)
                            .navigate(R.id.action_attendeeHome_to_attendeeEvent);
                }
            }
        });
    }

    public void handleCheckInQR(String eventUID) {
        EventCheckinHandler ecHandler = new EventCheckinHandler();

        userViewModel.getUser().observe(getViewLifecycleOwner(), user -> {
            if (user != null) {
                try {
                    ecHandler.checkInUser(eventUID, user.getUuid());
                } catch (Exception e) {
                    Log.e("AttendeeHome", "Error checking in user: " + e);
                }
            }
        });

    }
}