package com.example.nostack.views.event.adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.nostack.R;
import com.example.nostack.models.Attendance;
import com.example.nostack.models.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;

public class AnnouncementHistoryArrayAdapter extends ArrayAdapter<HashMap<String, String>> {
    private ArrayList<HashMap<String, String>> announcements;
    public AnnouncementHistoryArrayAdapter(@NonNull Context context, ArrayList<HashMap<String, String>> announcements, Fragment currfragment) {
        super(context, 0);
        this.announcements = announcements;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View view;
        if (convertView == null) {
            view = LayoutInflater.from(getContext()).inflate(R.layout.announcement_list_content,parent,false);
        } else {
            view = convertView;
        }

        HashMap<String, String> announcementHash =  getItem(position);
        String dateunix = announcementHash.keySet().iterator().next();
        String message = announcementHash.get(dateunix);
        TextView announcementDesc = view.findViewById(R.id.AnnouncementDescription);
        announcementDesc.setText(message);

        return view;
    }


}
