package com.example.nostack.ui.attendee;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.fragment.NavHostFragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.nostack.R;
import com.example.nostack.model.Events.Event;
import com.example.nostack.model.State.UserViewModel;
import com.example.nostack.utils.EventCheckinHandler;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link AttendeeEvent#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AttendeeEvent extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters

    private Event event;
    private String mParam1;
    private String mParam2;
    UserViewModel userViewModel;

    public AttendeeEvent() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment AttendeeEvent.
     */
    // TODO: Rename and change types and number of parameters
    public static AttendeeEvent newInstance(String param1, String param2) {
        AttendeeEvent fragment = new AttendeeEvent();
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
            event = (Event) getArguments().getSerializable("eventData");
        }

        userViewModel = new ViewModelProvider((AppCompatActivity) getActivity() ).get(UserViewModel.class);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_attendee_event, container, false);

        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_attendee_event, container, false);
    }

    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        view.findViewById(R.id.backButton).setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                NavHostFragment.findNavController(AttendeeEvent.this)
                        .navigate(R.id.action_attendeeEvent_to_attendeeHome);
            }
        });

        view.findViewById(R.id.checkInButton).setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                EventCheckinHandler ecHandler = new EventCheckinHandler();
                userViewModel.getUser().observe(getViewLifecycleOwner(), user -> {

                    try {
                        ecHandler.checkInUser(event.getId(), user.getUuid());
                    } catch (Exception e) {
                        // Handle the exception here
                        e.printStackTrace();
                    }
                });

            }
        });

    }
}