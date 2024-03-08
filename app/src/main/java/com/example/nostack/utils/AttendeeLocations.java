package com.example.nostack.utils;

import java.io.Serializable;
import java.util.ArrayList;

public class AttendeeLocations implements Serializable {
    private ArrayList<GeoLocation> locations;
    public AttendeeLocations() {
        locations = new ArrayList<>();
    }
    public void addLocations(GeoLocation location){
        locations.add(location);
    }
    public ArrayList<GeoLocation> getLocations() {
        return locations;
    }
}
