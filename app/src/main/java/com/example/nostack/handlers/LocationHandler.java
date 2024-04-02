package com.example.nostack.handlers;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProvider;

import com.example.nostack.viewmodels.UserViewModel;

public class LocationHandler {
    public static final int LOCATION_PERMISSION_REQUEST_CODE = 1001;
    private static LocationManager locationManager;
    private static Activity activity;
    private static Context context;
    private static LocationHandler singleInstance = null;

    public static void setSingleton(Context context, Activity activity, LocationManager locationManager) {
        if (activity == null) {
            throw new RuntimeException("Activity, context, and location manager must be set in MainActivity.");
        }
        singleInstance = new LocationHandler();
    }
    public static LocationHandler getSingleton() {
        if (singleInstance == null) {
            throw new RuntimeException("Activity, context, and location manager must be set in MainActivity.");
        }
        return singleInstance;
    }

    public LocationHandler() {}

    public void handleLocationPermissions() {
        if (!isLocationEnabled()) {
            showLocationSettingsDialog();
            return;
        }

        if (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, LOCATION_PERMISSION_REQUEST_CODE);
        }
    }

    private boolean isLocationEnabled() {
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
    }

    public Location getLocation() {
        if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) && !locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
            Log.e("LocationHandler", "Location services are not enabled.");
            return null;
        }

        // Define a location listener
        LocationListener locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                // Location received, return it
                locationManager.removeUpdates(this); // Stop listening for updates
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {}
            @Override
            public void onProviderEnabled(String provider) {}
            @Override
            public void onProviderDisabled(String provider) {}
        };

        // Register the location listener for updates
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            locationManager.requestSingleUpdate(LocationManager.GPS_PROVIDER, locationListener, null);
            locationManager.requestSingleUpdate(LocationManager.NETWORK_PROVIDER, locationListener, null);
        } else {
            Log.e("LocationHandler", "Location permissions not granted.");
            return null;
        }

        // Return the last known location if available
        Location lastKnownLocationGPS = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        Location lastKnownLocationNetwork = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
        if (lastKnownLocationGPS != null) {
            return lastKnownLocationGPS;
        } else if (lastKnownLocationNetwork != null) {
            return lastKnownLocationNetwork;
        } else {
            Log.e("LocationHandler", "No last known location available.");
            return null;
        }
    }

    private void showLocationSettingsDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Location Services Required");
        builder.setMessage("Please enable location services to continue.");
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                context.startActivity(intent);
            }
        });
        builder.setNegativeButton("Cancel", null);
        builder.create().show();
    }
}
