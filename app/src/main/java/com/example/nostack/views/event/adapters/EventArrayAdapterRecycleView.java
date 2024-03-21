package com.example.nostack.views.event.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.graphics.drawable.RoundedBitmapDrawable;
import androidx.core.graphics.drawable.RoundedBitmapDrawableFactory;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.example.nostack.R;
import com.example.nostack.models.Event;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

/**
 * EventArrayAdapter is an ArrayAdapter for the Event class
 */
public class EventArrayAdapterRecycleView extends RecyclerView.Adapter {

    private ArrayList<Event> events;
    private LayoutInflater inflater;
    private Fragment currFragment;
    private AdapterView.OnItemClickListener itemClickListener;

    TextView eventTitle;
    TextView eventStartDateTitle;
    TextView eventTimeTitle;
    TextView eventLocationTitle;
    ImageView eventImage;



    public EventArrayAdapterRecycleView(Context context, ArrayList<Event> events, Fragment currfragment) {
        this.events = events;
        this.inflater = LayoutInflater.from(context);
        this.currFragment = currfragment;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.eventlistcontent, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        Event event = events.get(position);
//        TODO: Set views here.

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
    }

    @Override
    public int getItemCount() {
        return events.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements AdapterView.OnItemClickListener, View.OnClickListener {

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            eventTitle = itemView.findViewById(R.id.EventListContentNameText);
            eventStartDateTitle = itemView.findViewById(R.id.EventListContentDateText);
            eventTimeTitle = itemView.findViewById(R.id.EventListContentTimeText);
            eventLocationTitle = itemView.findViewById(R.id.EventListContentLocationText);
            eventImage = itemView.findViewById(R.id.EventListContentPosterImage);
        }

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

        }
    }
}
