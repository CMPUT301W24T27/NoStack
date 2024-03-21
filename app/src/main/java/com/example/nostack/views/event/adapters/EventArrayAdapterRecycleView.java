package com.example.nostack.views.event.adapters;

import static java.util.Locale.CANADA;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
public class EventArrayAdapterRecycleView extends RecyclerView.Adapter<MyViewHolder> {

    private ArrayList<Event> events;
    private LayoutInflater inflater;
    private Fragment currFragment;
    private Context context;

    TextView eventTitle;
    TextView eventStartDateTitle;
    TextView eventTimeTitle;
    TextView eventLocationTitle;
    ImageView eventImage;



    public EventArrayAdapterRecycleView(Context context, ArrayList<Event> events, Fragment currfragment) {
        this.events = events;
        this.context = context;
        this.currFragment = currfragment;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MyViewHolder(LayoutInflater.from(context).inflate(R.layout.eventlistcontent,parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        holder.eventTitle.setText(events.get(position).getName());
        holder.eventLocationTitle.setText(events.get(position).getLocation());

        DateFormat df = new SimpleDateFormat("EEE, MMM d, yyyy", CANADA);
        DateFormat tf = new SimpleDateFormat("h:mm a", CANADA);

        String startDate = df.format(events.get(position).getStartDate());
        String endDate = df.format(events.get(position).getEndDate());
        String startTime = tf.format(events.get(position).getStartDate());
        String endTime = tf.format(events.get(position).getEndDate());

        if (!startDate.equals(endDate)) {
            holder.eventStartDateTitle.setText(startDate + " to");
            holder.eventTimeTitle.setText(endDate);
        } else {
            holder.eventStartDateTitle.setText(startDate);
            holder.eventTimeTitle.setText(startTime + " - " + endTime);
        }
        holder.eventImage.setVisibility(View.INVISIBLE);
        String uri = events.get(position).getEventBannerImgUrl();

        if (uri != null) {
            Log.d("TestURI", uri);
            // Get image from firebase storage
            StorageReference storageRef = FirebaseStorage.getInstance().getReferenceFromUrl(uri);
            final long ONE_MEGABYTE = 1024 * 1024;

            storageRef.getBytes(ONE_MEGABYTE).addOnSuccessListener(bytes -> {
                Bitmap bmp = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                Bitmap scaledBmp = Bitmap.createScaledBitmap(bmp, 250, 250, false);
                RoundedBitmapDrawable d = RoundedBitmapDrawableFactory.create(currFragment.getResources(), scaledBmp);
                d.setCornerRadius(50f);
                holder.eventImage.setImageDrawable(d);
                Log.d("EventImageLoader","Loading Event Image: " + events.get(position).getName());
            }).addOnFailureListener(exception -> {
                Log.w("User Profile", "Error getting profile image", exception);
            });
        }
        holder.eventImage.setVisibility(View.VISIBLE);
    }

    public void addEvent(Event event) {
        events.add(event);
    }

    public void clear() {
        events.clear();
    }

    public boolean containsEvent(Event event) {
        boolean contained = false;
        for (Event event1:events) {
            if (event.getId().equals(event1.getId())) {return true;}
        }
        return contained;
    }

    @Override
    public int getItemCount() {
        return events.size();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }
}
