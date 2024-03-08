package com.example.nostack;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

    /**
     * This method is called when the activity is being created and then sets the view for the activity
     * @param savedInstanceState If the activity is being re-created from
     * a previous saved state, this is the state.
     */
  public class MainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }
  }





