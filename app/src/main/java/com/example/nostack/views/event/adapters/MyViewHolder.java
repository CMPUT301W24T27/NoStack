package com.example.nostack.views.event.adapters;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.nostack.R;

public class MyViewHolder extends RecyclerView.ViewHolder{

    TextView eventTitle, eventStartDateTitle, eventTimeTitle, eventLocationTitle;
    ImageView eventImage;

    public MyViewHolder(@NonNull View itemView) {
        super(itemView);
        eventTitle = itemView.findViewById(R.id.EventListContentNameText);
        eventStartDateTitle = itemView.findViewById(R.id.EventListContentDateText);
        eventTimeTitle = itemView.findViewById(R.id.EventListContentTimeText);
        eventLocationTitle = itemView.findViewById(R.id.EventListContentLocationText);
        eventImage = itemView.findViewById(R.id.EventListContentPosterImage);
    }
}
