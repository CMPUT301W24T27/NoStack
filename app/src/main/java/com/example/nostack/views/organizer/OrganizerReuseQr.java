package com.example.nostack.views.organizer;

import android.graphics.Color;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.fragment.NavHostFragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.nostack.R;
import com.example.nostack.models.Event;
import com.example.nostack.models.QrCode;
import com.example.nostack.viewmodels.EventViewModel;
import com.example.nostack.viewmodels.QrCodeViewModel;
import com.example.nostack.views.organizer.adapters.InactiveQrEventAdapter;

import java.util.ArrayList;
import java.util.List;

import android.util.Pair;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link OrganizerReuseQr#newInstance} factory method to
 * create an instance of this fragment.
 */
public class OrganizerReuseQr extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private ArrayList<Pair<Event, QrCode>> dataList;
    private Event event;
    private QrCode selectedQrCode;
    private EventViewModel eventViewModel;
    private QrCodeViewModel qrCodeViewModel;
    private InactiveQrEventAdapter arrayAdapter;
    private ListView listView;
    private View previousView;
    public OrganizerReuseQr() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment OrganizerReuseQr.
     */
    // TODO: Rename and change types and number of parameters
    public static OrganizerReuseQr newInstance() {
        OrganizerReuseQr fragment = new OrganizerReuseQr();
        Bundle args = new Bundle();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        dataList = new ArrayList<>();
        eventViewModel = new ViewModelProvider(requireActivity()).get(EventViewModel.class);
        qrCodeViewModel = new ViewModelProvider(requireActivity()).get(QrCodeViewModel.class);
        qrCodeViewModel.fetchInactiveQrCodes();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_organizer_reuse_qr, container, false);
        arrayAdapter = new InactiveQrEventAdapter(getContext(), dataList);
        listView = view.findViewById(R.id.reusable_qrs);
        listView.setAdapter(arrayAdapter);

        // Watch for errors
        eventViewModel.getErrorLiveData().observe(getViewLifecycleOwner(), errorMessage -> {
            if (errorMessage != null && !errorMessage.isEmpty()) {
                Toast.makeText(getContext(), errorMessage, Toast.LENGTH_SHORT).show();
                eventViewModel.clearErrorLiveData();
            }
        });

        qrCodeViewModel.getErrorLiveData().observe(getViewLifecycleOwner(), errorMessage -> {
            if (errorMessage != null && !errorMessage.isEmpty()) {
                Toast.makeText(getContext(), errorMessage, Toast.LENGTH_SHORT).show();
                qrCodeViewModel.clearErrorLiveData();
            }
        });

        // Get Event
        eventViewModel.getEventWithReuse().observe(getViewLifecycleOwner(), e -> {
            if (e.first != null) {
                event = e.first;
            }

        });

        // Fetch and get QrCodes
        qrCodeViewModel.getInactiveQrCodes().observe(getViewLifecycleOwner(), qrCodes -> {
            if (qrCodes != null && qrCodes.size() > 0) {
                for (Pair<Event, QrCode> p: qrCodes) {
                    if(p.first == null || p.second == null) {
                        continue;
                    }
                    arrayAdapter.addQrCode(p);
                }
                arrayAdapter.notifyDataSetChanged();
            }
        });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (previousView != null) {
                    previousView.setBackgroundColor(getResources().getColor(R.color.darkGrey));
                    TextView eventName = previousView.findViewById(R.id.reuse_qr_event_name);
                    TextView qrId = previousView.findViewById(R.id.reuse_qr_code_id);
                    eventName.setTextColor(getResources().getColor(R.color.white));
                    qrId.setTextColor(getResources().getColor(R.color.grey));
                }

                previousView = view;
                view.setBackgroundColor(Color.YELLOW);
                TextView eventName = previousView.findViewById(R.id.reuse_qr_event_name);
                TextView qrId = previousView.findViewById(R.id.reuse_qr_code_id);
                eventName.setTextColor(Color.BLACK);
                qrId.setTextColor(getResources().getColor(R.color.darkGrey));

                Pair<Event, QrCode> clickedPair = arrayAdapter.getItem(position);
                selectedQrCode = clickedPair.second;
                eventViewModel.updateEventWithReuse(selectedQrCode.getId());
            }
        });

        view.findViewById(R.id.backButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NavHostFragment.findNavController(OrganizerReuseQr.this).popBackStack();
            }
        });

        view.findViewById(R.id.create_reuse_event).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (selectedQrCode != null) {
                    eventViewModel.addEventWithReuse();
                    if (NavHostFragment.findNavController(OrganizerReuseQr.this).popBackStack()) {
                        NavHostFragment.findNavController(OrganizerReuseQr.this).popBackStack();
                    };
                } else {
                    Toast.makeText(getContext(), "Please select a QR code first", Toast.LENGTH_SHORT).show();
                }
            }
        });

        return view;
    }


}