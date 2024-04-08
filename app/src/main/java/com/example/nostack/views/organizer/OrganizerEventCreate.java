package com.example.nostack.views.organizer;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.PickVisualMediaRequest;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.widget.SwitchCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.fragment.NavHostFragment;

import com.example.nostack.R;
import com.example.nostack.handlers.CurrentUserHandler;
import com.example.nostack.models.Event;
import com.example.nostack.models.QrCode;
import com.example.nostack.services.ImageUploader;
import com.example.nostack.services.NavbarConfig;
import com.example.nostack.viewmodels.EventViewModel;
import com.example.nostack.viewmodels.QrCodeViewModel;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.IOException;
import java.net.URI;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.UUID;

/**
 * A simple {@link Fragment} subclass.
 * Creates the fragment for the organizer to create an event
 */
public class OrganizerEventCreate extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "eventData";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private Event event;
    private String mParam2;
    private Activity activity;
    private ImageUploader imageUploader;
    private FirebaseFirestore db;
    private CollectionReference eventsRef;
    private FloatingActionButton backButton;
    private String previousLimit;
    private TextInputEditText eventTitleEditText;
    private TextInputEditText eventStartEditText;
    private TextInputLayout eventStartLayout;
    private TextInputEditText eventEndEditText;
    private TextInputLayout eventEndLayout;
    private TextView eventCreationTitle;
    private TextInputEditText eventLocationEditText;
    private TextInputEditText eventLimitEditText;
    private TextInputEditText eventDescEditText;
    private Button createButton;
    private CheckBox eventReuseQrCheckBox;
    private ImageView eventImageView;
    private SharedPreferences preferences;
    private SwitchCompat unlimitedButton;
    private ActivityResultLauncher<PickVisualMediaRequest> imagePickerLauncher;
    private EventViewModel eventViewModel;
    private QrCodeViewModel qrCodeViewModel;
    private CurrentUserHandler currentUserHandler;
    private NavbarConfig navbarConfig;
    private Boolean isEditing;
    private Boolean isUnlimited;

    public OrganizerEventCreate() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param event Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment OrganizerEvent.
     */
    // TODO: Rename and change types and number of parameters
    public static OrganizerEventCreate newInstance(Event event, String param2) {
        OrganizerEventCreate fragment = new OrganizerEventCreate();
        Bundle args = new Bundle();
        args.putSerializable(ARG_PARAM1, event);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            event = (Event) getArguments().getSerializable(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }

        eventViewModel = new ViewModelProvider(requireActivity()).get(EventViewModel.class);
        qrCodeViewModel = new ViewModelProvider(requireActivity()).get(QrCodeViewModel.class);
        imageUploader = new ImageUploader();
        currentUserHandler = CurrentUserHandler.getSingleton();
        navbarConfig = NavbarConfig.getSingleton();
        imagePickerLauncher = registerForActivityResult(new ActivityResultContracts.PickVisualMedia(), uri -> {
            if (uri != null) {
                eventImageView.setTag(uri);
                eventImageView.setImageURI(uri);
            }
        });

        // Prefetch reusable QR codes
        qrCodeViewModel.fetchInactiveQrCodes();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_organizer_event_create, container, false);

        eventTitleEditText = view.findViewById(R.id.EventCreationTitleEditText);
        eventStartEditText = view.findViewById(R.id.EventCreationStartDateTimeEditText);
        eventStartLayout = view.findViewById(R.id.EventCreationStartDateTimeLayout);
        eventEndEditText = view.findViewById(R.id.EventCreationEndDateTimeEditText);
        eventEndLayout = view.findViewById(R.id.EventCreationEndDateTimeLayout);
        eventLocationEditText = view.findViewById(R.id.EventCreationLocationEditText);
        eventDescEditText = view.findViewById(R.id.EventCreationDescriptionEditText);
        eventReuseQrCheckBox = view.findViewById(R.id.EventCreationReuseQRCheckBox);
        eventImageView = view.findViewById(R.id.EventCreationEventImageView);
        eventLimitEditText = view.findViewById(R.id.EventCreationLimitEditText);
        backButton = view.findViewById(R.id.backButton);
        createButton = view.findViewById(R.id.EventCreationCreateEventButton);
        unlimitedButton = view.findViewById(R.id.unlimitedUserSwitch);
        eventCreationTitle = view.findViewById(R.id.EventCreationTitle);

        // Check if the event is being edited
        eventViewModel.getEvent().observe(getViewLifecycleOwner(), ev -> {
            event = ev;
            checkEditEvent();
        });

        createButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (eventTitleEditText.getText().toString().isEmpty()) {
                    eventTitleEditText.setError("Event name is required");
                    showErrorMessage("Event name is required");
                } else if (eventStartEditText.getText().toString().isEmpty()) {
                    eventStartEditText.setError("Event start date/time is required");
                    showErrorMessage("Event start date/time is required");
                } else if (eventEndEditText.getText().toString().isEmpty()) {
                    eventEndEditText.setError("Event end date/time is required");
                    showErrorMessage("Event end date/time is required");
                } else if (eventLocationEditText.getText().toString().isEmpty()) {
                    eventLocationEditText.setError("Event location is required");
                    showErrorMessage("Event location is required");
                } else if (eventDescEditText.getText().toString().isEmpty()) {
                    eventDescEditText.setError("Event description is required");
                    showErrorMessage("Event description is required");
                } else if (eventLimitEditText.getText() != null
                        && (!eventLimitEditText.getText().toString().isEmpty())
                        && (Integer.parseInt(eventLimitEditText.getText().toString()) < 1)) {
                    eventLimitEditText.setError("Event limit must be greater than 0.");
                    showErrorMessage("Event limit must be greater than 0.");
                } else {
                    SimpleDateFormat formatter = new SimpleDateFormat("yyyy-M-d hh:mm");
                    try {
                        Date startDate = formatter.parse(eventStartEditText.getText().toString());
                        Date endDate = formatter.parse(eventEndEditText.getText().toString());
                        Date currentTime = new Date();

                        if (endDate.before(startDate)) {
                            eventEndEditText.setError("Event end date/time must be after start date/time");
                            showErrorMessage("Event end date/time must be after start date/time");
                            return;
                        } else if (endDate.before(currentTime)) {
                            eventEndEditText.setError("Event end date/time must be in the future");
                            showErrorMessage("Event end date/time must be in the future");
                            return;
                        }

                    } catch (ParseException e){
                        eventStartEditText.setError("Invalid date/time format");
                        eventEndEditText.setError("Invalid date/time format");
                        showErrorMessage("Invalid date/time format");
                        return;
                    }

                    Event event = createEvent();
                    Uri eventBannerUri;
                    try {
                        eventBannerUri = (Uri) eventImageView.getTag();
                    } catch (Exception e) {
                        eventBannerUri = Uri.parse((String) eventImageView.getTag());
                    }

                    Uri compressedImageUri = null;
                    if(eventBannerUri != null){
                        try {
                            compressedImageUri = ImageUploader.compressImage(eventBannerUri, 0.5, getContext());
                        } catch (Exception e) {
                            Log.w("Event creation", "Image compression failed:", e);
                            compressedImageUri = null;
                        }
                    }

                    // Watch for errors
                    eventViewModel.getErrorLiveData().observe(getViewLifecycleOwner(), errorMessage -> {
                        if (errorMessage != null && !errorMessage.isEmpty()) {
                            Toast.makeText(getContext(), errorMessage, Toast.LENGTH_SHORT).show();
                            eventViewModel.clearErrorLiveData();
                        }
                    });

                    if (isEditing) {
                        eventViewModel.updateEvent(event, compressedImageUri, new EventViewModel.UpdateImageCallback() {
                            @Override
                            public void onImageUpdated(Uri uri) {
                                if(uri == null){
                                    return;
                                }
                                eventImageView.setTag(uri);
                                eventImageView.setImageURI(Uri.parse(uri.toString()));
                                Log.d("URI", uri.toString());
                            }

                            @Override
                            public void onImageUpdateFailed() {
                                Toast.makeText(getContext(), "Failed to update image", Toast.LENGTH_SHORT).show();
                            }
                        });
                        NavHostFragment.findNavController(OrganizerEventCreate.this).popBackStack();
                    }
                    else {
                        Boolean reuse = eventReuseQrCheckBox.isChecked();
                        if (reuse) {
                            eventViewModel.setEventWithReuse(event, compressedImageUri);
                            NavHostFragment.findNavController(OrganizerEventCreate.this).navigate(R.id.action_organizerEventCreate_to_organizerReuseQr);
                        } else {
                            eventViewModel.addEvent(event, false, compressedImageUri);
                            NavHostFragment.findNavController(OrganizerEventCreate.this).popBackStack();
                        }
                    }
                }
            }
        });
        unlimitedButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    eventLimitEditText.setFocusable(false);
                    previousLimit = String.valueOf(eventLimitEditText.getText());
                    eventLimitEditText.setText("");
                    unlimitedButton.setTextColor(getContext().getColor(R.color.SignInUpTextColor));
                } else {
                    eventLimitEditText.setFocusableInTouchMode(true);
                    unlimitedButton.setTextColor(Color.GRAY);
                    if (previousLimit != null) {
                        eventLimitEditText.setText(previousLimit);
                    }
                }
            }
        });

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NavHostFragment.findNavController(OrganizerEventCreate.this).popBackStack();
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
        imagePickerLauncher.launch(new PickVisualMediaRequest.Builder().setMediaType(ActivityResultContracts.
                        PickVisualMedia.ImageOnly.INSTANCE)
                .build());
    }

    private Event createEvent() {
        String startDateString = eventStartEditText.getText().toString();
        String endDateString = eventEndEditText.getText().toString();

        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-M-d hh:mm");

        Event newEvent = null;
        try {
            if(event != null){
                isEditing = true;
                newEvent = event;
                newEvent.setName(eventTitleEditText.getText().toString());
                newEvent.setLocation(eventLocationEditText.getText().toString());
                newEvent.setDescription(eventDescEditText.getText().toString());
                newEvent.setStartDate(formatter.parse(startDateString));
                newEvent.setEndDate(formatter.parse(endDateString));
                newEvent.setActive(true);
            }
            else {
                isEditing = false;
                newEvent = new Event();
                newEvent.setId(UUID.randomUUID().toString());
                newEvent.setName(eventTitleEditText.getText().toString());
                newEvent.setLocation(eventLocationEditText.getText().toString());
                newEvent.setDescription(eventDescEditText.getText().toString());
                newEvent.setStartDate(formatter.parse(startDateString));
                newEvent.setEndDate(formatter.parse(endDateString));
                newEvent.setOrganizerId(currentUserHandler.getCurrentUserId());
                newEvent.setCurrentCapacity(0);
                newEvent.setActive(true);
            }

            if (eventLimitEditText.getText() != null && !eventLimitEditText.getText().toString().isEmpty()) {
                int limit = Integer.parseInt(eventLimitEditText.getText().toString());
                newEvent.setCapacity(limit);
            } else {
                newEvent.setCapacity(-1);
            }

        } catch (ParseException e) {
            throw new RuntimeException(e);
        }

        return newEvent;
    }

    private void openDateTimePickerDialog(TextInputEditText t) {
        Context context = this.getContext();
        Calendar now = Calendar.getInstance();
        DatePickerDialog dateDialog = new DatePickerDialog(context, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int day) {
                t.setText(year + "-" + (month + 1) + "-" + day);
            }
        }, now.get(Calendar.YEAR), now.get(Calendar.MONTH), now.get(Calendar.DAY_OF_MONTH));

        TimePickerDialog timeDialog = new TimePickerDialog(context, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hour, int minute) {
                t.append(" " + hour + ":" + minute);
            }
        }, now.get(Calendar.HOUR_OF_DAY), now.get(Calendar.MINUTE), true);

        // Show the date dialog first when it closes show the time dialog
        dateDialog.setOnDismissListener(dialog -> timeDialog.show());
        dateDialog.show();
    }

    private void checkEditEvent() {
        if (event != null) {
            String startDate = new SimpleDateFormat("yyyy-M-d hh:mm").format(event.getStartDate());
            String endDate = new SimpleDateFormat("yyyy-M-d hh:mm").format(event.getEndDate());

            eventTitleEditText.setText(event.getName());
            eventLocationEditText.setText(event.getLocation());
            eventDescEditText.setText(event.getDescription());
            eventStartEditText.setText(startDate);
            eventEndEditText.setText(endDate);
            eventImageView.setTag(event.getEventBannerImgUrl());
            if (event.getCapacity() == -1) {
                unlimitedButton.setChecked(true);
                isUnlimited = true;
                eventLimitEditText.setClickable(false);
                eventLimitEditText.setFocusable(false);
                unlimitedButton.setTextColor(getContext().getColor(R.color.SignInUpTextColor));
            } else {
                eventLimitEditText.setText(String.valueOf(event.getCapacity()));
                unlimitedButton.setTextColor(Color.GRAY);
            }
            eventReuseQrCheckBox.setVisibility(View.GONE);
            eventCreationTitle.setText("Edit Event");
            createButton.setText("Update Event");
        }
    }

    private void showErrorMessage(String message) {
        Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
    }
}