package com.example.nostack.ui.organizer;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import com.example.nostack.R;
import com.example.nostack.utils.AttendeeLocations;
import com.example.nostack.utils.GeoLocation;

import org.osmdroid.api.IMapController;
import org.osmdroid.config.Configuration;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link OrganizerAttendeeMap#newInstance} factory method to
 * create an instance of this fragment.
 */
public class OrganizerAttendeeMap extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public OrganizerAttendeeMap() {
        // Required empty public constructor
    }

    private AttendeeLocations locations;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @return A new instance of fragment OrganizerAttendeeMap.
     */
    // TODO: Rename and change types and number of parameters
    public static OrganizerAttendeeMap newInstance(GeoLocation[] param1) {
        OrganizerAttendeeMap fragment = new OrganizerAttendeeMap();
        Bundle args = new Bundle();
        args.putSerializable("locations", param1);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            locations = (AttendeeLocations) getArguments().getSerializable("locations");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View view = inflater.inflate(R.layout.fragment_organizer_attendee_map, container, false);
        mapLocations(view);
        view.findViewById(R.id.backButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                NavHostFragment.findNavController(OrganizerAttendeeMap.this).popBackStack();
            }
        });
        return view;
    }

    private void mapLocations(View view) {
        Configuration.getInstance().load(getContext(), getActivity().getSharedPreferences("osmdroid", 0));

        MapView mapView = view.findViewById(R.id.mapView);

        mapView.getOverlays().clear();
        mapView.invalidate();

        mapView.setTileSource(TileSourceFactory.MAPNIK); // Use Mapnik tile source
        IMapController mapController = mapView.getController();
        mapController.setZoom(15.0);

        ArrayList<GeoLocation> coords = locations.getLocations();
        if (coords.size() > 0) {
            GeoPoint mapCenter = new GeoPoint(coords.get(0).getLatitude(), coords.get(0).getLongitude());
            mapController.setCenter(mapCenter);
            for (GeoLocation c : coords) {
                Marker marker = new Marker(mapView);
                marker.setPosition(new GeoPoint(c.getLatitude(), c.getLongitude()));
                mapView.getOverlays().add(marker);
            }
        } else {
            // Default to U of A if nobody has checked in (or enabled locations)
            GeoPoint mapCenter = new GeoPoint(53.523, -113.526);
            mapController.setCenter(mapCenter);
        }


    }
}