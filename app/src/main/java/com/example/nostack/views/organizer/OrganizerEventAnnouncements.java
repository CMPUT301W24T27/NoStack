package com.example.nostack.views.organizer;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.fragment.NavHostFragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.example.nostack.R;
import com.example.nostack.controllers.UserController;
import com.example.nostack.handlers.CurrentUserHandler;
import com.example.nostack.models.User;
import com.example.nostack.viewmodels.EventViewModel;
import com.example.nostack.viewmodels.UserViewModel;
import com.example.nostack.views.attendee.AnnouncementHistory;
import com.example.nostack.views.event.adapters.AnnouncementHistoryArrayAdapter;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link AnnouncementHistory#newInstance} factory method to
 * create an instance of this fragment.
 */
public class OrganizerEventAnnouncements extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private AnnouncementHistoryArrayAdapter arrayAdapter;
    private ArrayList<HashMap<String,String>> dataList;
    private ListView announcementListView;
    private EventViewModel eventViewModel;



    public OrganizerEventAnnouncements() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment AnnouncementHistory.
     */
    // TODO: Rename and change types and number of parameters
    public static AnnouncementHistory newInstance(String param1, String param2) {
        AnnouncementHistory fragment = new AnnouncementHistory();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        dataList = new ArrayList<>();
        eventViewModel = new ViewModelProvider(requireActivity()).get(EventViewModel.class);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_organizer_event_announcements, container, false);
        view.findViewById(R.id.backButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NavHostFragment.findNavController(OrganizerEventAnnouncements.this).popBackStack();
            }
        });

        announcementListView = view.findViewById(R.id.event_announcement_list);

        arrayAdapter = new AnnouncementHistoryArrayAdapter(getContext(), dataList, this);

        announcementListView.setAdapter(arrayAdapter);

        eventViewModel.getEvent().observe(getViewLifecycleOwner(), event -> {
            if (event != null && event.getAnnouncements() != null && !event.getAnnouncements().isEmpty()) {
                arrayAdapter.clear();

                ArrayList<HashMap<String, String>> announcements = new ArrayList<>();
                announcements = event.getAnnouncements();

                Collections.sort(announcements, new Comparator<Map<String, String>>() {
                    @Override
                    public int compare(Map<String, String> map1, Map<String, String> map2) {
                        String key1 = map1.keySet().iterator().next();
                        String key2 = map2.keySet().iterator().next();
                        return key2.compareTo(key1);
                    }
                });

                for (HashMap<String, String> ann: announcements) {
                    arrayAdapter.add(ann);
                }
                arrayAdapter.notifyDataSetChanged();
                Log.d("OrganizerEventAnnouncements", "Announcement: " + event.getAnnouncements().toString());
            } else {
                Log.d("OrganizerEventAnnouncements", "Event does not have any announcement.");
            }
        });
        return view;
    }
}