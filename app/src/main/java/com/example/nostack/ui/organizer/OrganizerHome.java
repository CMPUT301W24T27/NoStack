package com.example.nostack.ui.organizer;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.example.nostack.R;
import com.example.nostack.utils.Event;
import com.example.nostack.utils.EventArrayAdapter;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import org.checkerframework.checker.units.qual.A;

import java.util.ArrayList;

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
    private SharedPreferences preferences;
    private CollectionReference eventsRef;
    private Activity activity;
    private String userUUID;


    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

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
        db = FirebaseFirestore.getInstance();
        eventsRef = db.collection("events");
        activity = getActivity();
        preferences = activity.getApplicationContext().getSharedPreferences("com.example.nostack", Context.MODE_PRIVATE);
        userUUID = preferences.getString("uuid", null);

        dataList = new ArrayList<>();

        eventsRef.whereEqualTo("organizerID",userUUID).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                for (QueryDocumentSnapshot document : task.getResult()) {
                    dataList.add(document.toObject(Event.class));
                }
            }

            eventArrayAdapter = new EventArrayAdapter(getContext(),dataList);
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_organizer_home,container,false);



        view.findViewById(R.id.AddEventButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NavHostFragment.findNavController(OrganizerHome.this)
                        .navigate(R.id.action_organizerHome_to_organizerEvent);
            }
        });

        return view;
    }
}