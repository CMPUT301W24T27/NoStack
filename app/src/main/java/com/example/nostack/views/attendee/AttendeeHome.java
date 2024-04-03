package com.example.nostack.views.attendee;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.graphics.drawable.RoundedBitmapDrawable;
import androidx.core.graphics.drawable.RoundedBitmapDrawableFactory;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.fragment.NavHostFragment;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import com.example.nostack.R;
import com.example.nostack.handlers.CurrentUserHandler;
import com.example.nostack.handlers.ImageViewHandler;
import com.example.nostack.handlers.LocationHandler;
import com.example.nostack.models.Event;
import com.example.nostack.models.ImageDimension;
import com.example.nostack.viewmodels.EventViewModel;
import com.example.nostack.views.event.adapters.EventArrayAdapter;
import com.example.nostack.viewmodels.UserViewModel;
import com.example.nostack.views.activity.ScanActivity;
import com.example.nostack.handlers.EventCheckinHandler;
import com.example.nostack.services.GenerateProfileImage;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.journeyapps.barcodescanner.ScanContract;
import com.journeyapps.barcodescanner.ScanOptions;
import com.tbuonomo.viewpagerdotsindicator.DotsIndicator;

import org.checkerframework.checker.units.qual.Current;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 * Creates the AttendeeHome fragment which is used to display the home page for the attendee
 */
public class AttendeeHome extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private UserViewModel userViewModel;
    private EventViewModel eventViewModel;
    private CurrentUserHandler currentUserHandler;
    public static final int LOCATION_PERMISSION_REQUEST_CODE = 1001;
    private ImageViewHandler imageViewHandler;
    private static final Class[] fragments = new Class[]{AttendeeBrowse.class, AttendeeEvents.class};
    private ViewPager2 viewPager;
    private DotsIndicator dotsIndicator;
    private LocationHandler locationHandler;


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
        eventViewModel = new ViewModelProvider(requireActivity()).get(EventViewModel.class);
        currentUserHandler = CurrentUserHandler.getSingleton();
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
     * @return
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment only once
        View rootView = inflater.inflate(R.layout.fragment_attendee_home, container, false);
        TextView userWelcome = (TextView) rootView.findViewById(R.id.text_userWelcome);
        viewPager = rootView.findViewById(R.id.event_tab_navigation);
        viewPager.setAdapter(new MyFragmentAdapter(this));
        dotsIndicator = rootView.findViewById(R.id.dots_indicator);
        dotsIndicator.attachTo(viewPager);

        Activity activity = getActivity();
        assert activity != null;
        LocationManager locationManager = (LocationManager) getContext().getSystemService(Context.LOCATION_SERVICE);
        locationHandler = new LocationHandler(getContext(), getActivity(), locationManager);
        locationHandler.handleLocationPermissions();

        Log.d("AttendeeHome", "UserViewModel: " + userViewModel.getUser().getValue());
        userViewModel.getUser().observe(getViewLifecycleOwner(), user -> {

            if (user != null) {
                Log.d("AttendeeHome", "User logged in: " + user.getUsername());
                userWelcome.setText(user.getUsername());
            } else {
                Log.d("AttendeeHome", "User is null");
            }
        });

        // Watch for errors
        eventViewModel.getErrorLiveData().observe(getViewLifecycleOwner(), errorMessage -> {
            if (errorMessage != null && !errorMessage.isEmpty()) {
                Toast.makeText(getContext(), errorMessage, Toast.LENGTH_SHORT).show();
                eventViewModel.clearErrorLiveData();
            }
        });

        return rootView;
    }

    /**
     * This method is called when the fragment has been created and also sets up the buttons
     *
     * @param view               The View returned by onCreateView
     * @param savedInstanceState If non-null, this fragment is being re-constructed
     *                           from a previous saved state as given here.
     */
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        view.findViewById(R.id.admin_profileButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NavHostFragment.findNavController(AttendeeHome.this)
                        .navigate(R.id.action_attendeeHome_to_userProfile);
            }
        });

        ImageButton profileImage = getView().findViewById(R.id.admin_profileButton);
        imageViewHandler.setUserProfileImage(currentUserHandler.getCurrentUser(), profileImage, getResources(), new ImageDimension(100, 100));

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

        /**
         * This method is called when the fragment is being created and then sets up the view for the fragment
         *
         * @param position The position of the fragment
         * @return a null fragment if there is an exception
         */
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

    /**
     * This method allows the user to scan a QR code by launching the ScanActivity
     */
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
            if (result.getContents().charAt(0) == '0') {
                handleCheckInQR(result.getContents().substring(2));
            } else if (result.getContents().charAt(0) == '1') {
                handleEventDescQR(result.getContents().substring(2));
            }
        }
    });

    public void handleEventDescQR(String eventUID) {
        eventViewModel.fetchEvent(eventUID);
        Event event = eventViewModel.getEvent().getValue();
        Bundle bundle = new Bundle();
        bundle.putSerializable("event", event);
        NavHostFragment.findNavController(AttendeeHome.this)
                .navigate(R.id.action_attendeeHome_to_attendeeEvent, bundle);
    }

    public void handleCheckInQR(String eventUID) {

        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Check-in Successful!");
        builder.setMessage("You have successfully checked in to the event!");
        Location location = locationHandler.getLocation();
        eventViewModel.eventCheckIn(currentUserHandler.getCurrentUserId(), eventUID, location);
        builder.setPositiveButton("Done", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        }).show();

    }
}