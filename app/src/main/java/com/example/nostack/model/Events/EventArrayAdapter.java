package com.example.nostack.model.Events;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.graphics.drawable.RoundedBitmapDrawable;
import androidx.core.graphics.drawable.RoundedBitmapDrawableFactory;
import androidx.fragment.app.Fragment;

import com.example.nostack.R;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

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
        add(event);
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
            view = LayoutInflater.from(getContext()).inflate(R.layout.eventlistcontent,parent,false);
        } else {
            view = convertView;
        }

        Event event = getItem(position);

        TextView eventTitle = view.findViewById(R.id.EventListContentNameText);
        TextView eventStartDateTitle = view.findViewById(R.id.EventListContentDateText);
        TextView eventTimeTitle = view.findViewById(R.id.EventListContentTimeText);
        TextView eventLocationTitle = view.findViewById(R.id.EventListContentLocationText);
        ImageView eventImage = view.findViewById(R.id.EventListContentPosterImage);

        if (event != null) {

            DateFormat df = new SimpleDateFormat("EEE, MMM d, yyyy");
            DateFormat tf = new SimpleDateFormat("h:mm a");

            String startDate = df.format(event.getStartDate());
            String endDate = df.format(event.getEndDate());
            String startTime = tf.format(event.getStartDate());
            String endTime = tf.format(event.getEndDate());

            if (!startDate.equals(endDate)) {
                eventStartDateTitle.setText(startDate + " to");
                eventTimeTitle.setText(endDate);
            } else {
                eventStartDateTitle.setText(startDate);
                eventTimeTitle.setText(startTime + " - " + endTime);
            }

            eventTitle.setText(event.getName());
            eventLocationTitle.setText(event.getLocation());

            String uri = event.getEventBannerImgUrl();

            if (uri != null) {
                // Get image from firebase storage
                StorageReference storageRef = FirebaseStorage.getInstance().getReferenceFromUrl(uri);
                final long ONE_MEGABYTE = 1024 * 1024;

                storageRef.getBytes(ONE_MEGABYTE).addOnSuccessListener(bytes -> {
                    Bitmap bmp = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                    Bitmap scaledBmp = Bitmap.createScaledBitmap(bmp, 250, 250, false);
                    RoundedBitmapDrawable d = RoundedBitmapDrawableFactory.create(currFragment.getResources(), scaledBmp);
                    d.setCornerRadius(50f);
                    eventImage.setImageDrawable(d);
                }).addOnFailureListener(exception -> {
                    Log.w("User Profile", "Error getting profile image", exception);
                });
            }
        }
        return view;
    }
}
