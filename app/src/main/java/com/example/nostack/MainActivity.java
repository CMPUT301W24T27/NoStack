package com.example.nostack;

import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.nostack.handlers.CurrentUserHandler;
import com.example.nostack.handlers.EventCheckinHandler;

  public class MainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        CurrentUserHandler.setOwnerActivity(this);
        CurrentUserHandler.setSingleton();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == EventCheckinHandler.LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Log.d("Main Activity", "Location permissions granted");
            } else {
                Log.d("Main Activity", "Location permissions denied");
            }
        }
    }
  }





