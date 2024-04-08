package com.example.nostack.views.admin.adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;

import com.example.nostack.R;
import com.example.nostack.handlers.ImageViewHandler;
import com.example.nostack.models.Image;
import com.example.nostack.models.ImageDimension;
import com.example.nostack.models.User;
import com.example.nostack.viewmodels.ImageViewModel;

import java.util.ArrayList;

public class ProfileArrayRecycleViewAdapter extends RecyclerView.Adapter<ProfileViewHolder>{
    private ArrayList<User> users;
    private Fragment currFragment;
    private Context context;
    private ProfileArrayRecycleViewInterface profileArrayRecycleViewInterface;
    private ImageViewHandler imageViewHandler;

    public ProfileArrayRecycleViewAdapter(Context context, ArrayList<User> users, Fragment currfragment, ProfileArrayRecycleViewInterface profileArrayRecycleViewInterface) {
        this.users = users;
        this.context = context;
        this.currFragment = currfragment;
        this.profileArrayRecycleViewInterface = profileArrayRecycleViewInterface;
        imageViewHandler = ImageViewHandler.getSingleton();
    }

    @NonNull
    @Override
    public ProfileViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(context).inflate(R.layout.profilelistcontent, parent, false);
        return new ProfileViewHolder(itemView, profileArrayRecycleViewInterface);
    }

    @Override
    public void onBindViewHolder(@NonNull ProfileViewHolder holder, int position) {
        holder.profileName.setText(users.get(position).getUsername());
        holder.profileEmail.setText(users.get(position).getEmailAddress() == null ? "No Email" : users.get(position).getEmailAddress());
        holder.profilePhone.setText(users.get(position).getPhoneNumber() == null ? "No Phone" : users.get(position).getPhoneNumber());


        if(users.get(position) != null) {
            try {
                Log.d("ProfileArrayRecycleViewAdapter", "onBindViewHolder: " + users.get(position).getUsername() + " " + users.get(position).getProfileImageUrl());
                imageViewHandler.setUserProfileImage(users.get(position), holder.profileIcon, currFragment.getResources(), new ImageDimension(100, 100));
            }
            catch (Exception e) {
                Log.d("ProfileArrayRecycleViewAdapter", "onBindViewHolder: " + e.getMessage() + " " + users.get(position).getProfileImageUrl());
            }
        } else {
            holder.profileIcon.setImageResource(R.drawable.baseline_account_circle_24);
        }

    }

    @Override
    public int getItemCount() {
        return users.size();
    }

    public void setUsers(ArrayList<User> users) {
        this.users = users;
        notifyDataSetChanged();
    }

    public void addUser(User user) {
        users.add(user);
        notifyDataSetChanged();
    }

    public void removeUser(User user) {
        users.remove(user);
        notifyDataSetChanged();
    }

    public User getUser(int position) {
        return users.get(position);
    }

    public void clear() {
        users.clear();
        notifyDataSetChanged();
    }
}
