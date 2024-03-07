package com.example.nostack.utils;

import static com.google.api.ResourceProto.resource;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;

import com.example.nostack.R;

import java.util.ArrayList;

public class EventArrayAdapter extends ArrayAdapter<Event> {

    private ConstraintLayout layout;

    public EventArrayAdapter(@NonNull Context context, ArrayList<Event> events) {
        super(context, 0,events);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View view;
        if (convertView == null) {
            view = LayoutInflater.from(getContext()).inflate(R.layout.eventlistcontent,parent,false);
        } else {
            view = convertView;
        }

        Event event = getItem(position);

        TextView eventTitle = view.findViewById(R.id.EventListContentNameText);
        TextView eventStartDateTitle = view.findViewById(R.id.EventListContentDateText);
        TextView eventTimeTitle = view.findViewById(R.id.EventListContentTimeText);
        TextView eventLocationTitle = view.findViewById(R.id.EventListContentLocationText);

        if (event != null) {
            eventTitle.setText(event.getName());
            eventStartDateTitle.setText("Jan 1, 2024");
            eventTimeTitle.setText("18:00 - 23:00");
            eventLocationTitle.setText(event.getLocation());
        }

        return view;
    }
}
