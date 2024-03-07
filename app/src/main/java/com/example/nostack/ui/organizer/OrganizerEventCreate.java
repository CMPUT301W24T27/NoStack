package com.example.nostack.ui.organizer;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.TimePicker;

import com.example.nostack.R;
import com.example.nostack.model.Events.Event;
import com.example.nostack.utils.ImageUploader;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link OrganizerEventCreate#newInstance} factory method to
 * create an instance of this fragment.
 */
public class OrganizerEventCreate extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private Activity activity;
    private ImageUploader imageUploader;
    private FirebaseFirestore db;
    private CollectionReference eventsRef;
    private FloatingActionButton backButton;
    private TextInputEditText eventTitleEditText;
    private TextInputEditText eventStartEditText;
    private TextInputLayout eventStartLayout;
    private TextInputEditText eventEndEditText;
    private TextInputLayout eventEndLayout;
    private TextInputEditText eventLocationEditText;
    private TextInputEditText eventLimitEditText;
    private TextInputEditText eventDescEditText;
    private CheckBox eventReuseQrCheckBox;
    private ImageView eventImageView;
    private SharedPreferences preferences;
    private String userUUID;
    private ActivityResultLauncher<String> imagePickerLauncher;


    public OrganizerEventCreate() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment OrganizerEvent.
     */
    // TODO: Rename and change types and number of parameters
    public static OrganizerEventCreate newInstance(String param1, String param2) {
        OrganizerEventCreate fragment = new OrganizerEventCreate();
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

        db = FirebaseFirestore.getInstance();
        eventsRef = db.collection("events");
        activity = getActivity();
        imageUploader = new ImageUploader();
        preferences = activity.getApplicationContext().getSharedPreferences("com.example.nostack", Context.MODE_PRIVATE);
        userUUID = preferences.getString("uuid", null);
        imagePickerLauncher = registerForActivityResult(new ActivityResultContracts.GetContent(), new ActivityResultCallback<Uri>() {
            @Override
            public void onActivityResult(Uri o) {
                eventImageView.setTag(o);
                eventImageView.setImageURI(o);
            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_organizer_event_create,container,false);

        eventTitleEditText = view.findViewById(R.id.EventCreationTitleEditText);
        eventStartEditText = view.findViewById(R.id.EventCreationStartDateTimeEditText);
        eventStartLayout = view.findViewById(R.id.EventCreationStartDateTimeLayout);
        eventEndEditText = view.findViewById(R.id.EventCreationEndDateTimeEditText);
        eventEndLayout = view.findViewById(R.id.EventCreationEndDateTimeLayout);
        eventLocationEditText = view.findViewById(R.id.EventCreationLocationEditText);
        eventDescEditText = view.findViewById(R.id.EventCreationDescriptionEditText);
        eventReuseQrCheckBox = view.findViewById(R.id.EventCreationReuseQRCheckBox);
        eventImageView = view.findViewById(R.id.EventCreationEventImageView);
        backButton = view.findViewById(R.id.backButton);
        eventLimitEditText = view.findViewById(R.id.EventCreationLimitEditText);
        backButton = view.findViewById(R.id.backButton);

        view.findViewById(R.id.EventCreationCreateEventButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (eventTitleEditText.getText().toString().isEmpty()) {
                    eventTitleEditText.setError("Event name is required");
                } else if (eventStartEditText.getText().toString().isEmpty()) {
                    eventStartEditText.setError("Event start date/time is required");
                } else if (eventEndEditText.getText().toString().isEmpty()) {
                    eventEndEditText.setError("Event end date/time is required");
                } else if (eventLocationEditText.getText().toString().isEmpty()) {
                    eventLocationEditText.setError("Event location is required");
                } else if (eventDescEditText.getText().toString().isEmpty()) {
                    eventDescEditText.setError("Event description is required");
                } else if (eventLimitEditText.getText() != null
                        && (!eventLimitEditText.getText().toString().isEmpty())
                        && (Integer.parseInt(eventLimitEditText.getText().toString()) < 1)){
                    eventLimitEditText.setError("Event limit must be greater than 0.");
                } else {

                    Event event = createEvent();

                    Uri eventBannerUri = (Uri) eventImageView.getTag();
                    if (eventBannerUri != null) {
                        storeToDbEventWithBanner(eventBannerUri, event);
                    } else {
                        storeEventToDb(event);
                    }
                    NavHostFragment.findNavController(OrganizerEventCreate.this)
                            .navigate(R.id.action_organizerEventCreate_to_organizerHome);
                }
            }
        });

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NavHostFragment.findNavController(OrganizerEventCreate.this)
                        .navigate(R.id.action_organizerEventCreate_to_organizerHome);
            }
        });

        eventStartLayout.setEndIconOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openDateTimePickerDialog(eventStartEditText);
            }
        });

        eventEndLayout.setEndIconOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openDateTimePickerDialog(eventEndEditText);
            }
        });

        eventImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openImagePicker();
            }
        });


        // TODO: ADD THE FUNCTIONALITY TO REUSE QR CODES

        return view;
    }

    private void openImagePicker() {
        imagePickerLauncher.launch("image/*");
    }

    private Event createEvent() {
        String startDateString = eventStartEditText.getText().toString();
        String endDateString = eventEndEditText.getText().toString();

        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-M-d hh:mm");

        Event newEvent = null;
        try {
            newEvent = new Event(
                    eventTitleEditText.getText().toString(),
                    eventLocationEditText.getText().toString(),
                    eventDescEditText.getText().toString(),
                    formatter.parse(startDateString),
                    formatter.parse(endDateString),
                    userUUID
            );

            if (eventLimitEditText.getText() != null && !eventLimitEditText.getText().toString().isEmpty()) {
                int limit = Integer.parseInt(eventLimitEditText.getText().toString());
                newEvent.setCapacity(limit);
            }




        } catch (ParseException e) {
            throw new RuntimeException(e);
        }

        return newEvent;
    }

    private void storeEventToDb(Event newEvent) {
        eventsRef
                .document(newEvent.getId())
                .set(newEvent)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Log.w("Firestore", "New event successfully added!");
                        Snackbar.make(activity.findViewById(android.R.id.content), "New event created.", Snackbar.LENGTH_LONG).show();
                    }
                })
                .addOnFailureListener(e -> Log.w("Firestore", "Error creating event", e));;
    }

    private void openDateTimePickerDialog(TextInputEditText t) {
        Context context = this.getContext();
        Calendar now = Calendar.getInstance();
        DatePickerDialog dateDialog = new DatePickerDialog(context, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int day) {
                t.setText(String.valueOf(year)+"-"+String.valueOf(month+1)+"-"+String.valueOf(day));
            }
        }, now.get(Calendar.YEAR), now.get(Calendar.MONTH), now.get(Calendar.DAY_OF_MONTH));

        TimePickerDialog timeDialog = new TimePickerDialog(context, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hour, int minute) {
                t.append(" "+String.valueOf(hour)+":"+String.valueOf(minute));
            }
        }, now.get(Calendar.HOUR_OF_DAY), now.get(Calendar.MINUTE), true);

        timeDialog.show();
        dateDialog.show();
    }

    private void storeToDbEventWithBanner(Uri imageUri, Event event) {
        imageUploader.uploadImage("event/banner/", imageUri, new ImageUploader.UploadListener() {
            @Override
            public void onUploadSuccess(String imageUrl) {
                event.setEventBannerImgUrl(imageUrl);
                storeEventToDb(event);
                Log.d("User edit", "Image upload successful.:");
            }

            @Override
            public void onUploadFailure(Exception exception) {
                Log.w("Event creation", "Image upload failed:", exception);
                // TODO: Show error to user
            }
        });
    }
}