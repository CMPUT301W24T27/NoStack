package com.example.nostack.views.organizer;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.fragment.NavHostFragment;

import com.example.nostack.R;
import com.example.nostack.handlers.CurrentUserHandler;
import com.example.nostack.handlers.NotificationHandler;
import com.example.nostack.models.Event;
import com.example.nostack.viewmodels.AttendanceViewModel;
import com.example.nostack.viewmodels.EventViewModel;
import com.example.nostack.viewmodels.QrCodeViewModel;
import com.example.nostack.viewmodels.UserViewModel;
import com.example.nostack.handlers.ImageViewHandler;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Locale;

/**
 * A simple {@link Fragment} subclass.
 * Creates the organizer event fragment so an organizer can manage their event
 */
public class OrganizerEvent extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private Event event;
    private EventViewModel eventViewModel;
    private AttendanceViewModel attendanceViewModel;
    private QrCodeViewModel qrCodeViewModel;
    private CurrentUserHandler currentUserHandler;
    private ImageViewHandler imageViewHandler;
    private NotificationHandler notificationHandler;


    public OrganizerEvent() {
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
    public static OrganizerEvent newInstance(Event param1) {
        OrganizerEvent fragment = new OrganizerEvent();
        Bundle args = new Bundle();
        args.putSerializable(ARG_PARAM1, param1);
        fragment.setArguments(args);
        return fragment;
    }


    /**
     * This method is called when the fragment is being created and checks to see if there are any arguments
     *
     * @param savedInstanceState If the fragment is being re-created from
     *                           a previous saved state, this is the state.
     */
    @Override

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        eventViewModel = new ViewModelProvider(requireActivity()).get(EventViewModel.class);
        qrCodeViewModel = new ViewModelProvider(requireActivity()).get(QrCodeViewModel.class);
        attendanceViewModel = new ViewModelProvider(requireActivity()).get(AttendanceViewModel.class);
        imageViewHandler = ImageViewHandler.getSingleton();
        currentUserHandler = CurrentUserHandler.getSingleton();
        notificationHandler = NotificationHandler.getSingleton();
    }

    /**
     * This method is called when the fragment needs to create its view and it will
     * control the navigation of the fragment
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
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_organizer_event, container, false);

        // Watch for errors
        eventViewModel.getErrorLiveData().observe(getViewLifecycleOwner(), errorMessage -> {
            if (errorMessage != null && !errorMessage.isEmpty()) {
                Toast.makeText(getContext(), errorMessage, Toast.LENGTH_SHORT).show();
                eventViewModel.clearErrorLiveData();
            }
        });

        eventViewModel.getEvent().observe(getViewLifecycleOwner(), ev -> {
            if (ev != null) {
                event = ev;
                updateScreenInformation(view);
            }
        });

        view.findViewById(R.id.backButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                eventViewModel.clearEventLiveData();
                NavHostFragment.findNavController(OrganizerEvent.this).popBackStack();
            }
        });

        view.findViewById(R.id.OrganizerEventQRCodeButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // If event is not ended
                if(event.getActive() == null || event.getActive()) {
                    qrCodeViewModel.fetchQrCode(event.getCheckInQrId());
                    NavHostFragment.findNavController(OrganizerEvent.this)
                            .navigate(R.id.action_organizer_event_to_organizerQRCode);
                } else {
                    FloatingActionButton button = view.findViewById(R.id.OrganizerEventQRCodeButton);
                    button.setImageResource(R.drawable.baseline_qr_code_24);
                    eventViewModel.reactivateEvent(event.getId(), currentUserHandler.getCurrentUserId());
                    Toast.makeText(getContext(), "Event has been reactivated.", Toast.LENGTH_SHORT).show();
                }
            }
        });

        view.findViewById(R.id.button_see_attendees).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                attendanceViewModel.fetchAttendanceByEvent(event.getId());
                attendanceViewModel.fetchPresentAttByEvent(event.getId());
                NavHostFragment.findNavController(OrganizerEvent.this)
                        .navigate(R.id.action_organizer_event_to_organizerEventAttendeeList);
            }
        });

        FloatingActionButton endButton = view.findViewById(R.id.button_end_event);
        endButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (event.getActive() == null || !event.getActive()) {
                    new AlertDialog.Builder(getContext())
                            .setTitle("Delete Event")
                            .setMessage("Are you sure you want to delete this event?")
                            .setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    eventViewModel.deleteEvent(event, new EventViewModel.DeleteEventCallback() {
                                        @Override
                                        public void onEventDeleted() {
                                            eventViewModel.clearEventLiveData();
                                            NavHostFragment.findNavController(OrganizerEvent.this).popBackStack();
                                        }

                                        @Override
                                        public void onEventDeleteFailed() {
                                            Toast.makeText(getContext(), "Failed to delete event...", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                }
                            })
                            .setNegativeButton("Cancel",null)
                            .show();
                } else {
                    eventViewModel.endEvent(event.getId(), currentUserHandler.getCurrentUserId());
                    Toast.makeText(getContext(), "Event has been successfully ended.", Toast.LENGTH_SHORT).show();
                }
            }
        });

        view.findViewById(R.id.editButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NavHostFragment.findNavController(OrganizerEvent.this)
                        .navigate(R.id.action_organizerEvent_to_organizerEventCreate2);
            }
        });

        // Test notifications
        view.findViewById(R.id.AttendeeEventTitleText).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                notificationHandler.sendEventNotification(event, "This is a test notification");
            }
        });

        return view;
    }

    /**
     * Updates the screen information with the event data
     *
     * @param view The view that the information will be updated on
     */
    public void updateScreenInformation(@NonNull View view) {
            TextView eventTitle = view.findViewById(R.id.AttendeeEventTitleText);
            TextView eventDescription = view.findViewById(R.id.OrganizerEventDescriptionText);
            TextView eventLocation = view.findViewById(R.id.OrganizerEventLocationText);
            TextView eventStartDate = view.findViewById(R.id.OrganizerEventDateText);
            TextView eventStartTime = view.findViewById(R.id.OrganizerEventTimeText);

            ImageView eventProfileImage = view.findViewById(R.id.AttendeeEventUserImage);
            ImageView eventBanner = view.findViewById(R.id.OrganizerEventImage);

            DateFormat df = new SimpleDateFormat("EEE, MMM d, yyyy", Locale.CANADA);
            DateFormat tf = new SimpleDateFormat("h:mm a", Locale.CANADA);

        if (event != null) {
            String startDate = df.format(event.getStartDate());
            String endDate = df.format(event.getEndDate());
            String startTime = tf.format(event.getStartDate());
            String endTime = tf.format(event.getEndDate());

            if (!startDate.equals(endDate)) {
                eventStartDate.setText(startDate + " to");
                eventStartTime.setText(endDate);
            } else {
                eventStartDate.setText(startDate);
                eventStartTime.setText(startTime + " - " + endTime);
            }

            if (!event.getActive() || event.getActive() == null) {
                eventTitle.setText(event.getName() + " (Ended)");
                FloatingActionButton button = view.findViewById(R.id.button_end_event);
                FloatingActionButton qrButton = view.findViewById(R.id.OrganizerEventQRCodeButton);
                qrButton.setImageResource(R.drawable.baseline_autorenew_24);
                button.setImageResource(R.drawable.baseline_delete_forever_24);

//                view.findViewById(R.id.OrganizerEventQRCodeButton).setClickable(false);
//                view.findViewById(R.id.OrganizerEventQRCodeButton).setAlpha(0.5f);
            } else {
                eventTitle.setText(event.getName());
                FloatingActionButton button = view.findViewById(R.id.button_end_event);
                button.setImageResource(R.drawable.baseline_block_24);
                view.findViewById(R.id.button_end_event).setClickable(true);
                view.findViewById(R.id.button_end_event).setAlpha(1f);
            }
            eventDescription.setText(event.getDescription());
            eventLocation.setText(event.getLocation());

            imageViewHandler.setUserProfileImage(currentUserHandler.getCurrentUser(), eventProfileImage, getResources(), null);
            imageViewHandler.setEventImage(event, eventBanner);
        }
    }
}