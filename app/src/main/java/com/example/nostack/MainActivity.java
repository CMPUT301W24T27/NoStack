package com.example.nostack;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.navigation.fragment.NavHostFragment;

import com.example.nostack.handlers.CurrentUserHandler;
import com.example.nostack.handlers.ImageViewHandler;
import com.example.nostack.handlers.LocationHandler;
import com.example.nostack.services.NavbarConfig;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Register Handlers and Services
        CurrentUserHandler.setOwnerActivity(this);
        CurrentUserHandler.setSingleton();

        ImageViewHandler.setOwnerActivity(this);
        ImageViewHandler.setSingleton();

        // Register NavbarConfig
        NavbarConfig.setOwnerActivity(this);
        NavbarConfig.setSingleton();


        // Register receiver for notifications
        LocalBroadcastManager.getInstance(this).registerReceiver(new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String title = intent.getStringExtra("title");
                String message = intent.getStringExtra("message");
                new AlertDialog.Builder(MainActivity.this)
                        .setTitle(title)
                        .setMessage(message)
                        .setPositiveButton(android.R.string.ok, null)
                        .show();
            }
        }, new IntentFilter("Notification"));

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == LocationHandler.LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Log.d("Main Activity", "Location permissions granted");
            } else {
                Log.d("Main Activity", "Location permissions denied");
            }
        }
    }
}





