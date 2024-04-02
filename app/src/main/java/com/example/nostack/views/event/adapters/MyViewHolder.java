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
    EventArrayRecycleViewInterface eventArrayRecycleViewInterface;

    public MyViewHolder(@NonNull View itemView, EventArrayRecycleViewInterface eventArrayRecycleViewInterface) {
        super(itemView);
        eventTitle = itemView.findViewById(R.id.EventListContentNameText);
        eventStartDateTitle = itemView.findViewById(R.id.EventListContentDateText);
        eventTimeTitle = itemView.findViewById(R.id.EventListContentTimeText);
        eventLocationTitle = itemView.findViewById(R.id.EventListContentLocationText);
        eventImage = itemView.findViewById(R.id.EventListContentPosterImage);

        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int pos = getAdapterPosition();
                if (pos != RecyclerView.NO_POSITION && eventArrayRecycleViewInterface != null) {
                    eventArrayRecycleViewInterface.OnItemClick(pos);
                }
            }
        });
    }
}
