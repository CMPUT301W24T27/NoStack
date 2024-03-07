package com.example.nostack.ui.organizer;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.example.nostack.R;
import com.example.nostack.utils.Event;

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

    public organizer_event() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
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

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            event = (Event) getArguments().getSerializable("eventData");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_organizer_event, container, false);

        TextView eventTitle = view.findViewById(R.id.OrganizerEventTitleText);
        TextView eventDescription = view.findViewById(R.id.OrganizerEventDescriptionText);
        TextView eventLocation = view.findViewById(R.id.OrganizerEventLocationText);
        TextView eventStartDate = view.findViewById(R.id.OrganizerEventDateText);
        TextView eventStartTime = view.findViewById(R.id.OrganizerEventTimeText);


        eventTitle.setText(event.getName());
        eventDescription.setText(event.getDescription());


        return view;
    }
}