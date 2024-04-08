package com.example.nostack.views.admin.adapters;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.nostack.R;


public class ProfileViewHolder extends RecyclerView.ViewHolder{
    public TextView profileName, profileEmail, profilePhone;
    public ImageView profileIcon;

    public ProfileViewHolder(@NonNull View itemView, ProfileArrayRecycleViewInterface profileArrayRecycleViewInterface) {
        super(itemView);
        profileName = itemView.findViewById(R.id.ProfileListContentNameText);
        profileEmail = itemView.findViewById(R.id.ProfileListContentEmailText);
        profilePhone = itemView.findViewById(R.id.ProfileListContentPhoneNumberText);
        profileIcon = itemView.findViewById(R.id.admin_EventListContentLocationImage);

        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int pos = getAdapterPosition();
                if (pos != RecyclerView.NO_POSITION && profileArrayRecycleViewInterface != null) {
                    profileArrayRecycleViewInterface.onProfileClick(pos);
                }
            }
        });
    }
}
