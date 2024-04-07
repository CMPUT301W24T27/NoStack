package com.example.nostack.views.admin.adapters;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.nostack.R;

public class ImageViewHolder extends RecyclerView.ViewHolder{
    public TextView imageName, imageDate, imageSize, imageType;

    public ImageViewHolder(@NonNull View itemView, ImageArrayRecycleViewInterface imageArrayRecycleViewInterface) {
        super(itemView);
        imageName = itemView.findViewById(R.id.ImageListContentNameText);
        imageDate = itemView.findViewById(R.id.ImageListContentCreatedText);
        imageSize = itemView.findViewById(R.id.ImageListContentSizeText);
        imageType = itemView.findViewById(R.id.ImageListContentTypeText);

        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int pos = getAdapterPosition();
                if (pos != RecyclerView.NO_POSITION && imageArrayRecycleViewInterface != null) {
                    imageArrayRecycleViewInterface.onImageClick(pos);
                }
            }
        });
    }
}
