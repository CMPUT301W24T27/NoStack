package com.example.nostack.views.event.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;

import com.example.nostack.R;
import com.example.nostack.models.Event;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

/**
 * EventArrayAdapter is an ArrayAdapter for the Event class
 */
public class EventArrayAdapter extends ArrayAdapter<Event> {

    private ConstraintLayout layout;
    private Fragment currFragment;
    private ArrayList<Event> ourEvents;

    /**
     * Constructor for the EventArrayAdapter
     * @param context The context of the current activity
     * @param events The list of events to display
     * @param currfragment The current fragment
     */
    public EventArrayAdapter(@NonNull Context context, ArrayList<Event> events, Fragment currfragment) {
        super(context, 0,events);
        currFragment = currfragment;
        ourEvents = events;
    }

    /**
     * Check if the event is already in the list
     * @param event The event to check
     * @return Returns true if the event is in the list, false otherwise
     */
    public boolean containsEvent(Event event) {
        boolean contained = false;
        for (Event event1:ourEvents) {
            if (event.getId().equals(event1.getId())) {return true;}
        }
        return contained;
    }

    /**
     * Add an event to the list
     * @param event The event to add
     */
    public void addEvent(Event event) {
        if (!containsEvent(event)) {
            add(event);
        }
    }

    /**
     * Get the view of the event
     * @param position The position of the item within the adapter's data set of the item whose view
     *        we want.
     * @param convertView The old view to reuse, if possible.
     * @param parent The parent that this view will eventually be attached to
     * @return Returns the view of the event
     */
    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View view;
        if (convertView == null) {
            view = LayoutInflater.from(getContext()).inflate(R.layout.fragment_admin_eventlistcontent,parent,false);
        } else {
            view = convertView;
        }

        Event event = getItem(position);

        TextView eventTitle = view.findViewById(R.id.admin_EventListContentNameText);
        TextView eventStartEndDateTitle = view.findViewById(R.id.admin_EventListContentDateText);
        TextView eventCreatedTitle = view.findViewById(R.id.admin_EventListContentCreatedText);
        TextView eventLocationTitle = view.findViewById(R.id.admin_EventListContentLocationText);
        TextView eventCapacityTitle = view.findViewById(R.id.admin_EventListContentCapacityText);

        if (event != null) {

            DateFormat df = new SimpleDateFormat("EEE, MMM d, yyyy");
            DateFormat tf = new SimpleDateFormat("h:mm a");

            String startDate = df.format(event.getStartDate());
            String endDate = df.format(event.getEndDate());
            String startTime = tf.format(event.getStartDate());
            String endTime = tf.format(event.getEndDate());
            String capacity = String.valueOf(event.getCapacity());

            if (!startDate.equals(endDate)) {
                eventStartEndDateTitle.setText(startDate + "to" + endDate);
            } else {
                eventStartEndDateTitle.setText(startDate);
            }

            eventCreatedTitle.setText("Created: " + event.getCreatedDate());
            eventTitle.setText(event.getName());
            eventLocationTitle.setText(event.getLocation());
            if (event.getCapacity() > 0){
                eventCapacityTitle.setText("Capacity: " + capacity);
            } else {
                eventCapacityTitle.setText("Capacity: N/A");
            }

        }
        return view;
    }
}
