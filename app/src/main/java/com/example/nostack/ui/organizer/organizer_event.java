package com.example.nostack.ui.organizer;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.drawable.RoundedBitmapDrawable;
import androidx.core.graphics.drawable.RoundedBitmapDrawableFactory;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.fragment.NavHostFragment;

import com.example.nostack.R;
import com.example.nostack.model.Events.Event;
import com.example.nostack.model.State.UserViewModel;
import com.example.nostack.utils.GenerateProfileImage;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Locale;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link organizer_event#newInstance} factory method to
 * create an instance of this fragment.
 */
public class organizer_event extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private Event event;
    private String mParam2;
    private ArrayList<Event> dataList;
    private FirebaseFirestore db;
    private CollectionReference eventsRef;
    private Activity activity;
    private UserViewModel userViewModel;
    private TextView msgTV;

    public organizer_event() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @return A new instance of fragment organizer_event.
     */
    // TODO: Rename and change types and number of parameters
    public static organizer_event newInstance(Event param1) {
        organizer_event fragment = new organizer_event();
        Bundle args = new Bundle();
        args.putSerializable(ARG_PARAM1, param1);
        fragment.setArguments(args);
        return fragment;
    }

    public void updateScreenInformation(@NonNull View view) {
        TextView eventTitle = view.findViewById(R.id.OrganizerEventTitleText);
        TextView eventDescription = view.findViewById(R.id.OrganizerEventDescriptionText);
        TextView eventLocation = view.findViewById(R.id.OrganizerEventLocationText);
        TextView eventStartDate = view.findViewById(R.id.OrganizerEventDateText);
        TextView eventStartTime = view.findViewById(R.id.OrganizerEventTimeText);

        ImageView eventProfileImage = view.findViewById(R.id.OrganizerEventUserImage);
        ImageView eventBanner = view.findViewById(R.id.OrganizerEventImage);

        DateFormat df = new SimpleDateFormat("EEE, MMM d, yyyy", Locale.CANADA);
        DateFormat tf = new SimpleDateFormat("h:mm a", Locale.CANADA);

        String startDate = df.format(event.getStartDate());
        String endDate = df.format(event.getEndDate());
        String startTime = tf.format(event.getStartDate());
        String endTime = tf.format(event.getEndDate());

        DisplayMetrics metrics = new DisplayMetrics();
        ((Activity) getContext()).getWindowManager().getDefaultDisplay().getMetrics(metrics);
        int screenWidth = metrics.widthPixels;
        int screenHeight = metrics.heightPixels;

        if (!startDate.equals(endDate)) {
            eventStartDate.setText(startDate + " to");
            eventStartTime.setText(endDate);
        } else {
            eventStartDate.setText(startDate);
            eventStartTime.setText(startTime + " - " + endTime);
        }

        eventTitle.setText(event.getName());
        eventDescription.setText(event.getDescription());
        eventLocation.setText(event.getLocation());

        //set profile image
        userViewModel.getUser().observe(getViewLifecycleOwner(), user -> {
            if (user.getProfileImageUrl() != null) {
                String uri = user.getProfileImageUrl();

                // Get image from firebase storage
                StorageReference storageRef = FirebaseStorage.getInstance().getReferenceFromUrl(uri);
                final long ONE_MEGABYTE = 1024 * 1024;

                storageRef.getBytes(ONE_MEGABYTE).addOnSuccessListener(bytes -> {
                    Bitmap bmp = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                    Bitmap scaledBmp = Bitmap.createScaledBitmap(bmp, 300, 300, false);
                    RoundedBitmapDrawable d = RoundedBitmapDrawableFactory.create(getResources(), scaledBmp);
                    eventProfileImage.setImageDrawable(d);
                }).addOnFailureListener(exception -> {
                    Log.w("User Profile", "Error getting profile image", exception);
                });
            }
            else{
                // generate profile image if user has no profile image
                Bitmap pfp = GenerateProfileImage.generateProfileImage(user.getFirstName(), user.getLastName());
                Bitmap scaledBmp = Bitmap.createScaledBitmap(pfp, 300, 300, false);
                RoundedBitmapDrawable d = RoundedBitmapDrawableFactory.create(getResources(), scaledBmp);
                d.setCornerRadius(100f);
                eventProfileImage.setImageDrawable(d);
            }
        });

        // Set Event Banner
        String uri_eventBanner = event.getEventBannerImgUrl();
        // Get image from firebase storage
        StorageReference storageRef = FirebaseStorage.getInstance().getReferenceFromUrl(uri_eventBanner);
        final long ONE_MEGABYTE = 1024 * 1024;

        storageRef.getBytes(ONE_MEGABYTE).addOnSuccessListener(bytes -> {
            Bitmap bmp = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
            Bitmap scaledBmp = Bitmap.createScaledBitmap(bmp, screenWidth, screenHeight, false);
            RoundedBitmapDrawable d = RoundedBitmapDrawableFactory.create(getResources(), scaledBmp);
            eventBanner.setImageDrawable(d);
        }).addOnFailureListener(exception -> {
            Log.w("User Profile", "Error getting profile image", exception);
        });
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            event = (Event) getArguments().getSerializable("eventData");
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
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_organizer_event, container, false);

        updateScreenInformation(view);

        view.findViewById(R.id.backButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NavHostFragment.findNavController(organizer_event.this)
                        .popBackStack();
            }
        });

        view.findViewById(R.id.OrganizerEventQRCodeButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                bundle.putSerializable("eventData", event);

                NavHostFragment.findNavController(organizer_event.this)
                        .navigate(R.id.action_organizer_event_to_organizerQRCode, bundle);
            }
        });

        view.findViewById(R.id.editButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                bundle.putSerializable("eventData", event);

                NavHostFragment.findNavController(organizer_event.this)
                        .navigate(R.id.action_organizerEvent_to_organizerEventCreate2, bundle);
            }
        });

        return view;
    }
}